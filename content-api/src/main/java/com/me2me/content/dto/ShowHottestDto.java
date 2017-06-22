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

        private int level;

        //发活动人的昵称
        private String nickName;

        //是否关注发活动的人 0未关注 1关注
        private int isFollowed;

        private int isFollowMe;

        //活动id
        private long id;

        //活动标题
        private String title;

        //活动封面
        private String coverImage;

        //活动更新时间
        private Date updateTime;

        private int likeCount;

        private int reviewCount;

        private int contentType;

        private String contentUrl;

        private int type;

    }

    //内容
    @Data
    public static class HottestContentElement extends BaseContentDto implements BaseEntity{
//
//        private List<ReviewElement> reviews = Lists.newArrayList();
//
//        public static ReviewElement createElement(){
//            return new ReviewElement();
//        }
//
//        @Data
//        public static class ReviewElement implements BaseEntity{
//
//            private long uid;
//
//            private String nickName;
//
//            private String avatar;
//
//            private Date createTime;
//
//            private String review;
//
//        }

        private long sinceId;

    }
}
