package com.me2me.mgmt.request;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

public class KingDayQueryDTO {

	@Getter
    @Setter
	private String startTime;
	@Getter
    @Setter
	private String endTime;
	
	@Getter
    @Setter
	private List<Item> result = Lists.newArrayList();
	
	public static class Item{
		@Getter
	    @Setter
		private String dayStr;//日期
		@Getter
	    @Setter
		private long totalKingdomCount = 0;//建立王国的总数量
		@Getter
	    @Setter
		private long newUserKingdomCount = 0;//新用户建王国的数量
		@Getter
	    @Setter
		private long updateKingdomCount = 0;//仍在更新的王国数量
		@Getter
	    @Setter
		private long totalKingFragmentCount = 0;//王国总更新数量
		@Getter
	    @Setter
		private long totalUserFragmentCount = 0;//王国总留言数
	}
}
