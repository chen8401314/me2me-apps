package com.me2me.ml.core.type;

import java.util.HashMap;
import java.util.Map;

public class TypeMapper {
	private int index;
	private String name;
	private String keywords;
	private Map<String, Float> weightKeyMap = new HashMap<String, Float>();

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
		for (String key : keywords.split(",")) {
			this.weightKeyMap.put(key, 2f);
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Float> getWeightKeyMap() {
		return weightKeyMap;
	}

	public void setWeightKeyMap(Map<String, Float> weightKeyMap) {
		this.weightKeyMap = weightKeyMap;
	}
	/**
	 * 取关键字的打分，默认返回1; 如果关键字是权重关键字，返回2，
	 * @author zhangjiwei
	 * @date May 22, 2017
	 * @param keyword
	 * @return
	 */
	public float getKeywordScore(String keyword){
		Float score = this.weightKeyMap.get(keyword);
		if(score==null){
			score= 1f;
		}
		return score;
	}
}