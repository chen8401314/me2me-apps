package com.me2me.user.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class NoticeMessagePushEvent implements BaseEntity {
	private static final long serialVersionUID = -8726623676708483602L;

	private long targetUid;
	private String message;
	private int level;
}
