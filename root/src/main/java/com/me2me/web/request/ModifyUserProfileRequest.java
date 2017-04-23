package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :11:31
 */
public class ModifyUserProfileRequest extends Request{

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private Integer gender;

    @Getter
    @Setter
    private String nickName;

    @Getter
    @Setter
    private Long yearsId;

    @Getter
    @Setter
    private String avatar;

    @Getter
    @Setter
    private String birthday;

    @Getter
    @Setter
    private String hobby;

    @Getter
    @Setter
    private String introduced;
    
    @Getter
    @Setter
    private int likeGender;		// 性取向
    @Getter
    @Setter
    private int ageGroup;		// 年龄段
    @Getter
    @Setter
    private int occupation;		// 职业 

}
