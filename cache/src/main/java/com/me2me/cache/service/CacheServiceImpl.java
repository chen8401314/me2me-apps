package com.me2me.cache.service;

import com.me2me.core.cache.JedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;
import javax.annotation.PostConstruct;
import java.util.Map;
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

//    @Bean
//    private HttpInvokerProxyFactoryBean initBean(){
//        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
//        httpInvokerProxyFactoryBean.setServiceUrl("");
//        httpInvokerProxyFactoryBean.setServiceInterface(null);
//        return httpInvokerProxyFactoryBean;
//    }


    @PostConstruct
    public void initPool(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxIdle(maxIdle);
        JedisPool jedisPool = new JedisPool(poolConfig,host,port,timeout);
        this.jedisTemplate.setJedisPool(jedisPool);
        log.info("init redis pool from server {} at port {} ",host,port);
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

    @Override
    public void setex(final String key, final String value, final int timeout) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.setex(key,timeout,value);
            }
        });
    }

    @Override
    public String get(final String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.get(key);
            }
        });
    }

    @Override
    public void sadd(final String key, final String ... values) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.sadd(key,values);
            }
        });
    }

    @Override
    public Set<String> smembers(final String key){
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.smembers(key);
            }
        });
    }

    @Override
    public void flushDB() {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.flushDB();
            }
        });
    }

    @Override
    public void lPush(final String key, final String ... value) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.lpush(key,value);
            }
        });
    }

    @Override
    public void rPush(final String key, final String... value) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.rpush(key, value);
            }
        });
    }

    @Override
    public String lPop(final String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.lpop(key);
            }
        });
    }

    @Override
    public void expire(final String key, final int timeout) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.expire(key, timeout);
            }
        });
    }

    public void hSet(final String key,final String field,final String value){
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.hset(key, field,value);
            }
        });
    }

    @Override
    public String hGet(final String key, final String field) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.hget(key,field);
            }
        });
    }

    @Override
    public void hDel(final String key, final String ... field) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.hdel(key, field);
            }
        });
    }

    @Override
    public Map<String,String> hGetAll(final String key) {
        return jedisTemplate.execute(new JedisTemplate.JedisActionResult() {
            @Override
            public <T> T actionResult(Jedis jedis) {
                return (T) jedis.hgetAll(key);
            }
        });
    }

    @Override
    public void hSetAll(final String key, final Map<String, String> stringMap) {
        jedisTemplate.execute(new JedisTemplate.JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.hmset(key,stringMap);
            }
        });
    }
}
