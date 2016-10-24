package com.me2me.kafka.service;

import com.me2me.common.web.BaseEntity;
import com.me2me.common.web.Request;
import com.me2me.common.web.Specification;
import com.me2me.kafka.model.ClientLog;

/**
 * Created by pc188 on 2016/10/19.
 */
public interface KafkaService {
    public void saveClientLog(BaseEntity request, String userAgent , Specification.ClientLogAction action);
    public void clientLog(ClientLog log);
}
