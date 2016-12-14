package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by 马秀成 on 2016/12/14.
 */
@Data
public class DoubleLiveDto implements BaseEntity {

    private int loveDay;//建立了几天

    private List<DoubleLiveElement> ownerInfo = Lists.newArrayList();

    private List<DoubleLiveElement> targetInfo = Lists.newArrayList();

    public DoubleLiveElement createDoubleLiveElement(){
        return new DoubleLiveElement();
    }

    @Data
    public class DoubleLiveElement implements BaseEntity {

        private long uid;

        private int robbed;//被抢次数

        private String nickName;

        private String avatar;

    }

}
