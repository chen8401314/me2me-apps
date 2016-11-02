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

    SET_LIVE_FAVORITE_SUCCESS("订阅成功","set live favorite success",20037),

    CANCEL_LIVE_FAVORITE_SUCCESS("取消订阅成功","cancel live favorite success",20038),

    USER_FOLLOW_SUCCESS("关注成功","user follow success",20039),

    USER_CANCEL_FOLLOW_SUCCESS("取消关注成功","user follow success",20040),

    SHOW_USER_FANS_LIST_SUCCESS("获取用户粉丝成功","show user fans success",20041),

    SHOW_USER_FOLLOW_LIST_SUCCESS("获取用户关注成功","show user follow success",20042),

    LIVE_REMOVE_SUCCESS("直播移除成功","live remove success",20043),

    LIVE_SIGN_OUT_SUCCESS("直播退出成功","live sign out success",20044),

    CONTENT_IS_PUBLIC_MODIFY_SUCCESS("内容权限修改成功","content is public modify success",20045),

    CONTENT_REVIEW_SUCCESS("评论成功","content review success",20046),

    VERSION_UPDATE_SUCCESS("版本已更新","version update success",20047),

    CONTENT_USER_LIKES_ALREADY("不能重复点赞","content user likes already",20048),

    CONTENT_USER_LIKES_CANCEL_ALREADY("不能重复取消点赞","content user likes cancel already",20049),

    SET_USER_EXCELLENT_SUCCESS("大V设置成功","set user excellent success",20050),

    LOGOUT_SUCCESS("退出成功","logout success",20051),

    FORWARD_SUCCESS("转发成功","user publish article success",20052),

    GET_LIVE_COVER_SUCCESS("直播封面获取成功","get live cover success",20053),

    GET_LIVE_BARRAGE_SUCCESS("直播弹幕获取成功","get live barrage success",20054),

    SHOW_MEMBER_CONSOLE_SUCCESS("获取成员列表成功","show member console success",20055),

    SHOW_MEMBERS_SUCCESS("获邀请列表成功","show members success",20056),

    MODIFY_CIRCLE_SUCCESS("修改社交关系成功","modify circle success",20057),

    QRCODE_SUCCESS("获取二维码成功","qrcode success",20058),

    HIGH_QUALITY_CONTENT_SUCCESS("置热成功","high quality content success",20059),

    HIGH_QUALITY_CONTENT_CANCEL_SUCCESS("取消置热成功","high quality content cancel success",20060),

    HIGH_QUALITY_CONTENT_YET("取消置热成功","high quality content YET",20061),

    USER_EXISTS("该账户已经注册过了","user exists",20062),

    GET_ACTIVITY_MODEL_SUCCESS("获取广告内容成功","get activity model success",20063),

    USER_NICKNAME_EXISTS("用户昵称已存在，请重新输入","user nickname exists",20064),

    USER_NICKNAME_DONT_EXISTS("该用户昵称不存在，可以注册","user nickname dont exists",20065),

    OPENID_DONT_EXISTS("该OPENID不存在，请上传头像","openid dont exists",20066),

    MOBILE_BIND_EXISTS("手机号已被注册或绑定过其他账号，请换号之后重试","mobile bind exists",20067),

    WEIXIN_BIND_EXISTS("该微信号已注册或绑定过其他账号,请换号之后重试","weixin bind exists",20068),

    QQ_BIND_EXISTS("该QQ号已注册或绑定过其他账号,请换号之后重试","weixin bind exists",20069),

    WEIBO_BIND_EXISTS("该微博微信号已注册或绑定过其他账号,请换号之后重试","weixin bind exists",20070),

    USER_V_EXISTS("该用户已经是大V用户，请重新选择","user v exists",20071),

    TOPIC_FRAGMENT_DELETE_SUCCESS("王国发言内容删除成功","topic fragment delete success",20072),

    RUN_OUT_OF_LOTTERY("抽奖次数已用完,分享内容可获得额外一次抽奖机会","run out of lottery",20073),

    AWARD_IS_END("活动已经结束","award is end",20074),

    AWARD_ISNOT_START("活动还未开始","award is not start",20075),

    AWARD_ISNOT_EXISTS("该活动不存在或已停用","award is not exists",20076),

    APPEASE_NOT_AWARD_TERM("不满足抽奖条件，请参阅活动规则","appease not award term",20077),

    AWARD_SHARE_SUCCESS("分享成功，多一次抽奖机会","award share success",20078),

    APPEASE_AWARD_TERM("满足抽奖条件，可以参加活动","appease award term",20079),

    USER_AWARD_INFO("获取用户中奖信息成功","user award info",20080),

    USER_AWARD_NOT_INFO("该用户没有中奖信息","user award not info",20081),

    EDIT_TOPIC_FRAGMENT_SUCCESS("编辑王国发言内容成功","edit topic fragment success",20082),

    GET_LIVE_DETAIL_SUCCESS("获取王国详情成功","get live detail success",20083),

    GET_REDDOT_SUCCESS("获取红点成功","get reddot success",20084),

    AWARD_MESSAGE_SUCCESS("中奖短信发送成功","award message success",20085),

    THIRDPARTUSER_EXISTS("第三方账户已存在","thirdPartUser exists",20086),

    GET_LIVE_UPDATE_SUCCESS("获取王国更新内容数量成功","get live update num success",20087),



















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

    CAN_NOT_FOLLOW_YOURSELF("自己不能关注自己","can not follow yourself",50025),

    USER_MODIFY_ENCRYPT_THE_SAME_ERROR("老密码和新密码一样，不能修改"," user the old and new password are the same",50026),

    FORWARD_CONTENT_NOT_EXISTS("转发的原内容不存在","forward content not exists",50027),

    QRCODE_FAILURE("获取二维码失败","qrcode failure",50028),

    NO_RIGHTS_TO_LIKE("作者已经将该内容设置为私有您无权限操作","no rights to likes",50029),

    SNS_CORE_CIRCLE_IS_FULL("核心成员已满，无法继续邀请。","sns core circle is full",50030),

    IS_ALREADY_SNS_CORE("您已经是核心成员！","is already sns core",50031),

    TOPIC_FRAGMENT_DELETE_FAILURE("王国发言内容删除失败","topic fragment delete failure",50032),

    TOPIC_FRAGMENT_CAN_NOT_DELETE("只有国王能删除王国里的发言","you are not the king",50033),

    GET_REDDOT_FAILURE("获取红点失败没有更新","get reddot failure",50035),

    CONTENT_DELETE_NO_AUTH("只有国王能删除自己的王国","content delete no auth",50036),

    LIVE_HAS_DELETED("该王国已删除","live has deleted",50037),

    EDIT_TOPIC_FRAGMENT_FAILURE("编辑王国发言内容失败","edit topic fragment failure",50038),

    AWARD_MESSAGE_FAILURE("中奖短信发送失败","award message failure",50040),

    GAG_IS_NOT_ADMIN("只有管理可以进行全局禁言","only admin can gag  all",50041),

    GAG_IS_NOT_KING("只有国王可以王国禁言","only king can gag  in kingdom",50042),

    GAG_IS_NOT_AUTHOR("只有作者可以UGC禁言","only author can gag  in ugc",50043),

    USER_HAS_GAGGED("该用户已被禁言","user has gagged",50044),
















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
