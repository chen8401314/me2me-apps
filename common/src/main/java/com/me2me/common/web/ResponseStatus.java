package com.me2me.common.web;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */

/**
 * 系统返回信息描述枚举类
 */
public enum  ResponseStatus {



    USER_SING_UP_SUCCESS("用户注册成功","user sign up success",2000),

    USER_LOGIN_SUCCESS("用户登录成功","user login success",2001),

    USER_MODIFY_ENCRYPT_SUCCESS("用户密码修改成功","user modifyEncrypt success",2002),

    USER_MODIFY_HOBBY_SUCCESS("用户爱好修改成功","user modifyHobby success",2003),

    USER_VERIFY_GET_SUCCESS("验证码发送成功","user verify get success",2004),

    USER_VERIFY_CHECK_SUCCESS("验证码通过","user verify check success",2005),

    USER_MODIFY_AVATAR_SUCCESS("用户头像修改成功","user modify avatar success",2006),

    USER_MODIFY_NICK_NAME_SUCCESS("用昵称像修改成功","user modify nickName success",2007),

    USER_MODIFY_PROFILE_SUCCESS("用户资料修改成功","user modify profile success",2008),

    USER_ADD_FRIEND_SUCCESS("用户创建好友成功","user create friend success",2009),

    USER_REMOVE_FRIEND_SUCCESS("用户删除好友成功","user remove friend success",20010),

    USER_CREATE_GROUP_SUCCESS("用户创建群组成功","user create group success",20011),

    ADD_GROUP_MEMBER_SUCCESS("添加群成员成功","add group member success",20012),

    REMOVE_GROUP_MEMBER_SUCCESS("移除群成员成功","remove group member success",20013),

    PUBLISH_ARTICLE_SUCCESS("用户发表文章成功","user publish article success",20014),

    CONTENT_USER_LIKES_SUCCESS("用户点赞成功","content user likes success",20015),

    USER_FIND_ENCRYPT_SUCCESS("用户密码找回成功","user find encrypt success",20016),

    CONTENT_USER_CANCEL_LIKES_SUCCESS("用户取消点赞成功","content user cancel likes success",20017),

    PASTE_TAG_SUCCESS("打标签成功","paste tag success",20018),

    CONTENT_DELETE_SUCCESS("删除成功","content delete success",20019),

    CONTENT_TAGS_LIKES_SUCCESS("标签发布成功","content tags likes success",20020),

    CONTENT_GET_SUCCESS("获取内容详情","content get success",20021),

    GET_QINIU_TOKEN_SUCCESS("获取七牛token成功","get qiniu token success",20022),

    GET_USER_NOTICE_SUCCESS("获取用户提醒成功","get user notice success",20024),

    GET_USER_TIPS_SUCCESS("获取用户消息成功","get user tips success",20025),

    CLEAN_USER_TIPS_SUCCESS("清空用户消息成功","clean user tips success",20026),

    USER_CREATE_REPORT_SUCCESS("举报成功","user create report success",20027),

    USER_CREATE_LIVE_SUCCESS("直播创建成功","user create live success",20028),

    GET_LIVE_TIME_LINE_SUCCESS("获取直播信息成功","get live time line success",20029),

    USER_SPEAK_SUCCESS("用户发言成功","user speak success",20030),

    GET_USER_TAGS_SUCCESS("获取用户标签成功","get user tags success",20031),

    USER_TAGS_LIKES_SUCCESS("点赞成功","user tags likes success",20032),

    USER_TAGS_LIKES_CANCEL_SUCCESS("取消点赞成功","user tags likes cancel success",20033),

    USER_FINISH_LIVE_SUCCESS("直接结束成功","user finish live success",20034),

    GET_MY_LIVE_SUCCESS("获取我的直播列表成功","get my live success",20035),

    GET_LIVES_SUCCESS("获取直播列表成功","get lives success",20036),

    SET_LIVE_FAVORITE_SUCCESS("关注成功","set live favorite success",20037),

    CANCEL_LIVE_FAVORITE_SUCCESS("取消关注成功","cancel live favorite success",20038),

    USER_FOLLOW_SUCCESS("关注成功","user follow success",20039),

    USER_CANCEL_FOLLOW_SUCCESS("取消关注成功","user follow success",20040),

    SHOW_USER_FANS_LIST_SUCCESS("获取用户粉丝成功","show user fans success",20041),

    SHOW_USER_FOLLOW_LIST_SUCCESS("获取用户关注成功","show user follow success",20042),

    LIVE_REMOVE_SUCCESS("直播移除成功","live remove success",20043),

    LIVE_SIGN_OUT_SUCCESS("直播退出成功","live sign out success",20044),

    CONTENT_IS_PUBLIC_MODIFY_SUCCESS("内容权限修改成功","content is public modify success",20045),

    CONTENT_REVIEW_SUCCESS("评论成功","content review success",20046),

    VERSION_UPDATE_SUCCESS("版本已更新","version update success",20047),













    USER_PASSWORD_ERROR("用户密码错误","user password error",5000),

    USER_NOT_EXISTS("该用户不存在","user not exists",5001),

    USER_VERIFY_GET_ERROR("验证码发送失败","user verify get error",5002),

    USER_VERIFY_CHECK_ERROR("验证码不正确","user verify check error",5003),

    USER_VERIFY_ERROR("验证码发送次数超限","user verify error",5004),

    USER_MODIFY_ENCRYPT_PASSWORD_NOT_SAME_ERROR("两次密码输入不一致"," user the two passwords are not the same",5005),

    USER_MODIFY_USER_PROFILE_ERROR("用户信息修改错误","user modify user profile error",5006),

    USER_FIND_ENCRYPT_PASSWORD_NOT_SAME_ERROR("用户找回密码两次密码不一致","user find encrypt password not same error",5007),

    USER_MOBILE_DUPLICATE("手机号码已经注册过了","user mobile duplicate",5008),

    DATA_DOES_NOT_EXIST("请求的数据不存在","data does not exist ",5009),

    DATA_IS_DELETE("请求的数据已删除","data is delete ",50010),

    USER_MOBILE_NO_SIGN_UP("手机号码还未注册","user mobile no sign up",50011),

    FINISH_LIVE_NO_POWER("您没有权限或者直播已经结束","finish live no power",50012),

    USER_LIVE_IS_OVER("直接已经结束","user live is over",50013),

    USER_ADD_FRIEND_ERROR("不能自己添加自己为好友","can't add yourself",50014),

    CAN_NOT_DUPLICATE_FOLLOW("不能重复关注","can't duplicate follow",50015),

    LIVE_REMOVE_IS_NOT_OVER("直接还未结束，不能移除"," live is not over",50016),

    LIVE_REMOVE_IS_NOT_YOURS("您不是直播创建人，不能移除","live is not yours",50017),

    LIVE_OWNER_CAN_NOT_SIGN_OUT("自己创建的直播不能退出","live owner can not sign out",50018),

    LIVE_IS_NOT_EXIST("直播不存在","live is not exist",50019),

    LIVE_IS_NOT_SIGN_IN("您未参与此直播","live is not sign in",50020),

    CONTENT_IS_NOT_EXIST("修改的内容不存在","content is not exist",50021),

    CONTENT_IS_NOT_YOURS("该内容你无权修改","content is not yours",50022),

    NICK_NAME_REQUIRE_UNIQUE("用户昵称必须唯一","nick name require unique",50023),

    CONTENT_LIKES_ERROR("用户点赞内容不存在","content likes error",50024),




















    ILLEGAL_REQUEST("非法的请求参数","illegal request",50099);

    public final String message;

    public final String englishMessage;

    public final int status;

    ResponseStatus(String message,String englishMessage,int status){
        this.message = message;
        this.englishMessage = englishMessage;
        this.status = status;
    }

}
