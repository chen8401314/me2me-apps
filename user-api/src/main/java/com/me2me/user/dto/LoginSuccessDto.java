package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Data
public class LoginSuccessDto implements BaseEntity {

    private long uid;

    private String userName;

    private String token;

    private String nickName;

    private int gender;

    private String meNumber;

    private String avatar;

    private long yearId;

    private int followedCount;

    private int fansCount;

    private String introduced;

    private int v_lv;

    private int isClientLogin;

    private int isNew;
    
    private int level;
}
