package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/7/14 0014.
 */
public class SetEncryptRequest extends Request{

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private String encrypt;

}
