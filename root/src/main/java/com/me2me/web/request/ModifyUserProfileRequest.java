package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :11:31
 */
public class ModifyUserProfileRequest {

    @Getter
    @Setter
    private int action;

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private int gander;


}
