package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.model.ContentLikesDetails;
import com.plusnet.search.content.api.ContentStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:04
 */
@Component
public class ArticleLikes extends AbstractLikes implements Likes {

    @Autowired
    private ContentStatusServiceProxyBean contentStatusServiceProxyBean;

    @Override
    public Response likes(LikeDto likeDto) {
        ContentLikesDetails contentLikesDetails = new ContentLikesDetails();
        contentLikesDetails.setUid(likeDto.getUid());
        contentLikesDetails.setCid(likeDto.getCid());
        ContentLikesDetails contentLikes = contentService.getContentLikesDetails(contentLikesDetails);
        if(contentLikes != null){
            return Response.success(ResponseStatus.CONTENT_USER_LIKES_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_ALREADY.message);
        }
        contentService.createArticleLike(likeDto);
        ContentStatService contentStatService = contentStatusServiceProxyBean.getTarget();
        contentStatService.thumbsUp(likeDto.getUid()+"",likeDto.getCid());
        return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
    }
}
