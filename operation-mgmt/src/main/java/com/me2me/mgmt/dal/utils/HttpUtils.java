package com.me2me.mgmt.dal.utils;

import java.io.UnsupportedEncodingException;

public class HttpUtils {
	/**
	 * 将字符转换为utf-8编码。
	 * @author zhangjiwei
	 * @date Mar 24, 2017
	 * @param iso8859String
	 * @return
	 */
	public static String toUTF8(String iso8859String){
		try {
			return new String(iso8859String.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
