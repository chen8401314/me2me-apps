package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/26.
 */
public class ShowContentsRequest extends Request {

    @Getter
    @Setter
    // 0 pgc 1 ugc
    private int type;

    @Getter
    @Setter
    private int page = 1;

    @Getter
    @Setter
    private int pageSize = 10;

    @Getter
    @Setter
    private String keyword;

    @Getter
    @Setter
    // 0 广场 1 精选
    private int articleType;
}
