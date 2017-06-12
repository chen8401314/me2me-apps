package com.me2me.search.service.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.FactoryBean;

public class ProducerFactory<K, V> implements FactoryBean<Producer> {
    
    private String bootstrapServers;
    
    private String keySerializer;
    
    private String valueSerializer;
    
    private Producer<K, V> producer;
    
    @Override
    public Producer getObject() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);

        producer = new KafkaProducer<>(props);
        
        return producer;
    }

    @Override
    public Class<?> getObjectType() {
        return Producer.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    
}
