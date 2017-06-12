package com.me2me.kafka.service;

import com.alibaba.fastjson.JSON;
import com.me2me.common.web.BaseEntity;
import com.me2me.common.web.Request;
import com.me2me.common.web.Specification;
import com.me2me.kafka.model.OperateLog;
import com.me2me.kafka.util.KafkaTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by pc188 on 2016/10/19.
 * v2.1.3版本暂时搁置，后续整体规划
 */
@Service
@Slf4j
public class KafkaServiceImpl implements  KafkaService{

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void clientLog(OperateLog operateLog) {
      /*  try {
            log.info("client log:"+ JSON.toJSONString(clientLog));
            kafkaTemplate.send(clientLog.getUserId()+""+clientLog.getLogTime(),  JSON.toJSONString(clientLog));
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }
}
