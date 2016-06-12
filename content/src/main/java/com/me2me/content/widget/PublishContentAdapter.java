package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.ContentDto;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
public class PublishContentAdapter implements InitializingBean {

    private Publish target;

    @Setter
    private int type;

    public Response execute(ContentDto contentDto){
        try {
            afterPropertiesSet();
            return target.publish(contentDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status,ResponseStatus.ILLEGAL_REQUEST.message);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.target = PublishFactory.getInstance(type);
    }
}
