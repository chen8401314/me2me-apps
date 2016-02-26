package com.me2me.common.web;

/**
 * Created by pc308 on 2016/1/11.
 */
public interface Specification {
    /**
     * 用户状态
     */
    public enum UserStatus{
        NORMAL("正常",0),
        LOCK("锁定",1),
        STOP("禁用",2);
        public String name;
        public int index;
        UserStatus(String name,int index){
            this.name = name;
            this.index = index;
        }
    }




}
