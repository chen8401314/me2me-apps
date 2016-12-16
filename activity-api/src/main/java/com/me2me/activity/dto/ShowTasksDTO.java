package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowTasksDTO implements BaseEntity {
	private static final long serialVersionUID = 8310415538546785832L;

	private int totalCount;
	private int totalPage;
	
	private List<TaskElement> result = Lists.newArrayList();
	
	@Data
	public static class TaskElement implements BaseEntity {
		private static final long serialVersionUID = 2926559746547555188L;
		
		private long id;
		private String title;
		private String content;
		private String link_url;
		private int type; //1单人任务  2双人任务
		
		private int status;//1已接受，2未接受
	}
}
