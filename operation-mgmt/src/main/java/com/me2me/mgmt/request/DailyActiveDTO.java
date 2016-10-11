package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

public class DailyActiveDTO {

	//请求参数----
	@Getter
    @Setter
	private int ddlClass = 0;
	@Getter
    @Setter
	private String ddlClassName;
	@Getter
    @Setter
	private String txtStartDate;
	@Getter
    @Setter
	private String txtEndDate;
	
	//---以下为结果参数
	@Getter
    @Setter
	private long boot = 0;
	@Getter
    @Setter
	private long reg = 0;
	@Getter
    @Setter
	private long login = 0;
	@Getter
    @Setter
	private long view = 0;
	@Getter
    @Setter
	private long pubCon = 0;
	@Getter
    @Setter
	private long pubLive = 0;
	@Getter
    @Setter
	private long zan = 0;
	@Getter
    @Setter
	private long czan = 0;
	@Getter
    @Setter
	private long common = 0;
	@Getter
    @Setter
	private long tags = 0;
	@Getter
    @Setter
	private long attention = 0;
	@Getter
    @Setter
	private long cattention = 0;
	@Getter
    @Setter
	private long forwarding = 0;
	@Getter
    @Setter
	private long hot = 0;
	@Getter
    @Setter
	private long anew = 0;
	@Getter
    @Setter
	private long aarticle = 0;
}
