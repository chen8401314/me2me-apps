package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/5
 * Time :11:43
 */
public class UserNoticeRequest extends Request{

    @Getter
    @Setter
    private int sinceId;

    @Getter
    @Setter
    private int level;//0老版本的， 1一级列表， 2二级列表（系统信息）

}
