package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by 马秀成 on 2016/12/8.
 */
@Data
public class AtopicInfoDto implements BaseEntity {

    private int total;

    private int isAlone;//是否单身 0单身 1不是单身

    private List<BlurSearchDto> blurSearchList = Lists.newArrayList();

}
