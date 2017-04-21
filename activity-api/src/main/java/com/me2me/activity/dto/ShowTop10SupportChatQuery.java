package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowTop10SupportChatQuery implements BaseEntity {
	private static final long serialVersionUID = -967881723235638373L;

	private List<Top10ChatElement> result = Lists.newArrayList();
	
	@Data
	public static class Top10ChatElement implements BaseEntity {
		private static final long serialVersionUID = -5211064333524272807L;
		
		private String nickName;
		private int userRank;
		private String areaName;
		private long score;
	}
}
