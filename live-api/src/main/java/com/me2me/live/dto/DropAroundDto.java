package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: 马秀成
 * Date: 2017/3/17
 */
@Data
public class DropAroundDto implements BaseEntity {

    private long topicId;

    private long cid;

    private int topicType;

    private int internalStatus;//核心圈身份 0圈外 2核心圈

    private String trackContent;//足迹内容

    private String trackImage;

}
