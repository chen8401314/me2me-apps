package com.me2me.mgmt.request;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class UserInvitationQueryDTO {

	private String nickName;
	private Long uid;
	private Long meNo;
	private String mobile;
	private String startTime;
	private String endTime;
	
	private List<Item> results = Lists.newArrayList();
	
	@Data
	public static class Item {
		private long uid;
		private String nichName;
		private Long meNo;
		private String mobile;
		private long totalCount = 0;
		private long iosCount = 0;
		private long androidCount = 0;
	}
}
