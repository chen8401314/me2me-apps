package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/14
 * Time :16:45
 */
@Data
public class PromoterDto implements BaseEntity{


    private List<PromoterElement> promoterElementList = Lists.newArrayList();

    public static PromoterElement createElement(){
        return new PromoterElement();
    }


    @Data
    public static class PromoterElement implements BaseEntity {

        private long uid;

        private String nickName;

        private String avatar;

        private int refereeCount;

        private int activateCount;
    }
}
