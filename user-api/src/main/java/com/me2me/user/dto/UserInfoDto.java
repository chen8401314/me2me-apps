package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/31
 * Time :13:40
 */
@Data
public class UserInfoDto implements BaseEntity{

    private User user = new User();

    private List<ContentElement> contentElementList = Lists.newArrayList();

    public static ContentElement createElement(){
        return new ContentElement();
    }

    @Data
    public static class User implements BaseEntity{

        private String nickName;

        private long uid;

        private String avatar;
        
        private String avatarFrame;

        private int gender;

        private String meNumber;

        private int isFollowed;

        private int isFollowMe;

        private int followedCount;

        private int fansCount;

        private String introduced;

        private int v_lv;
        
        private Date createTime;
        
        private int level;
    }

    @Data
    public static class ContentElement implements BaseEntity{

        private List<ReviewElement> reviews = Lists.newArrayList();

        public static ReviewElement createElement(){
            return new ReviewElement();
        }

        private long cid;

        private String tag;

        private String coverImage;

        private String content ;

        private String title;

        private Date createTime;

        private int likeCount;

        private int reviewCount;

        private int personCount;

        private int favoriteCount;

        private Long forwardCid;

        private Integer type;

        private Integer contentType;

        private int liveStatus;

        private int imageCount;

        private int favorite;

        private int isLike;

        private String forwardTitle;

        private String forwardUrl;

        private int readCount;

        private long lastUpdateTime;

        private int topicCount;

        @Data
        public static class ReviewElement implements BaseEntity{

            private long uid;

            private String nickName;

            private String avatar;

            private Date createTime;

            private String review;

        }

    }

}
