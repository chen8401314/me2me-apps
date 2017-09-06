package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowHarvestKingdomListDTO implements BaseEntity {
	private static final long serialVersionUID = -6647344687845925954L;

	private int totalCount;
	
	private List<HarvestKingdomElement> kingdomList = Lists.newArrayList();
	
	@Data
	public static class HarvestKingdomElement implements BaseEntity {
		private static final long serialVersionUID = 6235535940395884064L;
		
		private long topicId;
		private String coverImage;
		private String title;
		private int price;
		private int canHarvestCoins;
		private int onceLimit;
	}
}
