package com.me2me.user.widget;

import com.me2me.common.web.Specification;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public class MessageNotificationFactory {

    public static MessageNotification getInstance(int type){
        MessageNotification instance = null;
        //直播贴标
            if (type == Specification.PushMessageType.LIVE_TAG.index) {
                new LiveTagMessageNotification();
                //日记被贴标
            } else if (type == Specification.PushMessageType.TAG.index) {
               // pushMessageDto.setContent("你的日记:" + title + "收到了1个新感受");
                new TagMessageNotification();
                //直播回复
            } else if (type == Specification.PushMessageType.LIVE_REVIEW.index) {
                new LiveReviewMessageNotification();
               // UserProfile userProfile = getUserProfileByUid(sourceUid);
               // pushMessageDto.setContent(userProfile.getNickName() + "评论了你的直播:" + title);
                //日记被评论
            } else if (type == Specification.PushMessageType.REVIEW.index) {

               // UserProfile userProfile = getUserProfileByUid(sourceUid);
               // pushMessageDto.setContent(userProfile.getNickName() + "评论了你的日记:" + title);
                //直播置热
            } else if (type == Specification.PushMessageType.LIVE_HOTTEST.index) {
               // pushMessageDto.setContent("你的直播：" + title + "上热点啦！");
                //UGC置热
            } else if (type == Specification.PushMessageType.HOTTEST.index) {
               // pushMessageDto.setContent("你的日记：" + title + "上热点啦！");
                //被人关注
            } else if (type == Specification.PushMessageType.FOLLOW.index) {
//                UserProfile userProfile = getUserProfileByUid(sourceUid);
//                pushMessageDto.setContent(userProfile.getNickName() + "关注了你");
                //收藏的直播主播更新了
            } else if (type == Specification.PushMessageType.UPDATE.index) {
//                pushMessageDto.setContent("你订阅的直播：" + title + "更新了");
                //你关注的直播有了新的更新了
            } else if (type == Specification.PushMessageType.LIVE.index) {
//                UserProfile userProfile = getUserProfileByUid(sourceUid);
//                pushMessageDto.setContent("你关注的主播" + userProfile.getNickName() + "有了新直播：" + title);
            }
        return instance;
    }
}
