package com.me2me.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.me2me.common.web.Specification;
import org.apache.ibatis.javassist.CannotCompileException;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.NotFoundException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

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
        ClassPool classPool = ClassPool.getDefault();
        CtClass pageClass = classPool.get(Page.class.getName());
        pageClass.addInterface(classPool.makeInterface(Serializable.class.getName()));
        pageClass.writeFile();
    }
}
