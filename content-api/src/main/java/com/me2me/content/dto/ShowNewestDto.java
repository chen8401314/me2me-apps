package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/3
 * Time :9:28
 */
@Data
public class ShowNewestDto implements BaseEntity{

    private List<ContentElement> newestData = Lists.newArrayList();

    public static ContentElement createElement(){
        return new ContentElement();
    }


    @Data
    public static class ContentElement extends  BaseContentDto implements BaseEntity {

        private List<ReviewElement> reviews = Lists.newArrayList();

        public static ReviewElement createElement(){
            return new ReviewElement();
        }

        @Data
        public static class ReviewElement implements BaseEntity{

            private long uid;

            private String nickName;

            private String avatar;

            private Date createTime;

            private String review;

            private int v_lv;

        }
    }
}
