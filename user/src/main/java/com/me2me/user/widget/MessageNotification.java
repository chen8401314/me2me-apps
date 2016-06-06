package com.me2me.user.widget;

import com.me2me.sms.dto.PushMessageAndroidDto;
import com.me2me.sms.dto.PushMessageDto;
import com.me2me.sms.dto.PushMessageIosDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public interface MessageNotification {

    /**
     * 消息提醒接口
     */
    void notice(String title,long targetUid,long sourceUid);

}
