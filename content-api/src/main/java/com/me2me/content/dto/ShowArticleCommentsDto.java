package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :20:18
 */
@Data
public class ShowArticleCommentsDto implements BaseEntity{

    private int likeCount;

    private int reviewCount;

    private int readCount;

    private int isLike;

    private int v_lv;

    private List<ReviewElement> reviews = Lists.newArrayList();

    public static ReviewElement createElement(){
        return new ReviewElement();
    }

    @Data
    public static class ReviewElement extends LikeElement implements BaseEntity{

        private long id;

        private Date createTime;

        private String review;

        private String atNickName;

        private long atUid;
        
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

    private List<ContentTagElement> tags = Lists.newArrayList();

    public static ContentTagElement createContentTagElement(){
        return new ContentTagElement();
    }

    @Data
    public static class ContentTagElement implements BaseEntity {

        private String tag;

    }
}
