package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/20
 * Time :13:29
 */
@Data
public class ShowContentListDto implements BaseEntity {
    /**
     * 广场，规则
     * 1.广场首页前部分先显示活动内容，再显示广场内容
     *
     */

    //活动内容
    private List<ContentDataElement> activityData = Lists.newArrayList();

    //广场内容
    private List<ContentDataElement> squareData = Lists.newArrayList();



    public static ContentDataElement createElement(){
        return new ContentDataElement();
    }

    @Data
    public static class ContentDataElement implements BaseEntity{

        private long id;

        private long uid;

        // 原文ID
        private long forwardCid;

        private String avatar;

        private String nickName;

        private String content;

        private String tag;

        private int type;

        private Date createTime;

        private int isLike;

        private String coverImage;

        private int likeCount;

        private int hotValue;

        // 小编文章标题
        private String title;

        // 转发缩略图
        private String thumbnail;

        // 转发标题
        private String forwardTitle;

        // 转发文章类型 音乐 | 图片
        private int contentType;

        // 转发URL
        private String forwardUrl;

        private long tid;

        //是否关注
        private int isFollowed;

        private int tagCount;

        private int reviewCount;

        private int forwardCount;

        private int personCount;

        private int favorite;

        private int liveStatus;

        private List<TagElement> tags = Lists.newArrayList();

        public static TagElement createElement(){
            return new TagElement();
        }

        @Data
        public static class TagElement implements BaseEntity{

            private String tag;

            private long tid;

            private int likeCount;

            private int isLike;
        }



    }
}
