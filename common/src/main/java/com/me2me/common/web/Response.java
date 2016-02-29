package com.me2me.common.web;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 该接口是一个泛型类接口
 * 该接口必须实现序列化接口的实体类作为泛型实际参数
 * @param <T>
 */
public class Response<T extends Serializable>  {

    private static final String DEFAULT_MESSAGE_SUCCESS = "ok";

    private static final String DEFAULT_MESSAGE_FAILURE = "failure";

    private static final int DEDFAULT_CODE_SUCCESS = 200;

    private static final int DEFAULT_CODE_FAILURE = 500;

    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private int code;
    @Getter
    @Setter
    private T data;


    public Response(int code,String message,T data){
        this(code,message);
        this.data = data;
    }

    public Response(int code,String message){
        this.code = code;
        this.message = message;
    }


    /**
     * 系统默认成功
     * @param data
     * @return
     */
    public static <T extends BaseEntity> Response success(T data){
        return new Response(DEDFAULT_CODE_SUCCESS,DEFAULT_MESSAGE_SUCCESS,data);
    }

    /**
     * 系统默认失败
     * @param message
     * @return
     */
    public static Response failure(String message){
        return new Response(DEFAULT_CODE_FAILURE,DEFAULT_MESSAGE_FAILURE,message);
    }

    /**
     * 系统默认失败
     * @param message
     * @return
     */
    public static Response failure(int code,String message){
        return new Response(code,message);
    }

    /**
     * 请求成功
     * @param code
     * @param message
     * @return
     */
    public static <T extends BaseEntity> Response success(int code,String message){
        return new Response(code,message);
    }

    /**
     * 用户自定义成功
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static <T extends BaseEntity> Response success(int code,String message,T data){
        return new Response(code,message,data);
    }

    /**
     * 用户自定义失败
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static <T extends BaseEntity> Response failure(int code,String message,T data){
        return new Response(code,message,data);
    }


}
