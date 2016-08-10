package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/29.
 */
@Data
public class AtReviewDto implements BaseEntity {

    /**
     * 评论ID
     */
    private long reviewId;

    /**
     * at人ID
     */
    private long fromUid;

    /**
     * 被at人ID
     */
    private long toUid;

    /**
     * at内容
     */
    private String review;

}
