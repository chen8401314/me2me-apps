package com.me2me.mgmt.request;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class SearchUserDTO {

	private long uid;
	private String nickName;
	
	private List<Item> result = Lists.newArrayList();
	
	@Data
	public static class Item{
		private long uid;
		private String nickName;
	}
}
