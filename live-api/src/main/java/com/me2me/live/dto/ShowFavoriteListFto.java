package com.me2me.live.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/14
 * Time :14:19
 */
@Data
public class ShowFavoriteListFto implements BaseEntity {

    private  List<FavoriteUser> favoriteUserElements = Lists.newArrayList();

    public static FavoriteUser createElement(){
        return new FavoriteUser();
    }


    @Data
    public static class FavoriteUser implements  BaseEntity {

    private long uid;

    private String avatar;

    private String nickName;
}
}
