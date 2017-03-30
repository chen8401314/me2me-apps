package com.me2me.live.listener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.cache.LiveLastUpdate;
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.SpeakNewEvent;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class SpeakNewListener {

	private final ApplicationEventBus applicationEventBus;
	private final CacheService cacheService;
	private final UserService userService;
	private final LiveMybatisDao liveMybatisDao;
	
	@Autowired
    public SpeakNewListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao, CacheService cacheService, UserService userService){
        this.applicationEventBus = applicationEventBus;
        this.cacheService = cacheService;
        this.liveMybatisDao = liveMybatisDao;
        this.userService = userService;
    }
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
    public void speakNew(SpeakNewEvent speakNewEvent) {
		//新的逻辑，这里只要是可见内容的发言，都会通知非自己的人（@的因为已经直接通知了，所以这里不需要通知了）
		//核心圈发言，对核心圈直接发，对非核心圈有1小时延迟逻辑
		//非核心圈发言，对所有人都是直接发送
		Topic topic = liveMybatisDao.getTopicById(speakNewEvent.getTopicId());
		if(null == topic){
			return;
		}
		List<Long> coreUidList = new ArrayList<Long>();//核心圈UID集合
		List<Long> memberUidList = new ArrayList<Long>();//非核心圈UID集合
		List<Long> allUidList = new ArrayList<Long>();//所有的成员集合
		coreUidList.add(topic.getUid());//国王肯定是核心圈
		if(!StringUtils.isEmpty(topic.getCoreCircle())){
			JSONArray array = JSON.parseArray(topic.getCoreCircle());
			Long uid = null;
			for (int i = 0; i < array.size(); i++) {
				uid = array.getLong(i);
				if(!coreUidList.contains(uid)){
					coreUidList.add(uid);
				}
	        }
		}
		List<LiveFavorite> lfList = liveMybatisDao.getFavoriteAll(topic.getId());
		if(null != lfList && lfList.size() > 0){
			for(LiveFavorite lf : lfList){
				if(!coreUidList.contains(lf.getUid()) && !memberUidList.contains(lf.getUid())){
					memberUidList.add(lf.getUid());
				}
			}
		}
		if(coreUidList.size() > 0){
			allUidList.addAll(coreUidList);
		}
		if(memberUidList.size() > 0){
			allUidList.addAll(memberUidList);
		}
		
		long currentUid = speakNewEvent.getUid();
		
		//先弄红点，红点都是要有的
		//首先大红点（底下栏目上的红点）,其次针对当前王国cell上的红点
		MyLivesStatusModel livesStatusModel = null;
		MySubscribeCacheModel cacheModel= null;
		if(allUidList.size() > 0){
			for(Long uid : allUidList){
				if(uid.longValue() != currentUid){
					livesStatusModel = new MyLivesStatusModel(uid,"1");
		            cacheService.hSet(livesStatusModel.getKey(),livesStatusModel.getField(),"1");
		            
		            cacheModel = new MySubscribeCacheModel(topic.getUid(), topic.getId().toString(), String.valueOf(speakNewEvent.getFragmentId()));
		            String values = cacheService.hGet(cacheModel.getKey(), cacheModel.getField());
		            if(StringUtils.isEmpty(values) || Integer.parseInt(values) == 0){//如果不是0，那么认为有更历史的未读信息，则不进行新的设置
		                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
		            }
				}
			}
		}
		
		//下面开始相关推送
		boolean isCoreUser = false;
		if(coreUidList.contains(currentUid)){
			isCoreUser = true;
		}
		
		boolean isInvalid = true;//1小时缓存是否已失效
		if(isCoreUser){
			LiveLastUpdate liveLastUpdate = new LiveLastUpdate(topic.getId(),"1");
	        if(!StringUtils.isEmpty(cacheService.hGet(liveLastUpdate.getKey(),liveLastUpdate.getField()))){
	        	isInvalid = false;
	        }
	        //已失效的重新设置缓存时间
	        if(isInvalid) {
	            cacheService.hSet(liveLastUpdate.getKey(), liveLastUpdate.getField(), liveLastUpdate.getValue());
	            cacheService.expire(liveLastUpdate.getKey(), 3600);
	        }
		}
		
		UserProfile currentUserProfile = userService.getUserProfileByUid(currentUid);
		if(null == currentUserProfile){
			return;
		}
		String message = null;
		if(isCoreUser){//核心圈的发言
			if(speakNewEvent.getType() == Specification.LiveSpeakType.ANCHOR.index){//主播发言
				if(speakNewEvent.getContentType() == Specification.LiveContent.TEXT.index){//文字
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "更新了 “" + speakNewEvent.getFragmentContent() + "”";
				}else if(speakNewEvent.getContentType() == Specification.LiveContent.IMAGE.index){//图片
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "上传了图片";
				}
			}else if(speakNewEvent.getType() == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index){
				message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "更新了 “" + speakNewEvent.getFragmentContent() + "”";
			}else if(speakNewEvent.getType() == Specification.LiveSpeakType.VIDEO.index){
				message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "上传了视频";
			}else if(speakNewEvent.getType() == Specification.LiveSpeakType.SOUND.index){
				message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "上传了音频";
			}else if(speakNewEvent.getType() == 54 || speakNewEvent.getType() == 55 || speakNewEvent.getType() == 56){//聚合下发/转发内容
				if(speakNewEvent.getContentType() == 0 || speakNewEvent.getContentType() == 2){
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "更新了 “" + speakNewEvent.getFragmentContent()+"”";
				}else if(speakNewEvent.getContentType() == 1 || speakNewEvent.getContentType() == 51){
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "上传了图片";
				}else if(speakNewEvent.getContentType() == 62){
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "上传了视频";
				}else if(speakNewEvent.getContentType() == 63){
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "上传了音频";
				}
			}else if(speakNewEvent.getType() == 51 || speakNewEvent.getType() == 52){//分享内链
				if(speakNewEvent.getContentType() == 70 || speakNewEvent.getContentType() == 71
						|| speakNewEvent.getContentType() == 72 || speakNewEvent.getContentType() == 73
						|| speakNewEvent.getContentType() == 74){
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "转发了“" + speakNewEvent.getFragmentContent()+"”";
				}
			}
		}else{//非核心圈
			if(speakNewEvent.getType() == Specification.LiveSpeakType.FANS.index
					|| speakNewEvent.getType() == Specification.LiveSpeakType.FANS_WRITE_TAG.index){
				message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "回复了 “" + speakNewEvent.getFragmentContent()+"”";
			}else if(speakNewEvent.getType() == 51 || speakNewEvent.getType() == 52){//分享内链
				if(speakNewEvent.getContentType() == 70 || speakNewEvent.getContentType() == 71
						|| speakNewEvent.getContentType() == 72 || speakNewEvent.getContentType() == 73
						|| speakNewEvent.getContentType() == 74){
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "转发了 “" + speakNewEvent.getFragmentContent()+"”";
				}else if(speakNewEvent.getContentType() == 16){
					message = "《"+topic.getTitle() + "》 " + currentUserProfile.getNickName() + "留下了足迹 “" + speakNewEvent.getFragmentContent()+"”";
				}
			}
		}
		
		if(null == message){
			//不需要通知，结束
			return;
		}
		
		if(isCoreUser){//核心圈
			//给核心圈，直接骚扰
			if(coreUidList.size() > 0){
				for(Long uid : coreUidList){
					if(uid.longValue() != currentUid && this.checkTopicPush(topic.getId(), uid)){
						JsonObject jsonObject = new JsonObject();
			            jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
			            jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
			            jsonObject.addProperty("topicId", topic.getId());
			            jsonObject.addProperty("contentType", topic.getType());
			            jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//这里是给核心圈的通知，所以直接显示核心圈即可
			            String alias = String.valueOf(uid);
			            userService.pushWithExtra(alias, message, JPushUtils.packageExtra(jsonObject));
					}
				}
			}
			//给非核心圈的，则要有一小时机制
			if(isInvalid && memberUidList.size() > 0){
				for(Long uid : memberUidList){
					if(uid.longValue() != currentUid && this.checkTopicPush(topic.getId(), uid)){
						JsonObject jsonObject = new JsonObject();
			            jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
			            jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
			            jsonObject.addProperty("topicId", topic.getId());
			            jsonObject.addProperty("contentType", topic.getType());
			            jsonObject.addProperty("internalStatus", Specification.SnsCircle.OUT.index);//圈外人
			            String alias = String.valueOf(uid);
			            userService.pushWithExtra(alias, message, JPushUtils.packageExtra(jsonObject));
					}
				}
			}
		}else{//非核心圈
			//直接骚扰所有人
			if(allUidList.size() > 0){
				for(Long uid : allUidList){
					if(uid.longValue() != currentUid && this.checkTopicPush(topic.getId(), uid)){
						JsonObject jsonObject = new JsonObject();
			            jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
			            jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
			            jsonObject.addProperty("topicId", topic.getId());
			            jsonObject.addProperty("contentType", topic.getType());
			            if(coreUidList.contains(uid)){
			            	jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);
			            }else{
			            	jsonObject.addProperty("internalStatus", Specification.SnsCircle.OUT.index);//圈外人
			            }
			            String alias = String.valueOf(uid);
			            userService.pushWithExtra(alias, message, JPushUtils.packageExtra(jsonObject));
					}
				}
			}
		}
	}

	private boolean checkTopicPush(long topicId, long uid){
    	TopicUserConfig tuc = liveMybatisDao.getTopicUserConfig(uid, topicId);
    	if(null != tuc && tuc.getPushType().intValue() == 1){
    		return false;
    	}
    	return true;
    }
}
