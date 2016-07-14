package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/14
 * Time :14:25
 */
public class PromoterRequest extends Request {

    @Getter
    @Setter
    private String startDate;

    @Getter
    @Setter
    private String endDate;

    @Getter
    @Setter
    private String nickName;
}
