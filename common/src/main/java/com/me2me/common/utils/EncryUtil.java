package com.me2me.common.utils;

/**
 * Created by Administrator on 2016/10/24.
 */
/**
 * 加密解密工具类
 */
public class EncryUtil {

	/**
	 * 使用默认密钥进行DES加密
	 */
	public static String encrypt(String plainText) {
		try {
			return new DESUtil().encrypt(plainText);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 使用指定密钥进行DES解密
	 */
	public static String encrypt(String plainText, String key) {
		try {
			return new DESUtil(key).encrypt(plainText);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 使用默认密钥进行DES解密
	 */
	public static String decrypt(String plainText) {
		try {
			return new DESUtil().decrypt(plainText);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 使用指定密钥进行DES解密
	 */
	public static String decrypt(String plainText, String key) {
		try {
			return new DESUtil(key).decrypt(plainText);
		} catch (Exception e) {
			return null;
		}
	}

}
