package com.me2me.mgmt.dal.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Hex;

import com.me2me.mgmt.controller.RankingController;

import lombok.extern.slf4j.Slf4j;
@Slf4j
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
			String hex = new String(Hex.encodeHex(iso8859String.getBytes("iso-8859-1")));
			log.debug("receive:"+iso8859String+",hex:"+hex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iso8859String;
		/*
		try {
			return new String(iso8859String.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		 return null;
		}
		*/
	}
}
