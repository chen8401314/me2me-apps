package com.me2me.user.widget;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public interface MessageNotification {

    static final String TEMPLATE_LIVE_TAG = "你的直播：${title}收到了1个新感受";
    static final String TEMPLATE_TAG = "你的日记：${title}收到了1个新感受";
    static final String TEMPLATE_LIVE_REVIEW = "${nickName}评论了你的直播：${title}";
    static final String TEMPLATE_REVIEW = "${nickName}评论了你的日记：${title}";
    static final String TEMPLATE_LIVE_HOTTEST = "你的直播：${title}上热点啦！";
    static final String TEMPLATE_HOTTEST = "你的日记：${title}上热点啦！";
    static final String TEMPLATE_FOLLOW= "${nickName}关注了你";
    static final String TEMPLATE_UPDATES = "你订阅的直播：${title}更新了";
    static final String TEMPLATE_LIVE = "你关注的主播${nickName}有了新直播：${title}";
    static final String TEMPLATE_AT = "${nickName}@了你";


    /**
     * 消息提醒接口
     */
    void notice(String title,long targetUid,long sourceUid,int type);

}
