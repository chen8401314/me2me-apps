package com.me2me.user.widget;

import com.me2me.common.web.Specification;
import com.me2me.core.SpringContextHolder;

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
            instance = SpringContextHolder.getBean(LiveTagMessageNotification.class);
            //日记被贴标
        } else if (type == Specification.PushMessageType.TAG.index) {
            instance = SpringContextHolder.getBean(TagMessageNotification.class);
            //直播回复
        } else if (type == Specification.PushMessageType.LIVE_REVIEW.index) {
            instance = SpringContextHolder.getBean(LiveReviewMessageNotification.class);
            //日记被评论
        } else if (type == Specification.PushMessageType.REVIEW.index) {
            instance = SpringContextHolder.getBean(ReviewMessageNotification.class);
            //直播置热
        } else if (type == Specification.PushMessageType.LIVE_HOTTEST.index) {
            instance = SpringContextHolder.getBean(LiveHottestMessageNotification.class);
            //UGC置热
        } else if (type == Specification.PushMessageType.HOTTEST.index) {
            instance = SpringContextHolder.getBean(HottestMessageNotification.class);
            //被人关注
        } else if (type == Specification.PushMessageType.FOLLOW.index) {
            instance = SpringContextHolder.getBean(FollowMessageNotification.class);
            //收藏的直播主播更新了
        } else if (type == Specification.PushMessageType.UPDATE.index) {
            instance = SpringContextHolder.getBean(UpdateMessageNotification.class);
            //你关注的直播有了新的更新了
        } else if (type == Specification.PushMessageType.LIVE.index) {
            instance = SpringContextHolder.getBean(LiveMessageNotification.class);
        } else if (type == Specification.PushMessageType.AT.index) {
            instance = SpringContextHolder.getBean(AtMessageNotification.class);
        }
        return instance;
    }
}
