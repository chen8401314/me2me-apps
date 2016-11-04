package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pc41 on 2016/9/13.
 */
@Data
public class UserNickNameDto implements BaseEntity {

    private String nickName;

    private String openid;

    private String unionId;

    private int thirdPartType;

}
