package com.plusnet.deduplicate.utils;

import java.util.LinkedHashMap;

/**
 * 自增长索引器
 * @author zhangjiwei
 * @date Apr 28, 2017
 */
public class KeyIndexGenerator {
	private int curPos =0;
	private LinkedHashMap<String, Integer> typeMapping = new LinkedHashMap<String, Integer>();
	
	/**
	 * 取词的索引，从1开始。
	 * @author zhangjiwei
	 * @date Apr 28, 2017
	 * @param key
	 * @return
	 */
	public int getKeyIndex(String key){
		Integer typeIndex = typeMapping.get(key);
		if (typeIndex == null) {
			typeIndex = ++curPos;
			typeMapping.put(key, typeIndex);
		}
		return typeIndex;
	}

	public int getCurPos() {
		return curPos;
	}

	public void setCurPos(int curPos) {
		this.curPos = curPos;
	}

	public LinkedHashMap<String, Integer> getTypeMapping() {
		return typeMapping;
	}
	
}
