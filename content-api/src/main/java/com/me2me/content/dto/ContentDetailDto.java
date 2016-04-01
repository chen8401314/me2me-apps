package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/28
 * Time :16:10
 */
@Data
public class ContentDetailDto implements BaseEntity {

    private long id;

    private long uid;

    private long forwardCid;

    private String feeling;

    private int type;

    private String coverImage;

    private String forwardTitle;

    private String forwardUrl;

    private int contentType;

    private String content;

    private String thumbnail;

    private int hotValue;

    private int likeCount;

    private int authorization;

    private Date createTime;

    private List<ContentTop5FeelingElement> results = Lists.newArrayList();

    public static ContentTop5FeelingElement createElement(){
        return new ContentTop5FeelingElement();
    }

    @Data
    public static class ContentTop5FeelingElement implements BaseEntity {

        private String tag;

        private long cid;

        private long tid;

        private int likesCounts;

        private long uid;

        private String avatar;

        private String nickName;

        private String forwardTitle;
    }

}
