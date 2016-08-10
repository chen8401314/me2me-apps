package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :17:00
 */
@Data
public class ModifyUserHobbyDto implements BaseEntity{


    private long uid ;

    private String userName;

    private String hobby;
}
