package com.me2me.user.rule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

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


    public static Map<Integer,CoinRule> coinRules = Maps.newConcurrentMap();


    static{

        coinRules.put(SPEAK_KEY,new CoinRule(SPEAK_KEY,"发言",2,true));
        coinRules.put(PUBLISH_UGC_KEY,new CoinRule(PUBLISH_UGC_KEY,"发布UGC",3,true));
        coinRules.put(REVIEW_UGC_KEY,new CoinRule(REVIEW_UGC_KEY,"回复UGC",2,true));
        coinRules.put(LIKES_UGC_KEY,new CoinRule(LIKES_UGC_KEY,"点赞UGC",1,true));
        coinRules.put(FOLLOW_USER_KEY,new CoinRule(FOLLOW_USER_KEY,"关注一个新用户",1,true));
        coinRules.put(JOIN_KING_KEY,new CoinRule(JOIN_KING_KEY,"加入一个新王国",1,true));
        coinRules.put(SHARE_KING_KEY,new CoinRule(SHARE_KING_KEY,"对外分享王国/UGC",10,true));
        coinRules.put(CREATE_KING_KEY,new CoinRule(CREATE_KING_KEY,"建立王国/更新王国",5,false));
    }

}
