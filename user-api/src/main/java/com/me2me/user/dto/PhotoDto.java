package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/27
 * Time :23:03
 */
@Data
public class PhotoDto implements BaseEntity {

    private List<Photo> result = Lists.newArrayList();

    public static  Photo create(){
        return new Photo();
    }

    @Data
    public static class Photo implements BaseEntity {

        private long id;

        private String title;

        private String imageUrl;
    }
}
