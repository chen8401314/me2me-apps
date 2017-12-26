package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.me2me.content.dto.ShowHotListDTO.HotContentElement;
import com.me2me.content.dto.ShowMyPublishDto.OutDataElement;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pc339 on 2017/9/22.
 */
@Data
public class HotDto implements BaseEntity {

	private List<Object> data = Lists.newArrayList();

	private int openPushPositions;
	private int bootFromFollowing;

	@Data
	public static class BaseContentElement implements BaseEntity {
		
		private static final long serialVersionUID = 510214586715557816L;
		private int type;
		private long cid;
		private String title;

	}
	@Data
	public static class TagContentElement extends BaseContentElement {
		
		private static final long serialVersionUID = 102597088990701416L;
		private int size;
		private String nickName;
		private String avatar;
		
	}
	
	@Data
	public static class HeightWidthContentElement extends BaseContentElement{
		private static final long serialVersionUID = 1L;
		private int h;
		private int w;
		
	}

	@Data
	public static class InvitationElement implements BaseEntity {
		
		private static final long serialVersionUID = 5791045224437406531L;
		private int type;
		private String cText;
		private String btnText;
		private int htStart;
		private int htEnd;
		private String avatar;
		private String avatarFrame;
		private int v_lv;
		private long uid;
		private int invitationType;
		private int btnAction;
		private int coins;
	}
	
	@Data
	public static class HotContentElement implements BaseEntity, Comparable<HotContentElement> {

		private static final long serialVersionUID = 6044238989313830688L;
		private long sinceId;
		private long uid;
		private String avatar;
		private String nickName;
		private int vip;
		private int level;
		private String avatarFrame;
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
		private double priceRMB;
		private Integer showRMBBrand;
		private Integer showPriceBrand;
		private int isShowLikeButton;
		private long kcid;
		private String kcName;
		private String kcIcon;
		private String kcImage;
		private int rights;
		private int canView;
        private List<OutDataElement> textData = Lists.newArrayList();
        private List<OutDataElement> audioData = Lists.newArrayList();
        private List<OutDataElement> imageData = Lists.newArrayList();
        private List<OutDataElement> ugcData = Lists.newArrayList();


		// 和前端无关的两个字段
		private Long hid;
		private Long operationTime;

		@Override
		public int compareTo(HotContentElement o) {
			if (o.getOperationTime() > HotContentElement.this.getOperationTime())
				return 1;
			else if (o.getOperationTime() == HotContentElement.this.getOperationTime())
				return 0;
			else
				return -1;
		}

	}
    @Data
    public static class OutDataElement implements BaseEntity {
		private static final long serialVersionUID = -1680174794247251182L;
    	
		private long id;
		private int contentType;
		private int type;
		private String fragment;
		private String fragmentImage;
		private long atUid;
		private String atNickName;
		private String extra;
    }
}
