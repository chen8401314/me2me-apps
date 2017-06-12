package com.me2me.mgmt.request;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

public class ChannelRegisterQueryDTO {

	@Getter
    @Setter
	private String startTime;
	@Getter
    @Setter
	private String endTime;
	@Getter
    @Setter
	private String channelCode;
	
	
	@Getter
    @Setter
	private List<Item> result = Lists.newArrayList();
	
	public static class Item {
		@Getter
	    @Setter
		private String channelCode;
		@Getter
	    @Setter
		private long registerCount;
		@Getter
	    @Setter
		private long kingdomCount;
	}
}
