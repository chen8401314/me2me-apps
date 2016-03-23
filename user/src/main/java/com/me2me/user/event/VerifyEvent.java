package com.me2me.user.event;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Data
public class VerifyEvent {

    public VerifyEvent(String mobile,String verifyCode){
        this.mobile = mobile;
        this.verifyCode = verifyCode;
    }

    private String mobile;

    private String verifyCode;

}
