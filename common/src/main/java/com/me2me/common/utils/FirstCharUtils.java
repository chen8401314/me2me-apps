package com.me2me.common.utils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
/**
 * 字符工具。
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
public class FirstCharUtils {
	/**
	 * 取字母首字母。返回大写首字母，英文中文返回拼音首字母，其余返回#
	 * @author zhangjiwei
	 * @date Apr 20, 2017
	 * @param input
	 * @return
	 */
	public static String getFirstChar(String input) {
		if (input != null) {
			String result = "#";
			input=input.trim();
			if(input.length()>0){
				String mInput = input.trim().substring(0, 1);
				if (mInput.matches("[a-zA-Z]")) {
					result = mInput;
				} else if (mInput.matches("[\u4e00-\u9fa5]")) {
					try {
						result = PinyinHelper.getShortPinyin(mInput).substring(0, 1);
					} catch (PinyinException e) {
						e.printStackTrace();
					}
				}
			}
			return result.toUpperCase();
		} else {
			return null;
		}

	}

	public static void main(String[] args) {
		System.out.println(getFirstChar(null));
		System.out.println(getFirstChar("你好"));
		System.out.println(getFirstChar("张三"));
		System.out.println(getFirstChar("李四"));
		System.out.println(getFirstChar("123"));
		System.out.println(getFirstChar("43234234234"));
		System.out.println(getFirstChar("adfasd"));
		System.out.println(getFirstChar("ASDF"));
		System.out.println(getFirstChar("네이버"));
		System.out.println(getFirstChar("點擊轉換按鈕"));
		System.out.println(getFirstChar("繁體字"));
		System.out.println(getFirstChar(String.valueOf((char) 0x2)));// 乱码字符。
		System.out.println(getFirstChar(String.valueOf((char) 0xee43))); // 乱码
	}
}
