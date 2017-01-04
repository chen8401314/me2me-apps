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

        IMAGE("图片",1),

        LINK("浏览器链接", 17),
        
        KINGDOM("王国链接", 18);

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
        
        SYSTEM("系统", 100);
        
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

        SNS_CIRCLE("王国成员",3);

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
    	SPECIAL("特殊王国", 1);
    	
    	public final String name;
        public final int index;
        
        KingdomType(String name, int index){
            this.name = name;
            this.index = index;
        }
    }
    
    enum ActivityKingdomType{
    	SINGLEKING("单人王国", 1),
    	DOUBLEKING("双人王国", 2);
    	
    	public final String name;
        public final int index;
        
        ActivityKingdomType(String name, int index){
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
    	
    	
    	;
    	
    	public final String key;
        public final String desc;
        
        ActivityMiliDataKey(String key, String desc){
            this.key = key;
            this.desc = desc;
        }
    }
    
    enum LinkPushType{
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
    	;
    	
    	public final String message;
    	public final String linkUrl;
    	
    	LinkPushType(String message, String linkUrl){
    		this.message = message;
    		this.linkUrl = linkUrl;
    	}
    }

}
