package com.me2me.content.widget;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.model.ContentLikesDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/21
 * Time :16:43
 */
@Component
@Slf4j
public class ActivityLikes extends AbstractLikes implements Likes{

    @Autowired
    private ActivityService activityService;

    @Override
    public Response likes(LikeDto likeDto) {
        ContentLikesDetails contentLikesDetails = new ContentLikesDetails();
        contentLikesDetails.setUid(likeDto.getUid());
        contentLikesDetails.setCid(likeDto.getCid());
        ContentLikesDetails contentLikes = contentService.getContentLikesDetails(contentLikesDetails);
        if(contentLikes != null &&  likeDto.getAction() == Specification.IsLike.LIKE.index){
            return Response.success(ResponseStatus.CONTENT_USER_LIKES_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_ALREADY.message);
        }
        activityService.createActivityLikesDetails(likeDto.getCid(),likeDto.getUid());
        return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
    }
}
