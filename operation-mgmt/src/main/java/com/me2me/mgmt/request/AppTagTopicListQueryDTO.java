package com.me2me.mgmt.request;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class AppTagTopicListQueryDTO {

	@Getter
	@Setter
	private long tagId;
	@Getter
	@Setter
	private int page = 1;
	@Getter
	@Setter
	private int pageSize = 20;
	
	
	@Getter
    @Setter
	private int totalCount = 0;
	@Getter
    @Setter
	private int totalPage = 0;
	@Getter
    @Setter
	private List<Item> result = Lists.newArrayList();
	
	@Data
	public static class Item{
		private long tagTopicId;
		private long topicId;
		private String title;
		private Date createTime;
		private Date lastUpdateTime;
		private String tags;
		private String h5url;
	}
}
