package com.me2me.live.listener;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.cache.LiveLastUpdate;
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.AggregationPublishEvent;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicAggregation;
import com.me2me.live.model.TopicFragment;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.live.service.LiveServiceImpl;
import com.me2me.sms.service.JPushService;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class AggregationPublishListener {

	private final ApplicationEventBus applicationEventBus;
	private final LiveMybatisDao liveMybatisDao;
	private final UserService userService;
	private final ContentService contentService;
	private final CacheService cacheService;
	private final JPushService jPushService;
	
	@Autowired
	public AggregationPublishListener(ApplicationEventBus applicationEventBus, LiveMybatisDao liveMybatisDao,
			UserService userService, ContentService contentService, CacheService cacheService,
			JPushService jPushService){
		this.applicationEventBus = applicationEventBus;
		this.liveMybatisDao = liveMybatisDao;
		this.userService = userService;
		this.contentService = contentService;
		this.cacheService = cacheService;
		this.jPushService = jPushService;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
    public void publish(AggregationPublishEvent event) {
		log.info("aggregation publish begin...uid:"+event.getUid()+",topicId:"+event.getTopicId()+",fid:"+event.getFid());
		//获取所有子王国
		List<TopicAggregation> taList = liveMybatisDao.getTopicAggregationsByTopicId(event.getTopicId());
		if(null != taList && taList.size() > 0){
			TopicFragment tf = liveMybatisDao.getTopicFragmentById(event.getFid());
			TopicFragment newtf = new TopicFragment();
			newtf.setUid(event.getUid());//记录操作人的UID
			newtf.setFragmentImage(tf.getFragmentImage());
			newtf.setFragment(tf.getFragment());
			newtf.setType(54);//上位王国下发内容
			newtf.setContentType(this.genContentType(tf.getType(), tf.getContentType()));//转换成新的contentType
			String extra = tf.getExtra();
			//extra转换，添加from属性
			if(null == extra || "".equals(extra)){
				extra = "{}";
			}
			
			JSONObject obj = JSON.parseObject(extra);
			obj.put("action", Integer.valueOf(0));//下发的暂时没有什么花头，和前端沟通后，暂设为0
			
			UserProfile up = userService.getUserProfileByUid(event.getUid());
			Content topicContent = contentService.getContentByTopicId(event.getTopicId());
			Topic topic = liveMybatisDao.getTopicById(event.getTopicId());
			JSONObject fromObj = new JSONObject();
			fromObj.put("uid", Long.valueOf(event.getUid()));
			fromObj.put("avatar", Constant.QINIU_DOMAIN+"/"+up.getAvatar());
			fromObj.put("id", Long.valueOf(event.getTopicId()));
			fromObj.put("cid", topicContent.getId());
			fromObj.put("title", topic.getTitle());
			fromObj.put("cover", Constant.QINIU_DOMAIN+"/"+topic.getLiveImage());
			fromObj.put("url", Constant.Live_WEB_URL+event.getTopicId());
			obj.put("from", fromObj);
			
			Topic subTopic = null;
			String only = null;
			for(TopicAggregation ta : taList){
				subTopic = liveMybatisDao.getTopicById(ta.getSubTopicId());
				if(null != subTopic){//有可能已经被删除掉了
					if(subTopic.getAcPublishType() == 0 && ta.getIsPublish() == 0){//王国上接受下发的开关开，并且聚合关系上也是可以接受下发
						only = UUID.randomUUID().toString()+"-"+new Random().nextInt();
						obj.put("only", only);
						newtf.setExtra(obj.toJSONString());
						newtf.setTopicId(subTopic.getId());
						newtf.setId(null);
						liveMybatisDao.createTopicFragment(newtf);
						
//						if(this.isInCore(event.getUid(), subTopic.getCoreCircle())){
							//核心圈的，相当于核心圈发言，需要更新更新时间
							Calendar calendar = Calendar.getInstance();
							subTopic.setUpdateTime(calendar.getTime());
							subTopic.setLongTime(calendar.getTimeInMillis());
				            liveMybatisDao.updateTopic(subTopic);
//						}
						
						//更新缓存
						long lastFragmentId = newtf.getId();
                        int total = liveMybatisDao.countFragmentByTopicId(subTopic.getId());
                        String value = lastFragmentId + "," + total;
                        cacheService.hSet(LiveServiceImpl.TOPIC_FRAGMENT_NEWEST_MAP_KEY, "T_" + subTopic.getId(), value);
						
						//红点&&推送
                        //当核心圈发言，需要通知所有订阅者
                        MySubscribeCacheModel cacheModel= null;
                        //非国王发言，提醒国王红点
            	    	if(subTopic.getUid().longValue() != event.getUid()){
            	            cacheModel = new MySubscribeCacheModel(subTopic.getUid(), String.valueOf(subTopic.getId()), "1");
            	            cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            	        }
            	    	
            	    	LiveLastUpdate liveLastUpdate = new LiveLastUpdate(subTopic.getId(),"1");
            	        
            	    	boolean isInvalid = true;//1小时缓存是否已失效
            	    	if(!StringUtils.isEmpty(cacheService.hGet(liveLastUpdate.getKey(),liveLastUpdate.getField()))){
            	        	isInvalid = false;
            	        }
            	    	//已失效的重新设置缓存时间
            	        if(isInvalid) {
            	            log.info("set cache timeout");
            	            cacheService.hSet(liveLastUpdate.getKey(), liveLastUpdate.getField(), liveLastUpdate.getValue());
            	            cacheService.expire(liveLastUpdate.getKey(), 3600);
            	        }
            			
            			List<LiveFavorite> liveFavorites = liveMybatisDao.getFavoriteAll(subTopic.getId());
            			if(null != liveFavorites && liveFavorites.size() > 0){
            				for(LiveFavorite liveFavorite : liveFavorites){
            					if(liveFavorite.getUid().longValue() != event.getUid()) {
            						//一级菜单红点
            						MyLivesStatusModel livesStatusModel = new MyLivesStatusModel(liveFavorite.getUid(),"1");
            			            cacheService.hSet(livesStatusModel.getKey(),livesStatusModel.getField(),"1");
            			            //王国列表的单个王国上红点
            						cacheModel = new MySubscribeCacheModel(liveFavorite.getUid(), liveFavorite.getTopicId().toString(), "1");
            						cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            						
            						//推送
            						if(isInvalid && this.checkTopicPush(liveFavorite.getTopicId(), liveFavorite.getUid())){
            							JsonObject jsonObject = new JsonObject();
            			                jsonObject.addProperty("messageType", Specification.PushMessageType.UPDATE.index);
            			                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
            			                jsonObject.addProperty("topicId",subTopic.getId());
            			                jsonObject.addProperty("contentType", subTopic.getType());
            			                jsonObject.addProperty("internalStatus", this.getInternalStatus(subTopic, liveFavorite.getUid()));
            			                jsonObject.addProperty("fromInternalStatus", Specification.SnsCircle.CORE.index);//主播发言的，都是核心圈
            			                String alias = String.valueOf(liveFavorite.getUid());
            			                userService.pushWithExtra(alias,  "『"+subTopic.getTitle() + "』有更新", JPushUtils.packageExtra(jsonObject));
            						}
            					}
            				}
            			}
					}
				}
			}
		}
		
		log.info("aggregation publish end...");
	}
	
	private boolean isInCore(long uid, String coreCircle){
		boolean result = false;
		if(null != coreCircle && !"".equals(coreCircle)){
			JSONArray array = JSON.parseArray(coreCircle);
	        for (int i = 0; i < array.size(); i++) {
	            if (array.getLong(i).longValue() == uid) {
	            	result = true;
	                break;
	            }
	        }
		}
		return result;
	}
	
	private boolean checkTopicPush(long topicId, long uid){
    	TopicUserConfig tuc = liveMybatisDao.getTopicUserConfig(uid, topicId);
    	if(null != tuc && tuc.getPushType().intValue() == 1){
    		return false;
    	}
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
        return internalStatus;
    }
	
	private int genContentType(int oldType, int oldContentType){
		if(oldType == Specification.LiveSpeakType.ANCHOR.index){//主播发言
			
		}else if(oldType == Specification.LiveSpeakType.FANS.index){//粉丝回复
			
		}else if(oldType == Specification.LiveSpeakType.FORWARD.index){//转发
			
		}else if(oldType == Specification.LiveSpeakType.ANCHOR_WRITE_TAG.index){//主播贴标
			return 2;
		}else if(oldType == Specification.LiveSpeakType.FANS_WRITE_TAG.index){//粉丝贴标
			return 2;
		}else if(oldType == Specification.LiveSpeakType.LIKES.index){//点赞
			
		}else if(oldType == Specification.LiveSpeakType.SUBSCRIBED.index){//订阅
			
		}else if(oldType == Specification.LiveSpeakType.SHARE.index){//分享
			
		}else if(oldType == Specification.LiveSpeakType.FOLLOW.index){//关注
			
		}else if(oldType == Specification.LiveSpeakType.INVITED.index){//邀请
			
		}else if(oldType == Specification.LiveSpeakType.AT.index){//有人@
			return 10;
		}else if(oldType == Specification.LiveSpeakType.ANCHOR_AT.index){//主播@
			return 11;
		}else if(oldType == Specification.LiveSpeakType.VIDEO.index){//视频
			return 62;
		}else if(oldType == Specification.LiveSpeakType.SOUND.index){//语音
			return 63;
		}else if(oldType == Specification.LiveSpeakType.ANCHOR_RED_BAGS.index){//国王收红包
			
		}else if(oldType == Specification.LiveSpeakType.AT_CORE_CIRCLE.index){//@核心圈
			return 15;
		}else if(oldType == Specification.LiveSpeakType.SYSTEM.index){//系统
			
		}
		if(oldContentType == 1){
			return 51;
		}
		return oldContentType;
	}
}
