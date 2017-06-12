package com.me2me.web.utils;

public class VersionUtil {

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
}
