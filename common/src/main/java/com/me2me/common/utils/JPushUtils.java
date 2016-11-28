package com.me2me.common.utils;


import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/16
 * Time :17:46
 */
public class JPushUtils {


    public static Map<String,String> packageExtra(Object object) {
        Map<String, String> map = Maps.newHashMap();
        JsonObject jsonObject = (JsonObject) object;
        Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,JsonElement> entry = (Map.Entry<String, JsonElement>) it.next();
            String key = entry.getKey();
            JsonElement element = entry.getValue();
            map.put(key, element.toString().replace("\"",""));
        }
        return map;
    }


}
