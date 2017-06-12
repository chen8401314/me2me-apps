package com.me2me.mgmt.request;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Lists;

public class KingdomCreateDetailQueryDTO {

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
	private String nickName;
	@Getter
	@Setter
	private int page = 1;
	@Getter
	@Setter
	private int pageSize = 10;
	
	
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
		private String title;
		@Getter
		@Setter
		private long topicId;
		@Getter
		@Setter
		private String h5Addr;
		@Getter
		@Setter
		private long updateCount;
		@Getter
		@Setter
		private long memberCount;
		@Getter
		@Setter
		private long uid;
		@Getter
		@Setter
		private String nickName;
		@Getter
		@Setter
		private String registerMode;
		@Getter
		@Setter
		private Date registerDate;
		@Getter
		@Setter
		private String channel;
		@Getter
		@Setter
		private int platform;
	}
}
