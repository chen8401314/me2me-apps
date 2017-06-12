package com.me2me.mgmt.request;

import lombok.Data;

@Data
public class AppUIItem {

	private long id;
	private int sourceCode;
	private String description;
	private String startTime;
	private String endTime;
	private int status;
}
