package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/28
 * Time :16:10
 */
@Data
public class ContentDetailDto implements BaseEntity {

    private long id;

    private long uid;

    private String feeling;

    private String avatar;

    private int type;

    private String nickName;

    private int hotValue;

    private int likeCount;

    private int reviewCount;

    private int personCount;

    private int favoriteCount;

    private Date createTime;

    private int isLike;

    private String content;

    private String coverImage;

    private String title;

    private int contentType;

    private int rights;

    //是否关注
    private int isFollowed;

    private int isFollowMe;

    private int readCount;

    private int isHot;

    private int v_lv;

    private int level;

    private List<ImageElement> images = Lists.newArrayList();

    public static ImageElement createImageElement(){
        return new ImageElement();
    }

    @Data
    public static class ImageElement implements BaseEntity {

        private String image;

    }

    private List<ContentTagElement> tags = Lists.newArrayList();

    public static ContentTagElement createElement(){
        return new ContentTagElement();
    }

    @Data
    public static class ContentTagElement implements BaseEntity {

        private String tag;

    }

    private List<ReviewElement> reviews = Lists.newArrayList();

    public static ReviewElement createReviewElement(){
        return new ReviewElement();
    }

    @Data
    public static class ReviewElement extends LikeElement implements BaseEntity{

        private Date createTime;

        private String review;

        private long atUid;

        private String atNickName;
        
        private long id;
        
        private int v_lv;

        private String extra;

    }

    private List<LikeElement> likeElements = Lists.newArrayList();

    public static LikeElement createLikeElement(){
        return new LikeElement();
    }

    @Data
    public static class LikeElement implements BaseEntity{

        private long uid;

        private String nickName;

        private String avatar;
        private int level;
    }

}
