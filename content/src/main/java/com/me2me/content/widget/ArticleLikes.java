package com.me2me.content.widget;

import lombok.extern.slf4j.Slf4j;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
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
@Slf4j
public class ArticleLikes extends AbstractLikes implements Likes {

    @Autowired
    private ContentStatusServiceProxyBean contentStatusServiceProxyBean;

    @Override
    public Response likes(LikeDto likeDto) {
        ContentLikesDetails contentLikesDetails = new ContentLikesDetails();
        contentLikesDetails.setUid(likeDto.getUid());
        contentLikesDetails.setCid(likeDto.getCid());
        ContentLikesDetails contentLikes = contentService.getContentLikesDetails(contentLikesDetails);
        if(contentLikes != null &&  likeDto.getAction() == Specification.IsLike.LIKE.index){
            return Response.success(ResponseStatus.CONTENT_USER_LIKES_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_ALREADY.message);
        }
        contentService.createArticleLike(likeDto);
        try{
        	ContentStatService contentStatService = contentStatusServiceProxyBean.getTarget();
            contentStatService.thumbsUp(likeDto.getUid()+"",likeDto.getCid());
        }catch(Exception e){
        	log.error("老徐文章点赞接口调用失败", e);
        }
        return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
    }
}
