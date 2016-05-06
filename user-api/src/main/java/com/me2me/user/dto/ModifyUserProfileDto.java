package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/1
 * Time :15:34
 */
@Data
public class ModifyUserProfileDto implements BaseEntity{

    private int action;

    private long uid;

    private String userName;

    private String nickName;

    private int gender;

    private String avatar;

    private long yearsId;

    private long startId;

    private long socialClass;

    private long industry;

    private long marriageStatus;

    private long bearStatus ;

    private String birthday;

    private String hobby;
}
