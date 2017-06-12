package com.me2me.user.listener;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.me2me.cache.service.CacheService;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.user.dao.UserInitJdbcDao;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.ContactsMobileEvent;

@Component
@Slf4j
public class SaveContactsMobileListener {

	private final ApplicationEventBus applicationEventBus;
	private final CacheService cacheService;
	private final UserMybatisDao userMybatisDao;
	private final UserInitJdbcDao userInitJdbcDao;
	
	@Autowired
	public SaveContactsMobileListener(ApplicationEventBus applicationEventBus,CacheService cacheService,UserMybatisDao userMybatisDao,UserInitJdbcDao userInitJdbcDao){
		this.applicationEventBus = applicationEventBus;
		this.cacheService = cacheService;
		this.userMybatisDao = userMybatisDao;
		this.userInitJdbcDao = userInitJdbcDao;
	}
	
	@PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
	
	@Subscribe
    public void saveMobile(ContactsMobileEvent event){
		log.info("uid["+event.getUid()+"] update mobiles start...");
		//5分钟内只能更新一次（防止短时间内更新冲突）
		String userContactsKey = "user:contacts:mobiles:" + event.getUid();
		if(!StringUtils.isEmpty(cacheService.get(userContactsKey))){
			log.info("in 5min");
			return;//还在5分钟内，不需要更新
		}
		
		cacheService.set(userContactsKey, "1");
		cacheService.expire(userContactsKey, 300);//5分钟超时时间
		
		//先删除掉原有的
		userMybatisDao.deleteUserMobileListByUid(event.getUid());
		//再新增入
		if(null != event.getMobileList() && event.getMobileList().size() > 0){
			log.info("total mobiles " + event.getMobileList().size());
			userInitJdbcDao.batchInsertIntoUserMobileList(event.getUid(), event.getMobileList());
		}else{
			log.info("total mobiles 00");
		}
		log.info("update end!");
	}
}
