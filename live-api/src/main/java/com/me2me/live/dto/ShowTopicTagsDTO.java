package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowTopicTagsDTO implements BaseEntity {
	private static final long serialVersionUID = -3217432078616216163L;

	private String topicTags;
	private String myUsedTags;
	private List<TagElement> recTags = Lists.newArrayList();
	
	
	@Data
	public static class TagElement implements BaseEntity {
		private static final long serialVersionUID = -6093105404115207860L;
		
		private String tag;
		private long topicCount;
	}
}
