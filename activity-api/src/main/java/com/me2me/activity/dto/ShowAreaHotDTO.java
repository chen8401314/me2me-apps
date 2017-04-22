package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ShowAreaHotDTO implements BaseEntity {
	private static final long serialVersionUID = 8585867489676621479L;

	private long topicId;
	private String name;
	private long score;
	private int rank;
}
