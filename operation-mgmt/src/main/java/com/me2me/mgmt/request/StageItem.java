package com.me2me.mgmt.request;

import lombok.Data;

@Data
public class StageItem {

	private long id;
	private String name;
	private String startTime;
	private String endTime;
	private int status;
}
