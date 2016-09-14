package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/13.
 */
@Data
public class UserAccountBindStatusDto implements BaseEntity {


    private int type;

    private String name;

    private int status;

    public UserAccountBindStatusDto(){

    }

    public UserAccountBindStatusDto(int type,String name,int status){
        this.type = type;
        this.name = name;
        this.status = status;
    }

}
