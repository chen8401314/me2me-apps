package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
public class ForwardPublish extends AbstractPublish implements Publish {

    public Response publish(ContentDto contentDto){
        //// TODO: 2016/6/6 转发逻辑待定
        return null;
    }
}
