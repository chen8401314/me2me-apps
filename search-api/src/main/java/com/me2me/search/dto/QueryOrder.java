package com.me2me.search.dto;

/**
 * 搜索排序
 * 
 * @author zhangjiwei
 * @date Feb 14, 2017
 *
 */
public class QueryOrder {
	String orderBy;
	boolean isAsc;

	public QueryOrder(String orderBy, boolean isAsc) {
		this.orderBy = orderBy;
		this.isAsc = isAsc;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public boolean isAsc() {
		return isAsc;
	}
}