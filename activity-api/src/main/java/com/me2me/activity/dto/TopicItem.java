package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class TopicItem implements BaseEntity {
	private static final long serialVersionUID = -286647204677523845L;

	private long topicId;
	private long uid;
}
