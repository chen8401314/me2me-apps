package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/1
 * Time :15:34
 */
@Data
public class ModifyUserProfileDto implements BaseEntity{


    private long uid;

    private String userName;

    private String nickName;

    private Integer gender;

    private String avatar;

    private Long yearsId;

    private String birthday;

    private String hobby;

    private String introduced;
    
    private int likeGender;		// 性取向
    private int ageGroup;		// 年龄段
    private int occupation;		// 职业 
    
    
}
