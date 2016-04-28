package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :15:44
 */
public class ShowActivityRequest extends Request{

    @Getter
    @Setter
    private int page = 1;

    @Getter
    @Setter
    private int pageSize = 10;

    @Getter
    @Setter
    private String keyword;



}
