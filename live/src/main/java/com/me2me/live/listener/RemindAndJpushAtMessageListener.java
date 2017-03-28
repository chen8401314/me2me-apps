package com.me2me.live.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.event.RemindAndJpushAtMessageEvent;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.JpushToken;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class RemindAndJpushAtMessageListener {

	private final ApplicationEventBus applicationEventBus;
	private final LiveMybatisDao liveMybatisDao;
	private final UserService userService;
	private final JPushService jPushService;
	
	@Autowired
    public RemindAndJpushAtMessageListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao, UserService userService, JPushService jPushService){
        this.applicationEventBus = applicationEventBus;
        this.liveMybatisDao = liveMybatisDao;
        this.userService = userService;
        this.jPushService = jPushService;
    }
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
    public void remindAndPush(RemindAndJpushAtMessageEvent event) {
		log.info("remindAndPush start...");
		SpeakDto speakDto = event.getSpeakDto();
		if(null == speakDto){
			return;
		}
		JSONArray atArray = null;
        if(speakDto.getAtUid()==-1){  //atUid==-1时为多人@
            JSONObject fragment = JSON.parseObject(speakDto.getFragment());
            if(fragment==null)
                return;
            atArray = fragment.containsKey("atArray")?fragment.getJSONArray("atArray"):null;
            if(atArray==null)
                return;
        }else{
            atArray = new JSONArray();
            atArray.add(speakDto.getAtUid());
        }

        Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
        UserProfile userProfile = userService.getUserProfileByUid(speakDto.getUid());
        int fromStatus = this.getInternalStatus(topic, speakDto.getUid());
        for(int i=0;i<atArray.size();i++){
            long atUid = atArray.getLongValue(i);
            this.liveRemind(atUid, speakDto.getUid(), Specification.LiveSpeakType.FANS.index, speakDto.getTopicId(), speakDto.getFragment());
            
            if(this.checkTopicPush(speakDto.getTopicId(), atUid)){
	            JsonObject jsonObject = new JsonObject();
	            jsonObject.addProperty("messageType", Specification.PushMessageType.AT.index);
	            jsonObject.addProperty("topicId",speakDto.getTopicId());
	            jsonObject.addProperty("contentType", topic.getType());
	            jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
	            jsonObject.addProperty("internalStatus", this.getInternalStatus(topic, atUid));
	            jsonObject.addProperty("fromInternalStatus", fromStatus);
	            jsonObject.addProperty("AtUid",speakDto.getUid());
	            jsonObject.addProperty("NickName",userProfile.getNickName());
	            String alias = String.valueOf(atUid);
	            jPushService.payloadByIdExtra(alias, topic.getTitle()+" "+userProfile.getNickName() + "@了你 "+event.getSpeakDto().getFragment(), JPushUtils.packageExtra(jsonObject));
            }
        }
        log.info("remindAndPush end");
	}
	
	//at的这里更改了逻辑，不受推送开关控制
	private boolean checkTopicPush(long topicId, long uid){
//    	TopicUserConfig tuc = liveMybatisDao.getTopicUserConfig(uid, topicId);
//    	if(null != tuc && tuc.getPushType().intValue() == 1){
//    		return false;
//    	}
    	return true;
    }
	
	private int getInternalStatus(Topic topic, long uid) {
        String coreCircle = topic.getCoreCircle();
        JSONArray array = JSON.parseArray(coreCircle);
        int internalStatus = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.getLong(i) == uid) {
                internalStatus = Specification.SnsCircle.CORE.index;
                break;
            }
        }
//        if (internalStatus == 0) {
//            internalStatus = userService.getUserInternalStatus(uid, topic.getUid());
//        }

        return internalStatus;
    }
	
	private void liveRemind(long targetUid, long sourceUid ,int type ,long cid,String fragment ){
        if(targetUid == sourceUid){
            return;
        }
        UserProfile userProfile = userService.getUserProfileByUid(sourceUid);
        UserProfile customerProfile = userService.getUserProfileByUid(targetUid);
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setReadStatus(userNotice.getReadStatus());
        userNotice.setCid(cid);
        Topic topic = liveMybatisDao.getTopicById(cid);
        userNotice.setCoverImage(topic.getLiveImage());
        if (fragment.length() > 50) {
            userNotice.setSummary(fragment.substring(0, 50));
        } else {
            userNotice.setSummary(fragment);
        }

        userNotice.setToUid(customerProfile.getUid());
        userNotice.setLikeCount(0);
        if (type == Specification.LiveSpeakType.FANS_WRITE_TAG.index) {
            userNotice.setReview(fragment);
            userNotice.setTag("");
            userNotice.setNoticeType(Specification.UserNoticeType.LIVE_TAG.index);
        } else if (type == Specification.LiveSpeakType.FANS.index) {
            userNotice.setReview(fragment);
            userNotice.setTag("");
            userNotice.setNoticeType(Specification.UserNoticeType.LIVE_REVIEW.index);
        }
        userNotice.setReadStatus(0);
        userService.createUserNotice(userNotice);
        UserTips userTips = new UserTips();
        userTips.setUid(targetUid);
        if (type == Specification.LiveSpeakType.FANS_WRITE_TAG.index) {
            userTips.setType(Specification.UserNoticeType.LIVE_TAG.index);
        } else if (type == Specification.LiveSpeakType.FANS.index) {
            userTips.setType(Specification.UserNoticeType.LIVE_REVIEW.index);
        }
        UserTips tips = userService.getUserTips(userTips);
        if (tips == null) {
            userTips.setCount(1);
            userService.createUserTips(userTips);
            //修改推送为极光推送,兼容老版本
            JpushToken jpushToken = userService.getJpushTokeByUid(targetUid);
            if (jpushToken != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("count", "1");
                String alias = String.valueOf(targetUid);
                jPushService.payloadByIdForMessage(alias, jsonObject.toString());
            }

        } else {
            tips.setCount(tips.getCount() + 1);
            userService.modifyUserTips(tips);
            //修改推送为极光推送,兼容老版本
            JpushToken jpushToken = userService.getJpushTokeByUid(targetUid);
            if (jpushToken != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("count", "1");
                String alias = String.valueOf(targetUid);
                jPushService.payloadByIdForMessage(alias, jsonObject.toString());
            }
        }
    }
}
