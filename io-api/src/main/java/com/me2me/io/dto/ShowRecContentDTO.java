package com.me2me.io.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowRecContentDTO implements BaseEntity {
	private static final long serialVersionUID = 4403566997920095186L;

	private String resultCode;
	private String resultDesc;
	private List<RecContentElement> contents = Lists.newArrayList();
	
	@Data
	public static class RecContentElement implements BaseEntity {
		private static final long serialVersionUID = -4394707772759679563L;
		
		private long contentId;
		private String title;
		private String linkUrl;
		private String coverImage;
		private int contentType;
		private String tag;
		private String classify;
		private long updateTime;
		private String reason;
		private int readCount;
		private int likeCount;
		private int shareCount;
		private int favoriteCount;
		private int reviewCount;
	}
}
