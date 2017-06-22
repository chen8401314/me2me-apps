package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowTagKingdomsDTO implements BaseEntity {
	private static final long serialVersionUID = 8753602487976199184L;

	private int isForbidden;
	private List<KingdomElement> kingdomList = Lists.newArrayList();
	
	@Data
	public static class KingdomElement implements BaseEntity{
		private static final long serialVersionUID = -3955471170139881259L;
		
		private long sinceId;
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
		private int level;
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
}
