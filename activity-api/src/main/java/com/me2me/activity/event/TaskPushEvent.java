package com.me2me.activity.event;

import java.util.List;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class TaskPushEvent implements BaseEntity {
	private static final long serialVersionUID = 876530625726567958L;

	private String message;
	private String linkUrl;
	private List<Long> uids;
	
	public TaskPushEvent(String message, String linkUrl, List<Long> uids){
		this.message = message;
		this.linkUrl = linkUrl;
		this.uids = uids;
	}
}
