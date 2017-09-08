package com.me2me.sns.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :15:05
 */
@Data
public class SnsCircleDto implements BaseEntity {

    private long uid;

    private String nickName;

    private String avatar;
    
    private String avatarFrame;

    private String introduced;

    private int internalStatus;

    private int isFollowed;

    private int isFollowMe;

    private int v_lv;

    private int level;

}
