package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Data
public class UserSignUpDto implements BaseEntity {

    private String mobile ;

    private String encrypt ;

    private int gander ;

    private int star;

    private String verifyCode;

    

}
