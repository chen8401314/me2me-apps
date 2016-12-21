package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowActivity7DayUsersDTO implements BaseEntity {
	private static final long serialVersionUID = -529634740719957645L;

	private List<UserItemElement> result = Lists.newArrayList();
	
	@Data
	public static class UserItemElement{
		private String mobile;
		private String name;
		private int sex;
		private String channel;
		private String code;
		private long uid;
		private long kingdomCount;
	}
}
