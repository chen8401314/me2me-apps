package com.me2me.common.utils;

import java.util.Arrays;
import java.util.List;

public class CollectionUtils {
	/**
	 * 判断A中是否包含B
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @param a
	 * @param b
	 * @param isStringTest 是否是基于字符串的对比
	 * @return
	 */
	public static boolean contains(List a,List b,boolean textMode){
		if(a==null || b==null) return false;
		for(Object xa:a){
			for(Object xb:b){
				if(textMode){
					if(xa.toString().equals(xb.toString())){
						return true;
					}
				}else{
					if(xa.equals(xb)){
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 判断A中是否包含B
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @param a
	 * @param b
	 * @param textMode 是否以文本形式对比
	 * @return
	 */
	public static boolean contains(List a,Object[] b,boolean textMode){
		return contains(a, Arrays.asList(b), textMode);
	}
	/**
	 * 判断A中是否包含B
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @param a
	 * @param b
	 * @param textMode 是否以文本形式对比
	 * @return
	 */
	public static boolean contains(Object[] a,Object[] b,boolean textMode){
		return contains(Arrays.asList(a), Arrays.asList(b), textMode);
	}
	/**
	 * 判断A中是否包含B
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @param a
	 * @param b
	 * @param textMode 是否以文本形式对比
	 * @return
	 */
	public static boolean contains(Object[] a,List b,boolean textMode){
		return contains(Arrays.asList(a), b, textMode);
	}
	/**
	 * 判断数组中是否存在指定的字符串。
	 * @author zhangjiwei
	 * @date Sep 12, 2017
	 * @param finalTags
	 * @param adminTag
	 * @return
	 */
	public static boolean contains(String[] finalTags, String adminTag) {
		if(finalTags!=null && adminTag!=null){
			for(String fk:finalTags){
				if(fk!=null && fk.equals(adminTag)){
					return true;
				}
			}
		}
		return false;
	}
}
