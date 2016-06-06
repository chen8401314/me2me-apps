package com.me2me.core.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
public class RedisCache extends CacheProvider {

    private static JedisPool jedisPool = null;

    @Value("#{app.redis_server}")
    private String redisServer;

    @Value("#{app.redis_port}")
    private int redisPort;

    public RedisCache(){
        jedisPool = new JedisPool(redisServer,redisPort);
    }

    public Jedis getResource(){
        return jedisPool.getResource();
    }

    @Override
    public void sadd(String key, String value) {
        getResource().sadd(key,value);
    }

    @Override
    public String get(String key) {
        return getResource().get(key);
    }

    @Override
    public Set<String> smembers(String key) {
        return getResource().smembers(key);
    }
}
