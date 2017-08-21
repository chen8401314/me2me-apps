package com.me2me.sms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.me2me.cache.CacheConstant;
import com.me2me.cache.service.CacheService;
import com.me2me.common.sms.YunXinSms;
import com.me2me.common.utils.im.HostType;
import com.me2me.common.utils.im.IMHttpUtil;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.sms.channel.MessageClient;
import com.me2me.sms.dto.ImRefreshDto;
import com.me2me.sms.dto.ImSendMessageDto;
import com.me2me.sms.dto.ImUserInfoDto;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.event.VerifyEvent;
import com.me2me.sms.exception.SendMessageLimitException;
import com.me2me.sms.exception.SendMessageTimeException;
import com.me2me.sms.listener.VerifyCodeListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private ApplicationEventBus applicationEventBus;

    @Autowired
    private CacheService cacheService;

    private static final String VERIFY_PREFIX = "verify:";

    @Value("#{app.IM_APP_KEY}")
    private String IM_APP_KEY;

    @Value("#{app.IM_APP_SECRET}")
    private String IM_APP_SECRET;

    private Splitter splitter = Splitter.on("@").trimResults();

    @Autowired
    private VerifyCodeListener verifyCodeListener;

    @Autowired
    private MessageClient messageClient;

    /**
     * 发送验证码
     * @param verifyDto
     */
    @Override
    public Response send(VerifyDto verifyDto){
    	try{
    		verifyCodeListener.send(new VerifyEvent(verifyDto.getMobile(),verifyDto.getVerifyCode(),verifyDto.getChannel(),verifyDto.getIsTest()));
    	}catch(Exception e){
    		if(e instanceof SendMessageLimitException || e instanceof SendMessageTimeException){
    			return Response.success(20094,e.getMessage());
    		}else{
    			return Response.failure(e.getMessage());
    		}
    	}
    	return Response.success(ResponseStatus.USER_VERIFY_GET_SUCCESS.status,ResponseStatus.USER_VERIFY_GET_SUCCESS.message);
    }

    /**
     * 校验验证码
     * @param verifyDto
     */
    @Override
    public boolean verify(VerifyDto verifyDto) {
        // 网易云信通道验证
        if(verifyDto.getChannel() == ChannelType.NET_CLOUD_SMS.index) {
            return YunXinSms.verify(verifyDto.getMobile(), verifyDto.getVerifyCode());
        }
        // 获取redis中的数据
        String verifyCodeAndSendTimeMillis = cacheService.get(VERIFY_PREFIX+verifyDto.getMobile());
        if(!StringUtils.isEmpty(verifyCodeAndSendTimeMillis)){
            String verifyCode = splitter.splitToList(verifyCodeAndSendTimeMillis).get(0);
            if(verifyDto.getVerifyCode().equals(verifyCode))
                return true;
            return false;
        }
        return false;
    }

    @Override
    public boolean sendMessage(String nickName ,String awardName ,String mobile ,String OperateMobile) {
        Boolean isTrue = YunXinSms.sendSms2(nickName ,awardName ,mobile ,OperateMobile);
        if(isTrue){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean sendQIMessage(String mobile) {
        Boolean isTrue = YunXinSms.sendSms3(mobile);
        if(isTrue){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean sendQIauditMessage(List mobileList) {
        Boolean isTrue = YunXinSms.sendSms4(mobileList);
        if(isTrue){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void send7daySignUp(String mobile) {
        HashMap<String,Object> result = null;
        //没有需要传的数据传空，否则报错
        result = messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobile,"142378",new String[]{""});
        System.out.println(result);
    }

    @Override
    public void send7dayApply(List mobileList) {
        String mobiles = getListToString(mobileList);
        HashMap<String,Object> result = null;
        result = messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobiles,"106877",new String[]{"测试","5"});
    }

    @Override
    public void send7dayCommon(String templateId, List<String> mobileList, List<String> messageList) {
    	StringBuilder msb = new StringBuilder();
    	if(null != mobileList && mobileList.size() > 0){
    		for(String m : mobileList){
    			msb.append(",").append(m.trim());
    		}
    	}
    	String mobile = msb.toString();
    	if(mobile.length() > 0){
    		mobile = mobile.substring(1);
    	}
    	
    	try{
    		if(null == messageList || messageList.size() == 0){
        		messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobile, templateId, new String[]{""});
        	}else{
        		String[] msgs = new String[messageList.size()];
        		for(int i=0;i<messageList.size();i++){
        			msgs[i] = messageList.get(i);
        		}
        		messageClient.getCcpRestSmsSDK().sendTemplateSMS(mobile, templateId, msgs);
        	}
    	}catch(Exception e){
    		log.error("发送短信失败", e);
    	}
    }

    @Override
    public ImUserInfoDto getIMUsertoken(long uid,String nickName,String avatar) throws Exception {
        return getToken(String.valueOf(uid),nickName,avatar);
    }

    /**
     * List转换成String逗号分隔的形式
     *
     * @param list
     * @return
     */
    private String getListToString(List list) {
        return StringUtils.join(list.toArray(), ",");
    }

    /**
     * Im 获取调用远程接口获取token
     * @param userId
     * @param name
     * @param portraitUri
     * @return
     * @throws Exception
     */
    public ImUserInfoDto getToken(String userId, String name, String portraitUri) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("Paramer 'userId' is required");
        }

        if (name == null) {
            throw new IllegalArgumentException("Paramer 'name' is required");
        }

        if (portraitUri == null) {
            throw new IllegalArgumentException("Paramer 'portraitUri' is required");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&userId=").append(URLEncoder.encode(userId.toString(), "UTF-8"));
        sb.append("&name=").append(URLEncoder.encode(name.toString(), "UTF-8"));
        sb.append("&portraitUri=").append(URLEncoder.encode(portraitUri.toString(), "UTF-8"));
        String body = sb.toString();
        if (body.indexOf("&") == 0) {
            body = body.substring(1, body.length());
        }

        HttpURLConnection conn = IMHttpUtil.CreatePostHttpConnection(HostType.API, IM_APP_KEY, IM_APP_SECRET, "/user/getToken.json", "application/x-www-form-urlencoded");
        IMHttpUtil.setBodyParameter(body, conn);
        ImUserInfoDto result = JSON.parseObject(IMHttpUtil.returnResult(conn) ,ImUserInfoDto.class);

        return result;
    }
    @Override
    public ImSendMessageDto sendSysMessage(String userId, String content) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("Paramer 'userId' is required");
        }
        if (content == null) {
            throw new IllegalArgumentException("Paramer 'content' is required");
        }
        StringBuilder sb = new StringBuilder();
        String fromUserId = cacheService.get(CacheConstant.APP_CONFIG_KEY_PRE+"SELL_UID");
        sb.append("&fromUserId=").append(URLEncoder.encode(fromUserId.toString(), "UTF-8"));
        sb.append("&toUserId=").append(URLEncoder.encode(userId.toString(), "UTF-8"));
        JSONObject json = new JSONObject();
        json.put("content", content);
        sb.append("&content=").append(URLEncoder.encode(json.toJSONString(), "UTF-8"));
        sb.append("&objectName=").append(URLEncoder.encode("RC:TxtMsg", "UTF-8"));
        sb.append("&pushContent=").append(URLEncoder.encode(content, "UTF-8"));
        JSONObject json1 = new JSONObject();
        json1.put("pushData", content);
        sb.append("&pushData=").append(URLEncoder.encode(json1.toJSONString(), "UTF-8"));
        String body = sb.toString();
        if (body.indexOf("&") == 0) {
            body = body.substring(1, body.length());
        }
        HttpURLConnection conn = IMHttpUtil.CreatePostHttpConnection(HostType.API, IM_APP_KEY, IM_APP_SECRET, "/message/private/publish.json", "application/x-www-form-urlencoded");
        IMHttpUtil.setBodyParameter(body, conn);
        ImSendMessageDto result = JSON.parseObject(IMHttpUtil.returnResult(conn) ,ImSendMessageDto.class);
        return result;
    }
    
    /**
     * Im 获取调用远程接口刷新用户信息
     * @param userId
     * @param name
     * @param portraitUri
     * @return
     * @throws Exception
     */
    @Override
    public ImRefreshDto refreshUser(String userId, String name, String portraitUri) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("Paramer 'userId' is required");
        }

        if (name == null) {
            throw new IllegalArgumentException("Paramer 'name' is required");
        }

        if (portraitUri == null) {
            throw new IllegalArgumentException("Paramer 'portraitUri' is required");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&userId=").append(URLEncoder.encode(userId.toString(), "UTF-8"));
        sb.append("&name=").append(URLEncoder.encode(name.toString(), "UTF-8"));
        sb.append("&portraitUri=").append(URLEncoder.encode(portraitUri.toString(), "UTF-8"));
        String body = sb.toString();
        if (body.indexOf("&") == 0) {
            body = body.substring(1, body.length());
        }

        HttpURLConnection conn = IMHttpUtil.CreatePostHttpConnection(HostType.API, IM_APP_KEY, IM_APP_SECRET, "/user/refresh.json", "application/x-www-form-urlencoded");
        IMHttpUtil.setBodyParameter(body, conn);
        ImRefreshDto result = JSON.parseObject(IMHttpUtil.returnResult(conn) ,ImRefreshDto.class);
        return result;
    }
}
