package com.me2me.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/13.
 */
@Component
@Slf4j
public class JedisTemplate {

    private JedisPool jedisPool;

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public interface JedisAction{
        /**
         * 无返回结果
         * @param jedis
         */
        void action(Jedis jedis);

    }

    public interface JedisActionResult{
        /**
         * 有返回结果
         * @param jedis
         * @param <T>
         * @return
         */
        <T> T actionResult(Jedis jedis);
    }

    public void execute(JedisAction action){
        Jedis jedis = null;
        boolean broken = false;
        jedis = getJedis(jedis, broken);
        log.debug("execute redis no action solution.");
        action.action(jedis);
    }

    public <T> T execute(JedisActionResult action){
        Jedis jedis = null;
        boolean broken = false;
        jedis = getJedis(jedis, broken);
        log.debug("execute redis action result solution.");
        return action.actionResult(jedis);
    }

    private Jedis getJedis(Jedis jedis, boolean broken) {
        try {
            jedis = jedisPool.getResource();
        }catch (JedisException exception){
            log.error("jedis can't connection form server");
            broken = true;
        }finally {
            Assert.notNull(jedis,"this resource is not null");
            if(broken) {
                jedisPool.returnBrokenResource(jedis);
            }else{
                log.debug("return resource for redis");
                jedisPool.returnResource(jedis);
            }
        }
        return jedis;
    }

    public void set(final String key, final String value){
        execute(new JedisAction() {
            @Override
            public void action(Jedis jedis) {
                jedis.set(key,value);
            }
        });
    }


}
