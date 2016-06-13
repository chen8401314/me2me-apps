package com.me2me.cache.service;

import com.google.common.collect.Sets;
import com.me2me.core.cache.JedisTemplate;
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
public class CacheServiceImpl implements CacheService {

    @Value("#{app.redisHost}")
    private String host;
    @Value("#{app.redisPort}")
    private int port;
    @Value("#{app.redisTimeout}")
    private int timeout;
    @Value("#{app.redisMaxTotal}")
    private int maxTotal;
    @Value("#{app.redisMaxWaitMillis}")
    private int maxWaitMillis;
    @Value("#{app.redisMaxIdle}")
    private int maxIdle;

    @Autowired
    private JedisTemplate jedisTemplate;


    @PostConstruct
    public void initPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxIdle(maxIdle);
        JedisPool jedisPool = new JedisPool(poolConfig,host,port,timeout);
        this.jedisTemplate.setJedisPool(jedisPool);
        log.info("init redis pool ... ");
    }

    @Override
    public void set(final String key, final String value) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.set(key,value);
            }
        });
    }
}
