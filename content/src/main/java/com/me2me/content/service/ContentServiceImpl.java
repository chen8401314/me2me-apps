package com.me2me.content.service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dto.*;
import com.me2me.content.model.*;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.dto.UserInfoDto;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
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
    public Response highQuality(int sinceId,long uid) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.highQuality(sinceId);
        buildDatas(squareDataDto, contents, uid);
        return Response.success(squareDataDto);
    }

    private void buildDatas(SquareDataDto squareDataDto, List<Content> contents, long uid) {
        for(Content content : contents){
            SquareDataDto.SquareDataElement squareDataElement = SquareDataDto.createElement();
            squareDataElement.setId(content.getId());
            squareDataElement.setUid(content.getUid());
            squareDataElement.setForwardCid(content.getForwardCid());
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            squareDataElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            squareDataElement.setNickName(userProfile.getNickName());
            squareDataElement.setContent(content.getContent());
            squareDataElement.setTitle(content.getTitle());
            squareDataElement.setFeeling(content.getFeeling());
            ContentTags contentTags = contentMybatisDao.getContentTags(content.getFeeling());
            squareDataElement.setTid(contentTags.getId());
            squareDataElement.setType(content.getType());
            squareDataElement.setIsLike(isLike(uid,content.getId()));
            squareDataElement.setCreateTime(content.getCreateTime());
            if(!StringUtils.isEmpty(content.getConverImage())) {
                squareDataElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
            }else{
                squareDataElement.setCoverImage("");
            }
            ContentUserLikesCount c = new ContentUserLikesCount();
            c.setTid(contentTags.getId());
            c.setCid(content.getId());
            int likesCount = contentMybatisDao.getContentUserLikesCount(content.getId(),contentTags.getId());
            squareDataElement.setLikeCount(likesCount);
            squareDataElement.setHotValue(content.getHotValue());
            if(!StringUtils.isEmpty(content.getThumbnail())) {
                squareDataElement.setThumbnail(Constant.QINIU_DOMAIN + "/" + content.getThumbnail());
            }else{
                squareDataElement.setThumbnail("");
            }
            squareDataElement.setThumbnail(Constant.QINIU_DOMAIN + "/" + content.getThumbnail());
            squareDataElement.setForwardTitle(content.getForwardTitle());
            squareDataElement.setContentType(content.getContentType());
            squareDataElement.setForwardUrl(content.getForwardUrl());
            squareDataDto.getResults().add(squareDataElement);
        }
    }



    @Override
    public Response square(int sinceId,long uid) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.loadSquareData(sinceId);
        buildDatas(squareDataDto, contents,uid);
        return Response.success(squareDataDto);
    }

    @Override
    public Response publish(ContentDto contentDto) {
        CreateContentSuccessDto createContentSuccessDto = new CreateContentSuccessDto();
        String coverImage = "" ;
        Content content = new Content();
        content.setUid(contentDto.getUid());
        content.setContent(contentDto.getContent());
        content.setFeeling(contentDto.getFeeling());
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(contentDto.getFeeling());
        //保存感受标签
        contentMybatisDao.createTag(contentTags);
        contentTags = contentMybatisDao.getContentTags(content.getFeeling());
        if(!StringUtils.isEmpty(contentDto.getImageUrls())){
            String[] images = contentDto.getImageUrls().split(";");
            // 设置封面
            content.setConverImage(images[0]);
            coverImage = images[0] ;
        }
        content.setType(contentDto.getType());
        if(content.getType() == Specification.ArticleType.ORIGIN.index){
            // 原生文章
        }else if(content.getType() == Specification.ArticleType.FORWARD.index){
            // 转载文章
            long forwardCid = contentDto.getForwardCid();
            // TODO: 2016/3/25 添加转载
            content.setForwardUrl("");
            content.setForwardTitle("");
        }
        content.setContentType(contentDto.getContentType());
        contentMybatisDao.createContent(content);
        Content c = contentMybatisDao.getContentById(content.getId());
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
        createContentSuccessDto.setContent(c.getContent());
        createContentSuccessDto.setCreateTime(c.getCreateTime());
        createContentSuccessDto.setUid(c.getUid());
        createContentSuccessDto.setId(c.getId());
        createContentSuccessDto.setFeeling(c.getFeeling());
        createContentSuccessDto.setType(c.getType());
        createContentSuccessDto.setContentType(c.getContentType());
        createContentSuccessDto.setForwardCid(c.getForwardCid());
        if(!StringUtils.isEmpty(coverImage)) {
            createContentSuccessDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + coverImage);
        }else{
            createContentSuccessDto.setCoverImage("");
        }
        createContentSuccessDto.setTid(contentTags.getId());
        //创建标签的点赞数量
        ContentUserLikesCount contentUserLikesCount = new ContentUserLikesCount();
        contentUserLikesCount.setCid(content.getId());
        contentUserLikesCount.setTid(contentTags.getId());
        contentUserLikesCount.setLikecount(0);
        contentMybatisDao.addContentUserLikesCount(contentUserLikesCount);
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message,createContentSuccessDto);
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
            //记录点赞流水
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(likeDto.getUid());
            UserProfile customerProfile = userMybatisDao.getUserProfileByUid(likeDto.getCustomerId());
            Content content = contentMybatisDao.getContentById(likeDto.getCid());
            ContentImage contentImage = contentMybatisDao.getCoverImages(likeDto.getCid());
            ContentTags contentTags = contentMybatisDao.getContentTagsById(likeDto.getTid());
            UserNotice userNotice = new UserNotice();
            userNotice.setFromNickName(userProfile.getNickName());
            userNotice.setTag(contentTags.getTag());
            userNotice.setFromAvatar(userProfile.getAvatar());
            userNotice.setFromUid(userProfile.getUid());
            userNotice.setToNickName(customerProfile.getNickName());
            userNotice.setNoticeType(Specification.UserNoticeType.TAG.index);
            userNotice.setReadStatus(userNotice.getReadStatus());
            userNotice.setCid(likeDto.getCid());
            if(contentImage != null){
                userNotice.setCoverImage(contentImage.getImage());
                userNotice.setSummary("");
            }else{
                userNotice.setCoverImage("");
                userNotice.setSummary(content.getContent());

            }
            userNotice.setToUid(customerProfile.getUid());
            //// TODO: 2016/4/5 点赞数量
            userNotice.setLikeCount(0);
            userNotice.setReadStatus(Specification.NoticeReadStatus.UNREAD.index);
            userMybatisDao.createUserNotice(userNotice);
            UserTips userTips = new UserTips();
            userTips.setUid(likeDto.getCustomerId());
            userTips.setType(Specification.UserTipsType.LIKE.index);
            UserTips tips  = userMybatisDao.getUserTips(userTips);
            if(tips == null){
                userTips.setCount(1);
                userMybatisDao.createUserTips(userTips);
            }else{
                userTips.setCount(tips.getCount()+1);
                userMybatisDao.modifyUserTips(userTips);
            }
            //记录点赞流水 end

            //点赞时候点赞数量+1
            ContentUserLikesCount contentUserLikesCount = new ContentUserLikesCount();
            contentUserLikesCount.setTid(likeDto.getTid());
            contentUserLikesCount.setCid(likeDto.getCid());
            contentUserLikesCount.setLikecount(1);
            contentMybatisDao.likeTagCount(contentUserLikesCount);
        }else{
            addCount = -1;
            contentMybatisDao.deleteUserLikes(c.getId());

            //取消点赞时候点赞数量-1
            ContentUserLikesCount contentUserLikesCount = new ContentUserLikesCount();
            contentUserLikesCount.setTid(likeDto.getTid());
            contentUserLikesCount.setCid(likeDto.getCid());
            contentUserLikesCount.setLikecount(-1);
            contentMybatisDao.likeTagCount(contentUserLikesCount);
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
        contentTags = contentMybatisDao.getContentTags(writeTagDto.getTag());
        ContentTagLikes contentTagLikes = new ContentTagLikes();
        contentTagLikes.setUid(writeTagDto.getUid());
        contentTagLikes.setCid(writeTagDto.getCid());
        contentTagLikes.setTagId(contentTags.getId());
        contentMybatisDao.createContentTagLikes(contentTagLikes);
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(writeTagDto.getUid());
        UserProfile customerProfile = userMybatisDao.getUserProfileByUid(writeTagDto.getCustomerId());
        Content content = contentMybatisDao.getContentById(writeTagDto.getCid());
        ContentImage contentImage = contentMybatisDao.getCoverImages(writeTagDto.getCid());
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setTag(writeTagDto.getTag());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setNoticeType(Specification.UserNoticeType.TAG.index);
        userNotice.setReadStatus(userNotice.getReadStatus());
        userNotice.setCid(writeTagDto.getCid());
        if(contentImage != null){
            userNotice.setCoverImage(contentImage.getImage());
        }else{
            userNotice.setSummary(content.getContent());
        }
        userNotice.setToUid(customerProfile.getUid());
        userNotice.setLikeCount(0);
        userNotice.setReadStatus(Specification.NoticeReadStatus.UNREAD.index);

        userMybatisDao.createUserNotice(userNotice);
        UserTips userTips = new UserTips();
        userTips.setUid(writeTagDto.getCustomerId());
        userTips.setType(Specification.UserTipsType.LIKE.index);
        UserTips tips  = userMybatisDao.getUserTips(userTips);
        if(tips == null){
            userTips.setCount(1);
            userMybatisDao.createUserTips(userTips);
        }else{
            userTips.setCount(tips.getCount()+1);
            userMybatisDao.modifyUserTips(userTips);
        }
        //贴标签时候写标签点赞数量
        ContentUserLikesCount contentUserLikesCount = new ContentUserLikesCount();
        contentUserLikesCount.setCid(writeTagDto.getCid());
        contentUserLikesCount.setTid(contentTags.getId());
        contentUserLikesCount.setLikecount(0);
        contentMybatisDao.addContentUserLikesCount(contentUserLikesCount);

        //打标签的时候文章热度+1
        content.setHotValue(content.getHotValue()+1);
        contentMybatisDao.updateContentById(content);
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
    public Response getContentDetail(long id ,long uid) {
        ContentDetailDto contentDetailDto = new ContentDetailDto();
        Content content = contentMybatisDao.getContentById(id);
        contentDetailDto.setFeeling(content.getFeeling());
        contentDetailDto.setType(content.getType());
        contentDetailDto.setUid(content.getUid());
        contentDetailDto.setContent(content.getContent());
        contentDetailDto.setContentType(content.getContentType());
        contentDetailDto.setTitle(content.getTitle());
        contentDetailDto.setCoverImage("");
        List<ContentImage> contentImageList = contentMybatisDao.getContentImages(content.getId());
        for (ContentImage contentImage : contentImageList){
            if(contentImage.getCover() == Specification.CoverImageType.COVER.index){
                contentDetailDto.setCoverImage(Constant.QINIU_DOMAIN  + "/" + contentImage.getImage());
                break;
            }
        }
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(content.getUid());
        contentDetailDto.setNickName(userProfile.getNickName());
        contentDetailDto.setAvatar(Constant.QINIU_DOMAIN  + "/" + userProfile.getAvatar());
        contentDetailDto.setHotValue(content.getHotValue());
        contentDetailDto.setLikeCount(content.getLikeCount());
        contentDetailDto.setCreateTime(content.getCreateTime());
        contentDetailDto.setId(content.getId());
        ContentTags contentTags = contentMybatisDao.getContentTags(content.getFeeling());
        contentDetailDto.setTid(contentTags.getId());
        LikeDto likeDto = new LikeDto();
        likeDto.setTid(contentTags.getId());
        likeDto.setUid(uid);
        likeDto.setCid(content.getId());
        ContentUserLikes contentUserLikes = contentMybatisDao.getContentUserLike(likeDto);
        if(contentUserLikes == null) {
            contentDetailDto.setIsLike(0);
        }else{
            contentDetailDto.setIsLike(1);
        }
        int likeCount = contentMybatisDao.getContentUserLikesCount(content.getId(),contentTags.getId());
        contentDetailDto.setLikeCount(likeCount);
        List<Map<String,String>> list  = contentMybatisDao.loadAllFeeling(content.getId(),5);
        for (Map map : list){
            ContentDetailDto.ContentTop5FeelingElement contentTop5FeelingElement = ContentDetailDto.createElement();
            contentTop5FeelingElement.setTag(map.get("tag").toString());
            contentTop5FeelingElement.setTid(Long.parseLong(map.get("tag_id").toString()));
            contentTop5FeelingElement.setCid(Long.parseLong(map.get("cid").toString()));
            LikeDto like = new LikeDto();
            like.setCid(contentTop5FeelingElement.getCid());
            like.setTid(contentTop5FeelingElement.getTid());
            like.setUid(uid);
            ContentUserLikes contentUserLike = contentMybatisDao.getContentUserLike(like);
            if(contentUserLike == null) {
                contentTop5FeelingElement.setIsLike(0);
            }else{
                contentTop5FeelingElement.setIsLike(1);
            }
            int count = contentMybatisDao.getContentUserLikesCount(content.getId(),contentTop5FeelingElement.getTid());
            contentTop5FeelingElement.setLikeCount(count);
            contentTop5FeelingElement.setUid(Long.parseLong(map.get("uid").toString()));
            contentDetailDto.getTags().add(contentTop5FeelingElement);
        }
        return Response.success(contentDetailDto);
    }


    @Override
    public Response myPublish(long uid ,int sinceId) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.myPublish(uid,sinceId);
        buildDatas(squareDataDto, contents, uid);
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

    public Response getUserData(long uid){
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        List<Content> list = contentMybatisDao.myPublish(uid,5);
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.getUser().setNickName(userProfile.getNickName());
        userInfoDto.getUser().setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        userInfoDto.getUser().setGender(userProfile.getGender());
        userInfoDto.getUser().setUid(userProfile.getUid());
        for (Content content : list){
            UserInfoDto.ContentElement contentElement = UserInfoDto.createElement();
            contentElement.setFeeling(content.getFeeling());
            contentElement.setContent(content.getContent());
            contentElement.setCid(content.getId());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setHotValue(content.getHotValue());
            contentElement.setLikeCount(0);
            contentElement.setHotValue(content.getHotValue());
            contentElement.setAuthorization(content.getAuthorization());
            contentElement.setContentType(content.getContentType());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setThumbnail(content.getThumbnail());
            contentElement.setType(content.getType());
            contentElement.setForwardTitle(content.getForwardTitle());
            ContentImage contentImage = contentMybatisDao.getCoverImages(content.getId());
            if(contentImage != null) {
                contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
            }else{
                contentElement.setCoverImage("");
            }
            ContentTags contentTags =  contentMybatisDao.getContentTags(content.getFeeling());
            contentElement.setTid(contentTags.getId());
            userInfoDto.getContentElementList().add(contentElement);
        }
        return Response.success(userInfoDto);
    }


    @Override
    public Response editorPublish(ContentDto contentDto) {
        CreateContentSuccessDto createContentSuccessDto = new CreateContentSuccessDto();
        Content content = new Content();
        content.setUid(contentDto.getUid());
        content.setContent(contentDto.getContent());
        if(!StringUtils.isEmpty(contentDto.getFeeling()) && contentDto.getFeeling().contains(";")){
            content.setFeeling(contentDto.getFeeling().split(";")[0]);
            String[] tags = contentDto.getFeeling().split(";");
            for(String t : tags) {
                ContentTags contentTags = new ContentTags();
                contentTags.setTag(t);
                contentMybatisDao.createTag(contentTags);
            }
        }else{
            content.setFeeling(contentDto.getFeeling());
            ContentTags contentTags = new ContentTags();
            contentTags.setTag(content.getFeeling());
            contentMybatisDao.createTag(contentTags);
        }
        content.setConverImage(contentDto.getImageUrls());
        content.setTitle(contentDto.getTitle());
        content.setType(contentDto.getType());
        content.setContentType(contentDto.getContentType());
        contentMybatisDao.createContent(content);
        Content c = contentMybatisDao.getContentById(content.getId());
        createContentSuccessDto.setContent(c.getContent());
        createContentSuccessDto.setCreateTime(c.getCreateTime());
        createContentSuccessDto.setUid(c.getUid());
        createContentSuccessDto.setId(c.getId());
        createContentSuccessDto.setFeeling(c.getFeeling());
        createContentSuccessDto.setType(c.getType());
        createContentSuccessDto.setContentType(c.getContentType());
        createContentSuccessDto.setForwardCid(c.getForwardCid());
        createContentSuccessDto.setCoverImage(c.getConverImage());
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message,createContentSuccessDto);
    }


}
