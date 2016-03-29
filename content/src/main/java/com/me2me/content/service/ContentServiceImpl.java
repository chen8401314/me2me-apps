package com.me2me.content.service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dto.*;
import com.me2me.content.mapper.ContentMapper;
import com.me2me.content.mapper.ContentUserLikeMapper;
import com.me2me.content.model.*;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message);
    }

    /**
     * 点赞
     * @return
     */
    @Override
    public Response like(LikeDto likeDto) {
        int addCount = 1 ;
        ContentUserLike c = contentMybatisDao.getContentUserLike(likeDto);
        if(c == null){
        ContentUserLike contentUserLike = new ContentUserLike();
        contentUserLike.setUid(likeDto.getUid());
        contentUserLike.setCid(likeDto.getCid());
        contentMybatisDao.createContentUserLike(contentUserLike);
        }else{
        addCount = -1;
        contentMybatisDao.deleteUserLike(c.getId());
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
        contentMybatisDao.createTag(writeTagDto);
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

        return null;
    }


}
