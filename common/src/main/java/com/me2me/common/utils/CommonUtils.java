package com.me2me.common.utils;

import com.me2me.common.web.BaseEntity;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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

    /**
     * 当src为null时返回空串，不为null时，是什么返回什么
     * @param src
     * @return
     */
    public static String toUsefulString(String src){
    	if(null == src){
    		return "";
    	}
    	return src;
    }

    /**
     * 获取计算后的日期字符串
     * @return
     */
    public static String getCalculationDayStr(int number,String format){
    	try {
    		SimpleDateFormat sdf  = new SimpleDateFormat(format);
    		Date date = new Date();
    		 Calendar cal1 = Calendar.getInstance();
    		 cal1.setTime(date);
    		  cal1.add(Calendar.DATE, number);
    		  return sdf.format(cal1.getTime());
    	} catch (Exception e) {
    		return "";
    	}
    }
    /** 
     * 随机指定范围内N个不重复的数 
     * 在初始化的无重复待选数组中随机产生一个数放入结果中， 
     * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换 
     * 然后从len-2里随机产生下一个随机数，如此类推 
     * @param max  指定范围最大值 
     * @param min  指定范围最小值 
     * @param n  随机数个数 
     * @return int[] 随机数结果集 
     */ 
    public static int[] randomArray(int min,int max,int n){  
        int len = max-min+1;  
          
        if(max < min || n > len){  
            return null;  
        }  
        //初始化给定范围的待选数组  
        int[] source = new int[len];  
           for (int i = min; i < min+len; i++){  
            source[i-min] = i;  
           }  
             
           int[] result = new int[n];  
           Random rd = new Random();  
           int index = 0;  
           for (int i = 0; i < result.length; i++) {  
            //待选数组0到(len-2)随机一个下标  
               index = Math.abs(rd.nextInt() % len--);  
               //将随机到的数放入结果集  
               result[i] = source[index];  
               //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换  
               source[index] = source[len];  
           }  
           return result;  
    } 
    
    /**
     * 去除字符串中的空格、制表符、回车、换行
     * @param str
     * @return
     */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	/**
	 * 返回指定长度的缩略字符串，如果有缩率，则最后加...<br>
	 * 字母和数字算一个长度，其他算两个长度
	 * @param name		待缩率字符串
	 * @param length	所需长度
	 * @return
	 */
	public static String getShortName(String name, int needRealLength){
		if(StringUtils.isBlank(name)){
			return "";
		}
		if(needRealLength < 4){
			needRealLength = 4;
		}
		List<String> strList = new ArrayList<String>(name.length());
		int[] strLengthArr = new int[name.length()];
		int realLength = 0;
		for(int i=0;i<name.length();i++){
			char ch = name.charAt(i);
			strList.add(String.valueOf(ch));
			if(('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') 
					|| ('0' <= ch && ch <= '9') || ch == ','
					|| ch == '.' || ch == '?' || ch == '!'
					|| ch == '@' || ch == '$' || ch == '%'
					|| ch == '^' || ch == '&' || ch == '*'
					|| ch == '(' || ch == ')' || ch == '-'
					|| ch == '=' || ch == '{' || ch == '}'
					|| ch == '[' || ch == ']' || ch == '|'
					|| ch == ':' || ch == ';' || ch == '~'){
				realLength = realLength + 1;
				strLengthArr[i] = 1;
			}else{
				realLength = realLength + 2;
				strLengthArr[i] = 2;
			}
		}
		
		if(realLength <= needRealLength){//如果字符串比所需的少，直接返回，不需要缩略
			return name;
		}
		
		StringBuilder result = new StringBuilder();
		int l = 0;
		for(int i=0;i<strLengthArr.length;i++){
			l = l + strLengthArr[i];
			if(l > (needRealLength-2)){
				break;
			}
			result.append(strList.get(i));
		}
		result.append("...");
		
		return result.toString();
	}
}