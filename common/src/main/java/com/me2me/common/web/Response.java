package com.me2me.common.web;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */

import com.me2me.common.security.SecurityUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 该接口是一个泛型类接口
 * 该接口必须实现序列化接口的实体类作为泛型实际参数
 * @param <T>
 */
public class Response<T extends Serializable> implements BaseEntity  {

    private static final String DEFAULT_MESSAGE_SUCCESS = "ok";

    private static final String DEFAULT_MESSAGE_FAILURE = "failure";

    private static final int DEFAULT_CODE_SUCCESS = 200;

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
    @Getter
    @Setter
    private String accessToken;


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
        Response response =  new Response(DEFAULT_CODE_SUCCESS,DEFAULT_MESSAGE_SUCCESS,data);
        response.refreshAccessToken();
        return response;
    }

    /**
     * 系统默认成功
     * @return
     */
    public static Response success(){
        Response response =  new Response(DEFAULT_CODE_SUCCESS,DEFAULT_MESSAGE_SUCCESS);
        response.refreshAccessToken();
        return response;
    }

    /**
     * 系统默认失败 ,失败并设置data.
     * @param data 设置data.
     * @return
     */
    public static Response failure(String data){
        Response response =  new Response(DEFAULT_CODE_FAILURE,DEFAULT_MESSAGE_FAILURE,data);
        response.refreshAccessToken();
        return response;
    }

    /**
     * 系统默认失败
     * @param message
     * @return
     */
    public static Response failure(int code,String message){
        Response response =  new Response(code,message);
        response.refreshAccessToken();
        return response;

    }

    /**
     * 请求成功
     * @param code
     * @param message
     * @return
     */
    public static Response success(int code,String message){
        Response response =  new Response(code,message);
        response.refreshAccessToken();
        return response;
    }

    /**
     * 用户自定义成功
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static <T extends BaseEntity> Response success(int code,String message,T data){
        Response response =  new Response(code,message,data);
        response.refreshAccessToken();
        return response;
    }

    /**
     * 用户自定义失败
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static <T extends BaseEntity> Response failure(int code,String message,T data){

        Response response =  new Response(code,message,data);
        response.refreshAccessToken();
        return response;
    }

    public void refreshAccessToken(){
        this.accessToken = SecurityUtils.getAccessToken();
    }


}
