package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class KingdomImageMonthDTO implements BaseEntity {
	private static final long serialVersionUID = -5801279752121787131L;

	private int monthCount;
	private String showMonth;
	private List<MonthElement> monthData = Lists.newArrayList();
	
	@Data
	public static class MonthElement implements BaseEntity {
		private static final long serialVersionUID = -3089608417774341190L;
		
		private String month;
		private int imageCount;
	}
}
