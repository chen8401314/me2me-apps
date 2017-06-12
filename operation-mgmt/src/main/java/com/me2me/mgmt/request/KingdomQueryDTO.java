package com.me2me.mgmt.request;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class KingdomQueryDTO {

	private int page;
	private int pageSize;
	
	private String title;
	private String orderbyParam;
	private String orderby;
	
	
	private int totalPage;
	private List<Item> result = Lists.newArrayList();
	
	@Data
	public static class Item{
		private long id;
		private String title;
		private long uid;
		private String nickName;
		private Date createTime;
		private Date updateTime;
		private int type;
		private int price;
	}
}
