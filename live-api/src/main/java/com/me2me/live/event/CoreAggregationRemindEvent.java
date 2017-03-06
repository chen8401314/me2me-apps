package com.me2me.live.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;
import com.me2me.live.model.Topic;

@Data
public class CoreAggregationRemindEvent implements BaseEntity{
	private static final long serialVersionUID = -5383576560810987132L;

	private long sourceUid;
	private String review;
	private long applyId;
	private Topic sourceTopic;
	private Topic targetTopic;
	private String message;
}
