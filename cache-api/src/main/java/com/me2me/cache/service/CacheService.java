package com.me2me.cache.service;

import java.util.List;
import java.util.Map;
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
     * 删除普通键
     * @param key
     */
    void del(String key);

    /**
     * 设置一个key并设置他的ttl
     * @param key
     * @param value
     * @param timeout 单位秒
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
     * 移除set集合中的一个或多个成员
     * @param key
     * @param values
     */
    void srem(String key, String... values);

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

    Map<String,String> hGetAll(String key);

    void hSetAll(String key,Map<String,String> stringMap);

    /**
     * lrange操作
     * @param key
     * @param start
     * @param end
     * @return
     */
    List<String> lrange(final String key, final int start, int end);

    /**
     * list移除操作
     * @param key
     * @param count
     * @param value
     */
    void lrem(String key, long count, String value);


    /**
     * 自增
     * @param key
     * @return
     */
    long incr(String key);

    /**
     * 自减
     * @param key
     * @return
     */
    long decr(String key);
    
    /**
     * 自增一个数
     * @param key
     * @param num
     * @return
     */
    long incrby(String key, long num);
    
    /**
     * 自减一个数
     * @param key
     * @param num
     * @return
     */
    long decrby(String key, long num);

    /**
     * 通配符获取Key的集合
     * @param pattern
     * @return
     */
    Set<String> keys(String pattern);

    public Object getJavaObject(String key);
    	
    public void cacheJavaObject(String key,Object obj);
    		
    public void cacheJavaObject(String key,Object obj,int expireSeconds);

}
