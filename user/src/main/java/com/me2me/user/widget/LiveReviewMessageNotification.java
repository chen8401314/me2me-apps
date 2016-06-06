package com.me2me.user.widget;

import com.me2me.user.model.UserProfile;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/6
 * Time :17:39
 */
public class LiveReviewMessageNotification extends AbstractMessageNotification implements MessageNotification {

    @Override
    public void notice(String title, long targetUid, long sourceUid) {
        UserProfile userProfile = userService.getUserProfileByUid(sourceUid);
        String content = TEMPLATE_LIVE_REVIEW.replace("${title}",title).replace("${nickName}",userProfile.getNickName());
        super.notice(content,targetUid,sourceUid);
    }
}
