package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by 马秀成 on 2016/12/5.
 */
@Data
public class QiUserDto implements BaseEntity {

    private long uid;

    private String mobile;

    private String name;

    private Integer sex;

    private Integer age;

    private String channel;

    private Long activityId;

    private Integer status;

    private String auditDesc;

    private Long liveness;

    private String verifyCode;

    private int channelAdapter;

}
