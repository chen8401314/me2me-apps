package com.me2me.content.widget;

import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.CreateContentSuccessDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentImage;
import com.me2me.content.service.ContentService;
import com.me2me.user.dto.ModifyUserCoinDto;
import com.me2me.user.model.UserProfile;
import com.me2me.user.rule.Rules;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/6
 * Time :21:23
 */
@Slf4j
public class AbstractPublish {

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected CacheService cacheService;

    public Response publish(ContentDto contentDto) {
        log.info("abstract publish start .....");
        CreateContentSuccessDto createContentSuccessDto = new CreateContentSuccessDto();
        String coverImage = "" ;
        Content content = new Content();
        content.setUid(contentDto.getUid());
        content.setContent(contentDto.getContent());
        content.setFeeling(contentDto.getFeeling());
        content.setTitle(contentDto.getTitle());
        content.setFeeling(contentDto.getFeeling());
        if(!StringUtils.isEmpty(contentDto.getImageUrls())){
            String[] images = contentDto.getImageUrls().split(";");
            // 设置封面
            content.setConverImage(images[0]);
            coverImage = images[0] ;
        }
        content.setType(contentDto.getType());
        content.setContentType(contentDto.getContentType());
        content.setRights(contentDto.getRights());
        content.setForwardCid(contentDto.getForwardCid());
        // 自增标识
        long updateId = cacheService.incr("UPDATE_ID");
        content.setUpdateId(updateId);
        //保存内容
        contentService.createContent(content);
        log.info("create content success");
        //创建标签
        contentService.createTag(contentDto,content);
        log.info("create tag success");
        if(!StringUtils.isEmpty(contentDto.getImageUrls())){
            String[] images = contentDto.getImageUrls().split(";");
            // 保存用户图片集合
            for(String image : images){
                ContentImage contentImage = new ContentImage();
                contentImage.setCid(content.getId());
                if(image.equals(images[0])) {
                    contentImage.setCover(1);
                }
                contentImage.setImage(image);
                contentService.createContentImage(contentImage);
            }
            log.info("create content images success");
        }
        createContentSuccessDto.setContent(content.getContent());
        createContentSuccessDto.setCreateTime(content.getCreateTime());
        createContentSuccessDto.setUid(content.getUid());
        createContentSuccessDto.setId(content.getId());
        createContentSuccessDto.setFeeling(content.getFeeling());
        createContentSuccessDto.setType(content.getType());
        createContentSuccessDto.setContentType(content.getContentType());
        createContentSuccessDto.setForwardCid(content.getForwardCid());
        UserProfile profile = userService.getUserProfileByUid(contentDto.getUid());
        createContentSuccessDto.setV_lv(profile.getvLv());
        if(!StringUtils.isEmpty(coverImage)) {
            createContentSuccessDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + coverImage);
        }else{
            createContentSuccessDto.setCoverImage("");
        }
        log.info("abstract publish end .....");
        ModifyUserCoinDto modifyUserCoinDto = userService.coinRule(contentDto.getUid(), userService.getCoinRules().get(Rules.PUBLISH_UGC_KEY));
        Response response = Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message,createContentSuccessDto);
        createContentSuccessDto.setUpgrade(modifyUserCoinDto.getUpgrade());
        createContentSuccessDto.setCurrentLevel(modifyUserCoinDto.getCurrentLevel());
        return response ;
    }
}
