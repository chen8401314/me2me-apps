package com.me2me.search.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowSearchDTO implements BaseEntity {
	private static final long serialVersionUID = 4157093562302727932L;

	private long totalPage;
	private long totalRecord;
	private List<UserElement> userData = Lists.newArrayList();
	private List<KingdomElement> kingdomData = Lists.newArrayList();
	private List<UgcElement> ugcData = Lists.newArrayList();
	
	@Data
	public static class UserElement implements BaseEntity {
		private static final long serialVersionUID = -1042380710098678702L;
		
		private long uid;
		private String nickName;
		private String avatar;
		private int isFollowed;
		private int isFollowMe;
		private String introduced;
		private int v_lv;
	}
	
	@Data
	public static class KingdomElement implements BaseEntity {
		private static final long serialVersionUID = -1719202242187340090L;
		
		private long uid;
		private String coverImage;
		private String title;
		private String avatar;
		private String nickName;
		private long createTime;
		private long topicId;
		private long updateTime;
		private int isFollowed;
		private int isFollowMe;
		private long topicCount;
		private int v_lv;
		private int internalStatus;
		private int favorite;
		private long lastUpdateTime;
		private long cid;
		private long likeCount;
		private long reviewCount;
		private long favoriteCount;
		private long readCount;
		private int type;
		private int contentType;
		private long acCount;
		private String tags;
	}
	
	@Data
	public static class UgcElement implements BaseEntity {
		private static final long serialVersionUID = -2661881021230161502L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private int v_lv;
		private int isFollowed;
		private int isFollowMe;
		private String coverImage;
		private String title;
		private long cid;
		private int type;
		private String content;
		private long readCount;
		private long reviewCount;
	}
}
