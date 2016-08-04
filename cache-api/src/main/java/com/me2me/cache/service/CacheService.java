package com.me2me.cache.service;

import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/12.
 */
public interface CacheService {


    /**
     * 设置普通键值对
     * @param key
     * @param value
     */
    void set(String key,String value);

    /**
     * 设置一个key并设置他的ttl
     * @param key
     * @param value
     * @param timeout
     */
    void setex(String key,String value,int timeout);

    /**
     * 获取普通键值对值
     * @param key
     * @return
     */
    String get(String key);

    /**
     * set集合操作
     * @param key
     * @param values
     */
    void sadd(String key,String ... values);

    /**
     * 获取set集合成员
     * @param key
     * @return
     */
    Set<String> smembers(String key);

    /**
     * 清空缓存数据（当心使用）
     */
    void flushDB();

    /**
     * list操作
     * @param key
     * @param value
     */
    void lPush(String key,String ... value);
    /**
     * list操作
     * @param key
     * @param value
     */
    void rPush(String key,String ... value);
    /**
     * list操作
     * @param key
     */
    String lPop(final String key);

    /**
     * 指定某个键的过期时间
     * @param key
     * @param timeout
     */
    void expire(String key,int timeout);

    /**
     * map操作
     * @param key
     * @param field
     * @param value
     */
    void hSet(String key,String field,String value);

    String hGet(String key,String field);

    void hDel(String key,String... field);

}
