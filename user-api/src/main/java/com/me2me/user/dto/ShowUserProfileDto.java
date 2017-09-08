package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/5
 * Time :17:57
 */
@Data
public class ShowUserProfileDto implements BaseEntity{

    private long uid;

    private String nickName;

    private int gender;

    private String avatar;
    
    private String avatarFrame;

    private String birthday;

    private String meNumber;

    private int followedCount;

    private int fansCount;

    private String userName;

    private String token;

    private String introduced;

    private int isPromoter;

    private int power;

    private int ugcCount;

    private int liveCount;

    private String thirdPartBind;

    private int v_lv;

    private int acCount;

    private int upgrade ;

    private int currentLevel;

    private List<Hobby> hobbyList = Lists.newArrayList();

    // 用户级别
    private int level;
    // 可用米汤币
    private int availableCoin;
    //米汤币转换人民币
    private double  priceRMB;
    // 用户级别图标
    private String levelIcon;

    public Hobby createHobby(){ return new Hobby();}

    @Data
    public static class Hobby implements BaseEntity{

        private long hobby;

        private String value;

    }
}
