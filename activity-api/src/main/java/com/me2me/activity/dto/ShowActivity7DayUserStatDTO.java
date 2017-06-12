package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ShowActivity7DayUserStatDTO implements BaseEntity {
	private static final long serialVersionUID = 2962257780838082710L;

	private long totalUser = 0;
	private long manCount = 0;
	private long womanCount = 0;
	private long bindCount = 0;
}
