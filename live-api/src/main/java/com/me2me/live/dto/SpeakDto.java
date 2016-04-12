package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

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
}
