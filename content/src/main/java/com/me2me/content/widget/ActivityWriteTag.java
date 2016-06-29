package com.me2me.content.widget;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.ContentTags;
import com.me2me.content.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/15
 * Time :16:44
 */
@Component
@Slf4j
public class ActivityWriteTag extends AbstractWriteTag implements WriteTag{

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ContentService contentService;

    @Override
    public Response writeTag(WriteTagDto writeTagDto) {
        log.info("ActivityWriteTag writeTag ");
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(writeTagDto.getTag());
        contentService.createTag(contentTags);
        activityService.createActivityTagsDetails(writeTagDto.getCid(),writeTagDto.getUid(),contentTags.getId());
        return Response.success(ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.status,ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.message);
    }
}
