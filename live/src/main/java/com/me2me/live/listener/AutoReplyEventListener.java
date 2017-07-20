package com.me2me.live.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.live.cache.MyLivesStatusModel;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.event.AutoReplyEvent;
import com.me2me.live.event.SpeakNewEvent;
import com.me2me.live.model.LiveFavorite;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.live.service.KingdomRobot;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AutoReplyEventListener {

	private final ApplicationEventBus applicationEventBus;

	private final KingdomRobot kingdomRobot;

	@Autowired
    public AutoReplyEventListener(ApplicationEventBus applicationEventBus,KingdomRobot kingdomRobot){
        this.applicationEventBus = applicationEventBus;
        this.kingdomRobot = kingdomRobot;
    }
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
	public void autoReply(AutoReplyEvent autoReplyEvent){
    	log.info("auto replay execute start .... ");
		KingdomRobot.ExecutePolicy step1 = new KingdomRobot.ExecutePolicy();
		step1.setCreateTime(autoReplyEvent.getCreateTime());
		step1.setTopicId(autoReplyEvent.getTopicId());
		step1.setLastHour(1);
		step1.setMin(5);
		step1.setMax(3);


		KingdomRobot.ExecutePolicy step2 = new KingdomRobot.ExecutePolicy();
		step2.setCreateTime(autoReplyEvent.getCreateTime());
		step2.setTopicId(autoReplyEvent.getTopicId());
		step2.setLastHour(1);
		step2.setMin(8);
		step2.setMax(2);

		kingdomRobot.startWork(autoReplyEvent.getTopicId(),step1,step2);

	}
}
