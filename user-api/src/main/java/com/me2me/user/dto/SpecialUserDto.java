package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/23
 * Time :17:33
 */
@Data
public class SpecialUserDto implements BaseEntity {

    private String userName;

    private String realName;

    private String sex;

    private String birthday;

    private String interests;

    private String mobilePhone;

    private String identityCards;

    private String email;
}
