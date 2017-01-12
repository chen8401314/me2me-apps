package com.me2me.mgmt.request;

import lombok.Data;

@Data
public class LightBoxItem {

	private long id;
	private String image;
	private String mainText;
	private String secondaryText;
	private String mainTone;
	private String linkUrl;
	private String startTime;
	private String endTime;
	private int status;
}
