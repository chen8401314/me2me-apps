package com.me2me.article.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/24.
 */
@Data
public class FeedDto implements BaseEntity {

    private List<ImageDto> albums = Lists.newArrayList();

    private List<ImageDto> quImages = Lists.newArrayList();

    private List<JokeDto> jokes = Lists.newArrayList();

    public ImageDto createImageDto(){
        return new ImageDto();
    }

    public JokeDto createJokeDto(){
        return new JokeDto();
    }

    @Data
    public class ImageDto implements BaseEntity{

        private long id;

        private String title;

        private String image;

    }

    @Data
    public class JokeDto implements BaseEntity{

        private long id;

        /**
         * 标题(首页段子)
         */
        private String title;

        /**
         * 封面(首页段子)
         */
        private String coverImage;

        /**
         * 摘要信息(首页段子)
         */
        private String summary;
    }






}
