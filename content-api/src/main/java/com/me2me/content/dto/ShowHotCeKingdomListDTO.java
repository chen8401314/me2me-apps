package com.me2me.content.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowHotCeKingdomListDTO implements BaseEntity {
	private static final long serialVersionUID = 3142494224381887221L;

	private List<HotCeKingdomElement> hottestCeKingdomData = Lists.newArrayList();

	@Data
	public static class HotCeKingdomElement implements BaseEntity{
		private static final long serialVersionUID = 4196958942361116395L;
		
		private long sinceId;
		private long uid;
		private String avatar;
		private String nickName;
		private int v_lv;
		private int isFollowed;
		private int isFollowMe;
		private int favorite;
		private long topicId;
		private long forwardCid;
		private long id;
		private long cid;
		private String title;
		private String coverImage;
		private long createTime;
		private long updateTime;
		private long lastUpdateTime;
		private int contentType;
		private int internalStatus;
		private int acCount;
		private int favoriteCount;
		private String tags;
		
		private List<AcTopElement> acTopList = Lists.newArrayList();
		
		private List<MemberElement> memberList = Lists.newArrayList();
	}
	
	@Data
	public static class AcTopElement implements BaseEntity{
		private static final long serialVersionUID = -1923045175691333433L;
		
		private long topicId;
		private long cid;
		private String title;
		private String coverImage;
		private int contentType;
		private int internalStatus;
	}
	
	@Data
	public static class MemberElement implements BaseEntity{
		private static final long serialVersionUID = 7903623898656879546L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private int v_lv;
	}
}
