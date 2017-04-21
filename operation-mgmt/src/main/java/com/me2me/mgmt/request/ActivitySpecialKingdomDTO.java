package com.me2me.mgmt.request;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

@Data
public class ActivitySpecialKingdomDTO {

	private List<Item> result = Lists.newArrayList();
	
	@Data
	public static class Item{
		private long topicId;
		private String title;
		private String alias;
		private String h5url;
		private long score;
	}
}
