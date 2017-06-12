package com.me2me.search.service.kafka;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class KafkaTemplate<K, V> implements InitializingBean {
    protected static Logger logger = LoggerFactory.getLogger(KafkaTemplate.class);
    
    private String topic;
    
    private Producer<K, V> producer;
    
    private Consumer<K, V> consumer;

    private ExecutorService service;
    
    public void send(K key, V msg) {
        if (logger.isDebugEnabled())
            logger.debug("kafka发送消息: {} - {}", key, msg);
        producer.send(new ProducerRecord<K, V>(topic, key, msg));
    }
    
    public void receive(final KafkaCallback<K, V> callback) {
        service.execute(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    try {
                        consumer.subscribe(Arrays.asList(topic));
                        ConsumerRecords<K, V> records = consumer.poll(100);
                        callback.execute(records);
                    } catch(Throwable e) {
                        logger.error("kafka消息处理异常", e);
                    }
                }
            }
            
        });
        
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service = Executors.newCachedThreadPool();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Producer<K, V> getProducer() {
        return producer;
    }

    public void setProducer(Producer<K, V> producer) {
        this.producer = producer;
    }

    public Consumer<K, V> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<K, V> consumer) {
        this.consumer = consumer;
    }
    
    
}
