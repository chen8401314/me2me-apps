package com.me2me.content.event;

import com.me2me.common.web.BaseEntity;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.model.Content;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/9/20.
 */
@Data
public class ReviewEvent implements BaseEntity {

    private Content content;

    private ReviewDto reviewDto;

    private String isOnline;

}
