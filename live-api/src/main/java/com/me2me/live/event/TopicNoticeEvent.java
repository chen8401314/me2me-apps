package com.me2me.live.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class TopicNoticeEvent implements BaseEntity{
	private static final long serialVersionUID = -4747716189529138544L;

	private long uid;
    private long topicId;
    
    public TopicNoticeEvent(long uid, long topicId){
    	this.uid = uid;
    	this.topicId = topicId;
    }
}
