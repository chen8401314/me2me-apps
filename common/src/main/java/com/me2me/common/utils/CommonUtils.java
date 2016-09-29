package com.me2me.common.utils;

import com.me2me.common.web.BaseEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/28.
 */
public class CommonUtils {

    private static Random r = new Random();

    public static String getRandom(String prefix,int len){
        StringBuilder sb = new StringBuilder(prefix);
        for(int i = 0;i<len;i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    public static BaseEntity fromPojoToDto(Object fromData, BaseEntity toData) {
        Class from = fromData.getClass();
        Class to = toData.getClass();
        Field[] flds = from.getDeclaredFields();
        for(Field fld : flds){
            try {
                String name = fld.getName().substring(0,1).toUpperCase()+fld.getName().substring(1);
                Method fromMethod = from.getDeclaredMethod("get"+name);
                Method toMethod = to.getDeclaredMethod("set"+name,fld.getType());
                toMethod.invoke(toData,fromMethod.invoke(fromData));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return toData;
    }
}
