package com.me2me.live.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/13
 * Time :17:16
 */
@Data
public class ShowTopicListDto implements BaseEntity{

    private int inactiveLiveCount;

    private String liveTitle;

    private int liveCount;

    private int isUpdate;
    
    private List<GivenKingdom> givenKingdoms = Lists.newArrayList();
    private List<ShowTopicElement> showTopicElements = Lists.newArrayList();

    public static ShowTopicElement createShowTopicElement(){
        return new ShowTopicElement();
    }

    private List<UpdateLives> updateLives = Lists.newArrayList();

    public static UpdateLives createUpdateLivesElement(){
        return new UpdateLives();
    }

    private List<AttentionElement> attentionData = Lists.newArrayList();

    public static AttentionElement createAttentionElement(){
        return  new AttentionElement();
    }
    @Data
    public static class GivenKingdom extends ShowTopicElement implements BaseEntity{
		private static final long serialVersionUID = 1L;
		private long givenKingdomId;
    	private String summary;
    	private String tags;
    }
    @Data
    public static  class ShowTopicElement implements BaseEntity{

        /**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private long cid;

        private long topicId;

        private String title;

        private String coverImage;

        private long uid;

        private String avatar;
        
        private String avatarFrame;

        private Date createTime;

        private int lastContentType;

        private String lastFragment;

        private String lastFragmentImage;

        private String nickName;

        private int status;

        private int reviewCount;

        private int likeCount;

        private int isLike;

        private int personCount;

        private int favorite;

        private int favoriteCount;

        private long updateTime;

        private int isFollowed;

        private int isFollowMe;

        private long lastUpdateTime;

        private int topicCount;

        private int isUpdate;

        private int readCount;

        private int v_lv;

        private int contentType;

        private int acCount;

        private int lastStatus;

        private String lastExtra;

        private int isTop;

        private int lastType;

        private long lastAtUid;
        
        //0圈外 1圈内 2核心圈
        private int internalStatus;
        
        private long lastUid;
        private String lastNickName;
        private String lastAvatar;
        private String lastAvatarFrame;
        private int lastV_lv;
        private int price;
        private int level;
        private int lastLevel;
        
        private String kcName;
    }

    @Data
    public static class UpdateLives implements BaseEntity{

        private long uid;

        private String avatar;

        private int v_lv;

        private int level;
    }

    @Data
    public static class AttentionElement implements BaseEntity{

        private long uid;

        private String avatar;

        private int v_lv;

        private int level;

        private String avatarFrame;
    }

}
