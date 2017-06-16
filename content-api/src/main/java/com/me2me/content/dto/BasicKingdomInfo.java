package com.me2me.content.dto;

import lombok.Data;

/**
 * 基础王国信息。实现默认王国Builder.
 * 
 * @author zhangjiwei
 * @date Jun 9, 2017
 */
@Data
public class BasicKingdomInfo {
	private long subListId;

	private int subType;

	private long uid;

	private String avatar;

	private String nickName;

	private int v_lv;

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
}
