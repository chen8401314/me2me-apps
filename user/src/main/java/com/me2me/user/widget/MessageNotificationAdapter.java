package com.me2me.user.widget;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public class MessageNotificationAdapter {

    private MessageNotification target;

    public MessageNotificationAdapter(MessageNotification target){
        this.target = target;
    }

    public void notice(String content, long targetUid, long sourceUid){
        this.target.notice(content,targetUid,sourceUid);
    }



}
