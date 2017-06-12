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
	
	@Data
	public static class UserUgcElement extends UserContentElement{
		private static final long serialVersionUID = 5102500495554979439L;
		
		private long id;
		private long uid;
		private String title;
		private String feeling;
		private int type;
		private String converImage;
		private int contentType;
		private String content;
		private int reviewCount;
		private int likeCount;
		private int readCount;
		private int readCountDummy;
		private int isTop;
		private int rights;
		private int status;
		private Date createTime;
	}
	
	@Data
	public static class UserUgcReviewElement extends UserContentElement{
		private static final long serialVersionUID = -6013140392533371167L;
		
		private long id;
		private long uid;
		private String review;
		private long cid;
		private Date createTime;
		private long atUid;
		private String extra;
		private int status;
	}
	
	@Data
	public static class UserTopicElement extends UserContentElement{
		private static final long serialVersionUID = 3336271123366038098L;
		
		private long id;
		private long uid;
		private String liveImage;
		private String title;
		private int status;
		private Date createTime;
		private Date updateTime;
		private String coreCircle;
	}
	
	@Data
	public static class UserTopicFragmentElement extends UserContentElement{
		private static final long serialVersionUID = -4454539102713824546L;
		
		private long id;
		private long topicId;
		private long uid;
		private String fragmentImage;
		private String fragment;
		private int type;
		private int contentType;
		private Date createTime;
		private String extra;
		private int status;
	}
}
