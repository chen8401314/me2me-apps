package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/26.
 */
@Data
public class ShowContentDto implements BaseEntity {

    private List<ShowContentElement> result = Lists.newArrayList();

    public ShowContentElement createElement(){
        return new ShowContentElement();
    }

    /**
     * 总记录条数
     */
    private int total;

    /**
     * 总页数
     */
    private int totalPage;

    @Data
    public class ShowContentElement implements BaseEntity{


        private boolean isActivity;

        private String title;

        // 是否热门
        private boolean isHot;

        // 是否置顶
        private int topped;

        // for pgc
        private long uid;

        // for ugc
        private String nickName;

        private Date createTime;

        private Date updateTime;

        // 缩略图
        private String thumb;

        private String content;

        private long id;

        private int likeCount;

        private int reviewCount;


    }

}
