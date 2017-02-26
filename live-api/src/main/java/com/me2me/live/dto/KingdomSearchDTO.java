package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class KingdomSearchDTO implements BaseEntity {
	private static final long serialVersionUID = 4730237328120540928L;

	private long updateTime;
	private long searchUid;
	private int allowCore;
	private long exceptTopicId;
	private long topicId;
	private int topicType;
	private int searchType;
	private int searchRights;
	private String keyword;
	private int searchScene;
}
