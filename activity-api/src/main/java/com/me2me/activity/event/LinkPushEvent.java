package com.me2me.activity.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class LinkPushEvent implements BaseEntity {
	private static final long serialVersionUID = 1301494674846335824L;

	private String message;
	private String linkUrl;
	private long uid;
	
	public LinkPushEvent(String message, String linkUrl, long uid){
		this.message = message;
		this.linkUrl = linkUrl;
		this.uid = uid;
	}
}
