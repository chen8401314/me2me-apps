package com.me2me.kafka.util;

import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("consumerFactory")
public class ConsumerFactory<K, V> implements FactoryBean<Consumer> {

    @Value("#{app.bootstrap_servers}")
    private String bootstrapServers;

    @Value("#{app.zookeeper_connect}")
    private String zookeeperConnect;

    @Value("#{app.group_id}")
    private String groupId;

    @Value("#{app.zookeeper_session_timeout_ms}")
    private int timeout;

    @Value("#{app.zookeeper_sync_time_ms}")
    private int synctime;

    @Value("#{app.auto_commit_interval_ms}")
    private int commitInterval;

    @Value("#{app.key_deserializer}")
    private String keyDeserializer;

    @Value("#{app.value_deserializer}")
    private String valueDeserializer;
    
    private Consumer<K, V> consumer;
    
    @Override
    public Consumer getObject() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("zookeeper.connect", zookeeperConnect);  
        props.put("group.id", groupId);  
        props.put("zookeeper.session.timeout.ms", timeout);  
        props.put("zookeeper.sync.time.ms", synctime);  
        props.put("auto.commit.interval.ms", commitInterval); 
        props.put("key.deserializer", keyDeserializer);
        props.put("value.deserializer", valueDeserializer);
       
        consumer = new KafkaConsumer<>(props);
        
        return consumer;
    }

    @Override
    public Class<?> getObjectType() {
        return Consumer.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getZookeeperConnect() {
        return zookeeperConnect;
    }

    public void setZookeeperConnect(String zookeeperConnect) {
        this.zookeeperConnect = zookeeperConnect;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getSynctime() {
        return synctime;
    }

    public void setSynctime(int synctime) {
        this.synctime = synctime;
    }

    public int getCommitInterval() {
        return commitInterval;
    }

    public void setCommitInterval(int commitInterval) {
        this.commitInterval = commitInterval;
    }

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }
    
}
