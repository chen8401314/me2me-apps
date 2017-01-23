package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class CreateKingdomDto implements BaseEntity {

	private static final long serialVersionUID = 3117030793760629360L;

	private long uid;
	private String title;
	private String liveImage;
	private int contentType;
	private String fragment;
	private int source;
	private String extra;
	private int kType;
	private String cExtra;
	private String kConfig;
}
