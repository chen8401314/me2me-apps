package com.me2me.search.dto;

import java.io.Serializable;
import java.util.List;
/**
 * 搜索结果包装。
 * @author zhangjiwei
 * @date Feb 13, 2017
 *
 * @param <T>
 */
public class SearchResult<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	private long allItemCount;
	private List<T> dataList;
	private Integer curPage;
	private Integer pageSize;
	
	public Integer getCurPage() {
		return curPage;
	}
	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public long getAllItemCount() {
		return allItemCount;
	}
	public void setAllItemCount(long allItemCount) {
		this.allItemCount = allItemCount;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	
	public SearchResult(long allItemCount, List<T> dataList) {
		super();
		this.allItemCount = allItemCount;
		this.dataList = dataList;
	}
	public SearchResult() {
		super();
	}
	
	
}
