package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.service.ContentService;
import com.plusnet.search.content.api.ContentStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :17:16
 */
@Component
public class ArticleReview implements Review{

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentStatusServiceProxyBean contentStatusServiceProxyBean;

    public Response createReview(ReviewDto reviewDto) {
        contentService.createArticleReview(reviewDto);
        ContentStatService contentStatService = contentStatusServiceProxyBean.getTarget();
        contentStatService.comments(reviewDto.getUid()+"",reviewDto.getCid());
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    }
}
