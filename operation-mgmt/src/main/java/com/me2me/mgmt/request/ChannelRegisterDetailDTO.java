package com.me2me.mgmt.request;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

public class ChannelRegisterDetailDTO {

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
	private int page = 1;
	@Getter
    @Setter
	private int pageSize = 10;
	
	
	@Getter
    @Setter
	private long totalUserCount = 0;
	@Getter
    @Setter
	private long manCount = 0;
	@Getter
    @Setter
	private long womanCount = 0;
	@Getter
    @Setter
	private long totalKingdomCount = 0;
	
	@Getter
    @Setter
	private int totalCount = 0;
	@Getter
    @Setter
	private int totalPage = 0;
	@Getter
    @Setter
	private List<Item> result = Lists.newArrayList();
	
	public static class Item{
		@Getter
	    @Setter
		private long uid;
		@Getter
	    @Setter
		private String nickName;
		@Getter
	    @Setter
		private int sex;
		@Getter
	    @Setter
		private String mobile;
		@Getter
	    @Setter
		private long kingdomCount;
		@Getter
	    @Setter
		private Date registerTime;
	}
}
