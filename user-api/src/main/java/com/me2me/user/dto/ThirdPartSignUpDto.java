package com.me2me.user.dto;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/9/12.
 */
@Data
public class ThirdPartSignUpDto extends UserSignUpDto {

    private String thirdPartOpenId;

    private String thirdPartToken;

    private String avatar;

}
