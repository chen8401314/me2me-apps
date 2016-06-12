package com.me2me.user.widget;

import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
public class MessageNotificationAdapter implements InitializingBean {

    private MessageNotification target;

    @Setter
    private int type;


    public void notice(String content, long targetUid, long sourceUid){
        this.target.notice(content,targetUid,sourceUid);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.target = MessageNotificationFactory.getInstance(type);
    }
}
