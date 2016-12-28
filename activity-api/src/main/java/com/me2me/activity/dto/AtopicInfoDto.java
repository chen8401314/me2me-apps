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

    private int status; //0未报名过 1报名过

    private List<BlurSearchDto> blurSearchList = Lists.newArrayList();

}
