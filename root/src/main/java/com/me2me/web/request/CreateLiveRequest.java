package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/11
 * Time :18:27
 */
public class CreateLiveRequest  extends Request{

    @Getter
    @Setter
    private long uid;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String liveImage;
}
