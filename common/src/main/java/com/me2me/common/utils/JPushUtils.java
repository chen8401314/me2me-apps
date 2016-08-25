package com.me2me.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.me2me.common.web.Specification;
import org.apache.ibatis.javassist.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/16
 * Time :17:46
 */
public class JPushUtils {



    public static JsonObject packageExtra(JsonObject jsonObject){
        return jsonObject;
    }

    public static void main(String[] args) throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",100);
        jsonObject.addProperty("age",120);
        jsonObject.

    }
}
