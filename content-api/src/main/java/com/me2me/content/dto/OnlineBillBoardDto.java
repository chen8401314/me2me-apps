package com.me2me.content.dto;

import java.io.Serializable;

import com.me2me.content.model.BillBoard;
/**
 * 上线榜单。
 * @author zhangjiwei
 * @date Mar 22, 2017
 */
import com.me2me.content.model.BillBoardDetails;

public class OnlineBillBoardDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private BillBoard billbord;
	private BillBoardDetails detail;
	public BillBoard getBillbord() {
		return billbord;
	}
	public void setBillbord(BillBoard billbord) {
		this.billbord = billbord;
	}
	public BillBoardDetails getDetail() {
		return detail;
	}
	public void setDetail(BillBoardDetails detail) {
		this.detail = detail;
	}
	
}
