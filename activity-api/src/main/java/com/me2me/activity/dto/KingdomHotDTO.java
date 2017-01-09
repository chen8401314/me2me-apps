package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class KingdomHotDTO implements BaseEntity {
	private static final long serialVersionUID = -841311750137824509L;

	private String dayKey;
	private long uid;
	private long topicId;
	private long activityId;
	private long hot;
	private long conditions;
}
