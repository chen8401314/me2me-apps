package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class UserTaskStatusDTO implements BaseEntity {
	private static final long serialVersionUID = -5869267297685971009L;

	private int status;//1已接受,2未接受
}
