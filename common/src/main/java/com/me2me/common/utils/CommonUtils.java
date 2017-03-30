package com.me2me.common.utils;

import com.me2me.common.web.BaseEntity;

import lombok.extern.slf4j.Slf4j;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/28.
 */
@Slf4j
public class CommonUtils {

    private static Random r = new Random();

    public static String getRandom(String prefix,int len){
        StringBuilder sb = new StringBuilder(prefix);
        for(int i = 0;i<len;i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    public static boolean isNewVersion(String currentVersion, String baseVersion){
    	if(null == currentVersion || "".equals(currentVersion)){
    		return false;
    	}
    	String[] v = currentVersion.split("\\.");
    	String[] bv = baseVersion.split("\\.");
    	if(v.length < 3 || bv.length < 3){
    		return false;
    	}
    	
    	try{
    		int v1 = Integer.valueOf(v[0]);
    		int bv1 = Integer.valueOf(bv[0]);
    		if(v1 > bv1){
    			return true;
    		}else if(v1 < bv1){
    			return false;
    		}
    		int v2 = Integer.valueOf(v[1]);
    		int bv2 = Integer.valueOf(bv[1]);
    		if(v2 > bv2){
    			return true;
    		}else if(v2 < bv2){
    			return false;
    		}
    		int v3 = Integer.valueOf(v[2].substring(0,1));//第三个版本号只取第一个数字
    		int bv3 = Integer.valueOf(bv[2]);
    		if(v3 >= bv3){
    			return true;
    		}else{
    			return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
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
                log.error(e.getMessage());
            } catch (InvocationTargetException e) {
                log.error(e.getMessage());
            } catch (NoSuchMethodException e) {
                log.error(e.getMessage());
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
        return to;
    }

    public static String changeFirstCharToUpper(String str){
        return new StringBuilder(str.substring(0,1).toUpperCase()).append(str.substring(1)).toString();
    }



}