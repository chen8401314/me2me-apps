package com.me2me.common.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页请求。
 * 
 * @author zhangjiwei
 * @date Mar 17, 2017
 */
public class PageBean<T> implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private List<T> dataList;
	private int currentPage=1;
	private int pageSize=15;
	private long totalRecords;
	
	

	public long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

}
