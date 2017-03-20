package com.me2me.mgmt.request;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

@Data
public class VersionChannelAddrQueryDTO {

	private String channel;
	
	private List<Item> result = Lists.newArrayList();
	
	@Data
	public static class Item{
		private long id;
		private String channel;
		private int type;
		private String versionAddr;
	}
}
