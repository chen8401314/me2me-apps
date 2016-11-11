package com.me2me.activity.dto;

import java.util.Date;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class LuckStatusDTO implements BaseEntity {

	private static final long serialVersionUID = 1106678417709547845L;

	private int id;
	private String version;
	private int activityName;
	private String channel;
	private String awardTerm;
	private int awardStatus;
	private String operateMobile;
	private int awardSumChance;
	private Date startTime;
	private Date endTime;
	private Date createTime;
	
	private String activityNameStr;
}
