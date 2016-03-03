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

    /**
     * 修改用户信息Action
     */
    public enum ModifyUserProfileAction{

        AVATAR("修改头像",0),

        NICKNAME("修改昵称",1),

        USER_PROFILE("修改信息",2);

        public String name;
        public int index;
        ModifyUserProfileAction(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


    /**
     * 验证码枚举
     */
    public enum VerifyAction{

        GET("获取验证码",0),

        CHECK("验证验证码",1);

        public String name;
        public int index;
        VerifyAction(String name,int index){
            this.name = name;
            this.index = index;
        }
    }

    /**
     * 基础数据枚举
     */
    public enum UserBasicData{

        YEARS("年代情怀",2),

        START("星座",1),

        SOCIAL_CLASS("社会阶层",4),

        INDUSTRY("行业",3),

        MARRIAGE_STATUS("婚恋状态",5),

        BEAR_STATUS("生育状态",6);

        public String name;
        public int index;
        UserBasicData(String name,int index){
            this.name = name;
            this.index = index;
        }
    }


}
