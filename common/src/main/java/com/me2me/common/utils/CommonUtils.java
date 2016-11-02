package com.me2me.common.utils;

import com.me2me.common.web.BaseEntity;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public static boolean afterVersion(String currentVersion,String version){
        if(StringUtils.isEmpty(currentVersion))
            return false;
        String[] currentArray = currentVersion.split(".");
        String[] versionArray = version.split(".");
        for(int i=0;i<3;i++){
            if(Integer.parseInt(currentArray[i])>Integer.parseInt(versionArray[i])){
                return true;
            }
        }
        return false;
    }

    public static String wrapString(Object str,String symbol){
        return new StringBuilder(symbol).append(str).append(symbol).toString();
    }




    public static BaseEntity copyDto(Object from,BaseEntity to){
        Class clzd = to.getClass();
        Class clzr = from.getClass();
        Field[] flds = clzd.getDeclaredFields();
        for(Field fld : flds){
            String name =changeFirstCharToUpper(fld.getName());
            try {
                clzd.getMethod("set"+name,fld.getType()).invoke(to,clzr.getMethod("get"+name).invoke(from));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return to;
    }

    public static String changeFirstCharToUpper(String str){
        return new StringBuilder(str.substring(0,1).toUpperCase()).append(str.substring(1)).toString();
    }



}