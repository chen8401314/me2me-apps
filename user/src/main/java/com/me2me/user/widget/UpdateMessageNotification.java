package com.me2me.user.widget;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/6
 * Time :17:39
 */
public class UpdateMessageNotification extends AbstractMessageNotification implements MessageNotification {

    @Override
    public void notice(String title, long targetUid, long sourceUid) {
        String content = "xxx";
        super.notice(content,targetUid,sourceUid);
    }
}
