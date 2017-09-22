package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author zhangjiwei
 * @date Sep 22, 2017
 */
public class WxJsApiTicketRequest extends Request{

    @Setter
    @Getter
    private String appId;
    
    @Setter
    @Getter
    private String appSecert;
}
