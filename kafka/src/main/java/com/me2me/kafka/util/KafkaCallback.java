package com.me2me.kafka.util;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface KafkaCallback<K, V> {
    void execute(ConsumerRecords<K, V> records);
}
