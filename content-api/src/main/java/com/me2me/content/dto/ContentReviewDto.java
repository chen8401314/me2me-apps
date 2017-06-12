package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/7
 * Time :17:44
 */
@Data
public class ContentReviewDto implements BaseEntity {

    private List<ReviewElement> reviews = Lists.newArrayList();

    public static ReviewElement createElement(){
        return new ReviewElement();
    }

    @Data
    public static class ReviewElement implements BaseEntity{

        private long id;

        private long uid;
        
        private long cid;

        private String nickName;

        private String avatar;

        private Date createTime;

        private String review;

        private long atUid;

        private String atNickName;

        private int v_lv;

        private String extra;

    }

}
