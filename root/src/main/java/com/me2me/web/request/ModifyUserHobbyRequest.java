package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :12:43
 */
public class ModifyUserHobbyRequest extends Request{

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private String hobby;

}
