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
@Lazy(false)
@Component
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

    public static Map<Integer,CoinRule> coinRules = Maps.newConcurrentMap();

    @Autowired
    private UserService userService;



    @PostConstruct
    public void fillPointToCoinRules(){
        coinRules.put(SPEAK_KEY,new CoinRule(SPEAK_KEY,"发言",Integer.valueOf(userService.getAppConfigByKey("SPEAK_KEY")),true));
        coinRules.put(PUBLISH_UGC_KEY,new CoinRule(PUBLISH_UGC_KEY,"发布UGC",Integer.valueOf(userService.getAppConfigByKey("PUBLISH_UGC_KEY")),true));
        coinRules.put(REVIEW_UGC_KEY,new CoinRule(REVIEW_UGC_KEY,"回复UGC",Integer.valueOf(userService.getAppConfigByKey("REVIEW_UGC_KEY")),true));
        coinRules.put(LIKES_UGC_KEY,new CoinRule(LIKES_UGC_KEY,"点赞UGC",Integer.valueOf(userService.getAppConfigByKey("LIKES_UGC_KEY")),true));
        coinRules.put(FOLLOW_USER_KEY,new CoinRule(FOLLOW_USER_KEY,"关注一个新用户",Integer.valueOf(userService.getAppConfigByKey("FOLLOW_USER_KEY")),true));
        coinRules.put(JOIN_KING_KEY,new CoinRule(JOIN_KING_KEY,"加入一个新王国",Integer.valueOf(userService.getAppConfigByKey("JOIN_KING_KEY")),true));
        coinRules.put(SHARE_KING_KEY,new CoinRule(SHARE_KING_KEY,"对外分享王国/UGC",Integer.valueOf(userService.getAppConfigByKey("SHARE_KING_KEY")),true));
        coinRules.put(CREATE_KING_KEY,new CoinRule(CREATE_KING_KEY,"建立王国/更新王国",Integer.valueOf(userService.getAppConfigByKey("CREATE_KING_KEY")),false));
        coinRules.put(CREATE_KING_KEY,new CoinRule(LOGIN_KEY,"登录",Integer.valueOf(userService.getAppConfigByKey("LOGIN_KEY")),false));
    }




}
