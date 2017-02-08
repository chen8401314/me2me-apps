package com.me2me.user.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class NoticePushEvent implements BaseEntity {
	private static final long serialVersionUID = -2915053636809458850L;

	private long uid;
}
