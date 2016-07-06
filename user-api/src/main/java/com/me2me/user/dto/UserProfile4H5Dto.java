package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/5
 * Time :11:34
 */
@Data
public class UserProfile4H5Dto implements BaseEntity {

    private long uid;

    private String nickName;

    private String avatar;

    private String summary;


}
