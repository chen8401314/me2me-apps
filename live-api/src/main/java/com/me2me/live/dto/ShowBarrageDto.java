package com.me2me.live.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/14
 * Time :18:56
 */
@Data
public class ShowBarrageDto implements BaseEntity{

    private List<BarrageElement> barrageElements = Lists.newArrayList();

    public static BarrageElement createElement(){
        return new BarrageElement();
    }

    @Data
    public static class BarrageElement implements BaseEntity {

        private long id;

        private long uid;

        private String nickName;

        private int level;

        private Date createTime;

        private int contentType;

        private int type;

        private String fragment;

        private String fragmentImage;

        private String avatar;

        private long topId;

        private long bottomId;

        private int internalStatus;

    }

}
