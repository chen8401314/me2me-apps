package com.me2me.user.rule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.common.web.BaseEntity;
import com.me2me.user.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/6/16 0016.
 */
@Data
public class Rules implements BaseEntity {
    //王国留言
    public static final Integer SPEAK_KEY = 1;
    //发布UGC
    public static final Integer PUBLISH_UGC_KEY = 2;
    //回复UGC
    public static final Integer REVIEW_UGC_KEY = 3;
    //点赞UGC
    public static final Integer LIKES_UGC_KEY = 4;
    //关注一个新用户
    public static final Integer FOLLOW_USER_KEY = 5;
    //加入一个王国
    public static final Integer JOIN_KING_KEY = 6;
    //对外分享王国
    public static final Integer SHARE_KING_KEY = 7;
    //创建一个王国
    public static final Integer CREATE_KING_KEY = 8;
    //每天登录
    public static final Integer LOGIN_KEY = 9;

}
