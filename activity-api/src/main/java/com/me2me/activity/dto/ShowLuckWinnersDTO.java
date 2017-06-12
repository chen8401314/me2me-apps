package com.me2me.activity.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowLuckWinnersDTO implements BaseEntity {

	private static final long serialVersionUID = -8024704468067483677L;

	private List<LuckWinnersElement> result = Lists.newArrayList();
	
	public static LuckWinnersElement createLuckWinnersElement(){
		return new LuckWinnersElement();
	}
	
	//中奖提交信息
	@Data
	public static class LuckWinnersElement implements BaseEntity{

		private static final long serialVersionUID = -4096485109707932999L;
		
		private long uid;
		private int activityName;
		private String mobile;
		private int awardId;
		private String awardName;
		private Date createTime;
		
		private String activityNameStr;
	}
}
