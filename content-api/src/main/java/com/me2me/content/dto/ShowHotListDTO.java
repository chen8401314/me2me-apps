package com.me2me.content.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowHotListDTO implements BaseEntity {
	private static final long serialVersionUID = -7151479703018545020L;

	private List<HotActivityElement> activityData = Lists.newArrayList();
	
	private List<HotFamousUserElement> famousUserData = Lists.newArrayList();
	
	private List<HotCeKingdomElement> hottestCeKingdomData = Lists.newArrayList();
	
	private List<HotContentElement> hottestContentData = Lists.newArrayList();
	
	private List<BasicKingdomInfo> listingKingdoms=Lists.newArrayList(); 
	
	private List<HotTagElement> hotTagKingdomList = Lists.newArrayList();
	
	@Data
	public static class HotTagElement implements BaseEntity{
		private static final long serialVersionUID = -1636362714045596434L;
		private String tagName;
		private int personCount;
		private int kingdomCount;
		private double tagPrice;
		private List<BasicKingdomInfo> kingdomList=Lists.newArrayList();
	}
	
	@Data
	public static class HotActivityElement implements BaseEntity{
		private static final long serialVersionUID = -1636362714045596434L;
		
		private long id;
		private String title;
		private String coverImage;
		private long updateTime;
		private int contentType;
		private String contentUrl;
		private int type;
		private long topicId;
		private long cid;
		private int topicType;
		private int topicInternalStatus;
		private String linkUrl;
	}
	
	@Data
	public static class HotFamousUserElement implements BaseEntity{
		private static final long serialVersionUID = -3674098166681911218L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private String introduced;
		private int v_lv;
		private int level;
		private int isFollowed;
		private int isFollowMe;
	}
	
	@Data
	public static class HotCeKingdomElement implements BaseEntity{
		private static final long serialVersionUID = 4196958942361116395L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private int v_lv;
		private int level;
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
		private int price;
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
		private  int level;
	}
	
	@Data
	public static class HotContentElement implements BaseEntity{
		private static final long serialVersionUID = 7753880748064545469L;
		
		private long sinceId;
		private long uid;
		private String avatar;
		private String nickName;
		private int v_lv;
		private int level;
		private int isFollowed;
		private int isFollowMe;
		private int type;
		private int favorite;
		private long createTime;
		private long updateTime;
		private long id;
		private long cid;
		private long topicId;
		private long forwardCid;
		private String title;
		private String coverImage;
		private int contentType;
		private int internalStatus;
		private long lastUpdateTime;
		private int lastType;
		private int lastContentType;
		private String lastFragment;
		private String lastFragmentImage;
		private int lastStatus;
		private String lastExtra;
		private int favoriteCount;
		private int readCount;
		private int likeCount;
		private int reviewCount;
		private String content;
		private String tags;
		private int price;
	}
}
