package com.me2me.activity.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowActivity7DayTasksDTO implements BaseEntity {
	private static final long serialVersionUID = 8938276079163714480L;

	private int totalCount;
	private int totalPage;
	
	private List<TaskElement> result = Lists.newArrayList();
	
	@Data
	public static class TaskElement implements BaseEntity {
		private static final long serialVersionUID = 1208263000215014965L;
		
		private long id;
		private String title;
		private String content;
		private int type;
		private int status;
		private long activityId;
		private String miliContent;
		private Date updateTime;
	}
}
