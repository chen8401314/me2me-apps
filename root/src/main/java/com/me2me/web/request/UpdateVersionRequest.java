package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/10
 * Time :16:36
 */
public class UpdateVersionRequest extends Request{

    @Getter
    @Setter
    private String version;

    @Getter
    @Setter
    private Integer platform;

    @Getter
    @Setter
    private String updateDescription;

    @Getter
    @Setter
    private String updateUrl;
}
