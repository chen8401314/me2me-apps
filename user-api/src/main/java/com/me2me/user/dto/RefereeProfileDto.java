package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/6
 * Time :14:54
 */
@Data
public class RefereeProfileDto implements BaseEntity{

    private long uid;

    private String avatar;
    
    private String avatarFrame;

    private String nickName;

    private String introduced;

    private int contentCount;

    private int refereeCount;

    private int fansCount;

    private String regUrl;

    private String qrCodeUrl;

    private int v_lv;

    private int level;
}
