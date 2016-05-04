package com.me2me.content.service;

import com.google.common.collect.Lists;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dto.*;
import com.me2me.content.model.*;
import com.me2me.user.dto.UserInfoDto;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
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
    private ActivityService activityService;


    @Override
    public Response highQuality(int sinceId,long uid) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> contents = contentMybatisDao.highQuality(sinceId);
        buildDatas(squareDataDto, contents, uid);
        return Response.success(squareDataDto);
    }

    private List buildData(List<Content> activityData,long uid) {
        List<ShowContentListDto.ContentDataElement> result = Lists.newArrayList();
        for(Content content : activityData){
            ShowContentListDto.ContentDataElement contentDataElement = ShowContentListDto.createElement();
            contentDataElement.setId(content.getId());
            contentDataElement.setUid(content.getUid());
            contentDataElement.setForwardCid(content.getForwardCid());
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            contentDataElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentDataElement.setNickName(userProfile.getNickName());
            contentDataElement.setContent(content.getContent());
            contentDataElement.setTitle(content.getTitle());
            contentDataElement.setFeeling(content.getFeeling());
            ContentTags contentTags = contentMybatisDao.getContentTags(content.getFeeling());
            contentDataElement.setTid(contentTags.getId());
            contentDataElement.setType(content.getType());
            contentDataElement.setIsLike(isLike(uid,content.getId(),contentTags.getId()));
            contentDataElement.setCreateTime(content.getCreateTime());
            if(!StringUtils.isEmpty(content.getConverImage())) {
                contentDataElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
            }else{
                contentDataElement.setCoverImage("");
            }
            ContentUserLikesCount c = new ContentUserLikesCount();
            c.setTid(contentTags.getId());
            c.setCid(content.getId());
            int likesCount = contentMybatisDao.getContentUserLikesCount(content.getId(),contentTags.getId());
            contentDataElement.setLikeCount(likesCount);
            contentDataElement.setHotValue(content.getHotValue());
            if(!StringUtils.isEmpty(content.getThumbnail())) {
                contentDataElement.setThumbnail(Constant.QINIU_DOMAIN + "/" + content.getThumbnail());
            }else{
                contentDataElement.setThumbnail("");
            }
            contentDataElement.setThumbnail(Constant.QINIU_DOMAIN + "/" + content.getThumbnail());
            contentDataElement.setForwardTitle(content.getForwardTitle());
            contentDataElement.setContentType(content.getContentType());
            contentDataElement.setForwardUrl(content.getForwardUrl());
            long contentUid = content.getUid();
            int follow = userService.isFollow(contentUid,uid);
            contentDataElement.setIsFollow(follow);
            //如果是直播需要一个直播状态，当前用户是否收藏
            setLiveStatusAndFavorite(uid, content, contentDataElement);
            //设置数量
            List<LoadAllFeelingDto> list  = contentMybatisDao.loadAllFeeling(content.getId(),Integer.MAX_VALUE);
            int i = 0;
            for (LoadAllFeelingDto loadAllFeelingDto : list) {
                //只展示3条感受
                if (i > 3) {
                    break;
                }
                LikeDto likeDto = new LikeDto();
                likeDto.setCid(loadAllFeelingDto.getCid());
                likeDto.setUid(loadAllFeelingDto.getUid());
                //likeDto.setCustomerId(uid);
                //likeDto.setTid(loadAllFeelingDto.getTid());
                ContentUserLikes contentUserLikes = contentMybatisDao.getContentUserLike(likeDto);
                ShowContentListDto.ContentDataElement.TagElement tagElement = contentDataElement.createElement();
                if (contentUserLikes == null) {
                    tagElement.setIsLike(Specification.IsLike.UNLIKE.index);
                } else {
                    tagElement.setIsLike(Specification.IsLike.LIKE.index);
                }
                int likeCount = contentMybatisDao.getContentUserLikesCount(content.getId(), loadAllFeelingDto.getTid());
                tagElement.setLikeCount(likeCount);
                tagElement.setTag(loadAllFeelingDto.getTag());
                contentDataElement.getTags().add(tagElement);
                i++;
            }
            result.add(contentDataElement);
        }
        return result;
    }

    private void setLiveStatusAndFavorite(long uid, Content content, ShowContentListDto.ContentDataElement contentDataElement) {
        if(content.getType() == Specification.ArticleType.LIVE.index) {
            //查询直播状态
            int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
            contentDataElement.setLiveStatus(status);
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
            //直播是否收藏
            contentDataElement.setFavorite(favorite);
        }
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
            squareDataElement.setIsLike(isLike(uid,content.getId(),contentTags.getId()));
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
            long contentUid = content.getUid();
            int follow = userService.isFollow(contentUid,uid);
            squareDataElement.setIsFollow(follow);
            //如果是直播需要一个直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index) {
                //查询直播状态
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                squareDataElement.setLiveStatus(status);
                int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
                //直播是否收藏
                squareDataElement.setFavorite(favorite);
            }
            List<LoadAllFeelingDto> list  = contentMybatisDao.loadAllFeeling(content.getId(),Integer.MAX_VALUE);
            int i = 0;
            for (LoadAllFeelingDto loadAllFeelingDto : list) {
                if (i > 3) {
                    break;
                }
                LikeDto likeDto = new LikeDto();
                likeDto.setCid(loadAllFeelingDto.getCid());
                likeDto.setUid(loadAllFeelingDto.getUid());
//                likeDto.setCustomerId(uid);
//                likeDto.setTid(loadAllFeelingDto.getTid());
                ContentUserLikes contentUserLikes = contentMybatisDao.getContentUserLike(likeDto);
                SquareDataDto.SquareDataElement.TagElement tagElement = squareDataElement.createElement();
                if (contentUserLikes == null) {
                    tagElement.setIsLike(Specification.IsLike.UNLIKE.index);
                } else {
                    tagElement.setIsLike(Specification.IsLike.LIKE.index);
                }
                int likeCount = contentMybatisDao.getContentUserLikesCount(content.getId(), loadAllFeelingDto.getTid());
                tagElement.setLikeCount(likeCount);
                tagElement.setTag(loadAllFeelingDto.getTag());
                squareDataElement.getTags().add(tagElement);
                i++;
            }
            squareDataElement.setRights(content.getRights());
            squareDataDto.getResults().add(squareDataElement);
        }
    }



    @Override
    public Response square(int sinceId,long uid) {
        ShowContentListDto showContentListDto = new ShowContentListDto();
        // SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> list = Lists.newArrayList();
        if(Integer.MAX_VALUE == sinceId) {
            list = contentMybatisDao.loadActivityData(sinceId);
        }

        List<Content> contents = contentMybatisDao.loadSquareData(sinceId);

        showContentListDto.getActivityData().addAll(buildData(list,uid));
        showContentListDto.getSquareData().addAll(buildData(contents,uid));
        //buildDatas(squareDataDto, contents,uid);
        return Response.success(showContentListDto);
    }

    @Override
    public Response publish(ContentDto contentDto) {
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
        if(content.getType() == Specification.ArticleType.ORIGIN.index){
            // 原生文章
        }else if(content.getType() == Specification.ArticleType.FORWARD.index){
            // 转载文章
            long forwardCid = contentDto.getForwardCid();
            Content forwardContent = contentMybatisDao.getContentById(forwardCid);
            content.setForwardCid(forwardCid);
            content.setForwardUrl(Constant.FORWARD_URL_TEST+forwardCid);
            content.setForwardTitle(forwardContent.getTitle());
            content.setThumbnail(forwardContent.getConverImage());
        }else if(content.getType() == Specification.ArticleType.LIVE.index){
            content.setForwardCid(contentDto.getForwardCid());
        }
        content.setContentType(contentDto.getContentType());
        content.setRights(contentDto.getRights());
        //保存内容
        contentMybatisDao.createContent(content);
        //创建标签
        createTag(contentDto,content);
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
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message,createContentSuccessDto);
}


    /**
     * 点赞
     * @return
     */
    @Override
    public Response like(LikeDto likeDto) {
        Content content = contentMybatisDao.getContentById(likeDto.getCid());
        if(content == null){
            return Response.failure(ResponseStatus.CONTENT_LIKES_ERROR.status,ResponseStatus.CONTENT_LIKES_ERROR.message);
        }else{
            //取消点赞
            if(likeDto.getAction() == Specification.IsLike.LIKE.index){
                content.setLikeCount(content.getLikeCount() -1);
                contentMybatisDao.updateContentById(content);
                return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
            }else{
                content.setLikeCount(content.getLikeCount() +1);
                contentMybatisDao.updateContentById(content);
                remind(content,likeDto.getUid(),Specification.UserNoticeType.LIKE.index);
                return Response.success(ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.message);
            }
        }
    }

    private void remind(Content content ,long uid ,int type){
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        UserProfile customerProfile = userService.getUserProfileByUid(content.getUid());
        ContentImage contentImage = contentMybatisDao.getCoverImages(content.getId());
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(Constant.QINIU_DOMAIN  + "/" + userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setNoticeType(type);
        userNotice.setReadStatus(userNotice.getReadStatus());
        userNotice.setCid(content.getId());
        if(contentImage != null){
            userNotice.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
            userNotice.setSummary("");
        }else{
            userNotice.setCoverImage("");
            if(content.getContent().length() > 50) {
                userNotice.setSummary(content.getContent().substring(0,50));
            }else{
                userNotice.setSummary(content.getContent());
            }

        }
        userNotice.setToUid(customerProfile.getUid());
        userNotice.setLikeCount(0);
        userNotice.setReadStatus(type);
        userService.createUserNotice(userNotice);
        UserTips userTips = new UserTips();
        userTips.setUid(content.getUid());
        userTips.setType(Specification.UserTipsType.LIKE.index);
        UserTips tips  =  userService.getUserTips(userTips);
        if(tips == null){
            userTips.setCount(1);
            userService.createUserTips(userTips);
        }else{
            userTips.setCount(tips.getCount()+1);
            userService.modifyUserTips(userTips);
        }
    }

    /**
     * 点赞
     * @return
     */
  /*  @Override
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
            UserProfile userProfile = userService.getUserProfileByUid(likeDto.getUid());
            UserProfile customerProfile = userService.getUserProfileByUid(likeDto.getCustomerId());
            Content content = contentMybatisDao.getContentById(likeDto.getCid());
            ContentImage contentImage = contentMybatisDao.getCoverImages(likeDto.getCid());
            ContentTags contentTags = contentMybatisDao.getContentTagsById(likeDto.getTid());
            UserNotice userNotice = new UserNotice();
            userNotice.setFromNickName(userProfile.getNickName());
            userNotice.setTag(contentTags.getTag());
            userNotice.setFromAvatar(userProfile.getAvatar());
            userNotice.setFromUid(userProfile.getUid());
            userNotice.setToNickName(customerProfile.getNickName());
            userNotice.setNoticeType(Specification.UserNoticeType.LIKE.index);
            userNotice.setReadStatus(userNotice.getReadStatus());
            userNotice.setCid(likeDto.getCid());
            if(contentImage != null){
                userNotice.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
                userNotice.setSummary("");
            }else{
                userNotice.setCoverImage("");
                if(content.getContent().length() > 50) {
                    userNotice.setSummary(content.getContent().substring(0,50));
                }else{
                    userNotice.setSummary(content.getContent());
                }

            }
            userNotice.setToUid(customerProfile.getUid());
            userNotice.setLikeCount(0);
            userNotice.setReadStatus(Specification.NoticeReadStatus.UNREAD.index);
            userService.createUserNotice(userNotice);
            UserTips userTips = new UserTips();
            userTips.setUid(likeDto.getCustomerId());
            userTips.setType(Specification.UserTipsType.LIKE.index);
            UserTips tips  =  userService.getUserTips(userTips);
            if(tips == null){
                userTips.setCount(1);
                userService.createUserTips(userTips);
            }else{
                userTips.setCount(tips.getCount()+1);
                userService.modifyUserTips(userTips);
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
    }*/

    @Override
    public Response writeTag(WriteTagDto writeTagDto) {
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(writeTagDto.getTag());
        contentMybatisDao.createTag(contentTags);
        ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
        contentTagsDetails.setTid(contentTags.getId());
        contentTagsDetails.setCid(writeTagDto.getCid());
        contentMybatisDao.createContentTagsDetails(contentTagsDetails);
        Content content = contentMybatisDao.getContentById(writeTagDto.getCid());
        //添加贴标签提醒
        remind(content,writeTagDto.getUid(),Specification.UserNoticeType.TAG.index);
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
        //直播删除
        if(content.getType() == Specification.ArticleType.LIVE.index) {
            contentMybatisDao.deleteTopicById(content.getForwardCid());
        }
        return Response.failure(ResponseStatus.CONTENT_DELETE_SUCCESS.status,ResponseStatus.CONTENT_DELETE_SUCCESS.message);
    }

    private int isLike(long uid, long cid,long tid) {
        return contentMybatisDao.isLike(uid,cid,tid);
    }

    @Override
    public Response getContentDetail(long id ,long uid) {
        ContentDetailDto contentDetailDto = new ContentDetailDto();
        Content content = contentMybatisDao.getContentById(id);
        if(content == null){
            return Response.failure(ResponseStatus.DATA_DOES_NOT_EXIST.status,ResponseStatus.DATA_DOES_NOT_EXIST.message);
        }else if(content.getStatus() == Specification.ContentStatus.DELETE.index){
            return Response.failure(ResponseStatus.DATA_IS_DELETE.status,ResponseStatus.DATA_IS_DELETE.message);
        }
        contentDetailDto.setFeeling(content.getFeeling());
        contentDetailDto.setType(content.getType());
        contentDetailDto.setUid(content.getUid());
        contentDetailDto.setContent(content.getContent());
        contentDetailDto.setContentType(content.getContentType());
        contentDetailDto.setTitle(content.getTitle());
        String cover = content.getConverImage();
        if(!StringUtils.isEmpty(cover)) {
            contentDetailDto.setCoverImage(Constant.QINIU_DOMAIN  + "/" + content.getConverImage());
        }
        UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
        contentDetailDto.setNickName(userProfile.getNickName());
        contentDetailDto.setAvatar(Constant.QINIU_DOMAIN  + "/" + userProfile.getAvatar());
        contentDetailDto.setHotValue(content.getHotValue());
        contentDetailDto.setLikeCount(content.getLikeCount());
        contentDetailDto.setCreateTime(content.getCreateTime());
        contentDetailDto.setId(content.getId());
        // 获取感受标签前5条
        List<ContentTagsDetails> list  = contentMybatisDao.getContentTagsDetails(content.getId(),Integer.MAX_VALUE);
        for (ContentTagsDetails contentTagsDetails : list){
            ContentDetailDto.ContentTop5FeelingElement contentTop5FeelingElement = ContentDetailDto.createElement();
            ContentTags contentTags = contentMybatisDao.getContentTagsById(contentTagsDetails.getTid());
            contentTop5FeelingElement.setTag(contentTags.getTag());
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
        /**
         * 1. 文章
         2. 该文章被多少次转载
         3. 取出转载内容 + 转载tag
         */
        ContentAllFeelingDto contentAllFeelingDto = new ContentAllFeelingDto();
        List<ContentTagLikes> list = contentMybatisDao.getForwardContents(cid);
        for(ContentTagLikes contentTagLike : list){
            Content content = contentMybatisDao.getContentById(contentTagLike.getCid());
            ContentAllFeelingDto.ContentAllFeelingElement contentAllFeelingElement = ContentAllFeelingDto.createElement();
            // 转载内容
            if(content.getForwardCid()>0){
                contentAllFeelingElement.setType(Specification.IsForward.FORWARD.index);
                contentAllFeelingElement.setContent(content.getContent());
            }else{
                contentAllFeelingElement.setType(Specification.IsForward.NATIVE.index);
                contentAllFeelingElement.setContent("");
            }
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            contentAllFeelingElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentAllFeelingElement.setTid(contentTagLike.getTagId());
            contentAllFeelingElement.setNickName(userProfile.getNickName());
            int likeCount = contentMybatisDao.getContentUserLikesCount(contentTagLike.getCid(),contentTagLike.getTagId());
            contentAllFeelingElement.setLikesCount(likeCount);
            contentAllFeelingElement.setCid(contentTagLike.getCid());
            contentAllFeelingElement.setTag(content.getFeeling());
            contentAllFeelingDto.getResults().add(contentAllFeelingElement);
        }
        return Response.success(contentAllFeelingDto);

    }

    @Override
    public ContentH5Dto getContent(long id) {
        ContentH5Dto contentH5Dto = new ContentH5Dto();
        Content content = contentMybatisDao.getContentById(id);
        if(content ==null){
           return null;
        }
        List<ContentImage> list = contentMybatisDao.getContentImages(id);
        for (ContentImage contentImage : list){
            if(contentImage.getCover() == 1){
                contentH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
            }
            if(contentImage.getCover() == 0){
                contentH5Dto.getImageUrls().add(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
            }
        }
        if(content.getType()==Specification.ArticleType.EDITOR.index){
            contentH5Dto.setCoverImage(content.getConverImage());
        }
        contentH5Dto.setTitle(content.getTitle());
        contentH5Dto.setType(content.getType());
        contentH5Dto.setContent(content.getContent());
        return contentH5Dto;
    }

    public Response getUserData(long targetUid,long sourceUid){
        UserProfile userProfile = userService.getUserProfileByUid(targetUid);
        List<Content> list = contentMybatisDao.myPublish(targetUid,Integer.MAX_VALUE);
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.getUser().setNickName(userProfile.getNickName());
        userInfoDto.getUser().setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        userInfoDto.getUser().setGender(userProfile.getGender());
        userInfoDto.getUser().setUid(userProfile.getUid());
        userInfoDto.getUser().setIsFollow(userService.isFollow(targetUid,sourceUid));
        for (Content content : list){
            UserInfoDto.ContentElement contentElement = UserInfoDto.createElement();
            contentElement.setFeeling(content.getFeeling());
            contentElement.setContent(content.getContent());
            contentElement.setCid(content.getId());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setHotValue(content.getHotValue());
            contentElement.setLikeCount(content.getLikeCount());
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
        content.setFeeling(contentDto.getFeeling());
        content.setConverImage(contentDto.getImageUrls());
        content.setTitle(contentDto.getTitle());
        content.setType(contentDto.getType());
        content.setContentType(contentDto.getContentType());
        content.setStatus(Specification.ContentStatus.RECOVER.index);
        contentMybatisDao.createContent(content);
        //保存标签
        createTag(contentDto, content);
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

//        // 内容自动加精
//        HighQualityContent hdc = new HighQualityContent();
//        hdc.setCid(c.getId());
//        contentMybatisDao.createHighQualityContent(hdc);
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message,createContentSuccessDto);
    }

    private void createTag(ContentDto contentDto, Content content) {
        if(!StringUtils.isEmpty(contentDto.getFeeling()) && contentDto.getFeeling().contains(";")){
            String[] tags = contentDto.getFeeling().split(";");
            for(String t : tags) {
                ContentTags contentTags = new ContentTags();
                contentTags.setTag(t);
                ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
                contentMybatisDao.createTag(contentTags);
                contentTagsDetails.setTid(contentTags.getId());
                contentTagsDetails.setCid(content.getId());
                contentMybatisDao.createContentTagsDetails(contentTagsDetails);
            }
        }else{
            ContentTags contentTags = new ContentTags();
            contentTags.setTag(contentDto.getFeeling());
            contentMybatisDao.createTag(contentTags);
            ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
            contentTagsDetails.setTid(contentTags.getId());
            contentTagsDetails.setCid(content.getId());
            contentMybatisDao.createContentTagsDetails(contentTagsDetails);
        }
    }

    @Override
    public Response getSelectedData(int sinceId,long uid) {
        SquareDataDto squareDataDto = new SquareDataDto();
        //小编精选
        List<Content> contentList = contentMybatisDao.loadSelectedData(sinceId);
        buildDatas(squareDataDto, contentList, uid);
        return Response.success(squareDataDto);
    }

    @Override
    public Response highQualityIndex(int sinceId,long uid) {
        HighQualityContentDto highQualityContentDto = new HighQualityContentDto();
        //SquareDataDto squareDataDto = new SquareDataDto();
        //小编精选
        List<Content> contentList = Lists.newArrayList();
        if(Integer.MAX_VALUE == sinceId) {
            contentList = contentMybatisDao.loadSelectedData(sinceId);
        }
        //猜你喜欢
        List<Content> contents = contentMybatisDao.highQuality(sinceId);

        highQualityContentDto.getMakeUpData().addAll(buildData(contentList,uid));
        highQualityContentDto.getGussYouLikeData().addAll(buildData(contents,uid));
        //buildDatas(squareDataDto, contents, uid);
        return Response.success(highQualityContentDto);
    }

    @Override
    public Response modifyRights(int rights,long cid,long uid){
        Content content = contentMybatisDao.getContentById(cid);
        if(content == null){
            return Response.failure(ResponseStatus.CONTENT_IS_NOT_EXIST.status,ResponseStatus.CONTENT_IS_NOT_EXIST.message);
        }
        if(!content.getUid().equals(uid)){
            return Response.failure(ResponseStatus.CONTENT_IS_NOT_YOURS.status,ResponseStatus.CONTENT_IS_NOT_YOURS.message);
        }
        content.setRights(rights);
        contentMybatisDao.updateContentById(content);
        return Response.success(ResponseStatus.CONTENT_IS_PUBLIC_MODIFY_SUCCESS.status,ResponseStatus.CONTENT_IS_PUBLIC_MODIFY_SUCCESS.message);
    }

    @Override
    public Response getActivities(int sinceId,long uid) {
        SquareDataDto squareDataDto = new SquareDataDto();
        List<Content> list = contentMybatisDao.loadSelectedData(sinceId);
        buildDatas(squareDataDto, list, uid);
        return Response.success(squareDataDto);
    }

    @Override
    public Response showContents(EditorContentDto editorContentDto) {
        ShowContentDto showContentDto = new ShowContentDto();
        showContentDto.setTotal(contentMybatisDao.total(editorContentDto));
        int totalPage = (showContentDto.getTotal() + editorContentDto.getPageSize() -1) / editorContentDto.getPageSize();
        showContentDto.setTotalPage(totalPage);
        List<Content> contents = contentMybatisDao.showContentsByPage(editorContentDto);
        for(Content content : contents){
            ShowContentDto.ShowContentElement element = showContentDto.createElement();
            element.setUid(content.getUid());
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            element.setNickName(userProfile.getNickName());
            element.setTitle(content.getTitle());
            element.setId(content.getId());
            HighQualityContent highQualityContent = contentMybatisDao.getHQuantityByCid(content.getId());
            if(highQualityContent!=null) {
                element.setHot(true);
            }
            element.setLikeCount(content.getLikeCount());
            element.setCreateTime(content.getCreateTime());

            if(content.getConverImage().isEmpty()){
                element.setThumb(Constant.QINIU_DOMAIN +"/" + content.getThumbnail());
            }else{
                element.setThumb(Constant.QINIU_DOMAIN +"/"+ content.getConverImage());
            }
            element.setContent(content.getContent());
            showContentDto.getResult().add(element);
        }
        return Response.success(200,"数据获取成功",showContentDto);
    }

    @Override
    public Response getHottest(int sinceId,long uid){
        ShowHottestDto hottestDto = new ShowHottestDto();
        //活动
        if(sinceId == Integer.MAX_VALUE) {
            List<ActivityWithBLOBs> activityList = activityService.getActivityTop5();
            for (ActivityWithBLOBs activity : activityList) {
                ShowHottestDto.ActivityElement activityElement = ShowHottestDto.createActivityElement();
                activityElement.setTitle(activity.getActivityHashTitle());
                activityElement.setCoverImage(activity.getActivityCover());
                activityElement.setUpdateTime(activity.getUpdateTime());
                activityElement.setUid(activity.getUid());
                UserProfile userProfile = userService.getUserProfileByUid(activity.getUid());
                activityElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                activityElement.setNickName(userProfile.getNickName());
                int follow = userService.isFollow(activity.getUid(), uid);
                activityElement.setIsFollow(follow);
                activityElement.setId(activity.getId());
                hottestDto.getActivityData().add(activityElement);
            }
        }
        //内容
        List<Content> contentList = contentMybatisDao.getHottestContent(sinceId);
        for(Content content : contentList){
            ShowHottestDto.HottestContentElement hottestContentElement = ShowHottestDto.createHottestContentElement();
            hottestContentElement.setType(content.getType());
            hottestContentElement.setCoverImage(content.getConverImage());
            hottestContentElement.setId(content.getId());
            hottestContentElement.setContent(content.getContent());
            hottestContentElement.setLikeCount(content.getLikeCount());
            hottestContentElement.setReviewCount(content.getReviewCount());
            hottestContentElement.setTitle(content.getTitle());
            //系统文章不包含，用户信息
            if(content.getType() == Specification.ArticleType.SYSTEM.index){

           //直播 直播状态
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
                hottestContentElement.setUid(content.getUid());
                hottestContentElement.setForwardCid(content.getForwardCid());
                //查询直播状态
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                hottestContentElement.setLiveStatus(status);
                //直播是否收藏
                int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
                hottestContentElement.setFavorite(favorite);
                UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
                hottestContentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                hottestContentElement.setNickName(userProfile.getNickName());
                hottestContentElement.setFeeling(content.getFeeling());
                int follow = userService.isFollow(content.getUid(),uid);
                hottestContentElement.setIsFollow(follow);

                hottestContentElement.setPersonCount(content.getPersonCount());
            //原生
            }else if(content.getType() == Specification.ArticleType.ORIGIN.index){
                hottestContentElement.setUid(content.getUid());
                UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
                hottestContentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                hottestContentElement.setNickName(userProfile.getNickName());
                hottestContentElement.setFeeling(content.getFeeling());
                int follow = userService.isFollow(content.getUid(),uid);
                hottestContentElement.setIsFollow(follow);
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                hottestContentElement.setImageCount(imageCounts);
            }
            hottestDto.getHottestContentData().add(hottestContentElement);
        }
        return Response.success(hottestDto);
    }

    /**
     * 获取最新用户日记，直播
     * @param sinceId
     * @param uid
     * @return
     */
    @Override
    public Response getNewest(int sinceId, long uid) {
        ShowNewestDto showNewestDto = new ShowNewestDto();
        List<Content> newestList = contentMybatisDao.getNewest(sinceId);
        for(Content content : newestList){
            ShowNewestDto.ContentElement contentElement = ShowNewestDto.createElement();
            contentElement.setId(content.getId());
            contentElement.setUid(content.getUid());
            // 获取用户信息
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setContent(content.getContent());
            contentElement.setType(content.getType());
            contentElement.setTitle(content.getTitle());
            contentElement.setCoverImage(content.getConverImage());
            contentElement.setTag(content.getFeeling());
            if(content.getType() == Specification.ArticleType.LIVE.index) {
                //查询直播状态
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                contentElement.setLiveStatus(status);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
            //直播是否收藏
            contentElement.setFavorite(favorite);
            //判断人员是否关注
            int follow = userService.isFollow(content.getUid(),uid);
            contentElement.setIsFollow(follow);
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setReviewCount(content.getReviewCount());
            contentElement.setPersonCount(content.getPersonCount());
            showNewestDto.getNewestData().add(contentElement);
        }
        return Response.success(showNewestDto);
    }

    @Override
    public Response getAttention(int sinceId, long uid) {
        ShowAttentionDto showAttentionDto = new ShowAttentionDto();
        //获取此人关注的人是列表
        List<Long> list = userService.getFollowList(uid);
        List<Content> attentionList = contentMybatisDao.getAttention(sinceId ,list);
        for(Content content : attentionList){
            ShowAttentionDto.ContentElement contentElement = showAttentionDto.createElement();
            contentElement.setId(content.getId());
            contentElement.setUid(content.getUid());
            // 获取用户信息
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setContent(content.getContent());
            contentElement.setType(content.getType());
            contentElement.setTitle(content.getTitle());
            contentElement.setCoverImage(content.getConverImage());
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index)
            {
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                contentElement.setLiveStatus(status);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
            //直播是否收藏
            contentElement.setFavorite(favorite);
            //判断人员是否关注
            int follow = userService.isFollow(content.getUid(),uid);
            contentElement.setIsFollow(follow);
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setReviewCount(content.getReviewCount());
            contentElement.setPersonCount(content.getPersonCount());
            showAttentionDto.getAttentionData().add(contentElement);
        }
        return Response.success(showAttentionDto);
    }

    @Override
    public Response createReview(ReviewDto reviewDto) {
        ContentReview review = new ContentReview();
        review.setReview(reviewDto.getReview());
        review.setCid(reviewDto.getCid());
        review.setUid(reviewDto.getUid());
        contentMybatisDao.createReview(review);
        Content content = contentMybatisDao.getContentById(reviewDto.getCid());
        //更新评论数量
        content.setReviewCount(content.getReviewCount() +1);
        contentMybatisDao.updateContentById(content);
        //添加提醒
        remind(content,reviewDto.getUid(),Specification.UserNoticeType.REVIEW.index);
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    }

    @Override
    public Response option(long id, int optionAction, int action) {
        // pgc 1
        // ugc 0
        // 活动 2
        if(optionAction==0 || optionAction==1){
            // UGC操作
            optionContent(action,id);
        }else if(optionAction==2){
            // 活动操作
            // OptionUGC(action, id);
        }
        return null;
    }

    @Override
    public Content getContentByTopicId(long topicId) {
        List<Content> list = contentMybatisDao.getContentByTopicId(topicId);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    private void optionContent(int action, long id) {
        if(action==1){
            // UGC置热
            HighQualityContent highQualityContent = new HighQualityContent();
            highQualityContent.setCid(id);
            contentMybatisDao.createHighQualityContent(highQualityContent);
        }else{
            // 取消置热
            HighQualityContent temp = contentMybatisDao.getHQuantityByCid(id);
            contentMybatisDao.removeHighQualityContent(temp.getId());
        }
    }

}
