package com.me2me.mgmt.request;

import com.me2me.activity.dto.ShowActivity7DayTasksDTO;

import lombok.Data;

@Data
public class TaskQueryDTO {

	private int page;
	private int pageSize;
	private String title;
	
	private ShowActivity7DayTasksDTO data;
}
