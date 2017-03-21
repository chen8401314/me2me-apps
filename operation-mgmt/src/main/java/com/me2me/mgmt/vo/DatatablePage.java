package com.me2me.mgmt.vo;


import java.util.List;

import com.me2me.common.page.Page;
import com.me2me.common.page.PageBean;

/**
 * pagebean form jquery.datatable
 * @author jiwei.zhang
 * @date 2016年5月11日
 */
public class DatatablePage{
	private Integer draw;
	private Integer start;
	private Integer length;
	private List data;
	private Integer recordsTotal;
	private Integer recordsFiltered;
	private String error;
	
	public Integer getDraw() {
		return draw;
	}
	public void setDraw(Integer draw) {
		this.draw = draw;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}



	public Integer getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(Integer recordsTotal) {
		this.recordsTotal = recordsTotal;
		this.recordsFiltered=recordsTotal;
	}
	public Integer getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(Integer recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public PageBean toPageBean(){
		int currentPage = (this.start/this.length)+1;
		PageBean pageBean = new PageBean();
		pageBean.setCurrentPage(currentPage);
		pageBean.setPageSize(this.length);
		return pageBean;
	}
}
