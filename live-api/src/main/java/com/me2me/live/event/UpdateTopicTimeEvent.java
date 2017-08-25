package com.me2me.live.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class UpdateTopicTimeEvent implements BaseEntity {
	private static final long serialVersionUID = 7247981590319168661L;

	public UpdateTopicTimeEvent(long topicId, long longTime, long outTime){
		this.topicId = topicId;
		this.longTime = longTime;
		this.outTime = outTime;
	}
	
	private long topicId;
	private long longTime = 0;
	private long outTime = 0;
}
