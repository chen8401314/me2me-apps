package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentTags;
import com.me2me.content.model.ContentTagsDetails;
import com.me2me.content.service.ContentService;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.monitor.service.MonitorService;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/15
 * Time :16:55
 */
public class AbstractWriteTag {

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    @Autowired
    private MonitorService monitorService;

    public Response writeTag(WriteTagDto writeTagDto) {
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(writeTagDto.getTag());
        contentService.createTag(contentTags);
        ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
        contentTagsDetails.setTid(contentTags.getId());
        contentTagsDetails.setCid(writeTagDto.getCid());
        contentTagsDetails.setUid(writeTagDto.getUid());
        contentService.createContentTagsDetails(contentTagsDetails);
        Content content = contentService.getContentById(writeTagDto.getCid());
        //添加贴标签提醒
        contentService. remind(content,writeTagDto.getUid(), Specification.UserNoticeType.TAG.index,writeTagDto.getTag());
        //打标签的时候文章热度+1
        content.setHotValue(content.getHotValue()+1);
        contentService.updateContentById(content);
        //userService.push(content.getUid(),writeTagDto.getUid(),Specification.PushMessageType.TAG.index,content.getTitle());
        monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FEELING_TAG.index,0,writeTagDto.getUid()));
        return Response.success(ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.status,ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.message);
    }
}
