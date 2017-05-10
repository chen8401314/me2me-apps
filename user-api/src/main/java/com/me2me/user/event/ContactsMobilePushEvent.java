package com.me2me.user.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ContactsMobilePushEvent implements BaseEntity {
	private static final long serialVersionUID = 7457851773717634738L;

	private long uid;
	private String mobile;
}
