package com.me2me.mgmt.utils;

public class CommonsTldUtils {
	
	public static boolean containsInSet(String set,String str){
		if(set==null || str==null) return false;
		String[] strArr = set.split(",");
		for(String x:strArr){
			if(x.equals(str)){
				return true;
			}
		}
		return false;
	}
}
