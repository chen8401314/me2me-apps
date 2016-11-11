package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowLuckPrizeDTO implements BaseEntity {

	private static final long serialVersionUID = -8946242475842573468L;

	private List<LuckPrizeElement> result = Lists.newArrayList();
	
	@Data
	public static class LuckPrizeElement implements BaseEntity{

		private static final long serialVersionUID = 515025764439868996L;
		
		private int id;
		private int activityName;
		private String awardName;
		private int awardId;
		private float awardChance;
		private int number;
		
		private String activityNameStr;
	}
}
