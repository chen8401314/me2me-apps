package com.me2me.mgmt.request;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class UserRegisterQueryDTO {

	private String startTime;
	private String endTime;
	
	private List<Item> result = Lists.newArrayList();
	
	@Data
	public static class Item{
		private String dateStr;
		private long totalCount;
		private long phoneCount;
		private long qqCount;
		private long weixinCount;
	}
}
