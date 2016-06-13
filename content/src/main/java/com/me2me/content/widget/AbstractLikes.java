package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentLikesDetails;
import com.me2me.content.service.ContentService;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.monitor.event.MonitorEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:43
 */
public class AbstractLikes {

    @Autowired
    protected ContentService contentService;

    @Autowired
    private ApplicationEventBus applicationEventBus;

    public Response likes(LikeDto likeDto) {
        Content content = contentService.getContentById(likeDto.getCid());
        if(content == null){
            return Response.failure(ResponseStatus.CONTENT_LIKES_ERROR.status,ResponseStatus.CONTENT_LIKES_ERROR.message);
        }else{
            ContentLikesDetails contentLikesDetails = new ContentLikesDetails();
            contentLikesDetails.setUid(likeDto.getUid());
            contentLikesDetails.setCid(likeDto.getCid());
            //点赞
            ContentLikesDetails details = contentService.getContentLikesDetails(contentLikesDetails);
            if(likeDto.getAction() == Specification.IsLike.LIKE.index){
                if(details == null) {
                    content.setLikeCount(content.getLikeCount() + 1);
                    contentService.updateContentById(content);
                    contentService.createContentLikesDetails(contentLikesDetails);
                    if(likeDto.getUid() != content.getUid()) {
                        contentService.remind(content, likeDto.getUid(), Specification.UserNoticeType.LIKE.index, null);
                    }
                }else{
                    return Response.success(ResponseStatus.CONTENT_USER_LIKES_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_ALREADY.message);
                }
                applicationEventBus.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.LIKE.index,0,likeDto.getUid()));
                return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
            }else{
                if(details == null) {
                    Response.success(ResponseStatus.CONTENT_USER_LIKES_CANCEL_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_CANCEL_ALREADY.message);
                }else {
                    if ((content.getLikeCount() - 1) < 0) {
                        content.setLikeCount(0);
                    } else {
                        content.setLikeCount(content.getLikeCount() - 1);
                    }
                    contentService.updateContentById(content);

                    contentService.deleteContentLikesDetails(contentLikesDetails);
                }
                applicationEventBus.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.UN_LIKE.index,0,likeDto.getUid()));
                return Response.success(ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.message);
            }
        }
    }
}
