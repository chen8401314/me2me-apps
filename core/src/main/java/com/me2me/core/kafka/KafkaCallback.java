package com.me2me.core.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface KafkaCallback<K, V> {
    void execute(ConsumerRecords<K, V> records);
}
