package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import com.me2me.user.dto.*;
import com.me2me.user.dto.ModifyUserCoinDto;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/12
 * Time :12:04
 */
@Data
public class SpeakDto implements BaseEntity{

    private long fragmentId;

    private int contentType;

    private int type;

    private String fragment;

    private String fragmentImage;

    private long uid;

    private long topicId;

    private long topId;

    private long bottomId;

    private long atUid;

    private int mode;

    private int source;
    
    private int score;

    private String extra;

    private int v_lv;

    private int upgrade ;

    private int currentLevel;

    private long quotationInfoId;

    private int isFirstUpdate;
}
