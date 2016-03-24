package com.me2me.content.service;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.SquareDataDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.mapper.ContentMapper;
import com.me2me.content.mapper.ContentUserLikeMapper;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentImage;
import com.me2me.content.model.ContentUserLike;
import com.me2me.content.model.ContentUserLikeExample;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserProfileExample;
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

    @Autowired
    private ContentUserLikeMapper contentUserLikeMapper;

    @Autowired
    private ContentMapper contentMapper;

    @Override
    public Response recommend(int sinceId) {
        return null;
    }

    @Override
    public Response square(int sinceId) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.loadSquareData(sinceId);
        for(Content content : contents){
            SquareDataDto.SquareDataElement squareDataElement = SquareDataDto.createElement();
            squareDataElement.setId(content.getId());
            squareDataElement.setUid(content.getUid());
            squareDataElement.setForwardCid(content.getForwardCid());
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            squareDataElement.setAvatar(userProfile.getAvatar());
            squareDataElement.setNickName(userProfile.getNickName());
            squareDataElement.setContent(content.getContent());
            squareDataElement.setFeeling(content.getFeeling());
            squareDataElement.setType(content.getType());
            squareDataElement.setCreateTime(content.getCreateTime());
            squareDataElement.setCoverImage(content.getConverImage());
            squareDataElement.setLikeCount(content.getLikeCount());
            squareDataElement.setHotValue(content.getHotValue());
            squareDataElement.setThumbnail(content.getThumbnail());
            squareDataElement.setForwardTitle(content.getForwardTitle());
            squareDataElement.setContentType(content.getContentType());
            squareDataElement.setForwardUrl(content.getForwardUrl());
            squareDataDto.getSquareDatas().add(squareDataElement);
        }
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
        ContentUserLikeExample example = new ContentUserLikeExample();
        ContentUserLikeExample.Criteria criteria = example.createCriteria();
        criteria.andCidEqualTo(likeDto.getCid());
        criteria.andUidEqualTo(likeDto.getUid());
        List<ContentUserLike> list = contentUserLikeMapper.selectByExample(example);
        ContentUserLike c = list.size() > 0 ? list.get(0) : null;
        if(c == null){
            ContentUserLike contentUserLike = new ContentUserLike();
            contentUserLike.setUid(likeDto.getUid());
            contentUserLike.setCid(likeDto.getCid());
            contentUserLikeMapper.insertSelective(contentUserLike);
        }else{
            addCount = -1;
            contentUserLikeMapper.deleteByPrimaryKey(c.getId());
        }
        Content content = contentMapper.selectByPrimaryKey(likeDto.getCid());
        content.setLikeCount(content.getLikeCount() + addCount );
        contentMapper.updateByPrimaryKey(content);
        return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status ,ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
    }

    @Override
    public Response writeTag(WriteTagDto writeTagDto) {
        return null;
    }


}
