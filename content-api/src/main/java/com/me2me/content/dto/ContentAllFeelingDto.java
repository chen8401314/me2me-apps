package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/30
 * Time :9:39
 */
@Data
public class ContentAllFeelingDto implements BaseEntity {

    private List<ContentAllFeelingElement> results = Lists.newArrayList();

    public static ContentAllFeelingElement createElement(){
        return new ContentAllFeelingElement();
    }

    @Data
    public static class ContentAllFeelingElement implements BaseEntity {

        private String tag;

        private long cid;

        private long tid;

        private int likesCount;

        private long uid;

        private String avatar;

        private String nickName;

        private int type;

        private String content;
    }
}
