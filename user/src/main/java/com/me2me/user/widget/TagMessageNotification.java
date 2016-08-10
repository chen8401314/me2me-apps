package com.me2me.user.widget;

import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/6
 * Time :17:37
 */
@Component
public class TagMessageNotification extends AbstractMessageNotification implements MessageNotification {

    @Override
    public void notice(String title, long targetUid, long sourceUid,int type) {
        String content = TEMPLATE_TAG.replace("${title}",title);
        super.notice(content,targetUid,sourceUid,type);
    }
}
