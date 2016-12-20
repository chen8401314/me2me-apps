package com.me2me.mgmt.request;

import com.me2me.activity.dto.ShowActivity7DayUserStatDTO;
import com.me2me.activity.dto.ShowActivity7DayUsersDTO;

import lombok.Data;

@Data
public class StatUserDTO {

	private int page;
	private int pageSize;
	
	private String channel;
	private String code;
	private String startTime;
	private String endTime;
	
	private int totalPage;
	
	private ShowActivity7DayUserStatDTO userStatDTO;
	
	private ShowActivity7DayUsersDTO userDTO;

}
