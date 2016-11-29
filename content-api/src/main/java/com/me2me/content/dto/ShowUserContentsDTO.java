package com.me2me.content.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowUserContentsDTO implements BaseEntity {

	private static final long serialVersionUID = 3754516731196510322L;

	private int searchType;
	
	private int currentPage;
	private int totalCount;
	private int totalPage;
	
	private List<UserContentElement> result = Lists.newArrayList();
	
	@Data
	public static class UserContentElement implements BaseEntity{
		private static final long serialVersionUID = 1972623931570995358L;
	}
	
	@Data
	public static class UserArtcileReviewElement extends UserContentElement{
		private static final long serialVersionUID = -4747627916540969021L;
		
		private long id;
		private long articleId;
		private long uid;
		private String review;
		private Date createTime;
		private long atUid;
		private int status;
	}
}
