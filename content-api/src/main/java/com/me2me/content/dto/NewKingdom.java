package com.me2me.content.dto;

import java.util.ArrayList;
import java.util.List;

import com.me2me.content.dto.ShowAttentionDto.OutDataElement;

import lombok.Data;

/**
 * 新版列表王国，包括全字段，使用kingdombuilder按实际需求填充。
 * 
 * @author zhangjiwei
 * @date Sep 19, 2017
 */
@Data
public class NewKingdom {
	private int type;
	private String title;
	private String avatar;
	private String avatarFrame;
	private long cid;
	private long topicId;
	private String content;
	private int contentType;
	private String coverImage;
	private long createTime;
	private int favorite;
	private int favoriteCount;
	private long updateTime;
	private int isFollowMe;
	private int isFollowed;
	private int isShowLikeButton;
	private int isShowName;
	private int level;
	private int likeCount;
	private String nickName;
	private int personCount;
	private int price;
	private double priceRMB;
	private int readCount;
	private int reviewCount;
	private int showPriceBrand;
	private int showRMBBrand;
	private int showTag;
	private String tags;
	private int topicPrice;
	private int topicRMB;
	private int transferPrice;
	private int uid;
	private int kcid;
	private String kcIcon;
	private String kcName;
	private List<OutDataElement> audioData = new ArrayList<OutDataElement>();
	private List<OutDataElement> imageData = new ArrayList<OutDataElement>();
	private List<OutDataElement> textData = new ArrayList<OutDataElement>();
	private List<OutDataElement> ugcData = new ArrayList<OutDataElement>();
}
