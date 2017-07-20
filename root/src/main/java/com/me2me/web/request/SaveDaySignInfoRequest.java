package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017/7/20
 */
public class SaveDaySignInfoRequest extends Request {

    @Getter
    @Setter
    private String image;

    @Getter
    @Setter
    private int source;

    @Getter
    @Setter
    private String extra;
    
    @Getter
    @Setter
    private String uids;
    
    @Getter
    @Setter
    private String quotationIds;
}
