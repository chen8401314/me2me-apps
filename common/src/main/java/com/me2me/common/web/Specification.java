package com.me2me.common.web;

import org.apache.commons.lang3.StringUtils;

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

        LOGIN("登录获取验证码",4),

        SIGNUP("注册获取验证码",5),

        FIND_MY_ENCRYPT("找回验证码",2),
        
        SEND_MESSAGE("纯发短信", 3);

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

        FORWARD_ARTICLE("转发系统文章",9),

        TOPIC_UGC("王国详情UGC",10);

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

        REMOVE_SNS_CIRCLE("圈子移除",7),
        
        //以上为普通消息，以下为系统消息
        CORE_CIRCLE_APPLY("核心圈申请", 8),
        CORE_CIRCLE_NOTICE("核心圈通知", 9),
        
        AGGREGATION_APPLY("聚合申请", 10),
        AGGREGATION_NOTICE("聚合通知", 11),
        ;

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

        AT_CORE_CIRCLE("@核心圈",15),
        
        SYSTEM("系统", 1000);
        
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

        REMOVE_CORE_CIRCLE("从核心圈移除",12),
        
        QUIT_CORE_CIRCLE("退出核心圈",13),
    	
    	EMOTION_SUMMARY("情绪周总结提醒",14),
    	
    	LOTTERY("抽奖开奖提醒",15);

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
    	FORBID("禁言用户",3),

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

        TOPIC_BARRAGE("topic_barrage",2),
        
        ARTICLE_REVIEW("article_review", 3),
        
        CONTENT_REVIEW("content_review", 4),
        
        TOPIC("topic",5),
        
        UGC("ugc",6)
        ;

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

    enum  ClientLogAction{
        AD_REG("广告-注册",11),

        REG_PAGE1_RETURN("注册页面-第一页-返回",21),

        REG_PAGE1_GET_VERIFY("注册页面-第一页-获取验证码",22),

        REG_PAGE1_GET_VERIFY_AGAIN("注册页面-第一页-重新获取",23),

        REG_PAGE1_NEXT("注册页面-第一页-下一步",24),

        REG_PAGE1_WEIXIN("注册页面-第一页-微信",25),

        REG_PAGE1_QQ("注册页面-第一页-QQ",26),

        REG_PAGE2_RETURN("注册页面-第二页-返回",31),

        REG_PAGE2_SAVE("注册页面-第二页-注册",32),

        HOME_SEARCH("首页-搜索",41),

        LIVE_IN_UPDATE("王国-所有更新中的王国",51),

        LIVE_NOT_UPDATED("王国-最近未更新的王国",52),

        UGC_MORE("UGC/文章详情-右上角...",61),

        UGC_REVIEW_INPUT("UGC/文章详情-评论框",62),

        UGC_REVIEW("UGC/文章详情-评论",63),

        UGC_SHARE_FRIEND_CIRCLE("UGC/文章详情-分享-朋友圈",641),

        UGC_SHARE_WEIXIN("UGC/文章详情-分享-微信",642),

        UGC_SHARE_QQ("UGC/文章详情-分享-QQ",643),

        UGC_SHARE_QZONE("UGC/文章详情-分享-QQ空间",644),

        UGC_SHARE_WEIBO("UGC/文章详情-分享-微博",645),

        UGC_LIKES("UGC/文章详情-点赞",65),

        UGC_FEEL("UGC/文章详情-感受",66),

        LIVE_MEMBERS("王国详情-右上角-成员数",71),

        LIVE_SPEAK_INPUT("王国详情-评论框",72),

        LIVE_LIKES("王国详情-点赞",73),

        LIVE_JOIN("王国详情-加入王国",74),

        LIVE_OUT("王国详情-退出王国",75),

        LIVE_SHARE_FRIEND_CIRCLE("王国详情-分享-朋友圈",761),

        LIVE_SHARE_WEIXIN("王国详情-分享-微信",762),

        LIVE_SHARE_QQ("王国详情-分享-QQ",763),

        LIVE_SHARE_QZONE("王国详情-分享-QQ空间",764),

        LIVE_SHARE_WEIBO("王国详情-分享-微博",765);

        public final String name;

        public final int index;

        ClientLogAction(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum PushObjectType{

        UGC("UGC/文章",1),

        LIVE("王国/直播",2),

        SNS_CIRCLE("王国成员",3),
        
        LINK("链接跳转", 4),
        
        NOTICE("消息列表", 5),
        
        CONTACTS("通讯录", 6),
        
        BILLBOARD("榜单详情", 7),
        
        EMOTION("情绪", 8),
        
        LOTTERY("抽奖", 9)
        ;

        public final String name;

        public final int index;

        PushObjectType(String name,int index){
            this.name = name;
            this.index = index;
        }

    }
    
    enum ContentDelStatus{
    	NORMAL("正常", 0),
    	DELETE("删除", 1);
    	
    	public final String name;

        public final int index;

        ContentDelStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum OperateAction{
    	MAIN_HOT("MAIN_HOT","首页_热点"),
    	MAIN_FOLLOW("MAIN_FOLLOW", "首页_关注"),
    	MAIN_DISCOVER("MAIN_DISCOVER", "首页_发现"),
    	KINGDOM_LIST("KINGDOM_LIST", "王国_列表"),
    	KINGDOM_NOT_UPDATED("KINGDOM_NOT_UPDATED", "王国_未更新"),
    	KINGDOM_UPDATED("KINGDOM_UPDATED", "王国_已更新"),
    	INTELLIGENT_RECOMMENDED("INTELLIGENT_RECOMMENDED", "智能推荐"),
    	ENTRY_PAGE("ENTRY_PAGE", "入口页"),
    	MY_PROFILE("MY_PROFILE", "用户资料"),
    	MY_FOLLOW("MY_FOLLOW", "关注列表"),
    	MY_FANS("MY_FANS", "粉丝列表"),
    	MY_FEEL("MY_FEEL", "我的感受"),
    	MY_KINGDOM("MY_KINGDOM", "我的王国")
    	;
    	
    	public final String name;
    	public final String desc;
    	
    	OperateAction(String name, String desc){
    		this.name = name;
    		this.desc = desc;
    	}
    }
    
    enum LiveDetailDirection{
    	UP("向上遍历", 1),
    	DOWN("向下遍历", 0);
    	
    	public final String name;
    	public final int index;
    	
    	LiveDetailDirection(String name, int index){
    		this.name = name;
            this.index = index;
    	}
    }
    
    enum UserContentSearchType{
    	ARTICLE_REVIEW("文章评论", 1),
    	UGC("UGC", 2),
    	UGC_OR_PGC_REVIEW("UGC或PGC评论", 3),
    	KINGDOM("王国", 4),
    	KINGDOM_SPEAK("王国发言或评论", 5)
    	;
    	
    	public final String name;
    	public final int index;
    	
    	UserContentSearchType(String name, int index){
    		this.name = name;
            this.index = index;
    	}
    }

    enum ASevenDayType{
        SINGLE_TOPIC("单人王国", 1),
        DOUBLE_TOPIC("王国王国", 2),
        A_THREE_STAGE("第三阶段双人王国", 3),
        A_DOUBLE_STAGE("第二阶段单人王国", 2),
        A_FIRST_STAGE("第已阶段报名", 1)
        ;

        public final String name;
        public final int index;

        ASevenDayType(String name, int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum KingdomType{
    	NORMAL("普通王国", 0),
    	SPECIAL("特殊王国", 1),
    	AGGREGATION("聚合王国", 1000);
    	
    	public final String name;
        public final int index;
        
        KingdomType(String name, int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum KingdomRights{
    	PUBLIC_KINGDOM("公开王国", 1),
    	PRIVATE_KINGDOM("隐私王国", 2);
    	
    	public final String name;
        public final int index;
        
        KingdomRights(String name, int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum ActivityKingdomType{
    	SINGLEKING("单人王国", 1),
    	DOUBLEKING("双人王国", 2),
    	SPRINGKING("春节王国", 3)
    	;
    	
    	public final String name;
        public final int index;
        
        ActivityKingdomType(String name, int index){
            this.name = name;
            this.index = index;
        }
    }

    enum IsNewYear{
        COMMON_TYPE("普通标识", 0),
        NEWYAR_TYPE("新年标识", 1),
        ;

        public final String name;
        public final int index;

        IsNewYear(String name, int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum ActivityMiliDataKey{
    	
    	ENTER_COMMON("ENTER_COMMON", "每次进入"),
    	FIRST_ENTER("FIRST_ENTER", "首次进入"),
    	
    	APP_DOWNLOAD("APP_DOWNLOAD", "APP下载信息"),
    	
    	ACTIVITY_INFO("ACTIVITY_INFO", "活动信息"),
    	ACTIVITY_COUNTDOWN("ACTIVITY_COUNTDOWN", "活动倒计时"),
    	
    	ACTIVITY_TASK("ACTIVITY_TASK", "活动任务"),
    	
    	SIGNUP_STATUS_0_APP("SIGNUP_STATUS_0_APP", "没有报名信息并APP内"),
    	SIGNUP_STATUS_0_BROWSER("SIGNUP_STATUS_0_BROWSER", "没有报名信息并APP外"),
    	SIGNUP_STATUS_1("SIGNUP_STATUS_1", "报名状态审核中"),
    	SIGNUP_STATUS_2_APP("SIGNUP_STATUS_2_APP", "报名审核通过并没有单人王国并APP内"),
    	SIGNUP_STATUS_2_BROWSER("SIGNUP_STATUS_2_BROWSER", "报名审核通过并没有单人王国并APP外"),
    	SIGNUP_END_APP("SIGNUP_END_APP", "报名结束并APP内"),
    	SIGNUP_END_BROWSER("SIGNUP_END_BROWSER", "报名结束并APP外"),
    	SYSTEM_ARTICLE("SYSTEM_ARTICLE", "系统运营文章"),
    	UPDATE_SINGLE_KINGDOM("UPDATE_SINGLE_KINGDOM", "更新单人王国提醒"),
    	UPDATE_DOUBLE_KINGDOM("UPDATE_DOUBLE_KINGDOM", "更新双人王国提醒"),
    	RECOMMEND_USER_1("RECOMMEND_USER_1", "有效期推荐用户"),
    	RECOMMEND_USER_2("RECOMMEND_USER_2", "失效推荐用户"),
    	NO_DOUBLE_APPLY("NO_DOUBLE_APPLY", "没有我发出的也没有我收到的请求"),
    	HAS_DOUBLE_APPLY("HAS_DOUBLE_APPLY", "有请求"),
    	HAS_DOUBLE_KINGDOM("HAS_DOUBLE_KINGDOM", "有双人王国(配对)"),
    	HAS_DOUBLE_KINGDOM_2("HAS_DOUBLE_KINGDOM_2", "有双人王国(天数)"),
    	MY_DOUBLE_APPLY_REFUSED("MY_DOUBLE_APPLY_REFUSED", "我的双人王国请求被拒"),
    	MY_DOUBLE_APPLY_AGREED("MY_DOUBLE_APPLY_AGREED", "我的双人王国请求被同意"),
    	RECIVE_DOUBLE_APPLY("RECIVE_DOUBLE_APPLY", "接收到双人王国请求"),
    	RECIVE_DOUBLE_APPLY_DELETED("RECIVE_DOUBLE_APPLY_DELETED", "接收到的双人王国请求被撤销"),
    	CAN_ROB_BRIDE("CAN_ROB_BRIDE", "可以抢亲"),
    	HAS_ROB_BRIDE("HAS_ROB_BRIDE", "有抢亲操作"),
    	HAS_ROB_BRIDE_2("HAS_ROB_BRIDE_2", "有被抢亲操作"),
    	NO_ROB_BRIDE("NO_ROB_BRIDE", "有双人没有被抢过"),
    	MY_ROB_BRIDE_APPLY_REFUSED("MY_ROB_BRIDE_APPLY_REFUSED", "我的抢亲请求被拒"),
    	MY_ROB_BRIDE_APPLY_AGREED("MY_ROB_BRIDE_APPLY_AGREED", "我的抢亲请求被同意"),
    	RECIVE_ROB_BRIDE_APPLY("RECIVE_ROB_BRIDE_APPLY", "接收到抢亲请求"),
    	RECIVE_ROB_BRIDE_APPLY_DELETED("RECIVE_ROB_BRIDE_APPLY_DELETED", "接收到的抢亲请求被撤销"),
    	ROB_BRIDE_TARGET("ROB_BRIDE_TARGET", "你的对方被抢"),
    	FORCED_PAIRING("FORCED_PAIRING", "可以强配"),
    	FORCED_PAIRING_1("FORCED_PAIRING_1", "强配中"),
    	FORCED_PAIRING_2("FORCED_PAIRING_2", "强配成功"),
    	FORCED_PAIRING_END("FORCED_PAIRING_END", "强配结束"),
    	
    	NO_SPRING_KINGDOM_PREHEAT_1("NO_SPRING_KINGDOM_PREHEAT_1", "没有春节王国_预热期1(除最后一天)"),
    	NO_SPRING_KINGDOM_PREHEAT_2("NO_SPRING_KINGDOM_PREHEAT_2", "没有春节王国_预热期2(最后一天)"),
    	NO_SPRING_KINGDOM_PERIOD_1("NO_SPRING_KINGDOM_PERIOD_1", "没有春节王国_活动期1(第一天)"),
    	NO_SPRING_KINGDOM_PERIOD_2("NO_SPRING_KINGDOM_PERIOD_2", "没有春节王国_活动期2(除第一天)"),
    	HAS_SPRING_KINGDOM_PREHEAT_1("HAS_SPRING_KINGDOM_PREHEAT_1", "有春节王国_预热期1(除最后一天)"),
    	HAS_SPRING_KINGDOM_PREHEAT_2("HAS_SPRING_KINGDOM_PREHEAT_2", "有春节王国_预热期2(最后一天)"),
    	HAS_SPRING_KINGDOM_PERIOD_1("HAS_SPRING_KINGDOM_PERIOD_1", "有春节王国_活动期1(第一天)"),
    	HAS_SPRING_KINGDOM_PERIOD_2("HAS_SPRING_KINGDOM_PERIOD_2", "有春节王国_活动期2(除第一天)"),
    	NEW_YEARS_EVE_19("NEW_YEARS_EVE_19","除夕19点信息"),
    	NEW_YEAR_9("NEW_YEAR_9", "新年9点信息"),
    	NEW_YEAR_12_1("NEW_YEAR_12_1", "新年12点信息1"),
    	NEW_YEAR_12_2("NEW_YEAR_12_2", "新年12点信息2"),
    	NEW_YEAR_12_3("NEW_YEAR_12_3", "新年12点信息3"),
    	NEW_YEAR_12_4("NEW_YEAR_12_4", "新年12点信息4"),
    	NEW_YEAR_12_5("NEW_YEAR_12_5", "新年12点信息5"),
    	NEW_YEAR_12_6("NEW_YEAR_12_6", "新年12点信息6"),
    	SPRING_ACTIVITY_END("SPRING_ACTIVITY_END", "春节活动结束说明"),
    	SPRING_ACTIVITY_END_9("SPRING_ACTIVITY_END_9", "春节活动结束9点信息"),
    	;
    	
    	public final String key;
        public final String desc;
        
        ActivityMiliDataKey(String key, String desc){
            this.key = key;
            this.desc = desc;
        }
    }
    
    enum LinkPushType{
    	//7天活动
    	PAIR_APPLY("#{1}#向你抛出了绣球，申请跟你配对~", "/7day/my/pair"),
    	PAIR_REFUSE("遗憾地通知你，你向#{1}#发出的配对申请被残忍地拒绝了", "/7day/my/pair"),
    	PAIR_AGREE("恭喜！你中意的#{1}#已经同意了你的配对申请，赶紧共筑爱巢，开启你们的双人王国吧", "/7day/my/pair"),
    	CREATE_DOUBLE_KINGDOM_PARTNER("你和#{1}#的双人王国已被#{1}#创建成功", "/7day/main"),
    	CREATE_DOUBLE_KINGDOM_WOOER("来晚一步！你申请配对的#{1}#已经和别人创建了双人王国", "/7day/my/pair"),
    	DOUBLE_KINGDOM_BREAK("Sad！你和#{1}#的双人王国已成过往烟云", "/7day/main"),
    	FORCED_PAIRING("还没找到中意的TA？我们为你定制的缘分已经上线，快来把TA瞧个仔细！万一就看对眼了呢？", "/7day/main"),
    	ROB_APPLY_PARTNER("有人抢你的另一半，去看看谁这么不要脸~", "/7day/my/pair-status"),
    	ROB_APPLY("#{1}#向你发起了抢亲的请求，希望能和你共结连理，选TA？还是TA？你需要做出这个艰难的决定", "/7day/my/pair-status"),
    	ROB_AGREE("不好啦后院起火啦！你的#{1}#被抢亲的抱走啦！快拿起你的锄头，去挖别人的墙角吧！", "/7day/main"),
    	KINGDOM_NOT_UPDATE("紧急！你已经超过12小时没有更新王国了，有可能失去暗恋你的TA们哦", "/7day/main"),
    	TASK_PUSH("叮咚，是不是已经等不及要完成今天的任务了(嗯嗯)，赶紧去“七天之恋”主会场，要提高自己的热度值可就是今天啦！", "/7day/tasks"),
    	
    	
    	//春节活动
    	TOP10_PUSH("昨日的中奖名单已新鲜出炉，快来看看名单上有没有你？", "/NewYear/my/rank"),
    	ACTIVITY_START_PUSH("春节王国活动开始啦！！！接下来的7天里，每天分享你的春节生活，都有奖励哦。我就先问问年夜饭准备的如何了？这可是件顶大的事。", "/NewYear/my/main"),
    	NO_KINGDOM_PUSH("春节活动明天就开始了，分享你的春节故事还能赢奖品,不来写几句么？", "/NewYear/my/main"),
    	HAS_KINGDOM_PUSH("春节活动明天就开始了。怕你错过了奖品，先来提醒你！", "/NewYear/my/main"),
    	NO_LIST_PUSH("亲，你离今天的指标还差那么一点点哦，不如我们来聊聊今天吃了啥呗？", "/NewYear/my/main"),
    	LIST_TOP50_PUSH("棒棒哒！今天的任务你都完成啦，当前你的排名#{1}#位，请继续加油哦。", "/NewYear/my/main"),
    	LIST_TOP3_PUSH("你当前排行在前三名，太崇拜你了！后面的人正在努力追赶你呢，请保持哦。", "/NewYear/my/main"),
    	
    	;
    	
    	public final String message;
    	public final String linkUrl;
    	
    	LinkPushType(String message, String linkUrl){
    		this.message = message;
    		this.linkUrl = linkUrl;
    	}
    }

    enum SettingModify{

        COVER("封面",1),

        SUMMARY("简介",2),

        TAGS("标签",3),

        PUSH("王国推送",4),

        AGVERIFY("聚合王国审核",5),

        VERIFY("个人王国审核",6),

        ISSUED_MESSAGE("个人王国下发消息",7),

        LIVE_NAME("王国名",8),
        
        KINGDOM_CATEGORY("王国分类",9),
    	
    	SECRET_OPT("私密王国设置",10),
    	
    	AUTO_JOIN_CORE_OPT("加入并自动加入核心圈设置",11);

        public final String name;
        public final int index;

        SettingModify(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum KingdomLanuchType{

        PERSONAL_LANUCH("个人王国发起",1),

        AGGREGATION_LANUCH("聚合王国发起",2);

        public final String name;
        public final int index;

        KingdomLanuchType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum AggregationOptType{

        APPLY("申请",1),

        DISMISS("解散",2),

        TOP("置顶",3),

        CANCEL_TOP("取消置顶",4),

        ISSUED("接受下发",5),

        CANCEL_ISSUED("取消接受下发",6);

        public final String name;
        public final int index;

        AggregationOptType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    enum UserNoticeUnreadContentType{
    	KINGDOM("王国相关", 1),
    	UGC("UGC相关", 2),
    	ARTICLE("文章相关", 3);
    	
    	public final String name;
        public final int index;
        
        UserNoticeUnreadContentType(String name,int index){
        	this.name = name;
            this.index = index;
        }
    }
    
    enum UserNoticeLevel{
    	LEVEL_1("一级目录", 1),
    	LEVEL_2("二级目录", 2);
    	
    	public final String name;
        public final int index;
        
        UserNoticeLevel(String name,int index){
        	this.name = name;
            this.index = index;
        }
    }
    
    enum VoteStatus{

        DELETE("删除",0),

        NORMAL("正常进行",1),

        END("结束",2);

        public final String name;
        public final int index;

        VoteStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum TopicNewsType{
    	BUSINESS("交易信息", 0),
    	OVERLINE("过线信息", 1);
    	
    	public final String name;
        public final int index;

        TopicNewsType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum UserRecInitType{
    	
    	COMMON("COMMON", "无任何条件过滤的", "COMMON"),
    	SEX_FEMALE("SEX_FEMALE", "性别_女", "SEX_FEMALE"),
    	SEX_MALE("SEX_MALE","性别_男", "SEX_MALE"),
    	AGE_00("AGE_00","年龄段_00后", "AGE_1"),
    	AGE_95("AGE_95","年龄段_95后", "AGE_2"),
    	AGE_90("AGE_90","年龄段_90后", "AGE_3"),
    	AGE_85("AGE_85","年龄段_85后", "AGE_4"),
    	AGE_80("AGE_80","年龄段_80后", "AGE_5"),
    	AGE_OLD("AGE_OLD","年龄段_活化石", "AGE_6"),
    	HOBBY_LVXING("HOBBY_LVXING","兴趣_旅行", "HOBBY_96"),
    	HOBBY_YINYUE("HOBBY_YINYUE","兴趣_音乐", "HOBBY_80"),
    	HOBBY_MEISHI("HOBBY_MEISHI","兴趣_美食", "HOBBY_81"),
    	HOBBY_DONGMAN("HOBBY_DONGMAN","兴趣_动漫", "HOBBY_83"),
    	HOBBY_MEIRONGMEIZHUANG("HOBBY_MEIRONGMEIZHUANG","兴趣_美容美妆", "HOBBY_90"),
    	HOBBY_JIANKANGYANGSHENG("HOBBY_JIANKANGYANGSHENG","兴趣_健康养生", "HOBBY_85"),
    	HOBBY_SHISHANG("HOBBY_SHISHANG","兴趣_时尚", "HOBBY_87"),
    	HOBBY_YOUXI("HOBBY_YOUXI","兴趣_游戏", "HOBBY_84"),
    	HOBBY_SHEJI("HOBBY_SHEJI","兴趣_设计", "HOBBY_88"),
    	HOBBY_SHEYING("HOBBY_SHEYING","兴趣_摄影", "HOBBY_89"),
    	HOBBY_QICHE("HOBBY_QICHE","兴趣_汽车", "HOBBY_92"),
    	HOBBY_YULE("HOBBY_YULE","兴趣_娱乐", "HOBBY_97"),
    	HOBBY_JUNSHI("HOBBY_JUNSHI","兴趣_军事", "HOBBY_93"),
    	HOBBY_LISHI("HOBBY_LISHI","兴趣_历史", "HOBBY_101"),
    	HOBBY_TANSUO("HOBBY_TANSUO","兴趣_探索", "HOBBY_102"),
    	HOBBY_REDIANXINWEN("HOBBY_REDIANXINWEN","兴趣_热点新闻", "HOBBY_94"),
    	HOBBY_QUWEI("HOBBY_QUWEI","兴趣_趣味", "HOBBY_104"),
    	HOBBY_JUJIA("HOBBY_JUJIA","兴趣_居家", "HOBBY_82"),
    	HOBBY_KEJI("HOBBY_KEJI","兴趣_科技", "HOBBY_91"),
    	HOBBY_MEIWEN("HOBBY_MEIWEN","兴趣_美文", "HOBBY_103"),
    	HOBBY_JIAOYU("HOBBY_JIAOYU","兴趣_教育", "HOBBY_98"),
    	HOBBY_JINGGUAN("HOBBY_JINGGUAN","兴趣_经管", "HOBBY_99"),
    	HOBBY_WENHUA("HOBBY_WENHUA","兴趣_文化", "HOBBY_100"),
    	HOBBY_WENTIHUODONG("HOBBY_WENTIHUODONG","兴趣_文体活动", "HOBBY_86"),
    	HOBBY_CHONGWU("HOBBY_CHONGWU","兴趣_宠物", "HOBBY_154"),
    	MBTI_INTJ("MBTI_INTJ","MBTI_INTJ","MBTI_INTJ"),
    	MBTI_INTP("MBTI_INTP","MBTI_INTP","MBTI_INTP"),
    	MBTI_ENTJ("MBTI_ENTJ","MBTI_ENTJ","MBTI_ENTJ"),
    	MBTI_ENTP("MBTI_ENTP","MBTI_ENTP","MBTI_ENTP"),
    	MBTI_INFJ("MBTI_INFJ","MBTI_INFJ","MBTI_INFJ"),
    	MBTI_INFP("MBTI_INFP","MBTI_INFP","MBTI_INFP"),
    	MBTI_ENFJ("MBTI_ENFJ","MBTI_ENFJ","MBTI_ENFJ"),
    	MBTI_ENFP("MBTI_ENFP","MBTI_ENFP","MBTI_ENFP"),
    	MBTI_ISTJ("MBTI_ISTJ","MBTI_ISTJ","MBTI_ISTJ"),
    	MBTI_ISFJ("MBTI_ISFJ","MBTI_ISFJ","MBTI_ISFJ"),
    	MBTI_ESTJ("MBTI_ESTJ","MBTI_ESTJ","MBTI_ESTJ"),
    	MBTI_ESFJ("MBTI_ESFJ","MBTI_ESFJ","MBTI_ESFJ"),
    	MBTI_ISTP("MBTI_ISTP","MBTI_ISTP","MBTI_ISTP"),
    	MBTI_ISFP("MBTI_ISFP","MBTI_ISFP","MBTI_ISFP"),
    	MBTI_ESTP("MBTI_ESTP","MBTI_ESTP","MBTI_ESTP"),
    	MBTI_ESFP("MBTI_ESFP","MBTI_ESFP","MBTI_ESFP"),
    	EMOTION_CHONGSHI("EMOTION_CHONGSHI","情绪_充实","情绪_充实"),
    	EMOTION_CHONGPEI("EMOTION_CHONGPEI","情绪_充沛","情绪_充沛"),
    	EMOTION_KANGFENG("EMOTION_KANGFENG","情绪_亢奋","情绪_亢奋"),
    	EMOTION_TONGKUAI("EMOTION_TONGKUAI","情绪_痛快","情绪_痛快"),
    	EMOTION_ZIHAO("EMOTION_ZIHAO","情绪_自豪","情绪_自豪"),
    	EMOTION_FANGSI("EMOTION_FANGSI","情绪_放肆","情绪_放肆"),
    	EMOTION_CHAOYUE("EMOTION_CHAOYUE","情绪_超越","情绪_超越"),
    	EMOTION_RECHEN("EMOTION_RECHEN","情绪_热忱","情绪_热忱"),
    	EMOTION_KEWANG("EMOTION_KEWANG","情绪_渴望","情绪_渴望"),
    	EMOTION_YUKUAI("EMOTION_YUKUAI","情绪_愉快","情绪_愉快"),
    	EMOTION_ZIZAI("EMOTION_ZIZAI","情绪_自在","情绪_自在"),
    	EMOTION_ZIXIN("EMOTION_ZIXIN","情绪_自信","情绪_自信"),
    	EMOTION_JINGYANG("EMOTION_JINGYANG","情绪_敬仰","情绪_敬仰"),
    	EMOTION_CHUNCUI("EMOTION_CHUNCUI","情绪_纯粹","情绪_纯粹"),
    	EMOTION_AIMU("EMOTION_AIMU","情绪_爱慕","情绪_爱慕"),
    	EMOTION_ZHUANZHU("EMOTION_ZHUANZHU","情绪_专注","情绪_专注"),
    	EMOTION_QIDAI("EMOTION_QIDAI","情绪_期待","情绪_期待"),
    	EMOTION_PINGHE("EMOTION_PINGHE","情绪_平和","情绪_平和"),
    	EMOTION_XINREN("EMOTION_XINREN","情绪_信任","情绪_信任"),
    	EMOTION_HEMU("EMOTION_HEMU","情绪_和睦","情绪_和睦"),
    	EMOTION_SHIRAN("EMOTION_SHIRAN","情绪_释然","情绪_释然"),
    	EMOTION_MANGLU("EMOTION_MANGLU","情绪_忙碌","情绪_忙碌"),
    	EMOTION_XINDONG("EMOTION_XINDONG","情绪_心动","情绪_心动"),
    	EMOTION_BUAN("EMOTION_BUAN","情绪_不安","情绪_不安"),
    	EMOTION_PINGJING("EMOTION_PINGJING","情绪_平静","情绪_平静"),
    	EMOTION_QINSONG("EMOTION_QINSONG","情绪_轻松","情绪_轻松"),
    	EMOTION_FADAI("EMOTION_FADAI","情绪_发呆","情绪_发呆"),
    	EMOTION_YOUXIAN("EMOTION_YOUXIAN","情绪_悠闲","情绪_悠闲"),
    	EMOTION_ZHAOJI("EMOTION_ZHAOJI","情绪_着急","情绪_着急"),
    	EMOTION_JINZHANG("EMOTION_JINZHANG","情绪_紧张","情绪_紧张"),
    	EMOTION_YUMEN("EMOTION_YUMEN","情绪_郁闷","情绪_郁闷"),
    	EMOTION_YANJUAN("EMOTION_YANJUAN","情绪_厌倦","情绪_厌倦"),
    	EMOTION_WULIAO("EMOTION_WULIAO","情绪_无聊","情绪_无聊"),
    	EMOTION_CHOUCHANG("EMOTION_CHOUCHANG","情绪_惆怅","情绪_惆怅"),
    	EMOTION_WUNAI("EMOTION_WUNAI","情绪_无奈","情绪_无奈"),
    	EMOTION_JIAOLV("EMOTION_JIAOLV","情绪_焦虑","情绪_焦虑"),
    	EMOTION_FANZAO("EMOTION_FANZAO","情绪_烦躁","情绪_烦躁"),
    	EMOTION_EXIN("EMOTION_EXIN","情绪_恶心","情绪_恶心"),
    	EMOTION_YOULV("EMOTION_YOULV","情绪_忧虑","情绪_忧虑"),
    	EMOTION_HAIPA("EMOTION_HAIPA","情绪_害怕","情绪_害怕"),
    	EMOTION_SHANGGAN("EMOTION_SHANGGAN","情绪_伤感","情绪_伤感"),
    	EMOTION_TUIFEI("EMOTION_TUIFEI","情绪_伤感","情绪_伤感"),
    	EMOTION_KUANGZAO("EMOTION_KUANGZAO","情绪_狂躁","情绪_狂躁"),
    	EMOTION_YANWU("EMOTION_YANWU","情绪_厌恶","情绪_厌恶"),
    	EMOTION_MAMU("EMOTION_MAMU","情绪_麻木","情绪_麻木"),
    	EMOTION_LENGMO("EMOTION_LENGMO","情绪_冷漠","情绪_冷漠"),
    	EMOTION_KONGJU("EMOTION_KONGJU","情绪_恐惧","情绪_恐惧"),
    	EMOTION_XIEQI("EMOTION_XIEQI","情绪_泄气","情绪_泄气"),
    	EMOTION_JUEWANG("EMOTION_JUEWANG","情绪_绝望","情绪_绝望"),
    	CAREER_ZUOYEDANG("CAREER_ZUOYEDANG","职业_作业党","职业_1"),
    	CAREER_MANONG("CAREER_MANONG","职业_码农","职业_2"),
    	CAREER_JIALIDUN("CAREER_JIALIDUN","职业_家里蹲","职业_3"),
    	CAREER_SHOUYIREN("CAREER_SHOUYIREN","职业_手艺人","职业_4"),
    	CAREER_BADAOZONGCAI("CAREER_BADAOZONGCAI","职业_霸道总裁","职业_5"),
    	CAREER_BIANJIGOU("CAREER_BIANJIGOU","职业_编辑狗","职业_6"),
    	CAREER_KAOZUICHIFAN("CAREER_KAOZUICHIFAN","职业_靠嘴吃饭","职业_7"),
    	CAREER_BAIYITIANSHI("CAREER_BAIYITIANSHI","职业_白衣天使","职业_8"),
    	CAREER_YUANDING("CAREER_YUANDING","职业_园丁","职业_9"),
    	CAREER_WEIRENMINFUWU("CAREER_WEIRENMINFUWU","职业_为人民服务","职业_10")
    	
    	
    	;
    	
    	public final String type;
    	public final String desc;
    	public final String value;
        
        UserRecInitType(String type,String desc, String value){
            this.type = type;
            this.desc = desc;
            this.value = value;
        }
        
        public static UserRecInitType getUserRecInitTypeByValue(String value){
        	if(StringUtils.isEmpty(value)){
        		return null;
        	}
        	for(UserRecInitType t : UserRecInitType.values()){
        		if(value.equals(t.value)){
        			return t;
        		}
        	}
        	return null;
        }
    }
    
    enum UserFirstActionType{
    	SPEAK_UPDATE("王国更新", 1),
    	SUBSCRIBED_KINGDOM("加入王国", 2);
    	
    	public final String name;
        public final int index;

        UserFirstActionType(String name,int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum TopicPriceChangeType{
    	RECHARGE("充值", 0),
    	WITHDRAWALS("提现", 1);
    	
    	public final String name;
        public final int index;
    	
        TopicPriceChangeType(String name,int index){
        	this.name = name;
            this.index = index;
        }
    }
    
    enum UserInvitationType{
    	INVITING("邀请", 1),
    	INVITED("被邀请", 2);
    	
    	public final String name;
        public final int index;
        
        UserInvitationType(String name,int index){
        	this.name = name;
            this.index = index;
        }
    }
    
    /**
     * 推送类型
     * @author pc340
     *
     */
    enum PushType{
    	XIAOMI("小米推送", 1),
    	HUAWEI("华为推送", 2);
    	
    	public final String name;
        public final int index;
        
        PushType(String name,int index){
        	this.name = name;
            this.index = index;
        }
    }
    
    /**
     * 当前用户是否可见
     *
     */
    enum CanViewStatus{
    	CAN_VIEW("对当前用户可见",1),
    	NOT_CAN_VIEW("对当前用户不可见",0);
    	
    	public final String name;
    	public final int index;
    	
    	CanViewStatus(String name,int index){
    		this.name = name;
    		this.index = index;
    	}
    }
}

    
