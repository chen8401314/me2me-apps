package com.me2me.live.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class AggregationPublishEvent implements BaseEntity {
	private static final long serialVersionUID = 7946680075981883302L;

	private long uid;
	private long topicId;
	private long fid; 
	
	private String liveWebUrl;
}
