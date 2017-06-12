package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/3
 * Time :18:13
 */
@Data
public class ShowActivitiesDto implements BaseEntity{


    //活动
    private List<ActivityElement> activityData = Lists.newArrayList();

    public static ActivityElement createActivityElement(){
        return new ActivityElement();
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

        private int contentType;

        private String contentUrl;

        private int type;

        private long topicId;

        private long cid;

        private int topicType;

        private int topicInternalStatus;
        
        private String linkUrl;

    }
}
