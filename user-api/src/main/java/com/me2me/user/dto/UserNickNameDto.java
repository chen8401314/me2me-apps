package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc41 on 2016/9/13.
 */
@Data
public class UserNickNameDto implements BaseEntity {

    private String nickName;

    private String openid;
}
