package com.me2me.content.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.Constant;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dao.LiveForContentJdbcDao;
import com.me2me.content.dto.*;
import com.me2me.content.model.*;
import com.me2me.content.model.ArticleReview;
import com.me2me.content.model.ContentReview;
import com.me2me.content.widget.*;
import com.me2me.sms.service.JPushService;
import com.me2me.user.dto.UserInfoDto;
import com.me2me.user.dto.UserInfoDto2;
import com.me2me.user.model.*;
import com.me2me.user.service.UserService;
import com.plusnet.forecast.domain.ForecastContent;
import com.plusnet.search.content.RecommendRequest;
import com.plusnet.search.content.RecommendResponse;
import com.plusnet.search.content.api.ContentStatService;
import com.plusnet.search.content.domain.ContentTO;
import com.plusnet.search.content.domain.User;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Service
@Slf4j
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMybatisDao contentMybatisDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PublishContentAdapter publishContentAdapter;

    @Autowired
    private LikeAdapter likeAdapter;

    @Autowired
    private ReviewAdapter reviewAdapter;

    @Autowired
    private ContentRecommendServiceProxyBean contentRecommendServiceProxyBean;

    @Autowired
    private WriteTagAdapter writeTagAdapter;

    @Autowired
    private ContentStatusServiceProxyBean contentStatusServiceProxyBean;

    @Autowired
    private JPushService jPushService;

    @Autowired
    private LiveForContentJdbcDao liveForContentJdbcDao;

    @Value("#{app.recommend_domain}")
    private String recommendDomain;

    private Random random = new Random();

    private ExecutorService executorService= Executors.newFixedThreadPool(100);




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
        RecommendResponse recommendResponse =  contentRecommendServiceProxyBean.getTarget().recommend(recommendRequest);
        RecommendContentDto recommendContentDto = new RecommendContentDto();
        List<ForecastContent> list = recommendResponse.getContents();
        for(ForecastContent forecastContent : list){
            ContentTO contentTO = forecastContent.getContent();
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

    //获取所有(直播和UGC)
	private void buildDatas(SquareDataDto squareDataDto, List<Content> contents, long uid) {
		log.info("buildDatas ...");
		List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        for(Content idx : contents){
        	if(!uidList.contains(idx.getUid())){
        		uidList.add(idx.getUid());
        	}
        	if(idx.getType() == Specification.ArticleType.LIVE.index){//王国
        		topicIdList.add(idx.getForwardCid());
        	}
        }
        
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
        	for(UserProfile up : profileList){
        		profileMap.put(String.valueOf(up.getUid()), up);
        	}
        }
        
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
        	Long topicId = null;
        	for(Map<String,Object>  map : topicList){
        		topicId = (Long)map.get("id");
        		topicMap.put(topicId.toString(), map);
        	}
        }
        
        UserProfile userProfile = null;
		for (Content content : contents) {
			SquareDataDto.SquareDataElement squareDataElement = SquareDataDto.createElement();
			squareDataElement.setId(content.getId());
			squareDataElement.setUid(content.getUid());
			userProfile = profileMap.get(String.valueOf(content.getUid()));
			log.info(" get userProfile success");
			squareDataElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
			squareDataElement.setNickName(userProfile.getNickName());
			String contentStr = content.getContent();
			if (contentStr.length() > 100) {
				squareDataElement.setContent(contentStr.substring(0, 100));
			} else {
				squareDataElement.setContent(contentStr);
			}
			squareDataElement.setTitle(content.getTitle());
			squareDataElement.setTag(content.getFeeling());
			squareDataElement.setType(content.getType());
			squareDataElement.setCreateTime(content.getCreateTime());
			squareDataElement.setReviewCount(content.getReviewCount());
			squareDataElement.setForwardTitle(content.getForwardTitle());
			squareDataElement.setForwardUrl(content.getForwardUrl());
			squareDataElement.setForwardCid(content.getForwardCid());
			squareDataElement.setV_lv(userProfile.getvLv());
			if (!StringUtils.isEmpty(content.getConverImage())) {
				if (content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index) {
					squareDataElement.setCoverImage(content.getConverImage());
				} else {
					squareDataElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + content.getConverImage());
				}
			} else {
				squareDataElement.setCoverImage("");
			}
			squareDataElement.setContentType(content.getContentType());
			int follow = userService.isFollow(content.getUid(), uid);
			int followMe = userService.isFollow(uid, content.getUid());
			log.info(" get isFollow success");
			squareDataElement.setIsFollowed(follow);
			squareDataElement.setIsFollowMe(followMe);
			// 如果是直播需要一个直播状态
			if (content.getType() == Specification.ArticleType.LIVE.index) {
				// 查询直播状态
				int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
				log.info(" get live status success");
				squareDataElement.setLiveStatus(status);
				int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
				// 直播是否收藏
				squareDataElement.setFavorite(favorite);
				squareDataElement.setForwardCid(content.getForwardCid());
				int reviewCount = contentMybatisDao.countFragment(content.getForwardCid(), content.getUid());
				squareDataElement.setReviewCount(reviewCount);
				squareDataElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
				squareDataElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
				//王国增加身份信息
            	Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
            	if(null != topic){
            		squareDataElement.setInternalStatus(this.getInternalStatus(topic, uid));
            	}
			}
			squareDataElement.setLikeCount(content.getLikeCount());
			squareDataElement.setPersonCount(content.getPersonCount());
			squareDataElement.setFavoriteCount(content.getFavoriteCount());
			squareDataElement.setRights(content.getRights());
			squareDataElement.setIsLike(isLike(content.getId(), uid));
			int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
			log.info(" get imageCounts success");
			squareDataElement.setImageCount(imageCounts);

			int readCountDummy = content.getReadCountDummy();
			squareDataElement.setReadCount(readCountDummy);

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
        log.info("publish start ...");
        return publishContentAdapter.execute(contentDto);
    }

    @Override
    public Response publish(ContentDto contentDto) {
        log.info("live publish");
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
                //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.LIKE.index,0,likeDto.getUid()));
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
                //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.UN_LIKE.index,0,likeDto.getUid()));
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
        log.info("content like start...request:"+JSON.toJSONString(likeDto));
        return likeAdapter.execute(likeDto);
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
        contentReview.setAtUid(review.getAtUid());
        long atUid = review.getAtUid();
        if(atUid==-1){
            JSONObject extra = JSON.parseObject(review.getExtra());
            if(extra!=null){
                JSONArray atArray = extra.containsKey("atArray")?extra.getJSONArray("atArray"):null;
                if(atArray!=null&&atArray.size()>0) {
                    contentReview.setAtUid(atArray.getLongValue(0));
                }
            }
        }
        contentReview.setExtra(review.getExtra());
        contentReview.setStatus(Specification.ContentDelStatus.NORMAL.index);
        contentMybatisDao.createReview(contentReview);
    }

    @Override
    public Response getArticleComments(long uid ,long id) {
        log.info("getArticleComments start ...");
        ShowArticleCommentsDto showArticleCommentsDto = new ShowArticleCommentsDto();
        List<ArticleLikesDetails> articleLikesDetails =  contentMybatisDao.getArticleLikesDetails(id);
        List<ArticleReview> articleReviews = contentMybatisDao.getArticleReviews(id ,Integer.MAX_VALUE);
        showArticleCommentsDto.setLikeCount(articleLikesDetails.size());
        showArticleCommentsDto.setReviewCount(articleReviews.size());
        showArticleCommentsDto.setIsLike(0);
        //获取用户信息取得是否大V
        UserProfile userProfile1 = userService.getUserProfileByUid(uid);
        showArticleCommentsDto.setV_lv(userProfile1.getvLv());
        for(ArticleReview articleReview : articleReviews) {
            ShowArticleCommentsDto.ReviewElement reviewElement = ShowArticleCommentsDto.createElement();
            reviewElement.setUid(articleReview.getUid());
            reviewElement.setCreateTime(articleReview.getCreateTime());
            reviewElement.setReview(articleReview.getReview());
            UserProfile userProfile = userService.getUserProfileByUid(articleReview.getUid());
            reviewElement.setNickName(userProfile.getNickName());
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            UserProfile atUser = userService.getUserProfileByUid(articleReview.getAtUid());
            reviewElement.setAtUid(atUser.getUid());
            reviewElement.setAtNickName(atUser.getNickName());
            reviewElement.setId(articleReview.getId());
            showArticleCommentsDto.getReviews().add(reviewElement);
        }
        for(ArticleLikesDetails likesDetails : articleLikesDetails){
            ShowArticleCommentsDto.LikeElement likeElement = ShowArticleCommentsDto.createLikeElement();
            likeElement.setUid(likesDetails.getUid());
            UserProfile user = userService.getUserProfileByUid(likesDetails.getUid());
            likeElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
            likeElement.setNickName(user.getNickName());
            showArticleCommentsDto.getLikeElements().add(likeElement);
            if(likesDetails.getUid() == uid){
                showArticleCommentsDto.setIsLike(1);
            }
        }
        List<ArticleTagsDetails> detailsList = contentMybatisDao.getArticleTagsDetails(id);
        for(ArticleTagsDetails tagsDetails : detailsList){
            ShowArticleCommentsDto.ContentTagElement contentTagElement = ShowArticleCommentsDto.createContentTagElement();
            ContentTags contentTags = contentMybatisDao.getContentTagsById(tagsDetails.getTid());
            contentTagElement.setTag(contentTags.getTag());
            showArticleCommentsDto.getTags().add(contentTagElement);
        }
        ContentStatService contentStatService = contentStatusServiceProxyBean.getTarget();
        contentStatService.read(uid+"",id);
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
                        //修改推送为极光推送,兼容老版本
                        JpushToken jpushToken = userService.getJpushTokeByUid(content.getUid());
                        if(jpushToken != null) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("count","1");
                            String alias = String.valueOf(content.getUid());
                            jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                        }
                        else
                        {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("count","1");
                            String alias = String.valueOf(content.getUid());
                            jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                        }
                    }
                }else {
                    userService.createUserTips(userTips);
                    //修改推送为极光推送,兼容老版本
                    JpushToken jpushToken = userService.getJpushTokeByUid(content.getUid());
                    if(jpushToken != null) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("count","1");
                        String alias = String.valueOf(content.getUid());
                        jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                    }
                    else
                    {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("count","1");
                        String alias = String.valueOf(content.getUid());
                        jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                    }
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
                        //修改推送为极光推送,兼容老版本
                        JpushToken jpushToken = userService.getJpushTokeByUid(content.getUid());
                        if(jpushToken != null) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("count","1");
                            String alias = String.valueOf(content.getUid());
                            jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                        }
                        else
                        {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("count","1");
                            String alias = String.valueOf(content.getUid());
                            jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                        }
                    }
                }else {
                    userService.modifyUserTips(tips);
                    //修改推送为极光推送,兼容老版本
                    JpushToken jpushToken = userService.getJpushTokeByUid(content.getUid());
                    if(jpushToken != null) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("count","1");
                        String alias = String.valueOf(content.getUid());
                        jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                    }
                    else
                    {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("count","1");
                        String alias = String.valueOf(content.getUid());
                        jPushService.payloadByIdForMessage(alias,jsonObject.toString());
                    }
                }
            }
        }
    }

    @Override
    public void remind(Content content, long uid, int type, String arg, long atUid) {
        if(atUid == uid){
            return;
        }
        UserProfile userProfile = userService.getUserProfileByUid(uid);
        UserProfile customerProfile = userService.getUserProfileByUid(atUid);
        String contentImage = content.getConverImage();
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setNoticeType(type);
        userNotice.setReadStatus(userNotice.getReadStatus());
        userNotice.setCid(content.getId());
        if(!StringUtils.isEmpty(contentImage)){
            userNotice.setCoverImage(contentImage);
            userNotice.setSummary("");
        }else{
            userNotice.setCoverImage("");
            if(content.getContent().length() > 50) {
                userNotice.setSummary(content.getContent().substring(0,50));
            }else{
                userNotice.setSummary(content.getContent());
            }

        }
        userNotice.setToUid(atUid);
        userNotice.setLikeCount(0);
        userNotice.setReview(arg);
        userNotice.setTag("");
        userNotice.setReadStatus(0);
        userService.createUserNotice(userNotice);
        //如果@人和被@人都不是ugc作者，则需要给ugc作者发送一个提醒消息
        if(content.getUid()!=atUid&&content.getUid()!=uid){
            UserProfile autherProfile = userService.getUserProfileByUid(content.getUid());
            userNotice.setToUid(content.getUid());
            userNotice.setToNickName(autherProfile.getNickName());
            userNotice.setId(null);
            userService.createUserNotice(userNotice);
        }
        UserTips userTips = new UserTips();
        userTips.setUid(atUid);
        userTips.setType(type);
        UserTips tips  =  userService.getUserTips(userTips);
        if(tips == null){
            userTips.setCount(1);
            userService.createUserTips(userTips);
            //修改推送为极光推送,兼容老版本
            localJpush(atUid);
            //如果@人和被@人都不是ugc作者，则需要给ugc作者发送一个提醒消息
            if(content.getUid()!=atUid&&content.getUid()!=uid) {
                localJpush(content.getUid());
            }
        }else{
            tips.setCount(tips.getCount()+1);
            userService.modifyUserTips(tips);
            //修改推送为极光推送,兼容老版本
            localJpush(atUid);
            //如果@人和被@人都不是ugc作者，则需要给ugc作者发送一个提醒消息
            if(content.getUid()!=atUid&&content.getUid()!=uid) {
                localJpush(content.getUid());
            }
        }
    }
private void localJpush(long toUid){
    JpushToken jpushToken = userService.getJpushTokeByUid(toUid);
    if(jpushToken != null) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("count","1");
        String alias = String.valueOf(toUid);
        jPushService.payloadByIdForMessage(alias,jsonObject.toString());
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
        //userService.push(content.getUid(),writeTagDto.getUid(),Specification.PushMessageType.TAG.index,content.getTitle());
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FEELING_TAG.index,0,writeTagDto.getUid()));
        return Response.success(ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.status,ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.message);
    }

    public Response writeTag2(WriteTagDto writeTagDto) {
        return writeTagAdapter.execute(writeTagDto);
    }

    @Override
    public Response modifyPGC(ContentDto contentDto) {
        Content content = contentMybatisDao.getContentById(contentDto.getId());
        if(contentDto.getAction()==1){
            // 是否置顶
            content.setIsTop(contentDto.getIsTop());
        }else {
            content.setUid(contentDto.getUid());
            createTag(contentDto, content);
            content.setTitle(contentDto.getTitle());
            content.setFeeling(contentDto.getFeeling());
            content.setConverImage(contentDto.getCoverImage());
            content.setContent(contentDto.getContent());
        }
        contentMybatisDao.modifyPGCById(content);
        return showUGCDetails(contentDto.getId());
    }

    /**
     * 机器点赞
     */
    @Override
    public void robotLikes(final LikeDto likeDto) {
        // 获取所需要的机器人随机为3-8个
        int limit = random.nextInt(5)+3;
        List<com.me2me.user.model.User> robots = userService.getRobots(limit);
        // 在3分钟之内完成点赞操作
        for(int i = 0;i<robots.size();i++) {

            final  long uid = robots.get(i).getUid();
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        int threadTimes = random.nextInt(60000*20)+60000;
                        Thread.sleep(threadTimes);
                        likeDto.setUid(uid);
                        like2(likeDto);
                        log.error("robot like success...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        log.error("robot like failure...");
                    }

                }
            });
        }
    }

    @Override
    public Response kingTopic(KingTopicDto kingTopic) {
        ShowKingTopicDto showKingTopicDto = new ShowKingTopicDto();
        String nickName = kingTopic.getNickName();
        UserProfile userProfile = null;
        if(!StringUtils.isEmpty(nickName)) {
            userProfile = userService.getUserByNickName(nickName);
            kingTopic.setUid(userProfile.getUid());
        }
            List<ResultKingTopicDto> list = contentMybatisDao.kingTopic(kingTopic);
            for (ResultKingTopicDto topicDto : list) {
                ShowKingTopicDto.KingTopicElement element = showKingTopicDto.createKingTopicElement();
                element.setLikeCount(topicDto.getLikeCount());
                element.setUid(topicDto.getUid());
                element.setReviewCount(topicDto.getReviewCount());
                element.setCreateTime(topicDto.getCreateTime());
                element.setTitle(topicDto.getTitle());
                element.setTopicId(topicDto.getTopicId());
                userProfile = userService.getUserProfileByUid(topicDto.getUid());
                element.setNickName(userProfile.getNickName());
                element.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                element.setCoverImage(Constant.QINIU_DOMAIN + "/" + topicDto.getCoverImage());
                showKingTopicDto.getResult().add(element);
            }
            return Response.success(showKingTopicDto);
    }

    @Override
    public Response myPublishByType(long uid, int sinceId, int type,long updateTime,long currentUid) {
        MyPublishDto dto = new MyPublishDto();
        dto.setType(type);
        dto.setSinceId(sinceId);
        dto.setUid(uid);
        dto.setUpdateTime(updateTime);
        ShowMyPublishDto showMyPublishDto = new ShowMyPublishDto();
        List<Content> contents = contentMybatisDao.myPublishByType(dto);
        List<Long> topicIdList = new ArrayList<Long>();
        for(Content idx : contents){
        	if(idx.getType() == Specification.ArticleType.LIVE.index){//王国
        		topicIdList.add(idx.getForwardCid());
        	}
        }
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
        	Long topicId = null;
        	for(Map<String,Object>  map : topicList){
        		topicId = (Long)map.get("id");
        		topicMap.put(topicId.toString(), map);
        	}
        }
        
        for (Content content : contents){
            ShowMyPublishDto.MyPublishElement contentElement = ShowMyPublishDto.createElement();
            contentElement.setTag(content.getFeeling());
            String contentStr = content.getContent();
            if(contentStr.length() > 100){
                contentElement.setContent(contentStr.substring(0,100));
            }else{
                contentElement.setContent(contentStr);
            }
            contentElement.setId(content.getId());
            contentElement.setTitle(content.getTitle());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setReviewCount(content.getReviewCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            contentElement.setContentType(content.getContentType());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setType(content.getType());

                SystemConfig systemConfig =userService.getSystemConfig();
                int start = systemConfig.getReadCountStart();
                int end = systemConfig.getReadCountEnd();
                int readCountDummy = content.getReadCountDummy();
                Random random = new Random();
                //取1-6的随机数每次添加
                int value = random.nextInt(end)+start;
                int readDummy = readCountDummy+value;
                content.setReadCountDummy(readDummy);
                contentMybatisDao.updateContentById(content);
                contentElement.setReadCount(readDummy);

            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setForwardTitle(content.getForwardTitle());
            contentElement.setUid(content.getUid());
            String cover = content.getConverImage();

            if(!StringUtils.isEmpty(cover)){
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            if(type == Specification.ArticleType.LIVE.index) {
                contentElement.setLiveStatus(contentMybatisDao.getTopicStatus(content.getForwardCid()));
                int reviewCount = contentMybatisDao.countFragment(content.getForwardCid(),content.getUid());
                contentElement.setReviewCount(reviewCount);
                contentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                contentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
                //王国增加身份信息
            	Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
            	if(null != topic){
            		contentElement.setInternalStatus(this.getInternalStatus(topic, currentUid));
            	}
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), currentUid);
            log.info("get content favorite success");
            //直播是否收藏
            contentElement.setFavorite(favorite);
            contentElement.setIsLike(isLike(content.getId(),currentUid));
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
            log.info("get content review success");
            for(ContentReview contentReview : contentReviewList){
                ShowMyPublishDto.MyPublishElement.ReviewElement reviewElement = ShowMyPublishDto.MyPublishElement.createReviewElement();
                reviewElement.setUid(contentReview.getUid());
                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
                reviewElement.setNickName(user.getNickName());
                reviewElement.setCreateTime(contentReview.getCreateTime());
                reviewElement.setReview(contentReview.getReview());
                contentElement.getReviews().add(reviewElement);
            }
            showMyPublishDto.getMyPublishElements().add(contentElement);
        }
        return Response.success(showMyPublishDto);
    }

    @Override
    public Response deleteContent(long id, long uid) {
        log.info("deleteContent start ...");
        Content content = contentMybatisDao.getContentById(id);
        //直播删除
        if(content.getType() == Specification.ArticleType.LIVE.index) {
            if(uid!=content.getUid()&&!userService.isAdmin(uid)){ //只有王国自己，或者管理员能删除王国
                return Response.failure(ResponseStatus.CONTENT_DELETE_NO_AUTH.status,ResponseStatus.CONTENT_DELETE_NO_AUTH.message);
            }
            contentMybatisDao.deleteTopicById(content.getForwardCid());
            log.info("topic delete");
        }

        content.setStatus(Specification.ContentStatus.DELETE.index);
        log.info("content status delete");
        contentMybatisDao.updateContentById(content);
        log.info("deleteContent end ...");
        return Response.failure(ResponseStatus.CONTENT_DELETE_SUCCESS.status,ResponseStatus.CONTENT_DELETE_SUCCESS.message);
    }

    @Override
    public Response contentDetail(long id ,long uid) {
        log.info("getContentDetail start ...");
        ContentDetailDto contentDetailDto = new ContentDetailDto();
        Content content = contentMybatisDao.getContentById(id);
        if(content == null){
            return Response.failure(ResponseStatus.DATA_DOES_NOT_EXIST.status,ResponseStatus.DATA_DOES_NOT_EXIST.message);
        }else if(content.getStatus() == Specification.ContentStatus.DELETE.index){
            return Response.failure(ResponseStatus.DATA_IS_DELETE.status,ResponseStatus.DATA_IS_DELETE.message);
        }
        log.info("get content data success");
        contentDetailDto.setFeeling(content.getFeeling());
        contentDetailDto.setType(content.getType());
        contentDetailDto.setUid(content.getUid());
        contentDetailDto.setContent(content.getContent());
        contentDetailDto.setContentType(content.getContentType());
        contentDetailDto.setTitle(content.getTitle());
        contentDetailDto.setIsLike(isLike(content.getId(),uid));

            SystemConfig systemConfig =userService.getSystemConfig();
            int start = systemConfig.getReadCountStart();
            int end = systemConfig.getReadCountEnd();
            int readCountDummy = content.getReadCountDummy();
            Random random = new Random();
            //取1-6的随机数每次添加
            int value = random.nextInt(end)+start;
            int readDummy = readCountDummy+value;
            content.setReadCountDummy(readDummy);
            contentMybatisDao.updateContentById(content);
            contentDetailDto.setReadCount(readDummy);

        contentDetailDto.setRights(content.getRights());

        String cover = content.getConverImage();
        if(!StringUtils.isEmpty(cover)) {
            contentDetailDto.setCoverImage(Constant.QINIU_DOMAIN  + "/" + content.getConverImage());
        }

        UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
        contentDetailDto.setV_lv(userProfile.getvLv());

        log.info("get userProfile data success");
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
        HighQualityContent qualityContent = contentMybatisDao.getHQuantityByCid(id);
        if(qualityContent != null) {
            contentDetailDto.setIsHot(1);
        }
        // 获取感受
        List<ContentTagsDetails> list  = contentMybatisDao.getContentTagsDetails(content.getId(),content.getCreateTime(),Integer.MAX_VALUE);
        log.info("get contentTagDetail success");
        for (ContentTagsDetails contentTagsDetails : list){
            ContentDetailDto.ContentTagElement contentTagElement = ContentDetailDto.createElement();
            ContentTags contentTags = contentMybatisDao.getContentTagsById(contentTagsDetails.getTid());
            contentTagElement.setTag(contentTags.getTag());
            contentDetailDto.getTags().add(contentTagElement);
        }
        List<ContentReview> reviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
        log.info("get content review success");
        for(ContentReview review :reviewList){
            ContentDetailDto.ReviewElement reviewElement = ContentDetailDto.createReviewElement();
            reviewElement.setUid(review.getUid());
            reviewElement.setCreateTime(review.getCreateTime());
            reviewElement.setReview(review.getReview());
            UserProfile user = userService.getUserProfileByUid(review.getUid());
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
            reviewElement.setNickName(user.getNickName());
            UserProfile atUser = userService.getUserProfileByUid(review.getAtUid());
            reviewElement.setAtUid(atUser.getUid());
            reviewElement.setAtNickName(atUser.getNickName());
            reviewElement.setId(review.getId());
            contentDetailDto.getReviews().add(reviewElement);
        }

        //点赞top30
        List<ContentLikesDetails> contentLikesDetailsList = contentMybatisDao.getContentLikesDetails(id);
        for(ContentLikesDetails contentLikesDetails : contentLikesDetailsList){
            ContentDetailDto.LikeElement likeElement = ContentDetailDto.createLikeElement();
            likeElement.setUid(contentLikesDetails.getUid());
            UserProfile user = userService.getUserProfileByUid(contentLikesDetails.getUid());
            likeElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
            likeElement.setNickName(user.getNickName());
            contentDetailDto.getLikeElements().add(likeElement);
        }
        //文章图片
        if(content.getType() == Specification.ArticleType.ORIGIN.index){
            List<ContentImage> contentImageList = contentMybatisDao.getContentImages(content.getId());
            log.info("get contentImage success");
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
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.CONTENT_VIEW.index,0,uid));
        log.info("monitor log");
        //阅读数量+1
        content.setReadCount(content.getReadCount()+1);
        contentMybatisDao.updateContentById(content);
        log.info("update readCount success");
        log.info("getContentDetail end ...");
        return Response.success(contentDetailDto);
    }

    @Override
    public Response myPublish(long uid ,long updateTime ,int type ,int sinceId ,int newType) {
        log.info("myPublish start ...");
        SquareDataDto squareDataDto = new SquareDataDto();
        if(newType == 1) {//新版本2.1.1
            //获取所有播内容
//        List<Content> contents = contentMybatisDao.myPublish(uid,sinceId);
            if (type == 1) {
                log.info("myPublish ugc getData ...");
                //获取非直播内容
                List<Content> contents = contentMybatisDao.myPublishUgc(uid, sinceId);
                buildDatas(squareDataDto, contents, uid);
            } else if (type == 2) {
                log.info("my publish live getData ...");
                //获取直播内容
                List<Content> contents = contentMybatisDao.myPublishLive(uid, updateTime);
                buildDatas(squareDataDto, contents, uid);
            } else if (type == 0) {
                log.info("my publish ugc and live getData ...");
                //兼容2.1.0之前版本 获取UGC和直播
                List<Content> contents = contentMybatisDao.myPublish(uid, sinceId);
                buildDatas(squareDataDto, contents, uid);
            }
            log.info("myPublish end ...");
        }else if(newType == 0){//老版本
            if (type == 1) {
                log.info("myPublish ugc getData ...");
                //获取非直播内容
                List<Content> contents = contentMybatisDao.myPublishUgc(uid, sinceId);
                buildDatas(squareDataDto, contents, uid);
            } else if (type == 2) {
                log.info("my publish live getData ...");
                //获取直播内容
                List<Content> contents = contentMybatisDao.myPublishLive2(uid, sinceId);
                buildDatas(squareDataDto, contents, uid);
            } else if (type == 0) {
                log.info("my publish ugc and live getData ...");
                //兼容2.1.0之前版本 获取UGC和直播
                List<Content> contents = contentMybatisDao.myPublish(uid, sinceId);
                buildDatas(squareDataDto, contents, uid);
            }
            log.info("myPublish end ...");
        }
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
    public ContentH5Dto contentH5(long id) {
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
        //记录阅读数量+1
        content.setReadCount(content.getReadCount()+1);
        contentMybatisDao.updateContentById(content);
        return contentH5Dto;
    }

    @Override
    public Response UserData(long targetUid,long sourceUid){
        log.info("getUserData start ...targetUid = " + targetUid + " sourceUid = "+ sourceUid);
        UserProfile userProfile = userService.getUserProfileByUid(targetUid);
        log.info("get userData success ");
        List<Content> list = contentMybatisDao.myPublish(targetUid,Integer.MAX_VALUE);
        log.info("get user content success ");
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
        userInfoDto.getUser().setIntroduced(userProfile.getIntroduced());
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

                SystemConfig systemConfig =userService.getSystemConfig();
                int start = systemConfig.getReadCountStart();
                int end = systemConfig.getReadCountEnd();
                int readCountDummy = content.getReadCountDummy();
                Random random = new Random();
                //取1-6的随机数每次添加
                int value = random.nextInt(end)+start;
                int readDummy = readCountDummy+value;
                content.setReadCountDummy(readDummy);
                contentMybatisDao.updateContentById(content);
                contentElement.setReadCount(readDummy);

            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setForwardTitle(content.getForwardTitle());
            String cover =  content.getConverImage();
            if(!StringUtils.isEmpty(cover)){
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index)
            {
                contentElement.setLiveStatus(contentMybatisDao.getTopicStatus(content.getForwardCid()));
                int reviewCount = contentMybatisDao.countFragment(content.getForwardCid(),content.getUid());
                contentElement.setReviewCount(reviewCount);
                contentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                contentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), sourceUid);
            log.info("get content favorite success");
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
            log.info("get content review success");
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
        log.info("getUserData end ...");
        return Response.success(userInfoDto);
    }

    @Override
    public Response UserData2(long targetUid,long sourceUid){
        UserInfoDto2 userInfoDto = new UserInfoDto2();
        log.info("getUserData2 start ...targetUid = " + targetUid + " sourceUid = "+ sourceUid);
        UserProfile userProfile = userService.getUserProfileByUid(targetUid);
        log.info("get getUserData2 success ");
       // List<Content> list = contentMybatisDao.myPublish(targetUid,Integer.MAX_VALUE);
        MyPublishDto dto = new MyPublishDto();
        dto.setUid(targetUid);
        dto.setSinceId(Integer.MAX_VALUE);
        dto.setType(Specification.ArticleType.ORIGIN.index);
        if(targetUid == sourceUid) {
            dto.setIsOwner(1);
        }else {
            dto.setIsOwner(0);
        }
        //非直播文章
        List<Content> contents = contentMybatisDao.myPublishByType(dto);
        userInfoDto.setContentCount(contentMybatisDao.countMyPublishByType(dto));
        log.info("get user content success ");
        userInfoDto.getUser().setV_lv(userProfile.getvLv());
        userInfoDto.getUser().setNickName(userProfile.getNickName());
        userInfoDto.getUser().setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        userInfoDto.getUser().setGender(userProfile.getGender());
        userInfoDto.getUser().setUid(userProfile.getUid());
        userInfoDto.getUser().setMeNumber(userService.getUserNoByUid(targetUid));
        userInfoDto.getUser().setIsFollowed(userService.isFollow(targetUid,sourceUid));
        userInfoDto.getUser().setIsFollowMe(userService.isFollow(sourceUid,targetUid));
        userInfoDto.getUser().setFollowedCount(userService.getFollowCount(targetUid));
        userInfoDto.getUser().setFansCount(userService.getFansCount(targetUid));
        userInfoDto.getUser().setIntroduced(userProfile.getIntroduced());
        buildUserData(sourceUid, contents,Specification.ArticleType.ORIGIN.index,userInfoDto);
        //直播
        dto.setType(Specification.ArticleType.LIVE.index);
        Calendar calendar = Calendar.getInstance();
        dto.setUpdateTime(calendar.getTimeInMillis());
        List<Content> lives = contentMybatisDao.myPublishByType(dto);
        buildUserData(sourceUid, lives,Specification.ArticleType.LIVE.index,userInfoDto);
        userInfoDto.setLiveCount(contentMybatisDao.countMyPublishByType(dto));
        log.info("getUserData end ...");
        return Response.success(userInfoDto);
    }

    private void buildUserData(long sourceUid, List<Content> contents,int type,UserInfoDto2 userInfoDto) {
    	List<Long> topicIdList = new ArrayList<Long>();
        for(Content idx : contents){
        	if(idx.getType() == Specification.ArticleType.LIVE.index){//王国
        		topicIdList.add(idx.getForwardCid());
        	}
        }
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
        	Long topicId = null;
        	for(Map<String,Object>  map : topicList){
        		topicId = (Long)map.get("id");
        		topicMap.put(topicId.toString(), map);
        	}
        }
        for (Content content : contents){
            UserInfoDto2.ContentElement contentElement = UserInfoDto2.createElement();
            contentElement.setTag(content.getFeeling());
            String contentStr = content.getContent();
            if(contentStr.length() > 100){
                contentElement.setContent(contentStr.substring(0,100));
            }else{
                contentElement.setContent(contentStr);
            }
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

                SystemConfig systemConfig =userService.getSystemConfig();
                int start = systemConfig.getReadCountStart();
                int end = systemConfig.getReadCountEnd();
                int readCountDummy = content.getReadCountDummy();
                Random random = new Random();
                //取1-6的随机数每次添加
                int value = random.nextInt(end)+start;
                int readDummy = readCountDummy+value;
                content.setReadCountDummy(readDummy);
                contentMybatisDao.updateContentById(content);
                contentElement.setReadCount(readDummy);

            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setForwardTitle(content.getForwardTitle());
            contentElement.setUid(content.getUid());
            UserProfile profile = userService.getUserProfileByUid(content.getUid());
            contentElement.setV_lv(profile.getvLv());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)){
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            if(type == Specification.ArticleType.LIVE.index) {
                contentElement.setLiveStatus(contentMybatisDao.getTopicStatus(content.getForwardCid()));
                int reviewCount = contentMybatisDao.countFragment(content.getForwardCid(),content.getUid());
                contentElement.setReviewCount(reviewCount);
                contentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                contentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
                
                //王国增加身份信息
            	Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
            	if(null != topic){
            		contentElement.setInternalStatus(this.getInternalStatus(topic, sourceUid));
            	}
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), sourceUid);
            log.info("get content favorite success");
            //直播是否收藏
            contentElement.setFavorite(favorite);
            contentElement.setIsLike(isLike(content.getId(),sourceUid));
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
//            ContentImage contentImage = contentMybatisDao.getCoverImages(content.getId());
//            if(contentImage != null) {
//                contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
//            }else{
//                contentElement.setCoverImage("");
//            }
            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            log.info("get content review success");
            for(ContentReview contentReview : contentReviewList){
                UserInfoDto2.ContentElement.ReviewElement reviewElement = UserInfoDto2.ContentElement.createElement();
                reviewElement.setUid(contentReview.getUid());
                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
                reviewElement.setNickName(user.getNickName());
                reviewElement.setCreateTime(contentReview.getCreateTime());
                reviewElement.setReview(contentReview.getReview());
                contentElement.getReviews().add(reviewElement);
            }
            if(type == Specification.ArticleType.LIVE.index){
                userInfoDto.getLiveElementList().add(contentElement);
            }else{
                userInfoDto.getContentElementList().add(contentElement);
            }
        }
    }

    @Override
    public Response editorPublish(ContentDto contentDto) {
        log.info("editorPublish start ...");
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
        log.info("content create success");
        //保存标签
        createTag(contentDto, content);
        log.info("contentTag create success");
        log.info("editorPublish end ...");
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message);
    }

    public void createTag(ContentDto contentDto, Content content) {
        log.info("createTag start ...");
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
            log.info("create tag and tagDetail success");
        }else{
            ContentTags contentTags = new ContentTags();
            contentTags.setTag(contentDto.getFeeling());
            contentMybatisDao.createTag(contentTags);
            ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
            contentTagsDetails.setTid(contentTags.getId());
            contentTagsDetails.setCid(content.getId());
            contentTagsDetails.setUid(content.getUid());
            contentMybatisDao.createContentTagsDetails(contentTagsDetails);
            log.info("create tag and tagDetail success");
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
    public Response SelectedData(int sinceId,long uid) {
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
        log.info("modifyRights start ...");
        Content content = contentMybatisDao.getContentById(cid);
        if(content == null){
            return Response.failure(ResponseStatus.CONTENT_IS_NOT_EXIST.status,ResponseStatus.CONTENT_IS_NOT_EXIST.message);
        }
        if(!content.getUid().equals(uid)){
            return Response.failure(ResponseStatus.CONTENT_IS_NOT_YOURS.status,ResponseStatus.CONTENT_IS_NOT_YOURS.message);
        }
        content.setRights(rights);
        contentMybatisDao.updateContentById(content);
        log.info("modifyRights end ...");
        return Response.success(ResponseStatus.CONTENT_IS_PUBLIC_MODIFY_SUCCESS.status,ResponseStatus.CONTENT_IS_PUBLIC_MODIFY_SUCCESS.message);
    }

    @Override
    public Response Activities(int sinceId,long uid) {
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
            element.setTopped(content.getIsTop());
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
        return Response.success(showContentDto);
    }

    @Override
    public Response getHottest(int sinceId,long uid){
        log.info("getHottest start ...");
        ShowHottestDto hottestDto = new ShowHottestDto();
        //活动
        if(sinceId == Integer.MAX_VALUE) {
            List<ActivityWithBLOBs> activityList = activityService.getActivityTop5();
            log.info("getActivityTop5 success ");
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
                activityElement.setReviewCount(activityService.getReviewCount(activity.getId()));
                activityElement.setLikeCount(activityService.getLikeCount(activity.getId()));
                hottestDto.getActivityData().add(activityElement);
            }
        }
        // 置顶内容
        if(sinceId==Integer.MAX_VALUE) {
            List<Content> contentTopList = contentMybatisDao.getHottestTopsContent();
            builderContent(uid, contentTopList, hottestDto.getTops());
        }
        //内容
        List<Content> contentList = contentMybatisDao.getHottestContent(sinceId);
        log.info("getHottestContent success");
        builderContent(uid, contentList,hottestDto.getHottestContentData());
        //log.info("monitor");
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.HOTTEST.index,0,uid));
        log.info("getHottest end ...");
        return Response.success(hottestDto);
    }

    @Override
    public Response Hottest2(int sinceId,long uid){
        log.info("getHottest2 start ...");
        ShowHottestDto hottestDto = new ShowHottestDto();
        //活动
        if(sinceId == Integer.MAX_VALUE) {
            List<ActivityWithBLOBs> activityList = activityService.getActivityTop5();
            log.info("getActivityTop5 success ");
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
                int followMe = userService.isFollow(uid,activity.getUid());
                activityElement.setIsFollowMe(followMe);
                activityElement.setId(activity.getId());
                activityElement.setReviewCount(activityService.getReviewCount(activity.getId()));
                activityElement.setLikeCount(activityService.getLikeCount(activity.getId()));
                hottestDto.getActivityData().add(activityElement);
            }
        }
        // 置顶内容
        if(sinceId==Integer.MAX_VALUE) {
            List<Content> contentTopList = contentMybatisDao.getHottestTopsContent();
            builderContent(uid, contentTopList, hottestDto.getTops());
        }
        //内容
        List<Content2Dto> contentList = contentMybatisDao.getHottestContentByUpdateTime(sinceId);
        log.info("getHottestContent success");
        builderContent2(uid, contentList,hottestDto.getHottestContentData());
        //log.info("monitor");
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.HOTTEST.index,0,uid));
        log.info("getHottest end ...");
        return Response.success(hottestDto);
    }

    //获取非直播总数
    @Override
    public int getUgcCount(long uid) {
        return contentMybatisDao.getUgcCount(uid);
    }

    @Override
    public int getLiveCount(long uid) {
        return contentMybatisDao.getLiveCount(uid);
    }




    private void builderContent(long uid,List<Content> contentList, List<ShowHottestDto.HottestContentElement> container) {
        for(Content content : contentList){
            ShowHottestDto.HottestContentElement hottestContentElement = ShowHottestDto.createHottestContentElement();
            hottestContentElement.setType(content.getType());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)) {
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
                    hottestContentElement.setCoverImage(cover);
                }else {
                    hottestContentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            hottestContentElement.setId(content.getId());
            String contentStr = content.getContent();
            if(contentStr.length() > 100){
                hottestContentElement.setContent(contentStr.substring(0,100));
            }else{
                hottestContentElement.setContent(contentStr);
            }
            hottestContentElement.setLikeCount(content.getLikeCount());
            hottestContentElement.setReviewCount(content.getReviewCount());
            hottestContentElement.setTitle(content.getTitle());
            hottestContentElement.setCreateTime(content.getCreateTime());
            hottestContentElement.setIsLike(isLike(content.getId(),uid));
            hottestContentElement.setForwardUrl(content.getForwardUrl());
            hottestContentElement.setForwardTitle(content.getForwardTitle());

//                SystemConfig systemConfig =userService.getSystemConfig();
//                int start = systemConfig.getReadCountStart();
//                int end = systemConfig.getReadCountEnd();
                int readCountDummy = content.getReadCountDummy();
//                Random random = new Random();
//                //取1-6的随机数每次添加
//                int value = random.nextInt(end)+start;
//                int readDummy = readCountDummy+value;
//                content.setReadCountDummy(readDummy);
//                contentMybatisDao.updateContentById(content);
                hottestContentElement.setReadCount(readCountDummy);

//            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
//            log.info("getContentReviewTop3ByCid success");
//            for(ContentReview contentReview : contentReviewList){
//                ShowHottestDto.HottestContentElement.ReviewElement reviewElement = ShowHottestDto.HottestContentElement.createElement();
//                reviewElement.setUid(contentReview.getUid());
//                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
//                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
//                reviewElement.setNickName(user.getNickName());
//                reviewElement.setCreateTime(contentReview.getCreateTime());
//                reviewElement.setReview(contentReview.getReview());
//                hottestContentElement.getReviews().add(reviewElement);
//            }
            //系统文章不包含，用户信息
            if(content.getType() == Specification.ArticleType.SYSTEM.index){

           //直播 直播状态
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
                int reviewCount = contentMybatisDao.countFragment(content.getForwardCid(),content.getUid());
                hottestContentElement.setReviewCount(reviewCount);
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
                hottestContentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                hottestContentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
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
            container.add(hottestContentElement);
        }
    }

    private void builderContent2(long uid,List<Content2Dto> contentList, List<ShowHottestDto.HottestContentElement> container) {
    	List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        for(Content2Dto idx : contentList){
        	if(!uidList.contains(idx.getUid())){
        		uidList.add(idx.getUid());
        	}
        	if(idx.getType() == Specification.ArticleType.LIVE.index){//王国
        		topicIdList.add(idx.getForwardCid());
        	}
        }
        
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
        	for(UserProfile up : profileList){
        		profileMap.put(String.valueOf(up.getUid()), up);
        	}
        }
        
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
        	Long topicId = null;
        	for(Map<String,Object>  map : topicList){
        		topicId = (Long)map.get("id");
        		topicMap.put(topicId.toString(), map);
        	}
        }
        
        UserProfile userProfile = null;
        for(Content2Dto content : contentList){
            ShowHottestDto.HottestContentElement hottestContentElement = ShowHottestDto.createHottestContentElement();
            hottestContentElement.setType(content.getType());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)) {
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
                    hottestContentElement.setCoverImage(cover);
                }else {
                    hottestContentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            hottestContentElement.setId(content.getId());
            String contentStr = content.getContent();
            if(contentStr.length() > 100){
                hottestContentElement.setContent(contentStr.substring(0,100));
            }else{
                hottestContentElement.setContent(contentStr);
            }
            
            hottestContentElement.setLikeCount(content.getLikeCount());
            hottestContentElement.setReviewCount(content.getReviewCount());
            hottestContentElement.setTitle(content.getTitle());
            hottestContentElement.setCreateTime(content.getCreateTime());
            hottestContentElement.setIsLike(isLike(content.getId(),uid));
            hottestContentElement.setForwardUrl(content.getForwardUrl());
            hottestContentElement.setForwardTitle(content.getForwardTitle());
            //为了update查询的
//          Content content1 = contentMybatisDao.getContentById(content.getId());
            int readCountDummy = content.getReadCountDummy();
            hottestContentElement.setReadCount(readCountDummy);

            hottestContentElement.setRights(content.getRights());
//            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
//            log.info("getContentReviewTop3ByCid success");
//            for(ContentReview contentReview : contentReviewList){
//                ShowHottestDto.HottestContentElement.ReviewElement reviewElement = ShowHottestDto.HottestContentElement.createElement();
//                reviewElement.setUid(contentReview.getUid());
//                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
//                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
//                reviewElement.setNickName(user.getNickName());
//                reviewElement.setCreateTime(contentReview.getCreateTime());
//                reviewElement.setReview(contentReview.getReview());
//                hottestContentElement.getReviews().add(reviewElement);
//            }
            //系统文章不包含，用户信息
            if(content.getType() == Specification.ArticleType.SYSTEM.index){

                //直播 直播状态
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
            	//王国增加身份信息
            	Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
            	if(null != topic){
            		hottestContentElement.setInternalStatus(this.getInternalStatus(topic, uid));
            	}
            	
                int reviewCount = contentMybatisDao.countFragment(content.getForwardCid(),content.getUid());
                hottestContentElement.setReviewCount(reviewCount);
                hottestContentElement.setUid(content.getUid());
                hottestContentElement.setForwardCid(content.getForwardCid());
                //查询直播状态
                int status = contentMybatisDao.getTopicStatus(content.getForwardCid());
                hottestContentElement.setLiveStatus(status);
                //直播是否收藏
                int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
                hottestContentElement.setFavorite(favorite);
                userProfile = profileMap.get(String.valueOf(content.getUid()));
                hottestContentElement.setV_lv(userProfile.getvLv());
                hottestContentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                hottestContentElement.setNickName(userProfile.getNickName());
                hottestContentElement.setTag(content.getFeeling());
                int follow = userService.isFollow(content.getUid(),uid);
                hottestContentElement.setIsFollowed(follow);
                int followMe = userService.isFollow(uid,content.getUid());
                hottestContentElement.setIsFollowMe(followMe);

                hottestContentElement.setPersonCount(content.getPersonCount());
                hottestContentElement.setFavoriteCount(content.getFavoriteCount());
                hottestContentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                hottestContentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
                //原生
            }else if(content.getType() == Specification.ArticleType.ORIGIN.index){
                hottestContentElement.setUid(content.getUid());
                userProfile = profileMap.get(String.valueOf(content.getUid()));
                hottestContentElement.setV_lv(userProfile.getvLv());
                hottestContentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                hottestContentElement.setNickName(userProfile.getNickName());
                hottestContentElement.setTag(content.getFeeling());
                int follow = userService.isFollow(content.getUid(),uid);
                hottestContentElement.setIsFollowed(follow);
                int followMe = userService.isFollow(uid,content.getUid());
                hottestContentElement.setIsFollowMe(followMe);
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                hottestContentElement.setImageCount(imageCounts);

            }
            hottestContentElement.setSinceId(content.getHid());
            container.add(hottestContentElement);
        }
    }


    /**
     * 获取最新用户日记，直播
     * @param sinceId
     * @param uid
     * @return
     */
    @Override
    public Response Newest(int sinceId, long uid) {
        log.info("getNewest start ...");
        ShowNewestDto showNewestDto = new ShowNewestDto();
        List<Content> newestList = contentMybatisDao.getNewest(sinceId);
        log.info("getNewest data success ");
        
        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        for(Content idx : newestList){
        	if(!uidList.contains(idx.getUid())){
        		uidList.add(idx.getUid());
        	}
        	if(idx.getType() == Specification.ArticleType.LIVE.index){//王国
        		topicIdList.add(idx.getForwardCid());
        	}
        }
        
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
        	for(UserProfile up : profileList){
        		profileMap.put(String.valueOf(up.getUid()), up);
        	}
        }
        
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
        	Long topicId = null;
        	for(Map<String,Object>  map : topicList){
        		topicId = (Long)map.get("id");
        		topicMap.put(topicId.toString(), map);
        	}
        }
        
        UserProfile userProfile = null;
        for(Content content : newestList){
            ShowNewestDto.ContentElement contentElement = ShowNewestDto.createElement();
            
            contentElement.setId(content.getId());
            contentElement.setUid(content.getUid());
            // 获取用户信息
            userProfile = profileMap.get(String.valueOf(content.getUid()));
            contentElement.setV_lv(userProfile.getvLv());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setCreateTime(content.getCreateTime());
            String contentStr = content.getContent();
            if(contentStr.length() > 100){
                contentElement.setContent(contentStr.substring(0,100));
            }else{
                contentElement.setContent(contentStr);
            }
            contentElement.setType(content.getType());
            contentElement.setTitle(content.getTitle());
            contentElement.setIsLike(isLike(content.getId(),uid));
            String cover = content.getConverImage();
            contentElement.setReviewCount(content.getReviewCount());

                int readCountDummy = content.getReadCountDummy();
                contentElement.setReadCount(readCountDummy);

            contentElement.setRights(content.getRights());
            if(!StringUtils.isEmpty(cover)) {
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            contentElement.setForwardCid(content.getForwardCid());
            buildLive(content, contentElement);
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
            	//王国增加身份信息
            	Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
            	if(null != topic){
            		contentElement.setInternalStatus(this.getInternalStatus(topic, uid));
            	}
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
            //直播是否收藏
            contentElement.setFavorite(favorite);
            //判断人员是否关注
            int follow = userService.isFollow(content.getUid(),uid);
            contentElement.setIsFollowed(follow);
            int followMe = userService.isFollow(uid,content.getUid());
            contentElement.setIsFollowMe(followMe);
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setForwardTitle(content.getForwardTitle());
            contentElement.setContentType(content.getContentType());
//            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
//            log.info("content review data success");
//            for(ContentReview contentReview : contentReviewList){
//                ShowNewestDto.ContentElement.ReviewElement reviewElement = ShowNewestDto.ContentElement.createElement();
//                reviewElement.setUid(contentReview.getUid());
//                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
//                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
//                reviewElement.setNickName(user.getNickName());
//                reviewElement.setCreateTime(contentReview.getCreateTime());
//                reviewElement.setReview(contentReview.getReview());
//                contentElement.getReviews().add(reviewElement);
//            }
            showNewestDto.getNewestData().add(contentElement);
        }
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.NEWEST.index,0,uid));
        //log.info("monitor");
        return Response.success(showNewestDto);
    }

    private void buildLive(Content content, BaseContentDto contentElement) {
        if(content.getType() == Specification.ArticleType.LIVE.index) {
            //查询直播状态
            contentElement.setLiveStatus(contentMybatisDao.getTopicStatus(content.getForwardCid()));
            int reviewCount = contentMybatisDao.countFragment(content.getForwardCid(),content.getUid());
            contentElement.setReviewCount(reviewCount);
            contentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
            contentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
        }
    }

    //获取自己UGC和直播列表不加权限
    private void MyAttention(ShowAttentionDto showAttentionDto ,List<Content> contentList ,long uid){

        for(Content content : contentList) {
            ShowAttentionDto.ContentElement contentElement = showAttentionDto.createElement();
            contentElement
                    .setId(content.getId());
            contentElement.setUid(content.getUid());
            // 获取用户信息
            UserProfile userProfile = userService.getUserProfileByUid(content.getUid());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setRights(content.getRights());
            String contentStr = content.getContent();
            if (contentStr.length() > 100) {
                contentElement.setContent(contentStr.substring(0, 100));
            } else {
                contentElement.setContent(contentStr);
            }
            contentElement.setType(content.getType());
            contentElement.setTitle(content.getTitle());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setIsLike(isLike(content.getId(), uid));
            contentElement.setReviewCount(content.getReviewCount());

                SystemConfig systemConfig =userService.getSystemConfig();
                int start = systemConfig.getReadCountStart();
                int end = systemConfig.getReadCountEnd();
                int readCountDummy = content.getReadCountDummy();
                Random random = new Random();
                //取1-6的随机数每次添加
                int value = random.nextInt(end)+start;
                int readDummy = readCountDummy+value;
                content.setReadCountDummy(readDummy);
                contentMybatisDao.updateContentById(content);
                contentElement.setReadCount(readDummy);

            String cover = content.getConverImage();
            if (!StringUtils.isEmpty(cover)) {
                if (content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index) {
                    contentElement.setCoverImage(cover);
                } else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            buildLive(content, contentElement);
            if (content.getType() == Specification.ArticleType.ORIGIN.index) {
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            int favorite = contentMybatisDao.isFavorite(content.getForwardCid(), uid);
            //直播是否收藏
            contentElement.setFavorite(favorite);
            //判断人员是否关注
            int follow = userService.isFollow(content.getUid(), uid);
            contentElement.setIsFollowed(follow);
            int followMe = userService.isFollow(uid, content.getUid());
            contentElement.setIsFollowMe(followMe);
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            contentElement.setForwardTitle(content.getForwardTitle());
            contentElement.setForwardUrl(content.getForwardUrl());
            showAttentionDto.getAttentionData().add(contentElement);
            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            log.info("getContentReviewTop3ByCid data success");
            for (ContentReview contentReview : contentReviewList) {
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
    }
    @Override
    public Response Attention(int sinceId, long uid) {
        log.info("current sinceId is : " + sinceId);
        log.info("getAttention start ...");
        ShowAttentionDto showAttentionDto = new ShowAttentionDto();
        //获取此人关注的人是列表
        List<Long> list = userService.getFollowList(uid);
        log.info("get user follow");
        List<Content> attentionList = contentMybatisDao.getAttention(sinceId ,list,uid);
        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        for(Content idx : attentionList){
        	if(!uidList.contains(idx.getUid())){
        		uidList.add(idx.getUid());
        	}
        	if(idx.getType() == Specification.ArticleType.LIVE.index){//王国
        		topicIdList.add(idx.getForwardCid());
        	}
        }
        log.info("getAttention data");
        
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
        	for(UserProfile up : profileList){
        		profileMap.put(String.valueOf(up.getUid()), up);
        	}
        }
        
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
        	Long topicId = null;
        	for(Map<String,Object>  map : topicList){
        		topicId = (Long)map.get("id");
        		topicMap.put(topicId.toString(), map);
        	}
        }
        
        UserProfile userProfile = null;
        for(Content content : attentionList){
            ShowAttentionDto.ContentElement contentElement = showAttentionDto.createElement();
            contentElement.setId(content.getId());
            contentElement.setUid(content.getUid());
            // 获取用户信息
            userProfile = profileMap.get(String.valueOf(content.getUid()));
            contentElement.setV_lv(userProfile.getvLv());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setCreateTime(content.getCreateTime());
            contentElement.setRights(content.getRights());
            String contentStr = content.getContent();
            if(contentStr.length() > 100){
                contentElement.setContent(contentStr.substring(0,100));
            }else{
                contentElement.setContent(contentStr);
            }
            contentElement.setType(content.getType());
            contentElement.setContentType(content.getContentType());
            contentElement.setTitle(content.getTitle());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setIsLike(isLike(content.getId(),uid));
            contentElement.setReviewCount(content.getReviewCount());
            contentElement.setReadCount(content.getReadCountDummy());

            String cover =  content.getConverImage();
            if(!StringUtils.isEmpty(cover)){
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            //查询直播状态
            buildLive(content, contentElement);
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
            int followMe = userService.isFollow(uid,content.getUid());
            contentElement.setIsFollowMe(followMe);
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount());
            contentElement.setForwardTitle(content.getForwardTitle());
            contentElement.setForwardUrl(content.getForwardUrl());
            if(content.getType() == Specification.ArticleType.LIVE.index){//王国的话，获取身份信息
            	Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
            	if(null != topic){
            		contentElement.setInternalStatus(this.getInternalStatus(topic, uid));
            	}
            }
            
            showAttentionDto.getAttentionData().add(contentElement);
            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            log.info("getContentReviewTop3ByCid data success");
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
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FOLLOW_LIST.index,0,uid));
        log.info("monitor");
        return Response.success(showAttentionDto);
    }
    
    //判断核心圈身份
    private int getInternalStatus(Map<String, Object> topic, long uid) {
    	int internalStatus = 0;
        String coreCircle = (String)topic.get("core_circle");
        if(null != coreCircle){
        	JSONArray array = JSON.parseArray(coreCircle);
        	for (int i = 0; i < array.size(); i++) {
                if (array.getLong(i) == uid) {
                    internalStatus = Specification.SnsCircle.CORE.index;
                    break;
                }
            }
        }
        
        if (internalStatus == 0) {
            internalStatus = userService.getUserInternalStatus(uid, (Long)topic.get("uid"));
        }

        return internalStatus;
    }

    @Override
    public Response createReview(ReviewDto reviewDto) {
        log.info("createReview start ...");
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
        //userService.push(content.getUid(),reviewDto.getUid(),Specification.PushMessageType.REVIEW.index,content.getTitle());
        return Response.success(ResponseStatus.CONTENT_REVIEW_SUCCESS.status,ResponseStatus.CONTENT_REVIEW_SUCCESS.message);
    */
        //return new ReviewAdapter(ReviewFactory.getInstance(reviewDto.getType())).execute(reviewDto);
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.REVIEW.index,0,reviewDto.getUid()));
        log.info("createReview monitor success");
        return reviewAdapter.execute(reviewDto);
    }

    @Override
    public Response option(long id, int optionAction, int action) {
        // pgc 1
        // ugc 0
        // 活动 2
        if(optionAction==0 || optionAction==1){
            // UGC操作
           return optionContent(action,id);
        }else if(optionAction==2){
            // 活动操作
           return optionActivity(action, id);
        }
        return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status,ResponseStatus.ILLEGAL_REQUEST.message);
    }

    private Response optionActivity(int action, long id) {
        ActivityWithBLOBs activity = activityService.loadActivityById(id);
        if(action==1){
            // 取消置热
            activity.setStatus(0);
        }else{
            activity.setStatus(1);
        }
        activityService.modifyActivity(activity);
        return Response.success(ResponseStatus.HIGH_QUALITY_CONTENT_SUCCESS.status,ResponseStatus.HIGH_QUALITY_CONTENT_SUCCESS.message);
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
        showUGCDetailsDto.setTitle(content.getTitle());
        showUGCDetailsDto.setFeelings(content.getFeeling());
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
    public Response reviewList(long cid, long sinceId,int type) {
        log.info("reviewList start ...");
        ContentReviewDto contentReviewDto = new ContentReviewDto();
        List<ContentReview> list = null;
        if(type == Specification.ArticleType.SYSTEM.index){
            list = contentMybatisDao.getArticleReviewByCid(cid,sinceId);
        }else{
            list = contentMybatisDao.getContentReviewByCid(cid,sinceId);
        }
        log.info("reviewList get contentReview success");
        for(ContentReview contentReview : list){
            ContentReviewDto.ReviewElement reviewElement = ContentReviewDto.createElement();
            reviewElement.setUid(contentReview.getUid());
            reviewElement.setCid(contentReview.getCid());
            reviewElement.setReview(contentReview.getReview());
            reviewElement.setCreateTime(contentReview.getCreateTime());
            UserProfile userProfile = userService.getUserProfileByUid(contentReview.getUid());
            reviewElement.setNickName(userProfile.getNickName());
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            reviewElement.setId(contentReview.getId());
            reviewElement.setV_lv(userProfile.getvLv());
            if(!StringUtils.isEmpty(contentReview.getExtra())) {
                reviewElement.setExtra(contentReview.getExtra());
            }
            if(contentReview.getAtUid() != 0) {
                UserProfile atUser = userService.getUserProfileByUid(contentReview.getAtUid());
                reviewElement.setAtNickName(atUser.getNickName());
                reviewElement.setAtUid(atUser.getUid());
            }
            contentReviewDto.getReviews().add(reviewElement);

        }
        log.info("reviewList end ...");
        return Response.success(contentReviewDto);
    }

    @Override
    public void updateContentById(Content content) {
        contentMybatisDao.updateContentById(content);
    }

    private Response optionContent(int action, long id) {
        if(action==1){
            // UGC置热
            HighQualityContent highQualityContent = new HighQualityContent();
            highQualityContent.setCid(id);
            //自己发布的被置热
            Content content = contentMybatisDao.getContentById(id);
            //UGC置热
            HighQualityContent qualityContent = contentMybatisDao.getHQuantityByCid(id);
            if(qualityContent != null){
                return Response.success(ResponseStatus.HIGH_QUALITY_CONTENT_YET.status,ResponseStatus.HIGH_QUALITY_CONTENT_YET.message);
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index) {
                //userService.push(content.getUid(), 000000, Specification.PushMessageType.HOTTEST.index, content.getTitle());
            //直播置热
                //信鸽推送修改为极光推送
                JpushToken jpushToken = userService.getJpushTokeByUid(content.getUid());
                if(jpushToken == null){
                    //兼容老版本，如果客户端没有更新则还走信鸽push
                    userService.push(content.getUid(), 000000, Specification.PushMessageType.HOTTEST.index, content.getTitle());
                }else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("messageType",Specification.PushMessageType.LIVE_HOTTEST.index);
                    jsonObject.addProperty("type",Specification.PushObjectType.UGC.index);
                    jsonObject.addProperty("topicId",id);
                    String alias = String.valueOf(content.getUid());
                    jPushService.payloadByIdExtra(alias,"你发布的内容上热点啦！", JPushUtils.packageExtra(jsonObject));
                }
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
                JSONArray coreCircles = liveForContentJdbcDao.getTopicCoreCircle(content.getForwardCid());
                if(coreCircles!=null){
                    for(int i=0;i<coreCircles.size();i++){
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("messageType",Specification.PushMessageType.LIVE_HOTTEST.index);
                        jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
                        jsonObject.addProperty("topicId",content.getForwardCid());
                        jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//此处是核心圈的推送，所以直接设置核心圈
                        String alias =coreCircles.getString(i);
                        jPushService.payloadByIdExtra(alias,"『"+content.getTitle()+ "』上热点啦！",JPushUtils.packageExtra(jsonObject));
                    }
                }
            }

            contentMybatisDao.createHighQualityContent(highQualityContent);
            return Response.success(ResponseStatus.HIGH_QUALITY_CONTENT_SUCCESS.status,ResponseStatus.HIGH_QUALITY_CONTENT_SUCCESS.message);
        }else{
            // 取消置热
            HighQualityContent temp = contentMybatisDao.getHQuantityByCid(id);
            contentMybatisDao.removeHighQualityContent(temp.getId());
            return Response.success(ResponseStatus.HIGH_QUALITY_CONTENT_CANCEL_SUCCESS.status,ResponseStatus.HIGH_QUALITY_CONTENT_CANCEL_SUCCESS.message);
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

    @Override
    public void createTag(ContentTags contentTags) {
        contentMybatisDao.createTag(contentTags);
    }

    @Override
    public void createContentTagsDetails(ContentTagsDetails contentTagsDetails) {
        contentMybatisDao.createContentTagsDetails(contentTagsDetails);
    }

    @Override
    public void createContentArticleDetails(ArticleTagsDetails articleTagsDetails) {
        contentMybatisDao.createContentArticleDetails(articleTagsDetails);
    }


    @Override
    public void clearData() {
        contentMybatisDao.clearData();
    }

    public static void main(String[] args) {
        Random random = new Random();
        for(int i=0 ; i<9 ;i++){
            System.out.println(random.nextInt(5)+2);
        }
    }

    /**
     * 删除文章评论
     * 删除规则：--20161114
     * 1）管理员能删所有的评论
     * 2）非管理员只能删自己发的评论
     * @param delDTO
     */
	@Override
	public Response delArticleReview(ReviewDelDTO delDTO) {
		ArticleReview review = contentMybatisDao.getArticleReviewById(delDTO.getRid());
		if(null == review){//可能是删除那些未发送成功的，所以直接置为成功
			log.info("文章评论["+delDTO.getRid()+"]不存在");
			return Response.success(ResponseStatus.REVIEW_DELETE_SUCCESS.status, ResponseStatus.REVIEW_DELETE_SUCCESS.message);
		}
		//判断当前用户是否有删除本条评论的权限
		boolean canDel = false;
		//判断是否是管理员，管理员啥都能删
		if(userService.isAdmin(delDTO.getUid())){
			canDel = true;
    	}
		if(!canDel){
			//再判断是否是自己发的评论
			if(review.getUid() == delDTO.getUid()){//自己的
				canDel = true;
			}
		}
		
		if(!canDel){
			return Response.failure(ResponseStatus.REVIEW_CAN_NOT_DELETE.status, ResponseStatus.REVIEW_CAN_NOT_DELETE.message);
		}
		
		ArticleReview ar = new ArticleReview();
		ar.setId(review.getId());
		ar.setStatus(Specification.ContentDelStatus.DELETE.index);
		contentMybatisDao.updateArticleReview(ar);
		
		Content c = contentMybatisDao.getContentById(delDTO.getCid());
		if(null != c){
			int reviewCount = c.getReviewCount() -1;
			if(reviewCount < 0){
				reviewCount = 0;
			}
			c.setReviewCount(reviewCount);
			contentMybatisDao.updateContentById(c);
		}
		
		//记录下删除记录
		liveForContentJdbcDao.insertDeleteLog(Specification.DeleteObjectType.ARTICLE_REVIEW.index, delDTO.getRid(), delDTO.getUid());
		
		return Response.success(ResponseStatus.REVIEW_DELETE_SUCCESS.status, ResponseStatus.REVIEW_DELETE_SUCCESS.message);
	}

	/**
	 * 删除UGC/PGC评论
	 * 删除规则：--20161114
	 * 1）管理员能删所有的评论
	 * 2）非管理员只能删自己发的评论
	 */
	@Override
	public Response delContentReview(ReviewDelDTO delDTO) {
		ContentReview review = contentMybatisDao.getContentReviewById(delDTO.getRid());
		if(null == review){//可能是删除那些未发送成功的，所以直接置为成功
			log.info("UGC评论["+delDTO.getRid()+"]不存在");
			return Response.success(ResponseStatus.REVIEW_DELETE_SUCCESS.status, ResponseStatus.REVIEW_DELETE_SUCCESS.message);
		}
		//判断当前用户是否有删除本条评论的权限
		boolean canDel = false;
		//判断是否是管理员，管理员啥都能删
		if(userService.isAdmin(delDTO.getUid())){
			canDel = true;
    	}
		if(!canDel){
			//再判断是否是自己发的评论
			if(review.getUid() == delDTO.getUid()){//自己的
				canDel = true;
			}
		}
		
		if(!canDel){
			return Response.failure(ResponseStatus.REVIEW_CAN_NOT_DELETE.status, ResponseStatus.REVIEW_CAN_NOT_DELETE.message);
		}
		
		ContentReview cr = new ContentReview();
		cr.setId(review.getId());
		cr.setStatus(Specification.ContentDelStatus.DELETE.index);
		contentMybatisDao.updateContentReview(cr);
		
		Content c = contentMybatisDao.getContentById(delDTO.getCid());
		if(null != c){
			int reviewCount = c.getReviewCount() -1;
			if(reviewCount < 0){
				reviewCount = 0;
			}
			c.setReviewCount(reviewCount);
			contentMybatisDao.updateContentById(c);
		}
		
		//记录下删除记录
		liveForContentJdbcDao.insertDeleteLog(Specification.DeleteObjectType.CONTENT_REVIEW.index, delDTO.getRid(), delDTO.getUid());
		
		return Response.success(ResponseStatus.REVIEW_DELETE_SUCCESS.status, ResponseStatus.REVIEW_DELETE_SUCCESS.message);
	}

	@Override
	public Response deleteReview(ReviewDelDTO delDTO) {
		return reviewAdapter.executeDel(delDTO);
	}
	
	
}
