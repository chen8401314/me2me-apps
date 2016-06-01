package com.me2me.admin.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/30.
 */
public class ShowItemRequest extends Request {

    @Getter
    @Setter
    private long type;

    @Getter
    @Setter
    private int currentPage;

}
