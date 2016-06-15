package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.ArticleTagsDetails;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentTags;
import com.me2me.content.model.ContentTagsDetails;
import com.me2me.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/15
 * Time :16:43
 */
@Component
public class ArticleWriteTag extends AbstractWriteTag implements WriteTag{

    @Autowired
    private ContentService contentService;

    public Response writeTag(WriteTagDto writeTagDto) {
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(writeTagDto.getTag());
        contentService.createTag(contentTags);
        ArticleTagsDetails articleTagsDetails = new ArticleTagsDetails();
        articleTagsDetails.setTid(contentTags.getId());
        articleTagsDetails.setArticleId(writeTagDto.getCid());
        articleTagsDetails.setUid(writeTagDto.getUid());
        contentService.createContentArticleDetails(articleTagsDetails);
        return Response.success(ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.status,ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.message);
    }
}
