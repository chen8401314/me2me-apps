package com.me2me.search.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;


/**
 * 推荐用户
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
@Data
public class RecommendListDto implements BaseEntity {
	private static final long serialVersionUID = -3412681619710644087L;
	private RecPerson persona;
	private EmotionKingdom emotionKingdom;
	private List<RecommendUser> recUserData = Lists.newArrayList();
	private List<ContentData> recContentData = Lists.newArrayList();
	
	@Data
	public static class RecPerson implements BaseEntity{
		private static final long serialVersionUID = 1L;
		private long uid;
		private String nickName;
		private String avatar;
		private int v_lv;
		private int completion;
		private int sex;
		private int sexOrientation;
		private int ageGroup;
		private int career;
		private String hobby;
		private String mbti;
		private List<UserEmotion> emotionList = Lists.newArrayList();
	}
	
	@Data
	public static class EmotionKingdom implements BaseEntity{
		private static final long serialVersionUID = -3509135123860722764L;
		
		private long topicId;
		private String title;
		private String coverImage;
		private int contentType;
		private int internalStatus;
	}
	
	@Data
	public static class UserEmotion implements BaseEntity{
		private static final long serialVersionUID = -7182601989946502112L;
		
		private long id;
		private long rid;
		private String emotionName;
		private int happyValue;
		private int freeValue;
		private long topicId;
		private int internalStatus;
		private int recordCount;
		private long createTime;
		private long timeInterval;
		private EmotionPackage emotionPack;
	}
	
	@Data
	public static class EmotionPackage implements BaseEntity{
		private static final long serialVersionUID = -6992654088478152412L;
		
		private long id;
		private String title;
		private String content;
		private String image;
		private String thumb;
		private long w;
		private long h;
		private long thumb_w;
		private long thumb_h;
		private String extra;
		private int emojiType;
		
	}
	
	@Data
	public static class ContentData implements BaseEntity{
		private static final long serialVersionUID = 1L;
		private long uid;
		private String coverImage;
		private String title;
		private String avatar;
		private String nickName;
		private long createTime;
		private long  topicId;
		private long updateTime;
		private int isFollowed;
		private int isFollowMe;
		private int topicCount;
		private int v_lv;
		private int internalStatus;
		private int favorite;
		private long lastUpdateTime;
		private long  cid;
		private int likeCount;
		private int reviewCount;
		private int favoriteCount;
		private int readCount;
		private int type;
		private int contentType;
		private int acCount;
		private String tags;
		private String reason;
		
		private long contentId;
		private String linkUrl;
	}
}
