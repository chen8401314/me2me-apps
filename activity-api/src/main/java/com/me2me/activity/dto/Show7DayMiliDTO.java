package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class Show7DayMiliDTO implements BaseEntity{
	private static final long serialVersionUID = -7884830133991151802L;

	private List<MiliElement> result = Lists.newArrayList();
	
	@Data
	public static class MiliElement implements BaseEntity{
		private static final long serialVersionUID = 5841747979756164003L;
		
		private int type;
		private String content;
		private String linkUrl;
		private int order;//内部程序用
	}
}
