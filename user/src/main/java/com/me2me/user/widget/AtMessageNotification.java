package com.me2me.user.widget;

import com.me2me.user.model.UserProfile;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/30
 * Time :18:03
 */
@Component
public class AtMessageNotification  extends AbstractMessageNotification implements MessageNotification{

    @Override
    public void notice(String title, long targetUid, long sourceUid,int type) {
        UserProfile userProfile = userService.getUserProfileByUid(sourceUid);
        String content = TEMPLATE_AT.replace("${nickName}",userProfile.getNickName());
        super.notice(content,targetUid,sourceUid,type);
    }
}
