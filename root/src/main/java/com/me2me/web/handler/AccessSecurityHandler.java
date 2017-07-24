package com.me2me.web.handler;

import com.alibaba.dubbo.common.json.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.exception.AccessSignNotMatchException;
import com.me2me.core.exception.AppIdException;
import com.me2me.core.exception.TokenNullException;
import com.me2me.core.exception.UidAndTokenNotMatchException;
import com.me2me.core.exception.UserGagException;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.monitor.service.MonitorService;
import com.me2me.user.dto.BasicDataDto;
import com.me2me.user.dto.BasicDataSuccessDto;
import com.me2me.user.model.ApplicationSecurity;
import com.me2me.user.model.Dictionary;
import com.me2me.user.model.UserGag;
import com.me2me.user.model.UserToken;
import com.me2me.user.service.UserService;
import com.me2me.web.JsonSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/14.
 */
public class AccessSecurityHandler extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private MonitorService monitorService;

    private static List<String> WHITE_LIST = Lists.newArrayList();

    private static List<String> INTERNAL_WHITE_LIST = Lists.newArrayList();

    private static List<String> TRUST_REQUEST_LIST = Lists.newArrayList();

    private static List<String> MONITOR_INTERCEPTOR_URLS = Lists.newArrayList();
    
    private static List<String> NEED_CHECK_GAG_LIST = Lists.newArrayList();

    static {
        WHITE_LIST.add("/api/user/login");
        WHITE_LIST.add("/api/user/signUp");
        WHITE_LIST.add("/api/user/findEncrypt");
        WHITE_LIST.add("/api/user/verify");
        //验证码登录与注册
        WHITE_LIST.add("/api/user/loginByVerify");
        WHITE_LIST.add("/api/user/signUpByVerify");

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


        // 初始化拦截URL
        MONITOR_INTERCEPTOR_URLS.add("/api/user/login");
        MONITOR_INTERCEPTOR_URLS.add("/api/user/signUp");
        MONITOR_INTERCEPTOR_URLS.add("/api/user/follow");
        MONITOR_INTERCEPTOR_URLS.add("/api/user/versionControl");
        MONITOR_INTERCEPTOR_URLS.add("/api/content/publish");
        MONITOR_INTERCEPTOR_URLS.add("/api/content/likes");
        MONITOR_INTERCEPTOR_URLS.add("/api/content/writeTag");
        MONITOR_INTERCEPTOR_URLS.add("/api/content/getContentDetail");
        MONITOR_INTERCEPTOR_URLS.add("/api/content/review");
        MONITOR_INTERCEPTOR_URLS.add("/api/home/hottest");
        MONITOR_INTERCEPTOR_URLS.add("/api/home/newest");
        MONITOR_INTERCEPTOR_URLS.add("/api/home/attention");


        //需要判断禁言的接口
        NEED_CHECK_GAG_LIST.add("/api/content/publish");//发布UGC、PGC
        NEED_CHECK_GAG_LIST.add("/api/content/writeTag");//用户贴标
        NEED_CHECK_GAG_LIST.add("/api/live/createLive");//发布王国
        NEED_CHECK_GAG_LIST.add("/api/content/review");//UGC、文章评论
        NEED_CHECK_GAG_LIST.add("/api/live/speak");//王国发表
        NEED_CHECK_GAG_LIST.add("/api/live/createKingdom");//新创建王国接口
        
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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

    private boolean isNeedInterceptor(String uri){
        return MONITOR_INTERCEPTOR_URLS.contains(uri);
    }



    private MonitorEvent builder(HttpServletRequest request){
        String uri = request.getRequestURI();
        long uid = Long.valueOf(request.getParameter("uid") == null ? "0" : request.getParameter("uid") );
        String temp = request.getParameter("channel");
        int channel = (int) getChannel(temp);
        Set<String> boots = Sets.newConcurrentHashSet();
        boots.add("/api/user/versionControl");
        if(boots.contains(uri)){
            MonitorEvent monitorEvent = new MonitorEvent(Specification.MonitorType.BOOT.index,Specification.MonitorAction.BOOT.index,channel,uid);
            return monitorEvent;
        }else{
            Map<String,Integer> actionUri = Maps.newConcurrentMap();
            actionUri.put("/api/user/login",Specification.MonitorAction.LOGIN.index);
            actionUri.put("/api/user/signUp",Specification.MonitorAction.REGISTER.index);
            actionUri.put("/api/content/writeTag",Specification.MonitorAction.FEELING_TAG.index);
            actionUri.put("/api/content/getContentDetail",Specification.MonitorAction.CONTENT_VIEW.index);
            actionUri.put("/api/content/review",Specification.MonitorAction.REVIEW.index);
            actionUri.put("/api/home/hottest",Specification.MonitorAction.HOTTEST.index);
            actionUri.put("/api/home/newest",Specification.MonitorAction.NEWEST.index);
            actionUri.put("/api/home/attention",Specification.MonitorAction.FOLLOW_LIST.index);
            MonitorEvent monitorEvent = new MonitorEvent(Specification.MonitorType.ACTION.index,actionUri.get(uri),channel,uid);
            return monitorEvent;
        }
    }

    public static void main(String[] args) {
        Map<String,Integer> actionUri = Maps.newConcurrentMap();
        Integer value = actionUri.get("fds");
        new MonitorEvent(0,value,0,9);

    }


//    public void afterCompletionx(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        // super.afterCompletion(request, response, handler, ex);
//        if(isNeedInterceptor(request.getRequestURI())){
//            // 开启拦截
//            long uid = Long.valueOf(request.getParameter("uid") == null ? "0" : request.getParameter("uid") );
//            String channel = request.getParameter("channel");
//            int channelInt = (int) getChannel(channel);
//            if("/api/user/login".equals(request.getRequestURI())) {
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.LOGIN.index, channelInt, uid));
//            }else if("/api/user/signUp".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.REGISTER.index, channelInt, uid));
//            }else if("/api/user/follow".equals(request.getRequestURI())){
//                if(request.getParameter("action").equals("0")){
//                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.FOLLOW.index, channelInt, uid));
//                }else {
//                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.UN_FOLLOW.index, channelInt, uid));
//                }
//            } else if("/api/user/versionControl".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.BOOT.index, Specification.MonitorAction.BOOT.index, channelInt, uid));
//            } else if("/api/content/publish".equals(request.getRequestURI())){
//                if(request.getParameter("type").equals("0")){
//                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.CONTENT_PUBLISH.index, channelInt, uid));
//                }else if(request.getParameter("type").equals("3")){
//                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.LIVE_PUBLISH.index, channelInt, uid));
//                }else if(request.getParameter("type").equals("1")||request.getParameter("type").equals("6")||request.getParameter("type").equals("7")||request.getParameter("type").equals("8")||request.getParameter("type").equals("9")){
//                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.FORWARD.index, channelInt, uid));
//                }
//            } else if("/api/content/likes".equals(request.getRequestURI())){
//                if(request.getParameter("action").equals("0")) {
//                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.LIKE.index, channelInt, uid));
//                }else{
//                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.UN_LIKE.index, channelInt, uid));
//                }
//            }else if("/api/content/writeTag".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.FEELING_TAG.index, channelInt, uid));
//            }else if("/api/content/getContentDetail".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.CONTENT_VIEW.index, channelInt, uid));
//            }else if("/api/content/review".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.REVIEW.index, channelInt, uid));
//            }else if("/api/home/hottest".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.HOTTEST.index, channelInt, uid));
//            }else if("/api/home/newest".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.NEWEST.index, channelInt, uid));
//            }else if("/api/home/attention".equals(request.getRequestURI())){
//                monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.FOLLOW_LIST.index, channelInt, uid));
//            }
//        }
//
//    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(isNeedInterceptor(request.getRequestURI())){
            long uid = Long.valueOf(request.getParameter("uid") == null ? "0" : request.getParameter("uid") );
            String channel = request.getParameter("channel");
            int channelInt = (int) getChannel(channel);
            if("/api/user/follow".equals(request.getRequestURI())){
                if(request.getParameter("action").equals("0")){
                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.FOLLOW.index, channelInt, uid));
                }else {
                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.UN_FOLLOW.index, channelInt, uid));
                }
            }else if("/api/content/publish".equals(request.getRequestURI())){
                if(request.getParameter("type").equals("0")){
                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.CONTENT_PUBLISH.index, channelInt, uid));
                }else if(request.getParameter("type").equals("3")){
                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.LIVE_PUBLISH.index, channelInt, uid));
                }else if(request.getParameter("type").equals("1")||request.getParameter("type").equals("6")||request.getParameter("type").equals("7")||request.getParameter("type").equals("8")||request.getParameter("type").equals("9")){
                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.FORWARD.index, channelInt, uid));
                }
            }else if("/api/content/likes".equals(request.getRequestURI())){
                if(request.getParameter("action").equals("0")) {
                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.LIKE.index, channelInt, uid));
                }else{
                    monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index, Specification.MonitorAction.UN_LIKE.index, channelInt, uid));
                }
            }else{
                MonitorEvent monitorEvent = builder(request);
                monitorService.post(monitorEvent);
            }
        }

    }

    private long getChannel(String channel){
        // todo provider cache for basic data
        BasicDataDto basicDataDto = new BasicDataDto();
        basicDataDto.setType(5);
        BasicDataSuccessDto basicDataSuccessDto = (BasicDataSuccessDto) userService.getBasicDataByType(basicDataDto).getData();
        List<BasicDataSuccessDto.BasicDataSuccessElement> list = basicDataSuccessDto.getResults();
        for(BasicDataSuccessDto.BasicDataSuccessElement element : list){
            List<Dictionary> dictionaries = element.getList();
            for(Dictionary dictionary : dictionaries){
                if(dictionary.getValue().equals(channel)){
                    return dictionary.getId();
                }
            }
        }
       return -1;
    }
}
