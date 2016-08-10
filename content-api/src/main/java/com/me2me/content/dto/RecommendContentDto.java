package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Data
public class RecommendContentDto implements BaseEntity {

    private List<RecommendElement> result = Lists.newArrayList();


    public RecommendElement createElement(){
        return new RecommendElement();
    }

    @Data
    public class RecommendElement implements BaseEntity{

        private String title;

        private String linkUrl;

        private String coverImage;

        private int type;

    }

}
