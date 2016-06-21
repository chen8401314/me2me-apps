package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/28
 * Time :18:04
 */
@Data
public class ShowHottestDto implements BaseEntity {

    //活动
    private List<ActivityElement> activityData = Lists.newArrayList();

    // 最热内容项目前为5条数据
    private List<HottestContentElement> tops = Lists.newArrayList();

    public static ActivityElement createActivityElement(){
        return new ActivityElement();
    }

    //小编选的系统文章，小编选的用户文章，小编选的用户直播
    private List<HottestContentElement> hottestContentData = Lists.newArrayList();

    public static HottestContentElement createHottestContentElement(){
        return new HottestContentElement();
    }

    //活动
    @Data
    public static class ActivityElement implements BaseEntity{

        //发活动人的uid
        private long uid;

        //发活动人的图像
        private String avatar;

        //发活动人的昵称
        private String nickName;

        //是否关注发活动的人 0未关注 1关注
        private int isFollowed;

        //活动id
        private long id;

        //活动标题
        private String title;

        //活动封面
        private String coverImage;

        //活动更新时间
        private Date updateTime;


    }

    //内容
    @Data
    public static class HottestContentElement implements BaseEntity{

        private long id;

        private long uid;

        // 原文ID
        private long forwardCid;

        //图像，系统文章没有图像,昵称 感受标签。
        private String avatar;

        //昵称
        private String nickName;

        //文章标题
        private String title;

        //感受标签，多个标签分号分开。
        private String tag;

        //文章内容图片数量
        private int imageCount;

        //小编修改的时间
        private Date updateTime;

        //内容
        private String content;

        //封面
        private String coverImage;

        //点赞数量
        private int likeCount;

        //评论数
        private int reviewCount;

        //参与人数
        private int personCount;

        //直播收藏人数
        private int favoriteCount;

        //文章阅读数量
        private int readCount;

        //是否关注
        private int isFollowed;

        //直播状态
        private int liveStatus;

        //类型 5 系统 3 活动 0 原生
        private int type;

        //直播是否收藏
        private int favorite;

        private Date createTime;

        private int isLike;

        private String forwardUrl;

        private String forwardTitle;


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
