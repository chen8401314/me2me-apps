package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowRecQueryDTO implements BaseEntity {
	private static final long serialVersionUID = -6324508711149220473L;

	private List<KingdomElement> result = Lists.newArrayList();
	
	@Data
	public static class KingdomElement implements BaseEntity{
		private static final long serialVersionUID = -2757302618809417548L;
		
		private long sinceId;
		private long uid;
		private String coverImage;
		private String title;
		private String avatar;
		private String avatarFrame;
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
