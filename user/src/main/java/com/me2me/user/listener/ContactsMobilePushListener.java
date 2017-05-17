package com.me2me.user.listener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.user.cache.ContactsReddot;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.ContactsMobilePushEvent;
import com.me2me.user.model.UserMobileList;
import com.me2me.user.service.UserService;

@Component
@Slf4j
public class ContactsMobilePushListener {

	private final ApplicationEventBus applicationEventBus;
    private final UserMybatisDao userMybatisDao;
    private final UserService userService;
    private final CacheService cacheService;
    
    @Autowired
    public ContactsMobilePushListener(ApplicationEventBus applicationEventBus,UserMybatisDao userMybatisDao,UserService userService,CacheService cacheService){
    	this.applicationEventBus = applicationEventBus;
    	this.userMybatisDao = userMybatisDao;
    	this.userService = userService;
    	this.cacheService = cacheService;
    }
    
    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }
    
    @Subscribe
    public void mobilePush(ContactsMobilePushEvent event){
    	if(StringUtils.isEmpty(event.getMobile())){
    		return;
    	}
    	log.info("mobile["+event.getMobile()+"] signUp contacts push start...");
    	//获取所有有该手机号的用户(自己除外)
    	List<UserMobileList> list = userMybatisDao.getUserMobileListByMobile(event.getMobile(), event.getUid());
    	if(null != list && list.size() > 0){
    		List<Long> uidList = new ArrayList<Long>();
    		for(UserMobileList um : list){
    			if(null != um && null != um.getUid() && um.getUid() > 0){
    				if(!uidList.contains(um.getUid())){
    					uidList.add(um.getUid());
    				}
    			}
    		}
    		if(uidList.size() > 0){
    			log.info("total ["+uidList.size()+"] users need to push!");
    			String message = "你有通讯录好友新加入了米汤，快来瞅瞅！";
    			JsonObject jsonObject = null;
    			for(Long uid : uidList){
    				//推送
    				jsonObject = new JsonObject();
        	        jsonObject.addProperty("type",Specification.PushObjectType.CONTACTS.index);
        	        userService.pushWithExtra(uid.toString(), message, JPushUtils.packageExtra(jsonObject));
        	        
        	        //红点
        	        ContactsReddot cr = new ContactsReddot(uid, "1");
        	        cacheService.hSet(cr.getKey(), cr.getField(), cr.getValue());
    			}
    		}
    	}
    	
    	log.info("push end!");
    }
}
