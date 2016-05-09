package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/3
 * Time :16:31
 */
@Data
public class ShowAttentionDto implements BaseEntity {

    private List<ContentElement> attentionData = Lists.newArrayList();

    public static ContentElement createElement(){
        return new ContentElement();
    }


    @Data
    public static class ContentElement implements BaseEntity {

        //文章id
        private long id;

        //文章作者
        private long uid;

        // 作者头像
        private String avatar;

        //作者昵称
        private String nickName;

        //直播之后为直播id
        private long forwardCid;

        //文章缩略内容
        private String content;

        //标签（1-3个多个以逗号分割）
        private String tag;

        //文章类型 0原生 3直播
        private int type;

        // 创建时间
        private Date createTime;

        //文章封面图
        private String coverImage;

        // 直播标题
        private String title;

        //是否关注
        private int isFollowed;

        // 点赞数量
        private int likeCount;

        //评论数量
        private int reviewCount;

        //直播参与人数
        private int personCount;

        //文章权限 0仅自己 1所有人
        private int rights;

        //直播状态 0 正在直播 1已结束直播
        private int liveStatus;

        //直播是否收藏 0未收藏 1已收藏
        private int favorite;

        // 图片数量
        private int imageCount;

        private int isLike;

        private List<ReviewElement> reviews = Lists.newArrayList();

        public static ReviewElement createElement(){
            return new ReviewElement();
        }

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
