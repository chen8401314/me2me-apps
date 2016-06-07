package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ReviewDto;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:35
 */
public class ReviewAdapter {

    @Setter
    private Review target;

    public ReviewAdapter(Review review) {
        this.target = review;
    }

    public Response execute(ReviewDto reviewDto){
        return target.createReview(reviewDto);
    }
}
