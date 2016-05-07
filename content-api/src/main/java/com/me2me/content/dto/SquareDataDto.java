package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.me2me.content.model.Content;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Data
public class SquareDataDto implements BaseEntity {

    /**
     * 该版本广场规则
     * 1. 显示原生UGC和转发系统（图文|音乐）
     *
     */
    private List<SquareDataElement> results = Lists.newArrayList();

    public static SquareDataElement createElement(){
        return new SquareDataElement();
    }

    @Data
    public static class SquareDataElement implements BaseEntity{

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

        //点赞数
        private int likeCount;

        // 小编文章标题
        private String title;

        // 转发文章类型 音乐 | 图片
        private int contentType;

        //是否关注
        private int isFollowed;

        //评论数
        private int reviewCount;

        //直播参与人数
        private int personCount;

        //公开权限 0 仅自己 1 公开
        private int rights;

        //直播状态 0 正在直播 1直播已结束
        private int liveStatus;

        //是否收藏
        private int favorite;

        }

}
