package com.me2me.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.me2me.common.web.Specification;

import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/16
 * Time :17:46
 */
public class JPushUtils {

    public static Map<String,Object> packageExtra(Object object){
        Map<String,Object> map = Maps.newHashMap();
        map.put("extra",object.toString());
        return map;
    }
}
