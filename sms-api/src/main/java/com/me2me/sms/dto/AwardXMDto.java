package com.me2me.sms.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by 马秀成 on 2016/10/21.
 */
@Data
public class AwardXMDto implements BaseEntity {

    private String nickName;

    private String awardName;

    private String mobile;

    private List mobileList = Lists.newArrayList();

}
