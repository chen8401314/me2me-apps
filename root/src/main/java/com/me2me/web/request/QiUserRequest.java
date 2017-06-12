package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by 马秀成 on 2016/12/5.
 */
public class QiUserRequest {

    @Getter
    @Setter
    private long uid;

    @Getter
    @Setter
    private String mobile;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Integer sex;

    @Getter
    @Setter
    private Integer age;

    @Getter
    @Setter
    private String channel;

    @Getter
    @Setter
    private Long activityId;

    @Getter
    @Setter
    private Date createTime;

    @Getter
    @Setter
    private Integer status;

    @Getter
    @Setter
    private String auditDesc;

    @Getter
    @Setter
    private Long liveness;

    @Getter
    @Setter
    private String verifyCode;

    @Getter
    @Setter
    private int channelAdapter;

    @Getter
    @Setter
    private String topicName;

    @Getter
    @Setter
    private String nickName;

    @Getter
    @Setter
    private long targetUid;

    @Getter
    @Setter
    private int type;

    @Getter
    @Setter
    private int applyId;


    @Getter
    @Setter
    private int pageNum;

    @Getter
    @Setter
    private int pageSize;

    @Getter
    @Setter
    private int operaStatus;//操作

    @Getter
    @Setter
    private String date;

}
