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

        FORWARD_UGC("转发UGC",1),

        EDITOR("小编",2),

        LIVE("直播",3),

        ACTIVITY("活动",4),

        SYSTEM("系统",5),

        FORWARD_LIVE("转发直播",6),

        FORWARD_ACTIVITY("转发活动",7),

        FORWARD_SYSTEM("转发小编文章",8),

        FORWARD_ARTICLE("转发系统文章",9);


        public final String name;

        public final int index;

        ArticleType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum ContentStatus{

        NORMAL("正常",0),

        DELETE("删除",1),

        RECOVER("回收",2);

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

        LIKE("点赞",1),

        REVIEW("UGC评论",2),

        LIVE_TAG("直播贴标",3),

        LIVE_REVIEW("直播回复",4),

        UGCAT("UGC@",5),

        LIVE_INVITED("圈子邀请",6),

        REMOVE_SNS_CIRCLE("圈子移除",7);

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
     * 点赞操作
     */
    enum IsLike{

        LIKE("点赞",0),

        UNLIKE("取消点赞",1);


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

        ANCHOR_WRITE_TAG("主播贴标",3),

        FANS_WRITE_TAG("粉丝贴标",4),

        LIKES("点赞",5),

        SUBSCRIBED ("订阅",6),

        SHARE ("分享",7),

        FOLLOW ("关注",8),

        INVITED("邀请",9),

        AT("有人@",10),

        ANCHOR_AT("主播@",11),

        VIDEO("视频",12),

        SOUND("语音",13),

        ANCHOR_RED_BAGS("国王收红包",14),

        AT_CORE_CIRCLE("@核心圈",15);

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

    public  enum ActivityStatus{

        NORMAL("正常",0),

        STOP("下架",1);

        public final String name;
        public final int index;
        ActivityStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public  enum ActivityInternalStatus{

        NO_NOTICE("未发公告",0),

        NOTICED("已发公告",1);

        public final String name;
        public final int index;
        ActivityInternalStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 用户状态
     */
    public  enum ContentRights{

        SELF("仅自己",0),

        EVERY("所有人",1);

        public final String name;
        public final int index;
        ContentRights(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    /**
     * 内容类型
     */
    public  enum ContentType{

        TEXT("图文",0),

        OTHER("其他",1);

        public final String name;
        public final int index;
        ContentType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum VersionStatus{

        NEWEST("最新",0),

        UPDATE("需更新",1),

        FORCE_UPDATE("强制更新",2);

        public final String name;

        public final int index;

        VersionStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum PushMessageType{

        LIKE("点赞",0),

        TAG("日记贴标",1),

        LIVE_TAG("直播贴标",2),

        REVIEW("评论",3),

        LIVE_REVIEW("直播评论",4),

        LIVE("关注的人开播",5),

        FOLLOW("关注",6),

        HOTTEST("日记上最热",7),

        LIVE_HOTTEST("直播上最热",8),

        UPDATE("收藏的直播有更新",9),

        AT("有人@我",10),

        CORE_CIRCLE("邀请核心圈",11),

        REMOVE_CORE_CIRCLE("从核心圈移除",12);

        public final String name;

        public final int index;

        PushMessageType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum LiveTimeLineDirection{

        FIRST("第一次",0),

        NEXT("下一页",1),

        PREV("上一页",2);

        public final String name;

        public final int index;

        LiveTimeLineDirection(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum DevicePlatform{

        ANDROID("Android",1),

        IOS("Ios",2);


        public final String name;

        public final int index;

        DevicePlatform(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    public enum LikesType{

        CONTENT("原生UGC",1),

        LIVE("直播",2),

        ARTICLE("系统文章",3),

        ACTIVITY("活动",4);

        public final String name;

        public final int index;

        LikesType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum ReviewType{

        CONTENT("原生UGC",1),

        ARTICLE("系统文章",2),

        ACTIVITY("活动",3);

        public final String name;

        public final int index;

        ReviewType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum MonitorAction{
        BOOT(0,"用户启动"),
        LOGIN(1,"用户登录"),
        REGISTER(2,"用户注册"),
        CONTENT_VIEW(3,"用户浏览"),
        CONTENT_PUBLISH(4,"发布内容"),
        LIVE_PUBLISH(5,"发布直播"),
        LIKE(6,"用户点赞"),
        UN_LIKE(7,"用取消点赞"),
        REVIEW(8,"用户评论"),
        FEELING_TAG(9,"添加感受标签"),
        FOLLOW(10,"关注"),
        UN_FOLLOW(11,"取消关注"),
        FORWARD(12,"转发内容"),
        HOTTEST(13,"热门"),
        NEWEST(14,"最新"),
        FOLLOW_LIST(15,"关注文章");

        public int index;

        public String name;

        MonitorAction(int index,String name){
            this.index = index;
            this.name = name;
        }

    };

    public enum MonitorType{
        BOOT(0,"启动访问"),

        ACTION(1,"行为监控");

        public int index;

        public String name;

        MonitorType(int index,String name){
            this.index = index;
            this.name = name;
        }

    };

    public enum WriteTagType{

        CONTENT("原生内容",1),

        ARTICLE("系统文章",2),

        ACTIVITY("活动",3);

        public final String name;

        public final int index;

        WriteTagType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    public enum SnsCircle{

        CORE("核心圈",2),

        IN("圈内",1),

        OUT("圈外",0);

        public final String name;

        public final int index;

        SnsCircle(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum Favorite{

        FAVORITE("订阅",1),

        CANCEL_FAVORITE("取消订阅",2);

        public final String name;

        public final int index;

        Favorite(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum ModifyCircleType{

        CORE_CIRCLE("邀请核心圈",1),

        IN_CIRCLE("邀请圈内",2),

        CANCEL_CORE_CIRCLE("踢出核心",1),

        CANCEL_IN_CIRCLE("踢出圈内",2);

        public final String name;

        public final int index;

        ModifyCircleType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum PushStatus{

        UN_PUSHED("未推送",0),

        PUSHED("已推送",1);

        public final String name;

        public final int index;

        PushStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum UserActivate{

        UN_ACTIVATED("未激活",0),

        ACTIVATED("激活",1);

        public final String name;

        public final int index;

        UserActivate(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum LiveMode{

        COMMON("普通模式",0),

        SENIOR("高级模式",1);

        public final String name;

        public final int index;

        LiveMode(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    public enum LiveFist{

        YES("第一次",0),

        NOT("非第一次",1);

        public final String name;

        public final int index;

        LiveFist(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    public enum SearchType{

        ALL("所有人",0),

        FANS("粉丝",1);

        public final String name;

        public final int index;

        SearchType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * UGC和直播区分
     */
    enum UGCorLiveType{

        UGCandLive("直播和UGC",0),

        UGCList("UCG感受列表",1),

        LiveList("王国列表",2);

        public final String name;

        public final int index;

        UGCorLiveType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum ThirdPartType{

        MOBILE("mobile",0),

        QQ("qq",1),

        WEIXIN("weixin",2),

        WEIBO("weibo",3);

        public final String name;

        public final int index;

        ThirdPartType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum  DeleteObjectType{
        TOPIC_FRAGMENT("topic_frament",1),

        TOPIC_BARRAGE("topic_frament",1);

        public final String name;

        public final int index;

        DeleteObjectType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    enum VipLevel{

        noV("非大V",0),

        isV("是大V",1);

        public final String name;

        public final int index;

        VipLevel(String name,int index){
            this.name = name;
            this.index = index;
        }

    }

    enum TopicFragmentStatus{

        ENABLED("有效",1),

        DISABLED("无效已删除",0);

        public final String name;

        public final int index;

        TopicFragmentStatus(String name,int index){
            this.name = name;
            this.index = index;
        }

    }


}
