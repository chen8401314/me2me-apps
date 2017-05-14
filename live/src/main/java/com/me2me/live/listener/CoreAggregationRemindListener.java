package com.me2me.live.listener;

import java.util.Date;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.me2me.cache.service.CacheService;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.CoreAggregationRemindEvent;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserNoticeUnread;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class CoreAggregationRemindListener {

	private final ApplicationEventBus applicationEventBus;

    private final UserService userService;

    private final CacheService cacheService;
    
    private final LiveMybatisDao liveMybatisDao;
    
    @Autowired
    public CoreAggregationRemindListener(ApplicationEventBus applicationEventBus, UserService userService,
    		CacheService cacheService, LiveMybatisDao liveMybatisDao){
    	this.userService = userService;
    	this.applicationEventBus = applicationEventBus;
    	this.cacheService = cacheService;
    	this.liveMybatisDao = liveMybatisDao;
    }
    
    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
    
    @Subscribe
    public void coreAggregationRemind(CoreAggregationRemindEvent event) {
    	JSONArray array = JSON.parseArray(event.getTargetTopic().getCoreCircle());
        for (int i = 0; i < array.size(); i++) {
        	long targetUid = array.getLong(i);
            if(targetUid > 0){
            	//发送消息
                this.aggregationRemind(event.getSourceUid(), targetUid, event.getReview(), event.getApplyId(), 
                		event.getTargetTopic(), event.getSourceTopic(), Specification.UserNoticeType.AGGREGATION_APPLY.index);
                
                //发推送
                //本消息是由王国发起的，所以需要判断王国的配置
                if(this.checkTopicPush(event.getTargetTopic().getId(), targetUid)){
                	userService.noticeMessagePush(targetUid, event.getMessage(), 2);
                }
            }
        }
    }
    
    private void aggregationRemind(long sourceUid, long targetUid, String review, long cid, Topic textTopic, Topic coverTopic, int type) {
        if (targetUid == sourceUid) {
            return;
        }
        UserProfile userProfile = userService.getUserProfileByUid(sourceUid);
        UserProfile customerProfile = userService.getUserProfileByUid(targetUid);
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setToUid(customerProfile.getUid());
        
        userNotice.setCid(cid);
        userNotice.setReview(review);
        userNotice.setNoticeType(type);

        userNotice.setSummary("");
        userNotice.setLikeCount(0);
        userNotice.setTag("");
        userNotice.setReadStatus(0);
        
        userNotice.setCoverImage(coverTopic.getLiveImage());

        JSONObject obj = new JSONObject();
    	obj.put("textImage", textTopic.getLiveImage());
    	obj.put("textTitle", textTopic.getTitle());
    	obj.put("textType", textTopic.getType());
    	obj.put("textTopicId", textTopic.getId());
    	obj.put("coverImage", coverTopic.getLiveImage());
    	obj.put("coverTitle", coverTopic.getTitle());
    	obj.put("coverType", coverTopic.getType());
    	obj.put("coverTopicId", coverTopic.getId());
    	userNotice.setExtra(obj.toJSONString());
        
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
        unu.setLevel(Specification.UserNoticeLevel.LEVEL_2.index);
        userService.createUserNoticeUnread(unu);
        
        //添加系统消息红点
        cacheService.set("my:notice:level2:"+targetUid, "1");
        
        UserTips userTips = new UserTips();
        userTips.setUid(targetUid);
        userTips.setType(type);
        UserTips tips = userService.getUserTips(userTips);
        if (tips == null) {
            userTips.setCount(1);
            userService.createUserTips(userTips);
        } else {
            tips.setCount(tips.getCount() + 1);
            userService.modifyUserTips(tips);
        }
        userService.noticeCountPush(targetUid);
    }
    
    private boolean checkTopicPush(long topicId, long uid){
    	TopicUserConfig tuc = liveMybatisDao.getTopicUserConfig(uid, topicId);
    	if(null != tuc && tuc.getPushType().intValue() == 1){
    		return false;
    	}
    	return true;
    }
}
