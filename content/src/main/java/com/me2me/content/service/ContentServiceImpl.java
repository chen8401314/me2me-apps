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
import com.me2me.content.model.ArticleReview;
import com.me2me.content.model.ContentReview;
import com.me2me.content.widget.*;
import com.me2me.sms.service.XgPushService;
import com.me2me.user.dto.UserInfoDto;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;
import com.plusnet.search.content.RecommendRequest;
import com.plusnet.search.content.RecommendResponse;
import com.plusnet.search.content.api.ContentRecommendService;
import com.plusnet.search.content.domain.ContentTO;
import com.plusnet.search.content.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private ContentRecommendService contentRecommendService;

    @Autowired
    private XgPushService xgPushService;


    @Value("#{app.recommend_domain}")
    private String recommendDomain;

    @Override
    public Response recommend(long uid,String emotion) {
        RecommendRequest recommendRequest = new RecommendRequest();
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        User user = new User();
        user.setBirthday(userProfile.getBirthday());
        user.setMobilePhone(userProfile.getMobile());
        user.setSex(userProfile.getGender()==0?"女":"男");
        user.setUserName(userProfile.getNickName());
        String hobbies = userService.getUserHobbyByUid(uid);
        user.setInterests(hobbies);
        recommendRequest.setUser(user);
        recommendRequest.setUserId(userProfile.getUid().toString());
        recommendRequest.setEmotion(emotion);
        RecommendResponse recommendResponse =  contentRecommendService.recommend(recommendRequest);
        RecommendContentDto recommendContentDto = new RecommendContentDto();
        List<ContentTO> list = recommendResponse.getContents();
        for(ContentTO contentTO : list){
            RecommendContentDto.RecommendElement element = recommendContentDto.createElement();
            element.setTitle(contentTO.getTitle());
            element.setCoverImage(contentTO.getCover());
            if(!contentTO.getUrl().startsWith("http://")) {
                element.setLinkUrl(recommendDomain + contentTO.getUrl());
                element.setType(0);
            }else{
                element.setLinkUrl(contentTO.getUrl());
                element.setType(1);
            }

            recommendContentDto.getResult().add(element);
        }
        return Response.success(recommendContentDto);
    }


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
            contentDataElement.setTag(content.getFeeling());
            ContentTags contentTags = contentMybatisDao.getContentTags(content.getFeeling());
            contentDataElement.setTid(contentTags.getId());
            contentDataElement.setType(content.getType());
            contentDataElement.setCreateTime(content.getCreateTime());
            if(!StringUtils.isEmpty(content.getConverImage())) {
                contentDataElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
            }else{
                contentDataElement.setCoverImage("");
            }
            contentDataElement.setLikeCount(content.getLikeCount());
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
            contentDataElement.setIsFollowed(follow);
            //如果是直播需要一个直播状态，当前用户是否收藏
            setLiveStatusAndFavorite(uid, content, contentDataElement);

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
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            squareDataElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            squareDataElement.setNickName(userProfile.getNickName());
            squareDataElement.setContent(content.getContent());
            squareDataElement.setTitle(content.getTitle());
            squareDataElement.setTag(content.getFeeling());
            squareDataElement.setType(content.getType());
            squareDataElement.setCreateTime(content.getCreateTime());
            squareDataElement.setReviewCount(content.getReviewCount());
            if(!StringUtils.isEmpty(content.getConverImage())) {
                squareDataElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
            }else{
                squareDataElement.setCoverImage("");
            }
            squareDataElement.setContentType(content.getContentType());
            int follow = userService.isFollow(content.getUid(),uid);
            squareDataElement.setIsFollowed(follow);
            //如果是直播需要一个直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index) {
                //查询直播状态
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                squareDataElement.setLiveStatus(status);
                int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
                //直播是否收藏
                squareDataElement.setFavorite(favorite);
                squareDataElement.setForwardCid(content.getForwardCid());
                squareDataElement.setReviewCount(contentMybatisDao.countFragment(content.getForwardCid(),content.getUid()));
            }
            squareDataElement.setLikeCount(content.getLikeCount());
            squareDataElement.setPersonCount(content.getPersonCount());
            squareDataElement.setFavoriteCount(content.getFavoriteCount());
            squareDataElement.setRights(content.getRights());
            squareDataElement.setIsLike(isLike(content.getId(),uid));
            int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
            squareDataElement.setImageCount(imageCounts);
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
    public Response publish2(ContentDto contentDto) {
         return new PublishContentAdapter(PublishFactory.getInstance(contentDto.getType())).execute(contentDto);
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
            // 参与活动入口
            activityService.joinActivity(contentDto.getContent(),contentDto.getUid());
        }else if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
            // 转载文章(暂未启用)
//            long forwardCid = contentDto.getForwardCid();
//            Content forwardContent = contentMybatisDao.getContentById(forwardCid);
//            content.setForwardCid(forwardCid);
//            content.setForwardUrl(contentDetailPage+forwardCid);
//            content.setForwardTitle(forwardContent.getTitle());
//            content.setThumbnail(forwardContent.getConverImage());
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
            ContentLikesDetails contentLikesDetails = new ContentLikesDetails();
            contentLikesDetails.setUid(likeDto.getUid());
            contentLikesDetails.setCid(likeDto.getCid());
            //点赞
            ContentLikesDetails details = contentMybatisDao.getContentLikesDetails(contentLikesDetails);
            if(likeDto.getAction() == Specification.IsLike.LIKE.index){
                if(details == null) {
                    content.setLikeCount(content.getLikeCount() + 1);
                    contentMybatisDao.updateContentById(content);
                    contentMybatisDao.createContentLikesDetails(contentLikesDetails);
                    if(likeDto.getUid() != content.getUid()) {
                        remind(content, likeDto.getUid(), Specification.UserNoticeType.LIKE.index, null);
                    }
                }else{
                    return Response.success(ResponseStatus.CONTENT_USER_LIKES_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_ALREADY.message);
                }
                return Response.success(ResponseStatus.CONTENT_USER_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_LIKES_SUCCESS.message);
            }else{
                if(details == null) {
                    Response.success(ResponseStatus.CONTENT_USER_LIKES_CANCEL_ALREADY.status,ResponseStatus.CONTENT_USER_LIKES_CANCEL_ALREADY.message);
                }else {
                    if ((content.getLikeCount() - 1) < 0) {
                        content.setLikeCount(0);
                    } else {
                        content.setLikeCount(content.getLikeCount() - 1);
                    }
                    contentMybatisDao.updateContentById(content);

                    contentMybatisDao.deleteContentLikesDetails(contentLikesDetails);
                }
                return Response.success(ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.status,ResponseStatus.CONTENT_USER_CANCEL_LIKES_SUCCESS.message);
            }
        }
    }

    /**
     * 点赞
     * @return
     */
    @Override
    public Response like2(LikeDto likeDto) {
        return new LikeAdapter(LikesFactory.getInstance(likeDto.getType())).execute(likeDto);
    }

    @Override
    public void createArticleLike(LikeDto likeDto) {
        contentMybatisDao.createArticleLike(likeDto);
    }

    @Override
    public void createArticleReview(ReviewDto reviewDto) {
        contentMybatisDao.createArticleReview(reviewDto);
    }

    @Override
    public void createReview2(ReviewDto review) {
        ContentReview contentReview = new ContentReview();
        contentReview.setUid(review.getUid());
        contentReview.setCid(review.getCid());
        contentReview.setReview(review.getReview());
        contentMybatisDao.createReview(contentReview);
    }

    @Override
    public Response getArticleComments(long id) {
        ShowArticleCommentsDto showArticleCommentsDto = new ShowArticleCommentsDto();
        List<ArticleLikesDetails> articleLikesDetails =  contentMybatisDao.getArticleLikesDetails(id);
        List<ArticleReview> articleReviews = contentMybatisDao.getArticleReviews(id ,Integer.MAX_VALUE);
        if(articleReviews.size() > 3) {
            articleReviews = articleReviews.subList(0,2);
        }
        showArticleCommentsDto.setLikeCount(articleLikesDetails.size());
        showArticleCommentsDto.setReviewCunt(articleReviews.size());
        for(ArticleReview articleReview : articleReviews) {
            ShowArticleCommentsDto.ReviewElement reviewElement = ShowArticleCommentsDto.createElement();
            reviewElement.setUid(articleReview.getUid());
            reviewElement.setCreateTime(articleReview.getCreateTime());
            reviewElement.setReview(articleReview.getReview());
            UserProfile userProfile = userService.getUserProfileByUid(articleReview.getUid());
            reviewElement.setNickName(userProfile.getNickName());
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar() );
            showArticleCommentsDto.getReviews().add(reviewElement);
        }

        return Response.success(showArticleCommentsDto);
    }

    @Override
    public void remind(Content content ,long uid ,int type,String arg){
        if(content.getUid() == uid){
            return;
        }
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        UserProfile customerProfile = userService.getUserProfileByUid(content.getUid());
        ContentImage contentImage = contentMybatisDao.getCoverImages(content.getId());
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setNoticeType(type);
        userNotice.setReadStatus(userNotice.getReadStatus());
        userNotice.setCid(content.getId());
        if(contentImage != null){
            userNotice.setCoverImage(contentImage.getImage());
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
        if(type == Specification.UserNoticeType.REVIEW.index){
            userNotice.setReview(arg);
            userNotice.setTag("");
        }else if(type == Specification.UserNoticeType.TAG.index){
            userNotice.setReview("");
            userNotice.setTag(arg);
        }else if(type == Specification.UserNoticeType.LIKE.index){
            userNotice.setReview("");
            userNotice.setTag("");
        }
        userNotice.setReadStatus(0);
        UserNotice notice = userService.getUserNotice(userNotice);
        //非直播才提醒
        if(content.getType() != Specification.ArticleType.LIVE.index) {
            //点赞时候只提醒一次
            if (userNotice.getNoticeType() == Specification.UserNoticeType.LIKE.index) {
                if (notice == null) {
                    userService.createUserNotice(userNotice);
                }
            } else {
                userService.createUserNotice(userNotice);
            }
        }
        UserTips userTips = new UserTips();
        userTips.setUid(content.getUid());
        userTips.setType(type);
        UserTips tips  =  userService.getUserTips(userTips);
        if(tips == null){
            userTips.setCount(1);
            //非直播才提醒
            if(content.getType() != Specification.ArticleType.LIVE.index) {
                //点赞时候只提醒一次
                if (userNotice.getNoticeType() == Specification.UserNoticeType.LIKE.index) {
                    if (notice == null) {
                        userService.createUserTips(userTips);
                    }
                }else {
                    userService.createUserTips(userTips);
                }
            }
        }else{
            tips.setCount(tips.getCount()+1);
            //非直播才提醒
            if(content.getType() != Specification.ArticleType.LIVE.index) {
                //点赞时候只提醒一次
                if (userNotice.getNoticeType() == Specification.UserNoticeType.LIKE.index) {
                    if (notice == null) {
                        userService.modifyUserTips(tips);
                    }
                }else {
                    userService.modifyUserTips(tips);
                }
            }
        }
    }

    @Override
    public void deleteContentLikesDetails(ContentLikesDetails contentLikesDetails) {
        contentMybatisDao.deleteContentLikesDetails(contentLikesDetails);
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
        contentTagsDetails.setUid(writeTagDto.getUid());
        contentMybatisDao.createContentTagsDetails(contentTagsDetails);
        Content content = contentMybatisDao.getContentById(writeTagDto.getCid());
        //添加贴标签提醒
        remind(content,writeTagDto.getUid(),Specification.UserNoticeType.TAG.index,writeTagDto.getTag());
        //打标签的时候文章热度+1
        content.setHotValue(content.getHotValue()+1);
        contentMybatisDao.updateContentById(content);
        //添加提醒 UGC贴标签
        userService.push(content.getUid(),writeTagDto.getUid(),Specification.PushMessageType.TAG.index,content.getTitle());
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
        contentDetailDto.setIsLike(isLike(content.getId(),uid));
        String cover = content.getConverImage();
        if(!StringUtils.isEmpty(cover)) {
            contentDetailDto.setCoverImage(Constant.QINIU_DOMAIN  + "/" + content.getConverImage());
        }
        UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
        contentDetailDto.setNickName(userProfile.getNickName());
        contentDetailDto.setAvatar(Constant.QINIU_DOMAIN  + "/" + userProfile.getAvatar());
        contentDetailDto.setHotValue(content.getHotValue());
        contentDetailDto.setLikeCount(content.getLikeCount());
        contentDetailDto.setReviewCount(content.getReviewCount());
        contentDetailDto.setFavoriteCount(content.getFavoriteCount());
        contentDetailDto.setPersonCount(content.getPersonCount());
        contentDetailDto.setCreateTime(content.getCreateTime());
        contentDetailDto.setId(content.getId());
        contentDetailDto.setIsFollowed(userService.isFollow(content.getUid(),uid));
        contentDetailDto.setIsFollowMe(userService.isFollow(uid,content.getUid()));
        // 获取感受
        List<ContentTagsDetails> list  = contentMybatisDao.getContentTagsDetails(content.getId(),Integer.MAX_VALUE);
        for (ContentTagsDetails contentTagsDetails : list){
            ContentDetailDto.ContentTagElement contentTagElement = ContentDetailDto.createElement();
            ContentTags contentTags = contentMybatisDao.getContentTagsById(contentTagsDetails.getTid());
            if(content.getFeeling().indexOf(contentTags.getTag()) == -1) {
                contentTagElement.setTag(contentTags.getTag());
                contentDetailDto.getTags().add(contentTagElement);
            }
        }
        List<ContentReview> reviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
        for(ContentReview review :reviewList){
            ContentDetailDto.ReviewElement reviewElement = ContentDetailDto.createReviewElement();
            reviewElement.setUid(review.getUid());
            reviewElement.setCreateTime(review.getCreateTime());
            reviewElement.setReview(review.getReview());
            UserProfile user = userService.getUserProfileByUid(review.getUid());
            reviewElement.setAvatar(user.getAvatar());
            reviewElement.setNickName(user.getNickName());
            contentDetailDto.getReviews().add(reviewElement);
        }
        //文章图片
        if(content.getType() == Specification.ArticleType.ORIGIN.index){
            List<ContentImage> contentImageList = contentMybatisDao.getContentImages(content.getId());
            if(contentImageList != null && contentImageList.size() > 0) {
                for (ContentImage contentImage : contentImageList) {
                    ContentDetailDto.ImageElement imageElement = ContentDetailDto.createImageElement();
                    if(contentImage.getCover() != 1) {
                        imageElement.setImage(Constant.QINIU_DOMAIN  + "/" +contentImage.getImage());
                        contentDetailDto.getImages().add(imageElement);
                    }
                }
            }

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

//   @Override
//    public Response getContentFeeling(long cid, int sinceId) {
//        /**
//         * 1. 文章
//         2. 该文章被多少次转载
//         3. 取出转载内容 + 转载tag
//         */
//        ContentAllFeelingDto contentAllFeelingDto = new ContentAllFeelingDto();
//        List<ContentTagLikes> list = contentMybatisDao.getForwardContents(cid);
//        for(ContentTagLikes contentTagLike : list){
//            Content content = contentMybatisDao.getContentById(contentTagLike.getCid());
//            ContentAllFeelingDto.ContentAllFeelingElement contentAllFeelingElement = ContentAllFeelingDto.createElement();
//            // 转载内容
//            if(content.getForwardCid()>0){
//                contentAllFeelingElement.setType(Specification.IsForward.FORWARD.index);
//                contentAllFeelingElement.setContent(content.getContent());
//            }else{
//                contentAllFeelingElement.setType(Specification.IsForward.NATIVE.index);
//                contentAllFeelingElement.setContent("");
//            }
//            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
//            contentAllFeelingElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
//            contentAllFeelingElement.setTid(contentTagLike.getTagId());
//            contentAllFeelingElement.setNickName(userProfile.getNickName());
//            int likeCount = contentMybatisDao.getContentUserLikesCount(contentTagLike.getCid(),contentTagLike.getTagId());
//            contentAllFeelingElement.setLikesCount(likeCount);
//            contentAllFeelingElement.setCid(contentTagLike.getCid());
//            contentAllFeelingElement.setTag(content.getFeeling());
//            contentAllFeelingDto.getResults().add(contentAllFeelingElement);
//        }
//        return Response.success(contentAllFeelingDto);
//
//    }

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
            contentH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
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
        userInfoDto.getUser().setMeNumber(userService.getUserNoByUid(targetUid));
        userInfoDto.getUser().setIsFollowed(userService.isFollow(targetUid,sourceUid));
        userInfoDto.getUser().setIsFollowMe(userService.isFollow(sourceUid,targetUid));
        userInfoDto.getUser().setFollowedCount(userService.getFollowCount(targetUid));
        userInfoDto.getUser().setFansCount(userService.getFansCount(targetUid));
        for (Content content : list){
            UserInfoDto.ContentElement contentElement = UserInfoDto.createElement();
            contentElement.setTag(content.getFeeling());
            contentElement.setContent(content.getContent());
            contentElement.setCid(content.getId());
            contentElement.setTitle(content.getTitle());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setReviewCount(content.getReviewCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            contentElement.setContentType(content.getContentType());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setType(content.getType());
            String cover =  content.getConverImage();
            if(!StringUtils.isEmpty(cover)){
                contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
            }
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index)
            {
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                contentElement.setLiveStatus(status);
                contentElement.setReviewCount(contentMybatisDao.countFragment(content.getForwardCid(),content.getUid()));
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), sourceUid);
            //直播是否收藏
            contentElement.setFavorite(favorite);
            contentElement.setIsLike(isLike(content.getId(),sourceUid));
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            ContentImage contentImage = contentMybatisDao.getCoverImages(content.getId());
            if(contentImage != null) {
                contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
            }else{
                contentElement.setCoverImage("");
            }
            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            for(ContentReview contentReview : contentReviewList){
                UserInfoDto.ContentElement.ReviewElement reviewElement = UserInfoDto.ContentElement.createElement();
                reviewElement.setUid(contentReview.getUid());
                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
                reviewElement.setNickName(user.getNickName());
                reviewElement.setCreateTime(contentReview.getCreateTime());
                reviewElement.setReview(contentReview.getReview());
                contentElement.getReviews().add(reviewElement);
            }
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

    public void createTag(ContentDto contentDto, Content content) {
        if(!StringUtils.isEmpty(contentDto.getFeeling()) && contentDto.getFeeling().contains(";")){
            String[] tags = contentDto.getFeeling().split(";");
            for(String t : tags) {
                ContentTags contentTags = new ContentTags();
                contentTags.setTag(t);
                ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
                contentMybatisDao.createTag(contentTags);
                contentTagsDetails.setTid(contentTags.getId());
                contentTagsDetails.setCid(content.getId());
                contentTagsDetails.setUid(content.getUid());
                contentMybatisDao.createContentTagsDetails(contentTagsDetails);
            }
        }else{
            ContentTags contentTags = new ContentTags();
            contentTags.setTag(contentDto.getFeeling());
            contentMybatisDao.createTag(contentTags);
            ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
            contentTagsDetails.setTid(contentTags.getId());
            contentTagsDetails.setCid(content.getId());
            contentTagsDetails.setUid(content.getUid());
            contentMybatisDao.createContentTagsDetails(contentTagsDetails);
        }
    }

    @Override
    public void createContent(Content content) {
        contentMybatisDao.createContent(content);
    }

    @Override
    public void createContentImage(ContentImage contentImage) {
        contentMybatisDao.createContentImage(contentImage);
    }

    @Override
    public Content getContentById(long id) {
        return contentMybatisDao.getContentById(id);
    }

    @Override
    public ContentLikesDetails getContentLikesDetails(ContentLikesDetails contentLikesDetails) {
        return  contentMybatisDao.getContentLikesDetails(contentLikesDetails);
    }

    @Override
    public void createContentLikesDetails(ContentLikesDetails contentLikesDetails) {
        contentMybatisDao.createContentLikesDetails(contentLikesDetails);
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
                String cover = activity.getActivityCover();
                if(!StringUtils.isEmpty(cover)) {
                    activityElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
                activityElement.setUpdateTime(activity.getUpdateTime());
                activityElement.setUid(activity.getUid());
                UserProfile userProfile = userService.getUserProfileByUid(activity.getUid());
                activityElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                activityElement.setNickName(userProfile.getNickName());
                int follow = userService.isFollow(activity.getUid(), uid);
                activityElement.setIsFollowed(follow);
                activityElement.setId(activity.getId());
                hottestDto.getActivityData().add(activityElement);
            }
        }
        //内容
        List<Content> contentList = contentMybatisDao.getHottestContent(sinceId);
        for(Content content : contentList){
            ShowHottestDto.HottestContentElement hottestContentElement = ShowHottestDto.createHottestContentElement();
            hottestContentElement.setType(content.getType());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)) {
                hottestContentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
            }
            hottestContentElement.setId(content.getId());
            hottestContentElement.setContent(content.getContent());
            hottestContentElement.setLikeCount(content.getLikeCount());
            hottestContentElement.setReviewCount(content.getReviewCount());
            hottestContentElement.setTitle(content.getTitle());
            hottestContentElement.setCreateTime(content.getCreateTime());
            hottestContentElement.setIsLike(isLike(content.getId(),uid));
            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            for(ContentReview contentReview : contentReviewList){
                ShowHottestDto.HottestContentElement.ReviewElement reviewElement = ShowHottestDto.HottestContentElement.createElement();
                reviewElement.setUid(contentReview.getUid());
                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
                reviewElement.setNickName(user.getNickName());
                reviewElement.setCreateTime(contentReview.getCreateTime());
                reviewElement.setReview(contentReview.getReview());
                hottestContentElement.getReviews().add(reviewElement);
            }
            //系统文章不包含，用户信息
            if(content.getType() == Specification.ArticleType.SYSTEM.index){

           //直播 直播状态
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
                hottestContentElement.setReviewCount(contentMybatisDao.countFragment(content.getForwardCid(),content.getUid()));
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
                hottestContentElement.setTag(content.getFeeling());
                int follow = userService.isFollow(content.getUid(),uid);
                hottestContentElement.setIsFollowed(follow);

                hottestContentElement.setPersonCount(content.getPersonCount());
                hottestContentElement.setFavoriteCount(content.getFavoriteCount());
            //原生
            }else if(content.getType() == Specification.ArticleType.ORIGIN.index){
                hottestContentElement.setUid(content.getUid());
                UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
                hottestContentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                hottestContentElement.setNickName(userProfile.getNickName());
                hottestContentElement.setTag(content.getFeeling());
                int follow = userService.isFollow(content.getUid(),uid);
                hottestContentElement.setIsFollowed(follow);
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
            contentElement.setIsLike(isLike(content.getId(),uid));
            String cover = content.getConverImage();
            contentElement.setReviewCount(content.getReviewCount());
            if(!StringUtils.isEmpty(cover)) {
                contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
            }
            contentElement.setTag(content.getFeeling());
            contentElement.setForwardCid(content.getForwardCid());
            if(content.getType() == Specification.ArticleType.LIVE.index) {
                //查询直播状态
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                contentElement.setLiveStatus(status);
                contentElement.setReviewCount(contentMybatisDao.countFragment(content.getForwardCid(),content.getUid()));
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
            //直播是否收藏
            contentElement.setFavorite(favorite);
            //判断人员是否关注
            int follow = userService.isFollow(content.getUid(),uid);
            contentElement.setIsFollowed(follow);
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            for(ContentReview contentReview : contentReviewList){
                ShowNewestDto.ContentElement.ReviewElement reviewElement = ShowNewestDto.ContentElement.createElement();
                reviewElement.setUid(contentReview.getUid());
                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
                reviewElement.setNickName(user.getNickName());
                reviewElement.setCreateTime(contentReview.getCreateTime());
                reviewElement.setReview(contentReview.getReview());
                contentElement.getReviews().add(reviewElement);
            }
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
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setIsLike(isLike(content.getId(),uid));
            contentElement.setReviewCount(content.getReviewCount());
            String cover =  content.getConverImage();
            if(!StringUtils.isEmpty(cover)){
                contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
            }
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index)
            {
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                contentElement.setLiveStatus(status);
                contentElement.setReviewCount(contentMybatisDao.countFragment(content.getForwardCid(),content.getUid()));
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
            //直播是否收藏
            contentElement.setFavorite(favorite);
            //判断人员是否关注
            int follow = userService.isFollow(content.getUid(),uid);
            contentElement.setIsFollowed(follow);
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            showAttentionDto.getAttentionData().add(contentElement);
            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            for(ContentReview contentReview : contentReviewList){
                ShowAttentionDto.ContentElement.ReviewElement reviewElement = ShowAttentionDto.ContentElement.createElement();
                reviewElement.setUid(contentReview.getUid());
                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
                reviewElement.setNickName(user.getNickName());
                reviewElement.setCreateTime(contentReview.getCreateTime());
                reviewElement.setReview(contentReview.getReview());
                contentElement.getReviews().add(reviewElement);
            }
        }
        return Response.success(showAttentionDto);
    }

    @Override
    public Response createReview(ReviewDto reviewDto) {
      /*  ContentReview review = new ContentReview();
        review.setReview(reviewDto.getReview());
        review.setCid(reviewDto.getCid());
        review.setUid(reviewDto.getUid());
        contentMybatisDao.createReview(review);
        Content content = contentMybatisDao.getContentById(reviewDto.getCid());
        //更新评论数量
        content.setReviewCount(content.getReviewCount() +1);
        contentMybatisDao.updateContentById(content);
        //添加提醒
        remind(content,reviewDto.getUid(),Specification.UserNoticeType.REVIEW.index,reviewDto.getReview());
        //自己的日记被评论提醒
        userService.push(content.getUid(),reviewDto.getUid(),Specification.PushMessageType.REVIEW.index,content.getTitle());
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    */
        return new ReviewAdapter(ReviewFactory.getInstance(reviewDto.getType())).execute(reviewDto);
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
            optionActivity(action, id);
        }
        return Response.success();
    }

    private void optionActivity(int action, long id) {
        ActivityWithBLOBs activity = activityService.loadActivityById(id);
        if(action==1){
            // 取消置热
            activity.setStatus(0);
        }else{
            activity.setStatus(1);
        }
        activityService.modifyActivity(activity);
    }

    @Override
    public Content getContentByTopicId(long topicId) {
        List<Content> list = contentMybatisDao.getContentByTopicId(topicId);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public Response showUGCDetails(long id) {
        Content content =  contentMybatisDao.getContentById(id);
        ShowUGCDetailsDto showUGCDetailsDto = new ShowUGCDetailsDto();
        showUGCDetailsDto.setId(content.getId());
        showUGCDetailsDto.setCover(Constant.QINIU_DOMAIN  + "/" +content.getConverImage());
        showUGCDetailsDto.setContent(content.getContent());
        List<ContentImage> contentImages = contentMybatisDao.getContentImages(id);
        StringBuilder images = new StringBuilder();

        for(ContentImage contentImage : contentImages){
            if(contentImage.equals(contentImages.get(contentImages.size()-1))) {
                images.append(Constant.QINIU_DOMAIN).append("/").append(contentImage.getImage());
            }else{
                images.append(Constant.QINIU_DOMAIN).append("/").append(contentImage.getImage()).append(";");
            }
        }
        showUGCDetailsDto.setImages(images.toString());
        return Response.success(showUGCDetailsDto);
    }

    @Override
    public Response reviewList(long cid, long sinceId) {
        ContentReviewDto contentReviewDto = new ContentReviewDto();
        List<ContentReview> list = contentMybatisDao.getContentReviewByCid(cid,sinceId);
        for(ContentReview contentReview : list){
            ContentReviewDto.ReviewElement reviewElement = ContentReviewDto.createElement();
            reviewElement.setUid(contentReview.getUid());
            reviewElement.setReview(contentReview.getReview());
            reviewElement.setCreateTime(contentReview.getCreateTime());
            UserProfile userProfile = userService.getUserProfileByUid(contentReview.getUid());
            reviewElement.setNickName(userProfile.getNickName());
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            reviewElement.setId(contentReview.getId());
            contentReviewDto.getReviews().add(reviewElement);

        }
        return Response.success(contentReviewDto);
    }

    @Override
    public void updateContentById(Content content) {
        contentMybatisDao.updateContentById(content);
    }

    private void optionContent(int action, long id) {
        if(action==1){
            // UGC置热
            HighQualityContent highQualityContent = new HighQualityContent();
            highQualityContent.setCid(id);
            //自己发布的被置热
            Content content = contentMybatisDao.getContentById(id);
            //UGC置热
            if(content.getType() == Specification.ArticleType.ORIGIN.index) {
                userService.push(content.getUid(), 000000, Specification.PushMessageType.HOTTEST.index, content.getTitle());
            //直播置热
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
                userService.push(content.getUid(), 000000, Specification.PushMessageType.LIVE_HOTTEST.index, content.getTitle());
            }

            contentMybatisDao.createHighQualityContent(highQualityContent);
        }else{
            // 取消置热
            HighQualityContent temp = contentMybatisDao.getHQuantityByCid(id);
            contentMybatisDao.removeHighQualityContent(temp.getId());
        }
    }

    /**
     * 判断当前人是否给当前文章点赞过 0 未点赞 1点赞
     * @return
     */
    @Override
    public int isLike(long cid,long uid){
       return contentMybatisDao.isLike(cid,uid);
    }


    @Override
    public int countFragment(long topicId ,long uid){
        return contentMybatisDao.countFragment(topicId,uid);
    }


    @Override
    public Response getArticleReview(long id, long sinceId) {
        ShowArticleReviewDto showArticleReviewDto = new ShowArticleReviewDto();
        List<ArticleReview> articleReviews = contentMybatisDao.getArticleReviews(id ,sinceId);
        for(ArticleReview articleReview : articleReviews) {
            ShowArticleReviewDto.ReviewElement reviewElement = ShowArticleReviewDto.createElement();
            reviewElement.setUid(articleReview.getUid());
            reviewElement.setCreateTime(articleReview.getCreateTime());
            reviewElement.setReview(articleReview.getReview());
            UserProfile userProfile = userService.getUserProfileByUid(articleReview.getUid());
            reviewElement.setNickName(userProfile.getNickName());
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar() );
            showArticleReviewDto.getReviews().add(reviewElement);
        }
        return Response.success(showArticleReviewDto);
    }

}
