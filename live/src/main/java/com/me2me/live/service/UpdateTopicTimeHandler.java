package com.me2me.live.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.live.dao.LiveLocalJdbcDao;

@Component
@Slf4j
public class UpdateTopicTimeHandler {

	@Autowired
    private LiveLocalJdbcDao liveLocalJdbcDao;
	
	private static List<TopicUpdateTime> LOCAL_QUEUE_LIST = new ArrayList<TopicUpdateTime>();
	
	private Lock lock = new ReentrantLock();

	@PostConstruct
    public void init(){
		Thread dorun = new Thread(new Runnable() {
            @Override
            public void run() {
            	List<TopicUpdateTime> runList = null;
            	Map<String, TopicUpdateTime> runMap = null;
            	TopicUpdateTime t = null;
            	while(true){
            		if(LOCAL_QUEUE_LIST.size() > 0){
            			runList = new ArrayList<TopicUpdateTime>();
                    	try{
                    		lock.lock();
                    		runList.addAll(LOCAL_QUEUE_LIST);
                    		LOCAL_QUEUE_LIST.clear();
                    	}catch(Exception e){
                    		log.error("处理异常", e);
                    	}finally{
                    		lock.unlock();
                    	}
                    	log.info("本次处理["+runList.size()+"]次UpdateTopicTime请求");
                    	runMap = new HashMap<String, TopicUpdateTime>();
                    	for(TopicUpdateTime tut : runList){
                    		if(null != runMap.get(String.valueOf(tut.getTopicId()))){
                    			t = runMap.get(String.valueOf(tut.getTopicId()));
                    			if(t.getLongTime()<tut.getLongTime()){
                    				t.setLongTime(tut.getLongTime());
                    			}
                    			if(t.getOutTime()<tut.getOutTime()){
                    				t.setOutTime(tut.getOutTime());
                    			}
                    		}else{
                    			t = new TopicUpdateTime();
                    			t.setTopicId(tut.getTopicId());
                    			t.setLongTime(tut.getLongTime());
                    			t.setOutTime(tut.getOutTime());
                    			runMap.put(String.valueOf(tut.getTopicId()), t);
                    		}
                    	}
                    	
                    	if(runMap.size() > 0){
                    		for(Map.Entry<String, TopicUpdateTime> entry : runMap.entrySet()){
                    			t = entry.getValue();
                    			if(t.getLongTime() > 0){
                    				Date updateTime = new Date(t.getLongTime());
                    				liveLocalJdbcDao.updateTopicUpdateTime(t.getTopicId(), updateTime, t.getLongTime());
                    			}
                    			if(t.getOutTime() > 0){
                    				Date outTime = new Date(t.getOutTime());
                    				liveLocalJdbcDao.updateTopicOutTime(t.getTopicId(), outTime);
                    			}
                    		}
                    	}
                    }else{
                    	try {
    						Thread.sleep(1000);//停一秒
    					} catch (InterruptedException e) {
    						log.error("睡觉出错", e);
    					}
                    }
            	}
            }
        });
		dorun.start();
    }
	
	public void updateTime(long topicId, long longTime, long outTime){
		TopicUpdateTime tut = new TopicUpdateTime();
		tut.setTopicId(topicId);
		tut.setLongTime(longTime);
		tut.setOutTime(outTime);
		try{
			lock.lock();
			LOCAL_QUEUE_LIST.add(tut);
		}catch(Exception e){
			log.error("出错", e);
		}finally{
			lock.unlock();
		}
	}
	
	@Data
	public class TopicUpdateTime{
		private long topicId;
		private long longTime;
		private long outTime;
	}
}
