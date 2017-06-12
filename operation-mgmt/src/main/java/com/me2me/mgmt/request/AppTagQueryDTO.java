package com.me2me.mgmt.request;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class AppTagQueryDTO {

	@Getter
    @Setter
	private String tagName;
	@Getter
    @Setter
	private String startTime;
	@Getter
    @Setter
	private String endTime;
	@Getter
    @Setter
	private int isSys = -1;
	@Getter
    @Setter
	private int isRec = -1;
	@Getter
    @Setter
	private String topicCountStart;
	@Getter
    @Setter
	private String topicCountEnd;
	@Getter
	@Setter
	private int page = 1;
	@Getter
	@Setter
	private int pageSize = 10;
	
	
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
		private long id;
		private String tagName;
		private Date createTime;
		private int isSys;
		private int isRec;
		private int status;
		private long topicCount;
	}
}
