package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: 马秀成
 * Date: 2017/3/28
 */
@Data
public class DaoDaoDto implements BaseEntity {

    private int appid;

    private String idfa;

    private String ip;

    private String callback;

}
