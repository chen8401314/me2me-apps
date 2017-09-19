package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/25
 * Time :18:11
 */
@Data
public class BaseContentDto implements BaseEntity{

    //文章id
    private long id;

    //文章作者
    private long uid;

    // 作者头像
    private String avatar;
    
    private String avatarFrame;

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

    private int isFollowMe;

    // 点赞数量
    private int likeCount;

    //评论数量
    private int reviewCount;

    //直播参与人数
    private int personCount;

    //直播收藏人数
    private int favoriteCount;

    //文章阅读数量
    private int readCount;

    //文章权限 0仅自己 1所有人
    private int rights;

    //直播状态 0 正在直播 1已结束直播
    private int liveStatus;

    //直播是否收藏 0未收藏 1已收藏
    private int favorite;

    // 图片数量
    private int imageCount;

    private int isLike;

    private String forwardUrl;

    private String forwardTitle;

    private long lastUpdateTime;

    private Date finalUpdateTime;

    private int topicCount;

    private int contentType;

    private int v_lv;

    private int level;
    
    //0圈外 1圈内 2核心圈
    private int internalStatus;

    //聚集子王国数量
    private int acCount;
    
    private long forwardUid;
    
    private String forwardNickName;
    
    private int price;
    
    private String tags;
    
    private int kcid;//分类ID
    private String kcName;//分类名称
    private String kcImage;//分类封面
    private String kcIcon;//分类图标
}
