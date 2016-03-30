package com.me2me.admin.web.request;

import com.me2me.common.web.Request;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/30.
 */
public class ContentForwardRequest extends Request {


    private long id;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
