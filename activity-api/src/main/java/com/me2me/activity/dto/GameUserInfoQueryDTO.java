package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class GameUserInfoQueryDTO implements BaseEntity {
	private static final long serialVersionUID = 828932611862468575L;

	private long uid;
	private String nickName;
	private String avatar;
	private int record;
	private int rank;
	private int coins;
	private String price;
	private long gameId;
	
	private List<RankingElement> rankingList = Lists.newArrayList();
	
	@Data
	public static class RankingElement implements BaseEntity {
		private static final long serialVersionUID = 8051965029865622457L;
		
		private int rank;
		private long uid;
		private String nickName;
		private String avatar;
		private int record;
	}
}
