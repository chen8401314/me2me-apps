package com.me2me.kafka.service;

import com.me2me.kafka.model.OperateLog;

/**
 * Created by pc188 on 2016/10/19.
 */
public interface KafkaService {
    public void clientLog(OperateLog operateLog);
}
