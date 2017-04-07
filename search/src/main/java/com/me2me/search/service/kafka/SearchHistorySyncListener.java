package com.me2me.search.service.kafka;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.me2me.search.model.SearchHistoryCount;
import com.me2me.search.service.ContentSearchService;

/**
 * 同步搜索历史到mongodb.
 * @author zhangjiwei
 * @date Feb 15, 2017
 *
 */
@Component
public class SearchHistorySyncListener {
	@Autowired
	private ContentSearchService searchService;
	
	private static final String SEARCH_HISTORY_MONGO_COLLECTION = "search_history";		// 搜索历史mongo库名。
	private static final Logger logger = LoggerFactory.getLogger(SearchHistorySyncListener.class);
	
	@Resource(name="searchHistoryKafkaTemplate")
	private KafkaTemplate<String, String> kafka;
	
	/**
	 * 程序启动时监听kafka搜索消息
	 */
	@PostConstruct
	public void init() {
        
        logger.debug("kafka search history Consumer 初始化");
        // 将消息存放到mongodb.
        kafka.receive(new KafkaCallback<String, String>() {

            @Override
            public void execute(ConsumerRecords<String, String> records) {
               for (ConsumerRecord<String, String> record : records) {
                   //System.out.printf("收到消息：offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
                   try {
                	   String msg = record.value();                     
                	   /*
                       Query query = new Query(Criteria.where("_id").is(msg));
                       
                       Update update = new Update();
                       update.set("_id", msg);
                       update.inc("count", 1);
                       update.set("last_query_date",new Date());
                       
                       WriteResult wr = mongoTemplate.upsert(query, update, SEARCH_HISTORY_MONGO_COLLECTION);
                       */
                       SearchHistoryCount count = new SearchHistoryCount();
                       count.setName(msg);
                       searchService.logSearchHistory(msg);
                       logger.debug("保存用户日志: {}", msg);
                   } catch(Exception e) {
                       logger.error("消息处理异常：key = {}, value = {}", record.key(), record.value(), e);
                   }
               }
                
            }
            
        });
    }
}
