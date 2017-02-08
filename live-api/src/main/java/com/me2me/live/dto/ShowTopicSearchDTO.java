package com.me2me.live.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowTopicSearchDTO implements BaseEntity {
	private static final long serialVersionUID = -5312235639141472373L;

	private List<TopicElement> resultList = Lists.newArrayList();
	
	@Data
	public static class TopicElement implements BaseEntity {
		private static final long serialVersionUID = -1916442639822204616L;
		
		private long uid;
		private String coverImage;
		private String title;
		private String avatar;
		private String nickName;
		private Date createTime;
		private long topicId;
		private int status;
		private long updateTime;
		private int isFollowed;
        private int isFollowMe;
        private int topicCount;
        private int v_lv;
        private int internalStatus;//0圈外 1圈内 2核心圈
        private int favorite;
        private int isUpdate;
        
        private int LastContentType;
        private String lastFragment;
        private String lastFragmentImage;
        private long lastUpdateTime;
        
		private long cid;
		private int likeCount;
		private int personCount;
		private int reviewCount;
		private int favoriteCount;
		private int readCount;
        private int isLike;
        
        private int type;//内容type：ugc,王国之类
        private int contentType;//王国类型
        
        private int isTop;//是否置顶，0否，1是
        private int isPublish;//是否接受内容下发，0是，1否
	}
}
