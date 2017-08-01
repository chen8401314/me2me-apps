package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class DetailPageStatusDTO implements BaseEntity {

	private int page;
	private int records;
	private int isFull;
	private long updateTime;
}
