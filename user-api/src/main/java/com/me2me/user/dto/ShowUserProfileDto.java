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

    private String birthday;

    private String meNumber;

    private int followedCount;

    private int fansCount;

    private String userName;

    private String token;

    private String introduced;

    private List<Hobby> hobbyList = Lists.newArrayList();

    public Hobby createHobby(){ return new Hobby();}

    @Data
    public static class Hobby implements BaseEntity{

        private long hobby;

        private String value;

    }
}
