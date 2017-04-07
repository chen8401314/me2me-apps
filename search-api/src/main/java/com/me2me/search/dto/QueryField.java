package com.me2me.search.dto;

/**
 * 搜索字段
 * 
 * @author zhangjiwei
 * @date Feb 14, 2017
 */
public class QueryField {
	String fieldName;
	boolean isHighlight;

	/**
	 * 添加一个待搜索的字段
	 * 
	 * @param fieldName
	 *            字段名
	 * @param isHighlight
	 *            是否高亮显示
	 */
	public QueryField(String fieldName, boolean isHighlight) {
		super();
		this.fieldName = fieldName;
		this.isHighlight = isHighlight;
	}
}