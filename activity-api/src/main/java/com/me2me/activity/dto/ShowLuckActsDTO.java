package com.me2me.activity.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowLuckActsDTO implements BaseEntity {

	private static final long serialVersionUID = 4143943783362993040L;

	private List<LuckActElement> result = Lists.newArrayList();
	
	public static LuckActElement createLuckActElement(){
		return new LuckActElement();
	}
	
	//抽奖记录
	@Data
	public static class LuckActElement implements BaseEntity{

		private static final long serialVersionUID = -2487474349808005774L;
		
		private Integer activityName;
		private Long uid;
		private String mobile;
		private String nickName;
		private String avatar;
		private String ipAddress;
		private Integer awardId;
		private String proof;
		private Date creatTime;
		
		private String activityNameStr;
		private String awardName;
		private String awardPrize;
	}
}
