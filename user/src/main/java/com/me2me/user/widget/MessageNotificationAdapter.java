package com.me2me.user.widget;

import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
public class MessageNotificationAdapter {

    @Setter
    private int type;


    public void notice(String content, long targetUid, long sourceUid,int type){
        MessageNotificationFactory.getInstance(type).notice(content,targetUid,sourceUid,type);
    }

}
