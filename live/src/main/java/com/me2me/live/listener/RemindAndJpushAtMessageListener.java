package com.me2me.live.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
import com.me2me.user.model.JpushToken;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserNoticeUnread;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class RemindAndJpushAtMessageListener {

	private final ApplicationEventBus applicationEventBus;
	private final LiveMybatisDao liveMybatisDao;
	private final UserService userService;
	
	@Autowired
    public RemindAndJpushAtMessageListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao, UserService userService){
        this.applicationEventBus = applicationEventBus;
        this.liveMybatisDao = liveMybatisDao;
        this.userService = userService;
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
		
		List<Long> atUidList = new ArrayList<Long>();//被at的uid集合
		if(!StringUtils.isEmpty(speakDto.getExtra())){
			JSONObject obj = JSON.parseObject(speakDto.getExtra());
			if(null != obj.get("array")){
				JSONArray arr = obj.getJSONArray("array");
				if(null != arr && arr.size() > 0){
					for(int i=0;i<arr.size();i++){
						if(null != arr.getJSONObject(i).get("atUid")){
							atUidList.add(arr.getJSONObject(i).getLong("atUid"));
						}
					}
				}
				if(atUidList.size() == 0){
					atUidList.add(speakDto.getAtUid());
				}
			}else{
				atUidList.add(speakDto.getAtUid());
			}
		}else{
			atUidList.add(speakDto.getAtUid());
		}

        String message = "有人@你";
        
        Topic topic = liveMybatisDao.getTopicById(speakDto.getTopicId());
        int fromStatus = this.getInternalStatus(topic, speakDto.getUid());
        for(Long atUid : atUidList){
            this.liveRemind(atUid, speakDto.getUid(), Specification.LiveSpeakType.FANS.index, speakDto.getTopicId(), speakDto.getFragment());
            
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType", Specification.PushMessageType.AT.index);
            jsonObject.addProperty("topicId",speakDto.getTopicId());
            jsonObject.addProperty("contentType", topic.getType());
            jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
            jsonObject.addProperty("internalStatus", this.getInternalStatus(topic, atUid));
            jsonObject.addProperty("fromInternalStatus", fromStatus);
            userService.pushWithExtra(atUid.toString(), message, JPushUtils.packageExtra(jsonObject));
        }
        log.info("remindAndPush end");
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
        
        Date now = new Date();
        //V2.2.5版本开始使用新的红点体系
        UserNoticeUnread unu = new UserNoticeUnread();
        unu.setUid(targetUid);
        unu.setCreateTime(now);
        unu.setNoticeId(userNotice.getId());
        unu.setNoticeType(type);
        unu.setContentType(Specification.UserNoticeUnreadContentType.KINGDOM.index);
        unu.setCid(cid);
        unu.setLevel(Specification.UserNoticeLevel.LEVEL_1.index);
        userService.createUserNoticeUnread(unu);
        
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
                userService.pushWithExtra(alias, jsonObject.toString(), null);
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
                userService.pushWithExtra(alias, jsonObject.toString(), null);
            }
        }
    }
}
