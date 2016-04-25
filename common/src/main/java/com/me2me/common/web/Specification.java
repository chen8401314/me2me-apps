package com.me2me.common.web;

/**
 * Created by pc308 on 2016/1/11.
 */
public interface Specification {
    /**
     * 用户关注行为
     */
    enum UserFollowAction{

        FOLLOW("关注",0),

        UN_FOLLOW("取消",1);

        public final String name;

        public final int index;

        UserFollowAction(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 用户状态
     */
    public  enum UserStatus{

        NORMAL("正常",0),

        LOCK("锁定",1),

        STOP("禁用",2);

        public final String name;
        public final int index;
        UserStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 修改用户信息Action
     */
    enum ModifyUserProfileAction{

        AVATAR("修改头像",0),

        NICKNAME("修改昵称",1),

        USER_PROFILE("修改信息",2);

        public final String name;
        public final int index;
        ModifyUserProfileAction(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    /**
     * 验证码枚举
     */
    enum VerifyAction{

        GET("获取验证码",0),

        CHECK("验证验证码",1),

        FIND_MY_ENCRYPT("找回验证码",2);

        public final String name;
        public final int index;
        VerifyAction(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 基础数据枚举
     */
    enum UserBasicData{

        YEARS("年代情怀",2),

        START("星座",1),

        SOCIAL_CLASS("社会阶层",4),

        INDUSTRY("行业",3),

        MARRIAGE_STATUS("婚恋状态",5),

        BEAR_STATUS("生育状态",6);

        public final String name;
        public final int index;
        UserBasicData(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    enum ArticleType{

        ORIGIN("原生",0),

        FORWARD("转载",1),

        EDITOR("小编",2),

        LIVE("直播",3),

        ACTIVITY("活动",4);

        public final String name;

        public final int index;

        ArticleType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum ContentStatus{

        NORMAL("正常",0),

        DELETE("删除",1);

        public final String name;
        public final int index;
        ContentStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 提醒类型
     */
    enum UserNoticeType{

        TAG("贴标签",0),

        LIKE("点赞",1);

        public final String name;
        public final int index;
        UserNoticeType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 消息读取类型
     */
    enum NoticeReadStatus{

        UNREAD("未读",0),

        RED("已读",1);

        public final String name;
        public final int index;
        NoticeReadStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 用户提醒类型
     */
    enum UserTipsType{

        LIKE("点赞",1);


        public final String name;
        public final int index;
        UserTipsType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 图片类型
     */
    enum CoverImageType{

        CONTENT("内容图片",0),

        COVER("封面图片",1);


        public final String name;
        public final int index;
        CoverImageType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 是否点过赞
     */
    enum IsLike{

        UNLIKE("为点赞",0),

        ISLIKE("点赞",1);


        public final String name;
        public final int index;
        IsLike(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 感受列表类型
     */
    enum IsForward{

        NATIVE("原生",0),

        FORWARD("转发",1);


        public final String name;
        public final int index;
        IsForward(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     *直播文本内容类型
     */
    enum LiveContent{

        TEXT("文本",0),

        IMAGE("图片",1);


        public final String name;
        public final int index;
        LiveContent(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     *直播文本内容类型
     */
    enum LiveSpeakType{

        ANCHOR("主播发言",0),

        FANS("粉丝发言",1),

        FORWARD("转发",2),

        ANCHORWRITETAG("主播贴标",3),

        FANSWRITETAG("粉丝贴标",4),

        LIKES("点赞",5);

        public final String name;
        public final int index;
        LiveSpeakType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     *直播状态
     */
    enum LiveStatus{

        LIVING("正在直播",0),

        OVER("结束直播",1),

        REMOVE("移除直播",2);
        public final String name;
        public final int index;
        LiveStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     *直播是否收藏
     */
    enum LiveFavorite{

        NORMAL("未收藏",0),

        FAVORITE("收藏",1);
        public final String name;
        public final int index;
        LiveFavorite(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

}
