package com.me2me.common.web;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author: 马秀成
 * Date: 2017/3/9
 */
public class ResponseWapx<T extends Serializable> implements BaseEntity  {

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private boolean success;

    public ResponseWapx(String message , boolean success) {
        this.message = message;
        this.success = success;
    }

    public static ResponseWapx success(String message , boolean success){
        ResponseWapx response =  new ResponseWapx(message ,success);
        return response;

    }

}
