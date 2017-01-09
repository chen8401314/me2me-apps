package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class TopicCountDTO implements BaseEntity {
	private static final long serialVersionUID = -8869526745195246461L;

	private int readCount = 0;
	private int likeCount = 0;
	private int updateCount = 0;
	private int reviewCount = 0;
	
	private long topicId;
}
