package com.me2me.core.cache;

import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public interface Cache {

    void sadd(String key,String value);

    String get(String key);

    Set<String> smembers(String key);

}
