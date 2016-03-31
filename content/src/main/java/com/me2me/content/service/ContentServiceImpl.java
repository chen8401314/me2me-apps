package com.me2me.content.service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dto.*;
import com.me2me.content.mapper.ContentMapper;
import com.me2me.content.model.*;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.dto.UserInfoDto;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Service
public class ContentServiceImpl implements ContentService {


    @Autowired
    private ContentMybatisDao contentMybatisDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMybatisDao userMybatisDao;

    @Override
    public Response highQuality(int sinceId) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.highQuality(sinceId);
        buildDatas(squareDataDto, contents);
        return Response.success(squareDataDto);
    }

    private void buildDatas(SquareDataDto squareDataDto, List<Content> contents) {
        for(Content content : contents){
            SquareDataDto.SquareDataElement squareDataElement = SquareDataDto.createElement();
            squareDataElement.setId(content.getId());
            squareDataElement.setUid(content.getUid());
            squareDataElement.setForwardCid(content.getForwardCid());
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            squareDataElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            squareDataElement.setNickName(userProfile.getNickName());
            squareDataElement.setContent(content.getContent());
            squareDataElement.setFeeling(content.getFeeling());
            ContentTags contentTags = contentMybatisDao.getContentTags(content.getFeeling());
            squareDataElement.setTid(contentTags.getId());
            squareDataElement.setType(content.getType());
            squareDataElement.setIsLike(isLike(userProfile.getUid(),content.getId()));
            squareDataElement.setCreateTime(content.getCreateTime());
            squareDataElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
            squareDataElement.setLikeCount(content.getLikeCount());
            squareDataElement.setHotValue(content.getHotValue());
            squareDataElement.setThumbnail(Constant.QINIU_DOMAIN + "/" + content.getThumbnail());
            squareDataElement.setForwardTitle(content.getForwardTitle());
            squareDataElement.setContentType(content.getContentType());
            squareDataElement.setForwardUrl(content.getForwardUrl());
            squareDataDto.getResults().add(squareDataElement);
        }
    }



    @Override
    public Response square(int sinceId) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.loadSquareData(sinceId);
        buildDatas(squareDataDto, contents);
        return Response.success(squareDataDto);
    }

    @Override
    public Response publish(ContentDto contentDto) {
        Content content = new Content();
        content.setUid(contentDto.getUid());
        content.setContent(contentDto.getContent());
        content.setFeeling(contentDto.getFeeling());
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(contentDto.getFeeling());
        contentMybatisDao.createTag(contentTags);
        if(!StringUtils.isEmpty(contentDto.getImageUrls())){
            String[] images = contentDto.getImageUrls().split(";");
            // 设置封面
            content.setConverImage(images[0]);
        }
        content.setType(contentDto.getType());
        if(content.getType()== Specification.ArticleType.ORIGIN.index){
            // 原生文章
        }else if(content.getType()== Specification.ArticleType.FORWARD.index){
            // 转载文章
            long forwardCid = contentDto.getForwardCid();
            // // TODO: 2016/3/25 添加转载
            content.setForwardUrl("");
            content.setForwardTitle("");
        }
        content.setContentType(contentDto.getContentType());
        contentMybatisDao.createContent(content);
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
                contentMybatisDao.createContentImage(contentImage);
            }
        }
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message,contentDto);
}

    /**
     * 点赞
     * @return
     */
    @Override
    public Response like(LikeDto likeDto) {
        int addCount = 1 ;
        ContentUserLikes c = contentMybatisDao.getContentUserLike(likeDto);
        if(c == null){
            ContentUserLikes contentUserLikes = new ContentUserLikes();
            contentUserLikes.setUid(likeDto.getUid());
            contentUserLikes.setCid(likeDto.getCid());
            contentUserLikes.setTagId(likeDto.getTid());
            contentMybatisDao.createContentUserLikes(contentUserLikes);
        }else{
            addCount = -1;
            contentMybatisDao.deleteUserLikes(c.getId());
        }
        Content content = contentMybatisDao.getContentById(likeDto.getCid());
        content.setLikeCount(content.getLikeCount() + addCount );
        contentMybatisDao.updateContentById(content);
        if(c == null) {
            return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status, ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
        }else{
            return Response.success(ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.status, ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.message);
        }
        }

    @Override
    public Response writeTag(WriteTagDto writeTagDto) {
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(writeTagDto.getTag());
        contentMybatisDao.createTag(contentTags);
        ContentTagLikes contentTagLikes = new ContentTagLikes();
        contentTagLikes.setUid(writeTagDto.getUid());
        contentTagLikes.setCid(writeTagDto.getCid());
        contentTagLikes.setTagId(contentTags.getId());
        contentMybatisDao.createContentTagLikes(contentTagLikes);
        return Response.success(ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.status,ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.message);
    }

    @Override
    public Response deleteContent(long id) {
        Content content = contentMybatisDao.getContentById(id);
        content.setStatus(Specification.ContentStatus.DELETE.index);
        contentMybatisDao.updateContentById(content);
        return Response.failure(ResponseStatus.CONTENT_DELETE_SUCCESS.status,ResponseStatus.CONTENT_DELETE_SUCCESS.message);
    }

    @Override
    public int isLike(long uid, long cid) {
        return contentMybatisDao.isLike(uid,cid);
    }

    @Override
    public Response getContentDetail(long id) {
        ContentDetailDto contentDetailDto = new ContentDetailDto();
        Content content = contentMybatisDao.getContentById(id);
        contentDetailDto.setFeeling(content.getFeeling());
        contentDetailDto.setForwardCid(content.getForwardCid());
        contentDetailDto.setType(content.getType());
        contentDetailDto.setContentType(content.getType());
        contentDetailDto.setCoverImage(content.getContent());
        contentDetailDto.setForwardTitle(content.getForwardTitle());
        contentDetailDto.setUid(content.getUid());
        contentDetailDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
        contentDetailDto.setContent(content.getContent());
        contentDetailDto.setAuthorization(content.getAuthorization());
        contentDetailDto.setHotValue(content.getHotValue());
        contentDetailDto.setLikeCount(content.getLikeCount());
        contentDetailDto.setCreateTime(content.getCreateTime());
        contentDetailDto.setThumbnail(content.getThumbnail());
        return Response.success(contentDetailDto);
    }


    @Override
    public Response myPublish(long uid ,int sinceId) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.myPublish(uid,sinceId);
        buildDatas(squareDataDto, contents);
        return Response.success(squareDataDto);
    }

    @Override
    public Response getContentFeeling(long cid, int sinceId) {
        List<Map<String,String>> result = contentMybatisDao.loadAllFeeling(cid ,sinceId);
        ContentAllFeelingDto contentAllFeelingDto = new ContentAllFeelingDto();
        buildContentFeelingDatas(contentAllFeelingDto,result);
        return Response.success(contentAllFeelingDto);

    }

    private void buildContentFeelingDatas(ContentAllFeelingDto contentAllFeelingDto, List<Map<String,String>> list) {
        for (Map map : list) {
            ContentAllFeelingDto.ContentAllFeelingElement contentAllFeelingElement = ContentAllFeelingDto.createElement();
            contentAllFeelingElement.setTag(map.get("tag").toString());
            contentAllFeelingElement.setUid(Long.parseLong(map.get("uid").toString()));
            contentAllFeelingElement.setAvatar(Constant.QINIU_DOMAIN + "/" + map.get("avatar"));
            contentAllFeelingElement.setTid(Long.parseLong(map.get("tag_id").toString()));
            contentAllFeelingElement.setCid(Long.parseLong(map.get("cid").toString()));
            contentAllFeelingElement.setForwardTitle(map.get("forward_title").toString());
            contentAllFeelingElement.setNickName(map.get("nick_name").toString());
            contentAllFeelingElement.setLikesCounts(Integer.parseInt(map.get("like_count")== null? "0":map.get("like_count").toString()));
            contentAllFeelingDto.getResults().add(contentAllFeelingElement);
        }
    }
    @Override
    public ContentH5Dto getContent(long id) {
        ContentH5Dto contentH5Dto = new ContentH5Dto();
        Content content = contentMybatisDao.getContentById(id);
        List<ContentImage> list = contentMybatisDao.getContentImages(id);
        for (ContentImage contentImage : list){
            if(contentImage.getCover() == 1){
                contentH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
            }if(contentImage.getCover() == 0){
                contentH5Dto.getImageUrls().add(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
            }
        }
        contentH5Dto.setContent(content.getContent());
        return contentH5Dto;
    }

    public Response getUserInfo(long uid){
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        List<Content> list = contentMybatisDao.myPublish(uid,Integer.MAX_VALUE);
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setNickName(userProfile.getNickName());
        userInfoDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        userInfoDto.setGender(userProfile.getGender());
        userInfoDto.setUid(userProfile.getUid());
        for (Content content : list){
            UserInfoDto.ContentElement contentElement = UserInfoDto.createElement();
            contentElement.setFeeling(content.getFeeling());
            contentElement.setContent(content.getContent());
            contentElement.setCid(content.getId());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setHotValue(content.getHotValue());
            contentElement.setLikeCount(0);
            contentElement.setHotValue(content.getHotValue());
            userInfoDto.getContentElementList().add(contentElement);

        }
        return Response.success(userInfoDto);
    }

}
