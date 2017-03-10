package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/10
 * Time :15:24
 */
public class VersionControlRequest extends Request{

    @Getter
    @Setter
    private String version;

    @Getter
    @Setter
    private int platform;

    @Getter
    @Setter
    private String device;

    @Getter
    @Setter
    private String params;

}
