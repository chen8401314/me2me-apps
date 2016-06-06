package com.me2me.content.widget;

import com.me2me.common.web.Response;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
public class PublishContentAdapter {

    @Setter
    private Publish target;

    public PublishContentAdapter(Publish publish) {
        this.target = publish;
    }

    public Response execute(){
        return target.publish();
    }
}
