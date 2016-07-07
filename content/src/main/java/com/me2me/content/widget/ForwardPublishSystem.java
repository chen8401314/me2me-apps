package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.CreateContentSuccessDto;
import com.me2me.content.model.Content;
import com.me2me.content.service.ContentService;
import com.me2me.monitor.service.MonitorService;
import com.me2me.monitor.event.MonitorEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/13
 * Time :15:38
 */
@Component
@Slf4j
public class ForwardPublishSystem extends AbstractPublish implements Publish {

    @Autowired
    private ContentService contentService;

    @Autowired
    private MonitorService monitorService;

    public Response publish(ContentDto contentDto){
        log.info("forwardPublishSystem start ...");
        CreateContentSuccessDto createContentSuccessDto = new CreateContentSuccessDto();
        Content content = new Content();
        Content originalContent = contentService.getContentById(contentDto.getForwardCid());
        if(originalContent == null){
            return Response.success(ResponseStatus.FORWARD_CONTENT_NOT_EXISTS.status,ResponseStatus.FORWARD_CONTENT_NOT_EXISTS.message);
        }
        content.setForwardCid(originalContent.getId());
        content.setConverImage(originalContent.getConverImage());
        content.setForwardTitle(originalContent.getTitle());
        content.setContent(contentDto.getContent());
        content.setThumbnail(originalContent.getConverImage());
        content.setType(contentDto.getType());
        content.setContentType(contentDto.getContentType());
        content.setUid(contentDto.getUid());
        contentService.createContent(content);
        log.info("create forward success");
        createContentSuccessDto.setContent(content.getContent());
        createContentSuccessDto.setCreateTime(content.getCreateTime());
        createContentSuccessDto.setUid(content.getUid());
        createContentSuccessDto.setId(content.getId());
        createContentSuccessDto.setFeeling(content.getFeeling());
        createContentSuccessDto.setType(content.getType());
        createContentSuccessDto.setContentType(content.getContentType());
        createContentSuccessDto.setForwardCid(content.getForwardCid());
        createContentSuccessDto.setCoverImage(content.getConverImage());
        createContentSuccessDto.setForwardTitle(content.getForwardTitle());
        createContentSuccessDto.setForwardUrl(content.getForwardUrl());
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FORWARD.index,0,contentDto.getUid()));
        log.info("monitor ForwardPublishSystem ");
        log.info("forwardPublishSystem end ...");
        return Response.success(ResponseStatus.FORWARD_SUCCESS.status,ResponseStatus.FORWARD_SUCCESS.message,createContentSuccessDto);
    }
}
