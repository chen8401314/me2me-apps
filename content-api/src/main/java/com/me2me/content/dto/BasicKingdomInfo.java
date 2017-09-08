package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 基础王国信息。实现默认王国Builder.
 * 
 * @author zhangjiwei
 * @date Jun 9, 2017
 */
@Data
public class BasicKingdomInfo implements BaseEntity{
	private static final long serialVersionUID = 1L;

	private long subListId;

	private int subType;

	private long uid;

	private String avatar;

	private String nickName;

	private int v_lv;

	private int level;
	
	private String avatarFrame;

	private int isFollowed;

	private int isFollowMe;

	private int contentType;

	private int favorite;

	private long id;

	private long cid;

	private long topicId;

	private long forwardCid;

	private String title;

	private String coverImage;

	private int internalStatus;

	private int favoriteCount;

	private int readCount;

	private int likeCount;

	private int reviewCount;

	private String introduced;
	
	private String tags;
	
	private Integer price;
	
	private double priceRMB;
	
	private Integer showRMBBrand;
	
	private Integer showPriceBrand;
	
	private int type;
	
	
}
