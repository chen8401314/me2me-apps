package com.me2me.mgmt.request;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class ZmjxConfingDTO {

	private int activitySwitch = 0;//活动开关，0关，1开
	private long totalLimit = 0;
	private long dayLimit = 0;
	
	private List<LimitItem> limitList = Lists.newArrayList();
	
	@Data
	public static class LimitItem{
		private String key;
		private String title;
		private int personScoreAdd = 0;
		private int personScoreDel = 0;
		private int areaScoreAdd = 0;
		private int areaScoreDel = 0;
		private int limit = 0;
	}
}
