package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class SubscribedTopicDTO implements BaseEntity {
	private static final long serialVersionUID = -2985121106668357763L;

	private int currentLevel;
    private int upgrade;
    private int isFirst;
    private int internalStatus;
}
