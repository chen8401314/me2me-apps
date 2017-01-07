package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Author: 马秀成
 * Date: 2017/1/7
 */
@Data
public class LightBoxDto implements BaseEntity {

    private Long id;

    private String image;

    private String mainText;

    private String secondaryText;

    private Long mainTone;

    private String linkUrl;

    private Date startTime;

    private Date endTime;

    private Integer status;

}
