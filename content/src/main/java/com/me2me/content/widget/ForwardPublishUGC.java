package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/13
 * Time :14:50
 */
@Component
public class ForwardPublishUGC  extends AbstractPublish implements Publish {

    @Autowired
    private ContentService contentService;

    public Response publish(ContentDto contentDto){
        Content content = new Content();
        if(contentDto.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
            content.setForwardCid(contentDto.getForwardCid());
            content.setForwardUrl(contentDto.getForWardUrl());
            content.setForwardTitle(contentDto.getTitle());
            content.setThumbnail(contentDto.getImageUrls());
            content.setType(contentDto.getType());
        }else{
            long forwardCid = contentDto.getForwardCid();
            Content forwardContent = contentService.getContentById(forwardCid);
            content.setForwardCid(forwardCid);
            content.setForwardTitle(forwardContent.getTitle());
            content.setThumbnail(forwardContent.getConverImage());
        }
        contentService.createContent(content);
        return Response.success();
    }
}
