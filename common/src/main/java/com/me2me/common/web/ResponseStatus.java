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


	OPERATION_SUCCESS("操作成功", "operation success", 200),

    USER_SING_UP_SUCCESS("用户注册成功","user sign up success",2000),

    USER_LOGIN_SUCCESS("用户登录成功","user login success",2001),

    USER_MODIFY_ENCRYPT_SUCCESS("用户密码修改成功","user modifyEncrypt success",2002),

    USER_SET_ENCRYPT_SUCCESS("用户密码设置成功","user setEncrypt success",2002),

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

    ADD_COIN_SUCCESS("加分成功","content review success",200144),

    VERSION_UPDATE_SUCCESS("版本已更新","version update success",20047),

    LIKE_SUCCESS("点赞成功","content user likes success",20098),

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

    HIGH_QUALITY_CONTENT_YET("重复置热","high quality content YET",20061),

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

    AWARD_MESSAGE_SUCCESS("短信发送成功","award message success",20085),

    THIRDPARTUSER_EXISTS("第三方账户已存在","thirdPartUser exists",20086),

    GET_LIVE_UPDATE_SUCCESS("获取王国更新内容数量成功","get live update num success",20087),

    TOURIST_LOGIN_SUCCESS("游客模式登录成功","tourist login success",20088),

    REVIEW_DELETE_SUCCESS("评论删除成功","review delete success", 20089),

    QIACITIVITY_INFO_SUCCESS("获取用户活动信息成功","qiactivity info success", 20090),

    QIACITIVITY_NOT_INFO_SUCCESS("该用户没有报名过此次活动","qiactivity not info success", 20091),

    REGISTRATION_SUCCESS("用户报名成功","registration success", 20092),

    CAN_ONLY_SIGN_UP_ONCE("该用户已经报名过了","Can only sign up once", 20093),

    NOT_FIRST_STAGE("目前不处于报名阶段","not first stage", 20094),

    QIACTIVITY_NOT_START("活动未开始或已结束","qiactivity not start", 20095),

    QI_BIND_EXISTS("该账号已经绑定过了","qi bind exists", 20096),

    QI_QUERY_SUCCESS("七天活动状态查询成功","qi query exists", 20096),

    QI_QUERY_FAILURE("该用户不存在","qi query failue", 20097),

    AUDIT_FAILURE("该用户审核未通过","audit failure", 20098),

    IN_AUDIT("该用户审核中","in audit", 20099),

    CANNOT_FIND_AUSER("未查询到审核中的用户","cannot find auser", 200100),

    DOUBLE_TOPIC_NOT_OPEN("双人王国活动暂未开放","double topic not open", 200101),

    DOUBLE_TOPIC_NOT_CREATE("双人王国暂未创建","double topic not create", 200102),

    SINGLE_TOPIC_NOT_CREATE("单人王国暂未创建","double topic not create", 200103),

    SINGLE_TOPIC_GET_SUCCESS("单人王国信息获取成功","double topic not create", 200104),

    DOUBLE_TOPIC_GET_SUCCESS("双人王国息获取成功","double topic not create", 200105),

    SINGLE_TOPIC_NOT_OPEN("单人王国活动暂未开放","single topic not open", 200106),

    SEARCH_ATOPIC_SUCCESS("查询活动王国信息成功","search atopic success", 200107),

    SEARCH_ATOPIC_FAILURE("未查询到活动王国信息","search atopic failure", 200108),

    NUMBER_IS_BOUND("申请次数达到上限","number is bound", 200109),

    NOT_THREE_STAGE("目前不处于第三阶段","not three stage", 200110),

    APPLICATION_SUCCESS("申请成功","application success ", 200111),

    TOPIC_GET_FAILURE("未查询到任何王国信息","topic get failure", 200112),

    APPLY_LIST_SUCCESS("申请列表信息获取成功","apply list success", 200113),

    TARGET_CREATE_TOPIC("对方已经创建了双人王国不能同意","target create topic", 200114),

    CANT_DELETE("暂时不满足删除条件","cant delete", 200115),

    UPDATE_STATE_SUCCESS("申请状态更新成功","update state success", 200116),

    NOT_SINGLE_STAGE("目前不处于第一阶段","not single stage", 200117),

    CANT_APPLY_BRID("不满足抢亲条件","cant apply brid", 200118),

    APPLY_BRID_SUCCESS("抢亲申请提交成功","apply brid success", 200119),

    BRID_GET_LIST_SUCCESS("抢亲列表获取成功","brid get list success", 200120),

    BRID_GET_LIST_FAILURE("未查询到抢亲列表信息","brid get list failure", 200121),

    ONLY_AGREE_ONE_PEOPLE("你已经同意过其他人了","only agree one people", 200122),

    NOT_FOUR_STAGE("目前不处于抢亲阶段","not four stage", 200123),

    BRID_IS_SUCCESS("同意了你的抢亲邀请","brid is success", 200124),

    TARGET_NOT_CREATE_TOPIC("对方还未创建双人王国","target not create topic", 200125),

    BRID_IS_FAILURE("拒绝对方的抢亲","brid is failure", 200126),

    BRID_UPPER_LIMIT("抢亲次数已经到达上限","brid upper limit", 200127),

    NOT_GET_DOUBLELIVE("未查询到双人王国状态信息","not get doublelive", 200128),

    DIVORCE_SUCCESS("离婚成功","divorce success", 200129),

    APPLICATION_EXISTS("申请中或者对方已经同意了你的请求","application exists ", 200130),

    SEARCH_BRIDLIST_SUCCESS("查询抢亲列表信息成功","search bridlist success", 200131),

    BRID_IS_DELETE("撤销了抢亲请求成功","brid is delete", 200132),

    SEARCH_BRIDLIST_FAILURE("未查询到抢亲列表信息","search bridlist failure", 200133),

    SEARCH_LIGHTBOX_NOT_EXISTS("未查询到灯箱内容","search lightbox not exists", 200134),

    SEARCH_LIST_NOT_EXISTS("未查询到榜单信息","search list not exists", 200135),

    AGGREGATION_APPLY_SUCCESS("收录成功","apply success",200136),

    CREATE_VOTE_SUCCESS("投票创建成功","create vote success",200137),
    
    VOTE_SUCCESS("投票成功","vote success",200138),

    END_VOTE_SUCCESS("结束投票成功","end vote success",200140),

    RESEND_VOTE_SUCCESS("投票重新发送成功","resend vote success",200141),

    SEARCH_EMOTION_NOT_EXISTS("未匹配到情绪信息","search emotion not exists",200142),
    
    SEARCH_EMOTION_SUMMARY_NOT_EXISTS("未查询到情绪总结记录","search emotion summary not exists",200143),























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

    TOPIC_FRAGMENT_CAN_NOT_DELETE("你不能删除王国里的发言","you can not delete the fragment of the kingdom",50033),
    
    TOPIC_SPEAK_FAILURE("发言失败","speak error",50034),

    GET_REDDOT_FAILURE("获取红点失败没有更新","get reddot failure",50035),

    CONTENT_DELETE_NO_AUTH("只有国王能删除自己的王国","content delete no auth",50036),

    LIVE_HAS_DELETED("来晚一步！这个王国已经被删除了……","live has deleted",50037),

    EDIT_TOPIC_FRAGMENT_FAILURE("编辑王国发言内容失败","edit topic fragment failure",50038),

    AWARD_MESSAGE_FAILURE("短信发送失败","award message failure",50040),

    GAG_IS_NOT_ADMIN("只有管理可以进行全局禁言","only admin can gag  all",50041),

    GAG_IS_NOT_KING("只有国王可以王国禁言","only king can gag  in kingdom",50042),

    GAG_IS_NOT_AUTHOR("只有作者可以UGC禁言","only author can gag  in ugc",50043),

    USER_HAS_GAGGED("该用户已被禁言","user has gagged",50044),

    USER_IS_GAGGED("因违反用户协议，已被禁止使用此功能", "This feature has been banned for breach of user agreement", 50045),
    
    REVIEW_CAN_NOT_DELETE("无权删除评论", "you can not delete the review", 50046),

    USER_ACCOUNT_DISABLED("因违反相关协议，此账号已被禁用", "user account disabled", 50047),

    //重复了,同50037
//    KINGDOM_IS_NOT_EXIST("来晚一步！这个王国已经被删除了……", "the kingdom is not exist", 50048),

    KINGDOM_CREATE_FAILURE("王国创建失败", "Kingdom creation failed", 50049),

    APPLICATION_FAILURE("不满足申请双人王国条件","application failure", 50050),

    CAN_NOT_REPEAT_THE_APPLICATION("不能重复申请","Can not repeat the application", 50051),

    ACCEPT_TASK_ERROR("接受任务失败", "accept task error", 50052),

    USER_TASK_STATUS_QUERY_ERROR("用户任务状态查询失败", "user task status query error", 50053),

    APPLY_IS_CANCELED("申请已被对方撤销，请刷新页面", "apply is canceled", 50054),

    ACTION_NOT_SUPPORT("暂不支持的操作类型", "action not support", 50055),

    YOU_ARE_NOT_KING("只有国王才能操作", "you are not king", 50056),

    REPEATED_TREATMENT("重复处理", "repeated treatment", 50057),
    
    FRAGMENT_IS_NOT_EXIST("发言不存在或已删除","FRAGMENT IS NOT EXIST!", 50058),
    
    KINGDOM_IS_NOT_AGGREGATION("当前王国不是聚合王国","kingdom is not aggregation!", 50059),

    TOP_COUNT_OVER_LIMIT("置顶次数超过上限","top count over limit", 50060),
    
    AGGREGATION_PUBLISH_OVER_LIMIT("每天只能下发#{count}#次，今天不能再下发了哦。", "aggregation puhlish over limit", 50061),
    
    UGC_NO_RIGHTS("无权访问", "no rights", 50062),
    
    YOU_ARE_NOT_CORECIRCLE("只有核心圈才能操作", "you are not coreCircle", 50063),
    
    YOU_ARE_NOT_ADMIN("只有管理员才能操作", "you are not admin", 50064),

    YOU_NOT_JOIN_OWNER_TOPIC("你不能加入自己建立/自己是核心圈的王国", "you not join owner topic",50065),

    YOU_DO_NOT_HAVE_PERMISSION("你无权操作","you have no permission",50066),
    
    TAG_HAS_BEEN_FORBIDDEN("此标签已经禁用","tag has been forbidden", 50067),
    
    /**
     * 王国偷取相关
     */
    ERR_STEAL_SELF("不能偷取自己的王国","cannot steal your self's kingdom",50068),
    
	ERR_STEAL_MAX_LIMIT("该王国已经达到偷取上限","cannot steal this kingdom.",50069),
	
    //完善个人资料领取红包
    ERR_RED_BAG("不能重复领取","Cannot receive again.",50070),
    
    KINGDOM_ALL_FORBID("此王国处于全体禁言模式","kingdom all forbid", 50071),
    
    KINGDOM_USER_FORBID("你已被王国禁言","user forbid",50072),
    
    JOIN_LOTTER_FAILURE("参与抽奖失败","join lotter failure",50073),
    
    KINGDOM_STEAL_FAILURE("王国偷取失败","kingdom steal failure",50074),
    
    CONTENT_NOT_EXISTS("内容不存在","content not exists",50075),
    
    
    
    
    ILLEGAL_REQUEST("非法的请求参数","illegal request",50099),
	
    //创建王国余额不足
	CREATE_KINGDOM_PRICE_LACK("需要消耗#{price}#米汤币,当前余额不足！","create kingdom price lack",500100),

	COMMON_ERROR_RESULT("通用错误返回", "common error result", 500999);
	
    public final String message;

    public final String englishMessage;

    public final int status;

    ResponseStatus(String message,String englishMessage,int status){
        this.message = message;
        this.englishMessage = englishMessage;
        this.status = status;
    }

}
