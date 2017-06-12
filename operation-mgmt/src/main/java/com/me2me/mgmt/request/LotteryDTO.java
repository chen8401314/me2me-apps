package com.me2me.mgmt.request;

import lombok.Data;

@Data
public class LotteryDTO {

	private int id;
	private String version;
	private int activityName;
	private String activityNameStr;
	private String channel;
	private int awardStatus;
	private String operateMobile;
	private int awardSumChance;
	private String startTime;
	private String endTime;
	
}
