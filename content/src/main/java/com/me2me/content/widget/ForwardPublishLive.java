package com.me2me.content.widget;

import com.me2me.user.dto.ModifyUserCoinDto;
import com.me2me.user.rule.Rules;
import lombok.extern.slf4j.Slf4j;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.CreateContentSuccessDto;
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
@Slf4j
public class ForwardPublishLive  extends AbstractPublish implements Publish {

    @Autowired
    private ContentService contentService;

    public Response publish(ContentDto contentDto){
    	log.info("ForwardPublishLive start ...");
    	CreateContentSuccessDto createContentSuccessDto = new CreateContentSuccessDto();
    	Content content = new Content();
        content.setForwardCid(contentDto.getForwardCid());
        content.setForwardUrl(contentDto.getForWardUrl());
        content.setTitle(contentDto.getTitle());
        content.setFeeling(contentDto.getFeeling());
        content.setForwardTitle(contentDto.getForwardTitle());
        content.setThumbnail(contentDto.getImageUrls());
        content.setConverImage(contentDto.getCoverImage());
        content.setType(contentDto.getType());
        content.setContentType(contentDto.getContentType());
        content.setContent(contentDto.getContent());
        content.setUid(contentDto.getUid());
        content.setRights(contentDto.getRights());
        contentService.createContent(content);
        log.info("content create success");
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
    	
    	log.info("ForwardPublishLive end ...");
        ModifyUserCoinDto modifyUserCoinDto = userService.coinRule(contentDto.getUid(), userService.getCoinRules().get(Rules.SHARE_KING_KEY));
        createContentSuccessDto.setCurrentLevel(modifyUserCoinDto.getCurrentLevel());
        createContentSuccessDto.setUpgrade(modifyUserCoinDto.getUpgrade());
        Response response =Response.success(ResponseStatus.FORWARD_SUCCESS.status,ResponseStatus.FORWARD_SUCCESS.message,createContentSuccessDto);

    	return response;
    }
}
