package com.me2me.cache.service;

import com.google.common.collect.Sets;
import com.me2me.core.cache.JedisTemplate;
import com.me2me.monitor.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/12.
 */
@Service
@Slf4j
public class MonitorServiceImpl implements MonitorService {

    @Override
    public void mark() {

    }
}
