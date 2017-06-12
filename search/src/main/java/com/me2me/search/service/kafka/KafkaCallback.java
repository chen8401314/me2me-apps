package com.me2me.search.service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface KafkaCallback<K, V> {
    void execute(ConsumerRecords<K, V> records);
}
