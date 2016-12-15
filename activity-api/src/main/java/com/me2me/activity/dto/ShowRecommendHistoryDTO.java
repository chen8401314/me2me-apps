package com.me2me.activity.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowRecommendHistoryDTO implements BaseEntity {

	private static final long serialVersionUID = -2098842863833131065L;

	private int totalCount;
	private int totalPage;
	
	private List<RecommendElement> result = Lists.newArrayList();
	
	@Data
	public static class RecommendElement implements BaseEntity{
		private static final long serialVersionUID = -6959233784358475114L;
		
		private Date recommendTime;
		
		private List<RecommendUserItem> userList = Lists.newArrayList();
	}
	
	@Data
	public static class RecommendUserItem implements BaseEntity{
		private static final long serialVersionUID = -3275530265955874194L;
		
		//用户信息
		private long uid;
		private String nickName;
		private String avatar;
		private int sex;
		private int vlv;
		
		//单人王国信息
		private long topicId;
		private String title;
		private String converImage;
		private long hot;
		
		//用户活动状态
		private int status;//0单人王国不存在，1单身，2已申请，3已结婚
	}
}
