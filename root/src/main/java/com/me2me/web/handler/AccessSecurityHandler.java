package com.me2me.web.handler;

import com.alibaba.dubbo.common.json.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.me2me.cache.service.CacheService;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.utils.DateUtil;
import com.me2me.core.exception.AccessSignNotMatchException;
import com.me2me.core.exception.AppIdException;
import com.me2me.core.exception.TokenNullException;
import com.me2me.core.exception.UidAndTokenNotMatchException;
import com.me2me.core.exception.UserGagException;
import com.me2me.user.dto.AppHttpAccessDTO;
import com.me2me.user.model.ApplicationSecurity;
import com.me2me.user.model.UserGag;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;
import com.me2me.web.JsonSecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/14.
 */
public class AccessSecurityHandler extends HandlerInterceptorAdapter {

	private static final Logger log = LoggerFactory.getLogger(AccessSecurityHandler.class);
	
	private ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	
    @Autowired
    private UserService userService;
    @Autowired
    private CacheService cacheService;

    private static List<String> WHITE_LIST = Lists.newArrayList();

    private static List<String> INTERNAL_WHITE_LIST = Lists.newArrayList();

    private static List<String> TRUST_REQUEST_LIST = Lists.newArrayList();
    
    private static List<String> NEED_CHECK_GAG_LIST = Lists.newArrayList();

    static {
        WHITE_LIST.add("/api/user/login");
        WHITE_LIST.add("/api/user/signUp");
        WHITE_LIST.add("/api/user/findEncrypt");
        WHITE_LIST.add("/api/user/verify");
        WHITE_LIST.add("/api/user/thirdPartLogin");
        WHITE_LIST.add("/api/io/getQiniuAccessToken");
        //验证码登录与注册
        WHITE_LIST.add("/api/user/loginByVerify");
        WHITE_LIST.add("/api/user/signUpByVerify");
        WHITE_LIST.add("/api/user/getGuideInfo");
        WHITE_LIST.add("/api/user/getLevelList");
        
        WHITE_LIST.add("/api/user/getBasicDataByType");
        WHITE_LIST.add("/api/user/versionControl");
        WHITE_LIST.add("/api/user/activityModel");
        WHITE_LIST.add("/api/user/checkNameOpenId");
        WHITE_LIST.add("/api/user/touristLogin");
        WHITE_LIST.add("/api/live/testApi");
        WHITE_LIST.add("/api/activity/enterActivity");
        WHITE_LIST.add("/api/activity/getActivityUser");
        WHITE_LIST.add("/api/activity/bindGetActivityUser");
        WHITE_LIST.add("/api/activity/getActivityInfo");
        WHITE_LIST.add("/api/activity/oneKeyAudit");
        WHITE_LIST.add("/api/activity/milidata");
        WHITE_LIST.add("/api/activity/optForcedPairing");
        WHITE_LIST.add("/api/activity/recommendHistory");
        WHITE_LIST.add("/api/activity/getlightboxInfo");
        WHITE_LIST.add("/api/user/IOSWapxUserRegist");
        WHITE_LIST.add("/api/spread/check");
        WHITE_LIST.add("/api/spread/click");
        WHITE_LIST.add("/api/activity/billboard");
        WHITE_LIST.add("/api/activity/areaHot");
        WHITE_LIST.add("/api/activity/areaSupport");
        WHITE_LIST.add("/api/activity/chatQuery");
        WHITE_LIST.add("/api/activity/top10SupportChatQuery");
        WHITE_LIST.add("/api/activity/chat");


        INTERNAL_WHITE_LIST.add("/api/console/showContents");
        INTERNAL_WHITE_LIST.add("/api/console/showActivity");
        INTERNAL_WHITE_LIST.add("/api/content/publish");
        INTERNAL_WHITE_LIST.add("/api/console/createActivity");
        INTERNAL_WHITE_LIST.add("/api/console/option");
        INTERNAL_WHITE_LIST.add("/api/console/createActivityNotice");
        INTERNAL_WHITE_LIST.add("/api/console/showDetails");
        INTERNAL_WHITE_LIST.add("/api/console/bindAccount");
        INTERNAL_WHITE_LIST.add("/api/io/getQiniuAccessToken");
        INTERNAL_WHITE_LIST.add("/api/monitor/report");
        INTERNAL_WHITE_LIST.add("/api/console/modify");
        INTERNAL_WHITE_LIST.add("/api/console/kingTopic");
        INTERNAL_WHITE_LIST.add("/api/user/IOSWapxUserRegist");
        INTERNAL_WHITE_LIST.add("/api/spread/check");
        INTERNAL_WHITE_LIST.add("/api/spread/click");
        INTERNAL_WHITE_LIST.add("/api/activity/billboard");
        INTERNAL_WHITE_LIST.add("/api/activity/areaHot");
        INTERNAL_WHITE_LIST.add("/api/activity/areaSupport");
        INTERNAL_WHITE_LIST.add("/api/activity/chatQuery");
        INTERNAL_WHITE_LIST.add("/api/activity/top10SupportChatQuery");
        INTERNAL_WHITE_LIST.add("/api/activity/chat");


        TRUST_REQUEST_LIST.add("/api/user/getSpecialUserProfile");


        //需要判断禁言的接口
        NEED_CHECK_GAG_LIST.add("/api/content/publish");//发布UGC、PGC
        NEED_CHECK_GAG_LIST.add("/api/content/writeTag");//用户贴标
        NEED_CHECK_GAG_LIST.add("/api/live/createLive");//发布王国
        NEED_CHECK_GAG_LIST.add("/api/content/review");//UGC、文章评论
        NEED_CHECK_GAG_LIST.add("/api/live/speak");//王国发表
        NEED_CHECK_GAG_LIST.add("/api/live/createKingdom");//新创建王国接口
        NEED_CHECK_GAG_LIST.add("/api/live/createLottery");//创建抽奖
        NEED_CHECK_GAG_LIST.add("/api/live/editLottery");//编辑抽奖
        NEED_CHECK_GAG_LIST.add("/api/live/joinLottery");//参与抽奖
        NEED_CHECK_GAG_LIST.add("/api/live/harvestKingdomCoin");//收割米汤币
        NEED_CHECK_GAG_LIST.add("/api/live/sendGift");//送礼物
        
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	startTime.set(System.currentTimeMillis());
    	
        String is_skip = request.getParameter("is_skip");
        if(TRUST_REQUEST_LIST.contains(request.getRequestURI())){
            is_skip = "ok";
        }
        if(!"ok".equals(is_skip)){
            if(!INTERNAL_WHITE_LIST.contains(request.getRequestURI())) {
                String value = request.getParameter("security");
                JsonSecurity jsonSecurity = JSON.parse(value, JsonSecurity.class);
                // 检测签名
                ApplicationSecurity applicationSecurity = userService.getApplicationSecurityByAppId(jsonSecurity.getAppId());
                if (applicationSecurity == null) {
                    throw new AppIdException("appId not exists!");
                } else {
                    String secretKey = applicationSecurity.getSecretKey();
                    String sign = SecurityUtils.sign(jsonSecurity.getAppId(), secretKey, String.valueOf(jsonSecurity.getCurrentTime()), jsonSecurity.getNonce());
                    if (!sign.equals(jsonSecurity.getSign())) {
                        throw new AccessSignNotMatchException("app access sign not match,please check your application!");
                    }
                }
            }
        }
        //判断禁言
        if(NEED_CHECK_GAG_LIST.contains(request.getRequestURI())){
        	String uid = request.getParameter("uid");
        	if(!Strings.isNullOrEmpty(uid)){
        		UserGag gag = new UserGag();
        		gag.setCid(0l);
        		gag.setGagLevel(0);
        		gag.setTargetUid(Long.valueOf(uid));
        		gag.setType(0);
        		
        		if(userService.checkGag(gag)){
        			throw new UserGagException("user is gagged!");
        		}
        	}
        }
        
        if(!WHITE_LIST.contains(request.getRequestURI())) {
            String uid = request.getParameter("uid");
            String token = request.getParameter("token");
            if (Strings.isNullOrEmpty(uid) || Strings.isNullOrEmpty(token)) {
                throw new TokenNullException("uid or token is null!");
            } else {
                long tempUid = Long.valueOf(uid);
                UserToken userToken = userService.getUserByUidAndToken(tempUid, token);
                if (userToken == null) {
                    throw new UidAndTokenNotMatchException("uid and token not matches!");
                } else {
                    return true;
                }
            }
        }else{
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    	String uid = "0";
		Map<String, String> paramMap = new HashMap<>();
		Enumeration<String> em = request.getParameterNames();
		String paramName = null;
		while (em.hasMoreElements()) {
			paramName = em.nextElement();
			if("uid".equals(paramName)){
				uid = request.getParameter(paramName);
			}
			paramMap.put(paramName, request.getParameter(paramName));
		}
		
		String httpParams = com.alibaba.fastjson.JSON.toJSONString(paramMap);
		long currentTime = System.currentTimeMillis();
		long execTime = currentTime - startTime.get();
		log.info("[{}]-[{}]-[{}], EXECUTE TIME : [{}ms]", uid, request.getRequestURI(), httpParams, execTime);
		
		// 过滤一下接口
        if(request.getRequestURI().startsWith("/api/console")//ims接口
                || request.getRequestURI().startsWith("/api/home/initSquareUpdateId")//技术后门，初始化updateid
                || request.getRequestURI().startsWith("/api/mobile")//无效接口
                || request.getRequestURI().startsWith("/api/spread")//无效接口
                || request.getRequestURI().startsWith("/api/live/getUpdate")//王国详情轮询接口
                || request.getRequestURI().startsWith("/api/user/noticeReddotQuery")//通知红点轮询接口
                ){
            return;
        }
		
		long longuid = 0;
		try{
			longuid = Long.valueOf(uid);
		}catch(Exception ignore){}
		
		AppHttpAccessDTO dto = new AppHttpAccessDTO();
		dto.setUid(longuid);
		dto.setRequestUri(request.getRequestURI());
		dto.setRequestMethod(request.getMethod());
		dto.setRequestParams(httpParams);
		dto.setStartTime(startTime.get());
		dto.setEndTime(currentTime);
		userService.saveUserHttpAccess(dto);
		
		//记录用户请求时间
		if(longuid > 0){
			Date now = new Date();
			String timeStr = DateUtil.date2string(now, "yyyy-MM-dd HH:mm:ss");
			
			String key = "USER:LASTTIME:" + longuid;
			cacheService.set(key, timeStr);
		}
    }

}
