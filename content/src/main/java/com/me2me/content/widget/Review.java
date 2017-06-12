package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ReviewDelDTO;
import com.me2me.content.dto.ReviewDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :17:44
 */
public interface Review {

    Response createReview(ReviewDto reviewDto);

    Response delReview(ReviewDelDTO delDTO);
}
