package com.me2me.live.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/13
 * Time :15:42
 */
@Data
public class Live4H5Dto implements BaseEntity {

    private Live live ;

    private List<Fragment> fragments = Lists.newArrayList();

    public static Fragment createFragment(){
        return new Fragment();
    }

    @Data
    public static class Live implements BaseEntity {

        private String title;

        private String cover;

        private String nickName;

        private String avatar;

        private Date createTime;

    }

    @Data
    public static class Fragment implements BaseEntity {


        private String nickName;

        private Date createTime;

        private int contentType;

        private int type;

        private String fragment;

        private String fragmentImage;

        private String avatar;


    }

}
