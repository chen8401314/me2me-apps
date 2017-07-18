package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: chenxiang
 * Date: 2017/7/18
 */
@Data
public class GuideInfoDto implements BaseEntity {

    private long uid;

    private String nickName;

    private String avatar;


}
