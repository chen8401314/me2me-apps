package com.me2me.mgmt.request;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class AvatarFrameQueryDTO {

	private String name;
	
	
	private List<AvatarFrameItem> results = Lists.newArrayList();
	
	@Data
	public static class AvatarFrameItem {
		private long id;
		private String name;
		private String avatarFrame;
	}
}
