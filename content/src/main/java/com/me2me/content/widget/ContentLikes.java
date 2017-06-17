package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentLikesDetails;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:03
 */
@Component
public class ContentLikes extends AbstractLikes implements Likes {



    @Override
    public Response likes(LikeDto likeDto) {
        Content content = contentService.getContentById(likeDto.getCid());
        if(content.getRights()!= Specification.ContentRights.SELF.index && content.getStatus()!=Specification.ContentStatus.DELETE.index){
            ContentLikesDetails contentLikesDetails = new ContentLikesDetails();
            contentLikesDetails.setUid(likeDto.getUid());
            contentLikesDetails.setCid(likeDto.getCid());
            ContentLikesDetails contentLikes = contentService.getContentLikesDetails(contentLikesDetails);
            if(contentLikes != null && likeDto.getAction() == Specification.IsLike.LIKE.index){
                return Response.failure(ResponseStatus.CONTENT_USER_LIKES_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_ALREADY.message);
            }
            return super.likes(likeDto);
        }else{
            return Response.failure(ResponseStatus.NO_RIGHTS_TO_LIKE.status,ResponseStatus.NO_RIGHTS_TO_LIKE.message);
        }
    }
}
