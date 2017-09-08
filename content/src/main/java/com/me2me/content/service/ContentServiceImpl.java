package com.me2me.content.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.me2me.core.KeysManager;
import com.me2me.user.dto.*;
import com.me2me.user.rule.Rules;

import org.apache.commons.lang3.RandomUtils;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.activity.service.ActivityService;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.enums.USER_OPRATE_TYPE;
import com.me2me.common.page.PageBean;
import com.me2me.common.utils.CollectionUtils;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.builders.KingdomBuilder;
import com.me2me.content.dao.BillBoardJdbcDao;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dao.LiveForContentJdbcDao;
import com.me2me.content.dto.BangDanDto;
import com.me2me.content.dto.BasicKingdomInfo;
import com.me2me.content.dto.BillBoardDetailsDto;
import com.me2me.content.dto.BillBoardListDTO;
import com.me2me.content.dto.BillBoardRelationDto;
import com.me2me.content.dto.Content2Dto;
import com.me2me.content.dto.ContentDetailDto;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.ContentH5Dto;
import com.me2me.content.dto.ContentReviewDto;
import com.me2me.content.dto.CreateContentSuccessDto;
import com.me2me.content.dto.EditorContentDto;
import com.me2me.content.dto.EmojiPackDetailDto;
import com.me2me.content.dto.EmojiPackDto;
import com.me2me.content.dto.HighQualityContentDto;
import com.me2me.content.dto.KingTopicDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.MyPublishDto;
import com.me2me.content.dto.OnlineBillBoardDto;
import com.me2me.content.dto.PricedKingdomDto;
import com.me2me.content.dto.RecommendContentDto;
import com.me2me.content.dto.ResultKingTopicDto;
import com.me2me.content.dto.ReviewDelDTO;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.dto.ShowArticleCommentsDto;
import com.me2me.content.dto.ShowArticleReviewDto;
import com.me2me.content.dto.ShowAttentionDto;
import com.me2me.content.dto.ShowContentDto;
import com.me2me.content.dto.ShowContentListDto;
import com.me2me.content.dto.ShowHotCeKingdomListDTO;
import com.me2me.content.dto.ShowHotListDTO;
import com.me2me.content.dto.ShowHotListDTO.HotContentElement;
import com.me2me.content.dto.ShowHotListDTO.HotTagElement;
import com.me2me.content.dto.ShowHottestDto;
import com.me2me.content.dto.ShowKingTopicDto;
import com.me2me.content.dto.ShowMyPublishDto;
import com.me2me.content.dto.ShowNewestDto;
import com.me2me.content.dto.ShowUGCDetailsDto;
import com.me2me.content.dto.ShowUserContentsDTO;
import com.me2me.content.dto.SquareDataDto;
import com.me2me.content.dto.TagKingdomDto;
import com.me2me.content.dto.UserContentSearchDTO;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.mapper.EmotionPackDetailMapper;
import com.me2me.content.mapper.EmotionPackMapper;
import com.me2me.content.mapper.TopicTagSearchMapper;
import com.me2me.content.mapper.UserVisitLogMapper;
import com.me2me.content.model.ArticleLikesDetails;
import com.me2me.content.model.ArticleReview;
import com.me2me.content.model.ArticleTagsDetails;
import com.me2me.content.model.BillBoard;
import com.me2me.content.model.BillBoardDetails;
import com.me2me.content.model.BillBoardList;
import com.me2me.content.model.BillBoardRelation;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentImage;
import com.me2me.content.model.ContentLikesDetails;
import com.me2me.content.model.ContentReview;
import com.me2me.content.model.ContentShareHistory;
import com.me2me.content.model.ContentTags;
import com.me2me.content.model.ContentTagsDetails;
import com.me2me.content.model.EmotionPack;
import com.me2me.content.model.EmotionPackDetail;
import com.me2me.content.model.EmotionPackDetailExample;
import com.me2me.content.model.EmotionPackExample;
import com.me2me.content.model.HighQualityContent;
import com.me2me.content.model.TagInfo;
import com.me2me.content.model.UserVisitLog;
import com.me2me.content.widget.ContentRecommendServiceProxyBean;
import com.me2me.content.widget.ContentStatusServiceProxyBean;
import com.me2me.content.widget.LikeAdapter;
import com.me2me.content.widget.PublishContentAdapter;
import com.me2me.content.widget.ReviewAdapter;
import com.me2me.content.widget.WriteTagAdapter;
import com.me2me.sms.service.JPushService;
import com.me2me.user.cache.EmotionSummaryModel;
import com.me2me.user.model.EmotionInfo;
import com.me2me.user.model.EmotionRecord;
import com.me2me.user.model.JpushToken;
import com.me2me.user.model.SystemConfig;
import com.me2me.user.model.UserFamous;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserNoticeUnread;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;
import com.plusnet.forecast.domain.ForecastContent;
import com.plusnet.search.content.RecommendRequest;
import com.plusnet.search.content.RecommendResponse;
import com.plusnet.search.content.api.ContentStatService;
import com.plusnet.search.content.domain.ContentTO;
import com.plusnet.search.content.domain.User;

import lombok.extern.slf4j.Slf4j;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Service
@Slf4j
public class ContentServiceImpl implements ContentService {
	@Autowired
	private JdbcTemplate jdbc;
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
    private CacheService cacheService;

    @Autowired
    private LiveForContentJdbcDao liveForContentJdbcDao;

    @Value("#{app.recommend_domain}")
    private String recommendDomain;

    private Random random = new Random();

    private ExecutorService executorService= Executors.newFixedThreadPool(100);

    @Autowired
    private BillBoardJdbcDao billBoardJdbcDao;

    @Autowired
    private EmotionPackMapper emotionPackMapper;

    @Autowired
    private EmotionPackDetailMapper  emotionPackDetailMapper;

    @Autowired
    private KingdomBuilder kingdomBuider;

    @Autowired
    private TopicTagSearchMapper topicTagMapper;
    @Autowired
    private UserVisitLogMapper userVisitLogMapper;
    
    
    
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
            if(null != contentTags){
                contentDataElement.setTid(contentTags.getId());
            }
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
        List<Long> forwardTopicIdList = new ArrayList<Long>();
        for(Content idx : contents){
            if(!uidList.contains(idx.getUid())){
                uidList.add(idx.getUid());
            }
            if(idx.getType() == Specification.ArticleType.LIVE.index
                    || idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国
                if(!topicIdList.contains(idx.getForwardCid())){
                    topicIdList.add(idx.getForwardCid());
                }
                if(idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    if(!forwardTopicIdList.contains(idx.getForwardCid())){
                        forwardTopicIdList.add(idx.getForwardCid());
                    }
                }
            }
        }

        Map<String, Map<String, Object>> forwardTopicUserProfileMap = new HashMap<String, Map<String, Object>>();
        if(forwardTopicIdList.size() > 0){
            List<Map<String,Object>> topicUserProfileList = liveForContentJdbcDao.getTopicUserProfileByTopicIds(forwardTopicIdList);
            if(null != topicUserProfileList && topicUserProfileList.size() > 0){
                for(Map<String,Object> topicUserProfile : topicUserProfileList){
                    forwardTopicUserProfileMap.put(String.valueOf(topicUserProfile.get("id")), topicUserProfile);
                }
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

        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }

        //一次性获取当前用户针对于各王国是否收藏过
        Map<String, Map<String,Object>> liveFavoriteMap = new HashMap<String, Map<String,Object>>();
        List<Map<String,Object>> liveFavoriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(uid, topicIdList);
        if(null != liveFavoriteList && liveFavoriteList.size() > 0){
            for(Map<String,Object> lf : liveFavoriteList){
                liveFavoriteMap.put(String.valueOf(lf.get("topic_id")), lf);
            }
        }
        UserProfile userProfile = null;
        Map<String, Object> topicUserProfile = null;
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
            squareDataElement.setLevel(userProfile.getLevel());
            if (!StringUtils.isEmpty(content.getConverImage())) {
                if (content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index) {
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
            squareDataElement.setFavoriteCount(content.getFavoriteCount()+1);
            // 如果是直播需要一个直播状态
            if (content.getType() == Specification.ArticleType.LIVE.index
                    || content.getType() == Specification.ArticleType.FORWARD_LIVE.index) {

                if(content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国转发UGC的，那么需要返回原作者UID和昵称
                    topicUserProfile = forwardTopicUserProfileMap.get(content.getForwardCid().toString());
                    if(null != topicUserProfile){
                        squareDataElement.setForwardUid((Long)topicUserProfile.get("uid"));
                        squareDataElement.setForwardNickName((String)topicUserProfile.get("nick_name"));
                    }
                }else{
                    if(null != topicMemberCountMap.get(content.getForwardCid().toString())){
                        squareDataElement.setFavoriteCount(topicMemberCountMap.get(content.getForwardCid().toString()).intValue()+1);
                    }else{
                        squareDataElement.setFavoriteCount(1);
                    }
                }

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
                    int internalStatust = this.getInternalStatus(topic, uid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavoriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    squareDataElement.setInternalStatus(internalStatust);
                    //是否聚合王国
                    squareDataElement.setContentType((Integer) topic.get("type"));
                    if((Integer)topic.get("type") == 1000){
                        //查询聚合子王国
                        int acCount = liveForContentJdbcDao.getTopicAggregationCountByTopicId((Long) topic.get("id"));
                        squareDataElement.setAcCount(acCount);
                    }
                    squareDataElement.setPrice((Integer) topic.get("price"));
                    List<Map<String,Object>> list = liveForContentJdbcDao.getTopicTagDetailListByTopicId((Long) topic.get("id"));
                    StringBuffer tagsSB = new StringBuffer();
                    for (int i = 0; i < list.size(); i++) {
                        Map<String,Object> map = list.get(i);
                        if(i!=0){
                            tagsSB.append(";");
                        }
                        tagsSB.append((String)map.get("tag"));
                    }
                    squareDataElement.setTags(tagsSB.toString());

                }
            }
            squareDataElement.setLikeCount(content.getLikeCount());
            squareDataElement.setPersonCount(content.getPersonCount());
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
        content.setUpdateId(cacheService.incr("UPDATE_ID"));
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
//        List<ArticleReview> articleReviews = contentMybatisDao.getArticleReviews(id ,Integer.MAX_VALUE);
        List<ArticleReview> articleReviews = new ArrayList<ArticleReview>();
        showArticleCommentsDto.setLikeCount(articleLikesDetails.size());
        showArticleCommentsDto.setReviewCount(articleReviews.size());
//        showArticleCommentsDto.setReviewCount(contentMybatisDao.countArticleReviews(id));
        showArticleCommentsDto.setIsLike(0);
        //获取用户信息取得是否大V
        UserProfile userProfile1 = userService.getUserProfileByUid(uid);
        showArticleCommentsDto.setV_lv(userProfile1.getvLv());

        List<Long> uidList = new ArrayList<Long>();
        for(ArticleReview ar : articleReviews){
            if(!uidList.contains(ar.getUid())){
                uidList.add(ar.getUid());
            }
            if(!uidList.contains(ar.getAtUid())){
                uidList.add(ar.getAtUid());
            }
        }
        for(ArticleLikesDetails ald : articleLikesDetails){
            if(!uidList.contains(ald.getUid())){
                uidList.add(ald.getUid());
            }
        }

        List<UserProfile> upList = userService.getUserProfilesByUids(uidList);
        Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
        if(null != upList && upList.size() > 0){
            for(UserProfile up : upList){
                userMap.put(String.valueOf(up.getUid()), up);
            }
        }

        UserProfile user = null;
        UserProfile atUser = null;
        for(ArticleReview articleReview : articleReviews) {
            ShowArticleCommentsDto.ReviewElement reviewElement = ShowArticleCommentsDto.createElement();
            reviewElement.setUid(articleReview.getUid());
            reviewElement.setCreateTime(articleReview.getCreateTime());
            reviewElement.setReview(articleReview.getReview());
            user = userMap.get(String.valueOf(articleReview.getUid()));
            reviewElement.setNickName(user.getNickName());
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
            reviewElement.setV_lv(user.getvLv());
            if(articleReview.getAtUid() > 0){
                atUser = userMap.get(String.valueOf(articleReview.getAtUid()));
                reviewElement.setAtUid(atUser.getUid());
                reviewElement.setAtNickName(atUser.getNickName());
            }
            reviewElement.setId(articleReview.getId());
            reviewElement.setExtra(articleReview.getExtra());
            showArticleCommentsDto.getReviews().add(reviewElement);
        }
        for(ArticleLikesDetails likesDetails : articleLikesDetails){
            ShowArticleCommentsDto.LikeElement likeElement = ShowArticleCommentsDto.createLikeElement();
            likeElement.setUid(likesDetails.getUid());
            user = userMap.get(String.valueOf(likesDetails.getUid()));
            likeElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
            likeElement.setNickName(user.getNickName());
            likeElement.setLevel(user.getLevel());
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
        try{
            ContentStatService contentStatService = contentStatusServiceProxyBean.getTarget();
            contentStatService.read(uid+"",id);
        }catch(Exception e){
            log.error("老徐文章阅读接口调用失败", e);
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
        if(type == Specification.UserNoticeType.LIVE_TAG.index){
            userNotice.setCid(content.getForwardCid());
        }else{
            userNotice.setCid(content.getId());
        }
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
        if(type == Specification.UserNoticeType.REVIEW.index){//评论
            userNotice.setReview(arg);
            userNotice.setTag("");
        }else if(type == Specification.UserNoticeType.TAG.index){//贴标
            userNotice.setReview("");
            userNotice.setTag(arg);
        }else if(type == Specification.UserNoticeType.LIKE.index){//点赞
            userNotice.setReview("");
            userNotice.setTag("");
        }
        userNotice.setReadStatus(0);

        boolean needNotice = false;

        UserNotice notice = userService.getUserNotice(userNotice);
        //非直播才提醒
        if(content.getType() != Specification.ArticleType.LIVE.index
                && content.getType() != Specification.ArticleType.FORWARD_LIVE.index) {
            //点赞时候只提醒一次
            if (userNotice.getNoticeType() == Specification.UserNoticeType.LIKE.index) {
                if (notice == null) {
                    needNotice = true;
                }
            } else {
                needNotice = true;
            }
        }

        if(needNotice){
            long unid = userService.createUserNoticeAndReturnId(userNotice);

            Date now = new Date();
            //V2.2.5版本开始使用新的红点体系
            UserNoticeUnread unu = new UserNoticeUnread();
            unu.setUid(customerProfile.getUid());
            unu.setCreateTime(now);
            unu.setNoticeId(unid);
            unu.setNoticeType(type);
            unu.setContentType(Specification.UserNoticeUnreadContentType.UGC.index);//这里只有UGC的才进行消息
            unu.setCid(content.getId());
            unu.setLevel(Specification.UserNoticeLevel.LEVEL_1.index);
            userService.createUserNoticeUnread(unu);
        }

        //一下是老的红点推送相关，这部分代码在过两到三个版本差不多可以干掉了，目前修改版本V2.2.5
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
                            userService.pushWithExtra(alias,jsonObject.toString(), null);
                        }
                        else
                        {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("count","1");
                            String alias = String.valueOf(content.getUid());
                            userService.pushWithExtra(alias,jsonObject.toString(), null);
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
                        userService.pushWithExtra(alias,jsonObject.toString(), null);
                    }
                    else
                    {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("count","1");
                        String alias = String.valueOf(content.getUid());
                        userService.pushWithExtra(alias,jsonObject.toString(), null);
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
                            userService.pushWithExtra(alias,jsonObject.toString(), null);
                        }
                        else
                        {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("count","1");
                            String alias = String.valueOf(content.getUid());
                            userService.pushWithExtra(alias,jsonObject.toString(), null);
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
                        userService.pushWithExtra(alias,jsonObject.toString(), null);
                    }
                    else
                    {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("count","1");
                        String alias = String.valueOf(content.getUid());
                        userService.pushWithExtra(alias,jsonObject.toString(), null);
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
        long unid = userService.createUserNoticeAndReturnId(userNotice);

        Date now = new Date();
        //V2.2.5版本开始使用新的红点体系
        UserNoticeUnread unu = new UserNoticeUnread();
        unu.setUid(atUid);
        unu.setCreateTime(now);
        unu.setNoticeId(unid);
        unu.setNoticeType(type);
        unu.setContentType(Specification.UserNoticeUnreadContentType.UGC.index);
        unu.setCid(content.getId());
        unu.setLevel(Specification.UserNoticeLevel.LEVEL_1.index);
        userService.createUserNoticeUnread(unu);


        //如果@人和被@人都不是ugc作者，则需要给ugc作者发送一个提醒消息
//        if(content.getUid()!=atUid&&content.getUid()!=uid){
//            UserProfile autherProfile = userService.getUserProfileByUid(content.getUid());
//            userNotice.setToUid(content.getUid());
//            userNotice.setToNickName(autherProfile.getNickName());
//            userNotice.setId(null);
//            long unid2 = userService.createUserNoticeAndReturnId(userNotice);
//
//            unu = new UserNoticeUnread();
//            unu.setUid(content.getUid());
//            unu.setCreateTime(now);
//            unu.setNoticeId(unid2);
//            unu.setNoticeType(type);
//            unu.setContentType(Specification.UserNoticeUnreadContentType.UGC.index);
//            unu.setCid(content.getId());
//            unu.setLevel(Specification.UserNoticeLevel.LEVEL_1.index);
//            userService.createUserNoticeUnread(unu);
//        }



        //一下是老的那套红点通知逻辑，继续保留，因为还是有一部分用户在用户老版本的，等后面几个版本后就可以删掉了，当前版本V2.2.5
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
            userService.pushWithExtra(alias,jsonObject.toString(), null);
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
    public Response myPublishByType(long uid, int sinceId, int type,long updateTime,long currentUid,int vFlag) {
        MyPublishDto dto = new MyPublishDto();
        dto.setType(type);
        dto.setSinceId(sinceId);
        dto.setUid(uid);
        dto.setUpdateTime(updateTime);
        dto.setFlag(vFlag);
        ShowMyPublishDto showMyPublishDto = new ShowMyPublishDto();
        List<Content> contents = null;
        if(type == 3){//我的王国（包括核心圈）
        	contents = contentMybatisDao.loadMyKingdom(dto);
        } else if(type == 4){
        	contents = contentMybatisDao.loadMyJoinKingdom(dto);
        } else{
        	contents = new ArrayList<Content>();
        }
        
        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> forwardTopicIdList = new ArrayList<Long>();
        for(Content idx : contents){
        	if(!uidList.contains(idx.getUid())){
                uidList.add(idx.getUid());
            }
            if(idx.getType() == Specification.ArticleType.LIVE.index
                    || idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国
                if(!topicIdList.contains(idx.getForwardCid())){
                    topicIdList.add(idx.getForwardCid());
                }
                if(idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    if(!forwardTopicIdList.contains(idx.getForwardCid())){
                        forwardTopicIdList.add(idx.getForwardCid());
                    }
                }
            }
        }

        //一次性获取王国的外露内容
        Map<String, List<Map<String, Object>>> topicOutDataMap = new HashMap<String, List<Map<String, Object>>>();
        String v = userService.getAppConfigByKey("KINGDOM_OUT_MINUTE");
        int limitMinute = 3;
        if(!StringUtils.isEmpty(v)){
        	limitMinute = Integer.valueOf(v).intValue();
        }
        List<Long> privateTopicIds = new ArrayList<Long>();
        String specialTopicIds = userService.getAppConfigByKey("SPECIAL_KINGDOM_IDS");
        if(!StringUtils.isEmpty(specialTopicIds)){
        	String[] tmp = specialTopicIds.split(",");
        	for(String a : tmp){
        		if(!StringUtils.isEmpty(a)){
        			privateTopicIds.add(Long.valueOf(a));
        		}
        	}
        }
        List<Map<String,Object>> topicOutList = liveForContentJdbcDao.getOutFragments(topicIdList, limitMinute, privateTopicIds);
        if(null != topicOutList && topicOutList.size() > 0){
        	Long topicId = null;
        	List<Map<String, Object>> toList = null;
        	Long atUid = null;
        	Long fragmentUid = null;
        	for(Map<String,Object> m : topicOutList){
        		topicId = (Long)m.get("topic_id");
        		toList = topicOutDataMap.get(topicId.toString());
        		if(null == toList){
        			toList = new ArrayList<Map<String, Object>>();
        			topicOutDataMap.put(topicId.toString(), toList);
        		}
        		toList.add(m);
        		atUid = (Long)m.get("at_uid");
        		if(null != atUid && atUid.longValue() > 0){
        			if(!uidList.contains(atUid)){
                        uidList.add(atUid);
                    }
        		}
        		fragmentUid = (Long)m.get("uid");
        		if(null != fragmentUid && fragmentUid.longValue() > 0){
        			if(!uidList.contains(fragmentUid)){
                        uidList.add(fragmentUid);
                    }
        		}
        	}
        }
        
        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
        
        Map<String, Map<String, Object>> forwardTopicUserProfileMap = new HashMap<String, Map<String, Object>>();
        if(forwardTopicIdList.size() > 0){
            List<Map<String,Object>> topicUserProfileList = liveForContentJdbcDao.getTopicUserProfileByTopicIds(forwardTopicIdList);
            if(null != topicUserProfileList && topicUserProfileList.size() > 0){
                for(Map<String,Object> topicUserProfile : topicUserProfileList){
                    forwardTopicUserProfileMap.put(String.valueOf(topicUserProfile.get("id")), topicUserProfile);
                }
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
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }
        
        //一次性查询王国订阅信息
        Map<String, String> liveFavouriteMap = new HashMap<String, String>();
        List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
        if(null != liveFavouriteList && liveFavouriteList.size() > 0){
            for(Map<String,Object> lf : liveFavouriteList){
                liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
            }
        }
        
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }

        UserProfile userProfile = null;
        Map<String, Object> topicUserProfile = null;
        List<Map<String, Object>> topicOutDataList = null;
        Map<String, Object> topicOutData = null;
        ShowMyPublishDto.OutDataElement outElement = null;
        UserProfile atUserProfile = null;
        UserProfile lastUserProfile = null;
        for (Content content : contents){
            ShowMyPublishDto.MyPublishElement contentElement = ShowMyPublishDto.createElement();
            userProfile = profileMap.get(String.valueOf(content.getUid()));
            contentElement.setV_lv(userProfile.getvLv());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setLevel(userProfile.getLevel());
            contentElement.setUid(content.getUid());
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
            contentElement.setFavoriteCount(content.getFavoriteCount()+1);
            contentElement.setContentType(content.getContentType());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setType(content.getType());
            contentElement.setReadCount(content.getReadCountDummy());

            //判断人员是否关注
            if(null != followMap.get(uid+"_"+content.getUid())){
                contentElement.setIsFollowed(1);
            }else{
                contentElement.setIsFollowed(0);
            }
            if(null != followMap.get(content.getUid()+"_"+uid)){
                contentElement.setIsFollowMe(1);
            }else{
                contentElement.setIsFollowMe(0);
            }
            
            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setForwardTitle(content.getForwardTitle());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)){
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            contentElement.setFavoriteCount(content.getFavoriteCount()+1);
            //查询直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index
                    || content.getType() == Specification.ArticleType.FORWARD_LIVE.index) {

                if(content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国转发UGC的，那么需要返回原作者UID和昵称
                    topicUserProfile = forwardTopicUserProfileMap.get(content.getForwardCid().toString());
                    if(null != topicUserProfile){
                        contentElement.setForwardUid((Long)topicUserProfile.get("uid"));
                        contentElement.setForwardNickName((String)topicUserProfile.get("nick_name"));
                        contentElement.setLevel((Integer)topicUserProfile.get("level"));
                    }
                }else{
                    if(null != topicMemberCountMap.get(content.getForwardCid().toString())){
                        contentElement.setFavoriteCount(topicMemberCountMap.get(content.getForwardCid().toString()).intValue()+1);
                    }else{
                        contentElement.setFavoriteCount(1);
                    }
                }

                contentElement.setLiveStatus(0);
                if(null != topicCountMap.get(content.getForwardCid().toString())){
                	contentElement.setTopicCount(topicCountMap.get(content.getForwardCid().toString()).intValue());
                }
                if(null != reviewCountMap.get(content.getForwardCid().toString())){
                	contentElement.setReviewCount(reviewCountMap.get(content.getForwardCid().toString()).intValue());
                }
                //王国增加身份信息
                Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
                if(null != topic){
                	contentElement.setLastUpdateTime((Long)topic.get("long_time"));
                    int internalStatust = this.getInternalStatus(topic, currentUid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if(null != liveFavouriteMap.get(content.getForwardCid().toString())){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    contentElement.setInternalStatus(internalStatust);
                    contentElement.setContentType((Integer)topic.get("type"));
                    if(contentElement.getContentType() == 1000){//聚合王国要有子王国数
                        int acCount = liveForContentJdbcDao.getTopicAggregationCountByTopicId((Long) topic.get("id"));
                        contentElement.setAcCount(acCount);
                    }
                    contentElement.setPrice((Integer)topic.get("price"));
                }
                if(null != liveFavouriteMap.get(content.getForwardCid().toString())){
                	contentElement.setFavorite(1);
                }
            }else{
                ContentImage contentImage = contentMybatisDao.getCoverImages(content.getId());
                if(contentImage != null) {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + contentImage.getImage());
                }else{
                    contentElement.setCoverImage("");
                }
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());

            //增加王国外露内容
            if(content.getType().intValue() == Specification.ArticleType.LIVE.index){//王国才有外露
            	topicOutDataList = topicOutDataMap.get(content.getForwardCid().toString());
            	if(null != topicOutDataList && topicOutDataList.size() > 0){
            		//先判断是否UGC
            		//第一个如果是UGC则其他的不要了，如果不是，则后面如果有UGC则不要了
            		topicOutData = topicOutDataList.get(0);
            		lastUserProfile = profileMap.get(String.valueOf(topicOutData.get("uid")));
            		if(null != lastUserProfile){//这里放上最近发言的那个人的头像
            			contentElement.setUid(lastUserProfile.getUid());
            			contentElement.setNickName(lastUserProfile.getNickName());
            			contentElement.setV_lv(lastUserProfile.getvLv());
            			contentElement.setLevel(lastUserProfile.getLevel());
            			contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + lastUserProfile.getAvatar());
            			if(null != followMap.get(String.valueOf(topicOutData.get("uid"))+"_"+content.getUid())){
                            contentElement.setIsFollowed(1);
                        }else{
                            contentElement.setIsFollowed(0);
                        }
                        if(null != followMap.get(content.getUid()+"_"+String.valueOf(topicOutData.get("uid")))){
                            contentElement.setIsFollowMe(1);
                        }else{
                            contentElement.setIsFollowMe(0);
                        }
            		}
            		int t = ((Integer)topicOutData.get("type")).intValue();
            		int contentType = ((Integer)topicOutData.get("content_type")).intValue();
            		if((t == 0 || t == 52) && contentType == 23){//第一个是UGC
            			outElement = new ShowMyPublishDto.OutDataElement();
            			outElement.setId((Long)topicOutData.get("id"));
            			outElement.setType((Integer)topicOutData.get("type"));
            			outElement.setContentType((Integer)topicOutData.get("content_type"));
            			outElement.setFragment((String)topicOutData.get("fragment"));
            			String fragmentImage = (String)topicOutData.get("fragment_image");
                        if (!StringUtils.isEmpty(fragmentImage)) {
                        	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                        }
            			outElement.setAtUid((Long)topicOutData.get("at_uid"));
            			if(outElement.getAtUid() > 0){
            				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
            				if(null != atUserProfile){
            					outElement.setAtNickName(atUserProfile.getNickName());
            				}
            			}
            			outElement.setExtra((String)topicOutData.get("extra"));
            			contentElement.getUgcData().add(outElement);
            		}else{//第一个不是UGC
            			for(int i=0;i<topicOutDataList.size();i++){
            				topicOutData = topicOutDataList.get(i);
            				t = ((Integer)topicOutData.get("type")).intValue();
            				contentType = ((Integer)topicOutData.get("content_type")).intValue();
            				if((t == 0 || t == 52) && contentType == 23){//UGC不要了
            					continue;
            				}else if((t == 0 || t == 55) && contentType == 0){//文本
            					if(contentElement.getTextData().size() == 0){
            						outElement = new ShowMyPublishDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getTextData().add(outElement);
            					}
            				}else if(t==13 || (t == 55 && contentType == 63)){//音频
            					if(contentElement.getAudioData().size() == 0){
            						outElement = new ShowMyPublishDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getAudioData().add(outElement);
            					}
            				}else{//图片区展示部分
            					if(contentElement.getAudioData().size() < 4){
            						outElement = new ShowMyPublishDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getImageData().add(outElement);
            					}
            				}
            			}
            		}
            	}
            }
            showMyPublishDto.getMyPublishElements().add(contentElement);
        }
        return Response.success(showMyPublishDto);
    }

    @Override
    public Response deleteContent(long id, long uid, boolean isSys) {
        log.info("deleteContent start ...");
        Content content = contentMybatisDao.getContentById(id);
        //直播删除
        if(content.getType() == Specification.ArticleType.LIVE.index) {
            if(!isSys&&uid!=content.getUid()&&!userService.isAdmin(uid)){ //只有王国自己，或者管理员能删除王国
                return Response.failure(ResponseStatus.CONTENT_DELETE_NO_AUTH.status,ResponseStatus.CONTENT_DELETE_NO_AUTH.message);
            }
            contentMybatisDao.deleteTopicById(content.getForwardCid());
            log.info("topic delete");
            //记录下删除记录
            liveForContentJdbcDao.insertDeleteLog(Specification.DeleteObjectType.TOPIC.index, content.getForwardCid(), uid);

            //删除活动相关王国记录
            activityService.deleteAkingDomByTopicId(content.getForwardCid());
            //删除聚合相关关系记录
            liveForContentJdbcDao.deleteAggregationTopic(content.getForwardCid());
            //删除banner上的王国
            liveForContentJdbcDao.deleteBannerTopic(content.getForwardCid());
            //删除这个王国上的所有标签记录
            liveForContentJdbcDao.deleteTopicTagByTopicId(content.getForwardCid());
            //删除和这个王国有关的所有榜单记录
            liveForContentJdbcDao.deleteTopicBillboard(content.getForwardCid());
        }else{
            //记录下删除记录
            liveForContentJdbcDao.insertDeleteLog(Specification.DeleteObjectType.UGC.index, id, uid);
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
        }else if(content.getRights().intValue() == Specification.ContentRights.SELF.index && content.getUid().longValue() != uid){
            return Response.failure(ResponseStatus.UGC_NO_RIGHTS.status,ResponseStatus.UGC_NO_RIGHTS.message);
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
        contentDetailDto.setLevel(userProfile.getLevel());
        contentDetailDto.setHotValue(content.getHotValue());
        contentDetailDto.setLikeCount(content.getLikeCount());
//        contentDetailDto.setReviewCount(content.getReviewCount());
        contentDetailDto.setReviewCount(contentMybatisDao.countContentReviewByCid(content.getId()));

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
//        List<ContentReview> reviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
        List<ContentReview> reviewList = new ArrayList<ContentReview>();
        log.info("get content review success");

        //点赞top30
        List<ContentLikesDetails> contentLikesDetailsList = contentMybatisDao.getContentLikesDetails(id);

        List<Long> uidList = new ArrayList<Long>();
        for(ContentReview cr : reviewList){
            if(!uidList.contains(cr.getUid())){
                uidList.add(cr.getUid());
            }
            if(!uidList.contains(cr.getAtUid())){
                uidList.add(cr.getAtUid());
            }
        }
        for(ContentLikesDetails cld : contentLikesDetailsList){
            if(!uidList.contains(cld.getUid())){
                uidList.add(cld.getUid());
            }
        }

        List<UserProfile> upList = userService.getUserProfilesByUids(uidList);
        Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
        if(null != upList && upList.size() > 0){
            for(UserProfile up : upList){
                userMap.put(String.valueOf(up.getUid()), up);
            }
        }

        UserProfile user = null;
        UserProfile atUser = null;
        for(ContentReview review :reviewList){
            ContentDetailDto.ReviewElement reviewElement = ContentDetailDto.createReviewElement();
            reviewElement.setUid(review.getUid());
            reviewElement.setCreateTime(review.getCreateTime());
            reviewElement.setReview(review.getReview());
            user = userMap.get(String.valueOf(review.getUid()));
            reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
            reviewElement.setNickName(user.getNickName());
            reviewElement.setV_lv(user.getvLv());

            if(review.getAtUid() > 0){
                atUser = userMap.get(String.valueOf(review.getAtUid()));
                reviewElement.setAtUid(atUser.getUid());
                reviewElement.setAtNickName(atUser.getNickName());
            }
            reviewElement.setId(review.getId());
            reviewElement.setExtra(review.getExtra());
            contentDetailDto.getReviews().add(reviewElement);
        }

        //点赞top30
        for(ContentLikesDetails contentLikesDetails : contentLikesDetailsList){
            ContentDetailDto.LikeElement likeElement = ContentDetailDto.createLikeElement();
            likeElement.setUid(contentLikesDetails.getUid());
            user = userMap.get(String.valueOf(contentLikesDetails.getUid()));
            likeElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
            likeElement.setNickName(user.getNickName());
            likeElement.setLevel(user.getLevel());
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

        //将当前用户针对于本UGC的相关消息置为已读
        userService.clearUserNoticeUnreadByCid(uid, Specification.UserNoticeUnreadContentType.UGC.index, id);

        return Response.success(contentDetailDto);
    }

    @Override
    public Response myPublish(long uid ,long updateTime ,int type ,int sinceId ,int newType, int vFlag) {
        log.info("myPublish start ...");
        SquareDataDto squareDataDto = new SquareDataDto();
        if(newType == 1) {//新版本2.1.1
            //获取所有播内容
//        List<Content> contents = contentMybatisDao.myPublish(uid,sinceId);
            if (type == 1) {
                log.info("myPublish ugc getData ...");
                //获取非直播内容
//                List<Content> contents = contentMybatisDao.myPublishUgc(uid, sinceId, vFlag);
//                buildDatas(squareDataDto, contents, uid);
            } else if (type == 2) {
                log.info("my publish live getData ...");
                //获取直播内容
                MyPublishDto dto = new MyPublishDto();
                dto.setUid(uid);
                dto.setUpdateTime(updateTime);
                List<Content> contents = contentMybatisDao.loadMyKingdom(dto);
//                List<Content> contents = contentMybatisDao.myPublishLive(uid, updateTime);
                buildDatas(squareDataDto, contents, uid);
            } else if (type == 0) {
                log.info("my publish ugc and live getData ...");
                //兼容2.1.0之前版本 获取UGC和直播
//                List<Content> contents = contentMybatisDao.myPublish(uid, sinceId);
//                buildDatas(squareDataDto, contents, uid);
            }
            log.info("myPublish end ...");
        }else if(newType == 0){//老版本
            if (type == 1) {
                log.info("myPublish ugc getData ...");
                //获取非直播内容
//                List<Content> contents = contentMybatisDao.myPublishUgc(uid, sinceId, vFlag);
//                buildDatas(squareDataDto, contents, uid);
            } else if (type == 2) {
                log.info("my publish live getData ...");
                //获取直播内容
                List<Content> contents = contentMybatisDao.myPublishLive2(uid, sinceId);
                buildDatas(squareDataDto, contents, uid);
            } else if (type == 0) {
                log.info("my publish ugc and live getData ...");
                //兼容2.1.0之前版本 获取UGC和直播
//                List<Content> contents = contentMybatisDao.myPublish(uid, sinceId);
//                buildDatas(squareDataDto, contents, uid);
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
            contentElement.setFavoriteCount(content.getFavoriteCount()+1);
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
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
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
    public Response UserData2(long targetUid,long sourceUid,int vFlag){
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
        dto.setFlag(vFlag);
        //非直播文章
//        List<Content> contents = contentMybatisDao.myPublishByType(dto);
//        userInfoDto.setContentCount(contentMybatisDao.countMyPublishByType(dto));
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
        userInfoDto.getUser().setLevel(userProfile.getLevel());
        if(userService.isUserFamous(targetUid)){
            userInfoDto.getUser().setIsRec(1);
        }else{
            userInfoDto.getUser().setIsRec(0);
        }
        if(userService.isBlacklist(sourceUid, targetUid)){
        	userInfoDto.getUser().setIsBlacklist(1);
        }else{
        	userInfoDto.getUser().setIsBlacklist(0);
        }
//        buildUserData(sourceUid, contents,Specification.ArticleType.ORIGIN.index,userInfoDto);
        //直播
        dto.setType(Specification.ArticleType.LIVE.index);
        Calendar calendar = Calendar.getInstance();
        dto.setUpdateTime(calendar.getTimeInMillis());
        
        if(vFlag<2){
	        List<Content> lives = contentMybatisDao.loadMyKingdom(dto);
	        buildUserData(sourceUid, lives,Specification.ArticleType.LIVE.index,userInfoDto);
        }
        
        userInfoDto.setLiveCount(contentMybatisDao.countMyKingdom(dto));
        //我加入的
        userInfoDto.setJoinLiveCount(contentMybatisDao.countMyJoinKingdom(dto));
        log.info("getUserData end ...");
        return Response.success(userInfoDto);
    }

    private void buildUserData(long sourceUid, List<Content> contents,int type,UserInfoDto2 userInfoDto) {
    	List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> forwardTopicIdList = new ArrayList<Long>();
        for(Content idx : contents){
        	if(!uidList.contains(idx.getUid())){
        		uidList.add(idx.getUid());
        	}
            if(idx.getType() == Specification.ArticleType.LIVE.index){//王国
                topicIdList.add(idx.getForwardCid());
            }
            if(idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                if(!forwardTopicIdList.contains(idx.getForwardCid())){
                    forwardTopicIdList.add(idx.getForwardCid());
                }
            }
        }

        Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
        List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
        if(null != userList && userList.size() > 0){
        	for(UserProfile u : userList){
        		userMap.put(u.getUid().toString(), u);
        	}
        }
        
        Map<String, Map<String, Object>> forwardTopicUserProfileMap = new HashMap<String, Map<String, Object>>();
        if(forwardTopicIdList.size() > 0){
            List<Map<String,Object>> topicUserProfileList = liveForContentJdbcDao.getTopicUserProfileByTopicIds(forwardTopicIdList);
            if(null != topicUserProfileList && topicUserProfileList.size() > 0){
                for(Map<String,Object> topicUserProfile : topicUserProfileList){
                    forwardTopicUserProfileMap.put(String.valueOf(topicUserProfile.get("id")), topicUserProfile);
                }
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
        //一次性查询王国订阅信息
        Map<String, String> liveFavouriteMap = new HashMap<String, String>();
        List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(sourceUid, topicIdList);
        if(null != liveFavouriteList && liveFavouriteList.size() > 0){
            for(Map<String,Object> lf : liveFavouriteList){
                liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
            }
        }
        
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }
        
        UserProfile profile = null;
        Map<String, Object> topicUserProfile = null;
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
            contentElement.setFavoriteCount(content.getFavoriteCount()+1);
            contentElement.setContentType(content.getContentType());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setType(content.getType());
            contentElement.setReadCount(content.getReadCountDummy());
            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setForwardTitle(content.getForwardTitle());
            contentElement.setUid(content.getUid());
            profile = userMap.get(content.getUid().toString());
            contentElement.setV_lv(profile.getvLv());
            contentElement.setLevel(profile.getLevel());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)){
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            //查询直播状态
            if(content.getType() == Specification.ArticleType.LIVE.index
                    || content.getType() == Specification.ArticleType.FORWARD_LIVE.index) {

                if(content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国转发UGC的，那么需要返回原作者UID和昵称
                    topicUserProfile = forwardTopicUserProfileMap.get(content.getForwardCid().toString());
                    if(null != topicUserProfile){
                        contentElement.setForwardUid((Long)topicUserProfile.get("uid"));
                        contentElement.setForwardNickName((String)topicUserProfile.get("nick_name"));
                    }
                }

                contentElement.setLiveStatus(0);
                if(null != topicCountMap.get(content.getForwardCid().toString())){
                	contentElement.setTopicCount(topicCountMap.get(content.getForwardCid().toString()).intValue());
                }
                if(null != reviewCountMap.get(content.getForwardCid().toString())){
                	contentElement.setReviewCount(reviewCountMap.get(content.getForwardCid().toString()).intValue());
                }
                //王国增加身份信息
                Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
                if(null != topic){
                	contentElement.setLastUpdateTime((Long)topic.get("long_time"));
                    int internalStatust = this.getInternalStatus(topic, sourceUid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if(null != liveFavouriteMap.get(content.getForwardCid().toString())){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    contentElement.setInternalStatus(internalStatust);
                    contentElement.setContentType((Integer) topic.get("type"));
                    if((Integer)topic.get("type") == 1000){
                        //查询聚合子王国
                        int acCount = liveForContentJdbcDao.getTopicAggregationCountByTopicId((Long) topic.get("id"));
                        contentElement.setAcCount(acCount);
                    }
                }
                
                if(null != liveFavouriteMap.get(content.getForwardCid().toString())){
                	contentElement.setFavorite(1);
                }
            }
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
//            contentElement.setIsLike(isLike(content.getId(),sourceUid));
//            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
            log.info("get content review success");
//            for(ContentReview contentReview : contentReviewList){
//                UserInfoDto2.ContentElement.ReviewElement reviewElement = UserInfoDto2.ContentElement.createElement();
//                reviewElement.setUid(contentReview.getUid());
//                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
//                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
//                reviewElement.setNickName(user.getNickName());
//                reviewElement.setCreateTime(contentReview.getCreateTime());
//                reviewElement.setReview(contentReview.getReview());
//                contentElement.getReviews().add(reviewElement);
//            }
            if(type == Specification.ArticleType.LIVE.index){//我自己的王国
                userInfoDto.getLiveElementList().add(contentElement);
            } else{
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
        content.setUpdateId(cacheService.incr("UPDATE_ID"));
        contentMybatisDao.createContent(content);
        log.info("content create success");
        //保存标签
        createTag(contentDto, content);
        log.info("contentTag create success");
        log.info("editorPublish end ...");
        return Response.success(ResponseStatus.PUBLISH_ARTICLE_SUCCESS.status,ResponseStatus.PUBLISH_ARTICLE_SUCCESS.message);
    }

    public void createTag(ContentDto contentDto, Content content) {
        if(StringUtils.isEmpty(contentDto.getFeeling())){
            return;
        }
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
                activityElement.setLevel(userProfile.getLevel());
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
            List<Content> contentTopList = contentMybatisDao.getHottestTopsContent(0);
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
    public Response Hottest2(int sinceId,long uid, int flag){
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
                //这个接口肯定是低于V2.2.1版本的APP调用的，所以这里干脆将大于1的都置为0
                if(activity.getTyp() < 2){
                    activityElement.setContentType(activity.getTyp());
                }else{
                    activityElement.setContentType(0);
                }
                activityElement.setContentUrl(activity.getLinkUrl());
                activityElement.setType(4);
                hottestDto.getActivityData().add(activityElement);
            }
        }
        // 置顶内容
        if(sinceId==Integer.MAX_VALUE) {
            List<Content> contentTopList = contentMybatisDao.getHottestTopsContent(flag);
            builderContent(uid, contentTopList, hottestDto.getTops());
        }
        //内容
        List<Content2Dto> contentList = contentMybatisDao.getHottestContentByUpdateTime(sinceId, flag);
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
        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> forwardTopicIdList = new ArrayList<Long>();
        for(Content idx : contentList){
            if(!uidList.contains(idx.getUid())){
                uidList.add(idx.getUid());
            }
            if(idx.getType() == Specification.ArticleType.LIVE.index
                    || idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国/转发王国
                if(!topicIdList.contains(idx.getForwardCid())){
                    topicIdList.add(idx.getForwardCid());
                }
                if(idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    if(!forwardTopicIdList.contains(idx.getForwardCid())){
                        forwardTopicIdList.add(idx.getForwardCid());
                    }
                }
            }
        }

        Map<String, Map<String, Object>> forwardTopicUserProfileMap = new HashMap<String, Map<String, Object>>();
        if(forwardTopicIdList.size() > 0){
            List<Map<String,Object>> topicUserProfileList = liveForContentJdbcDao.getTopicUserProfileByTopicIds(forwardTopicIdList);
            if(null != topicUserProfileList && topicUserProfileList.size() > 0){
                for(Map<String,Object> topicUserProfile : topicUserProfileList){
                    forwardTopicUserProfileMap.put(String.valueOf(topicUserProfile.get("id")), topicUserProfile);
                }
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

		// 一次性获取当前用户针对于各王国是否收藏过
		Map<String, Map<String, Object>> liveFavoriteMap = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> liveFavoriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(uid,
				topicIdList);
		if (null != liveFavoriteList && liveFavoriteList.size() > 0) {
			for (Map<String, Object> lf : liveFavoriteList) {
				liveFavoriteMap.put(String.valueOf(lf.get("topic_id")), lf);
			}
		}

        UserProfile userProfile = null;
        Map<String, Object> topicUserProfile = null;
        for(Content content : contentList){
            ShowHottestDto.HottestContentElement hottestContentElement = ShowHottestDto.createHottestContentElement();
            hottestContentElement.setType(content.getType());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)) {
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
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
            hottestContentElement.setReadCount(content.getReadCountDummy());

            if(content.getType() == Specification.ArticleType.SYSTEM.index){//系统文章不包含，用户信息

            }else if(content.getType() == Specification.ArticleType.LIVE.index
                    || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//直播 直播状态d

                if(content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国转发UGC的，那么需要返回原作者UID和昵称
                    topicUserProfile = forwardTopicUserProfileMap.get(content.getForwardCid().toString());
                    if(null != topicUserProfile){
                        hottestContentElement.setForwardUid((Long)topicUserProfile.get("uid"));
                        hottestContentElement.setForwardNickName((String)topicUserProfile.get("nick_name"));
                    }
                }

                Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
                if(null != topic){
                    hottestContentElement.setContentType((Integer) topic.get("type"));
                    int internalStatust = this.getInternalStatus(topic, uid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavoriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    hottestContentElement.setInternalStatus(internalStatust);
                    if((Integer)topic.get("type") == 1000){
                        //查询聚合子王国
                        int acCount = liveForContentJdbcDao.getTopicAggregationCountByTopicId((Long) topic.get("id"));
                        hottestContentElement.setAcCount(acCount);
                    }
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
                hottestContentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                hottestContentElement.setNickName(userProfile.getNickName());
                hottestContentElement.setTag(content.getFeeling());
                int follow = userService.isFollow(content.getUid(),uid);
                hottestContentElement.setIsFollowed(follow);

                hottestContentElement.setPersonCount(content.getPersonCount());
                hottestContentElement.setFavoriteCount(content.getFavoriteCount()+1);
                hottestContentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                hottestContentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
            }else if(content.getType() == Specification.ArticleType.ORIGIN.index){//原生
                hottestContentElement.setUid(content.getUid());
                userProfile = profileMap.get(String.valueOf(content.getUid()));
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
        if(null == contentList || contentList.size() == 0){
            return;
        }
        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> forwardTopicIdList = new ArrayList<Long>();
        for(Content2Dto idx : contentList){
            if(!uidList.contains(idx.getUid())){
                uidList.add(idx.getUid());
            }
            if(idx.getType() == Specification.ArticleType.LIVE.index
                    || idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国/转发王国
                if(!topicIdList.contains(idx.getForwardCid())){
                    topicIdList.add(idx.getForwardCid());
                }
                if(idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    if(!forwardTopicIdList.contains(idx.getForwardCid())){
                        forwardTopicIdList.add(idx.getForwardCid());
                    }
                }
            }
        }

        Map<String, Map<String, Object>> forwardTopicUserProfileMap = new HashMap<String, Map<String, Object>>();
        if(forwardTopicIdList.size() > 0){
            List<Map<String,Object>> topicUserProfileList = liveForContentJdbcDao.getTopicUserProfileByTopicIds(forwardTopicIdList);
            if(null != topicUserProfileList && topicUserProfileList.size() > 0){
                for(Map<String,Object> topicUserProfile : topicUserProfileList){
                    forwardTopicUserProfileMap.put(String.valueOf(topicUserProfile.get("id")), topicUserProfile);
                }
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
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
		// 一次性获取当前用户针对于各王国是否收藏过
		Map<String, Map<String, Object>> liveFavoriteMap = new HashMap<String, Map<String, Object>>();
		List<Map<String, Object>> liveFavoriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(uid,
				topicIdList);
		if (null != liveFavoriteList && liveFavoriteList.size() > 0) {
			for (Map<String, Object> lf : liveFavoriteList) {
				liveFavoriteMap.put(String.valueOf(lf.get("topic_id")), lf);
			}
		}
        UserProfile userProfile = null;
        Map<String, Object> topicUserProfile = null;
        for(Content2Dto content : contentList){
            ShowHottestDto.HottestContentElement hottestContentElement = ShowHottestDto.createHottestContentElement();
            hottestContentElement.setType(content.getType());
            String cover = content.getConverImage();
            if(!StringUtils.isEmpty(cover)) {
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
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
            hottestContentElement.setReadCount(content.getReadCountDummy());
            hottestContentElement.setRights(content.getRights());

            if(content.getType() == Specification.ArticleType.SYSTEM.index){//系统文章不包含，用户信息

            }else if(content.getType() == Specification.ArticleType.LIVE.index
                    || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//直播 直播状态

                if(content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国转发UGC的，那么需要返回原作者UID和昵称
                    topicUserProfile = forwardTopicUserProfileMap.get(content.getForwardCid().toString());
                    if(null != topicUserProfile){
                        hottestContentElement.setForwardUid((Long)topicUserProfile.get("uid"));
                        hottestContentElement.setForwardNickName((String)topicUserProfile.get("nick_name"));
                    }
                }

                //王国增加身份信息
                Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
                if(null != topic){
                    hottestContentElement.setContentType((Integer) topic.get("type"));
                    int internalStatust = this.getInternalStatus(topic, uid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavoriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    hottestContentElement.setInternalStatus(internalStatust);
                    if((Integer)topic.get("type") == 1000){
                        //查询聚合子王国
                        int acCount = liveForContentJdbcDao.getTopicAggregationCountByTopicId((Long) topic.get("id"));
                        hottestContentElement.setAcCount(acCount);
                    }
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
                if(null != followMap.get(uid+"_"+content.getUid())){
                    hottestContentElement.setIsFollowed(1);
                }else{
                    hottestContentElement.setIsFollowed(0);
                }
                if(null != followMap.get(content.getUid()+"_"+uid)){
                    hottestContentElement.setIsFollowMe(1);
                }else{
                    hottestContentElement.setIsFollowMe(0);
                }

                hottestContentElement.setPersonCount(content.getPersonCount());
                hottestContentElement.setFavoriteCount(content.getFavoriteCount()+1);
                hottestContentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                hottestContentElement.setTopicCount(contentMybatisDao.getTopicCount(content.getForwardCid()) - reviewCount);
            } else if(content.getType() == Specification.ArticleType.ORIGIN.index){//原生
                hottestContentElement.setUid(content.getUid());
                userProfile = profileMap.get(String.valueOf(content.getUid()));
                hottestContentElement.setV_lv(userProfile.getvLv());
                hottestContentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                hottestContentElement.setNickName(userProfile.getNickName());
                hottestContentElement.setTag(content.getFeeling());
                if(null != followMap.get(uid+"_"+content.getUid())){
                    hottestContentElement.setIsFollowed(1);
                }else{
                    hottestContentElement.setIsFollowed(0);
                }
                if(null != followMap.get(content.getUid()+"_"+uid)){
                    hottestContentElement.setIsFollowMe(1);
                }else{
                    hottestContentElement.setIsFollowMe(0);
                }
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
    public Response Newest(long sinceId, long uid, int vFlag) {
        log.info("getNewest start ...");
        ShowNewestDto showNewestDto = new ShowNewestDto();
        
        List<Long> blacklistUids = liveForContentJdbcDao.getBlacklist(uid);
        
        List<Content> newestList = null;
        if(vFlag>0){//3.0.0版本以上
        	newestList = contentMybatisDao.getNewest(uid,sinceId,blacklistUids);
        }else{
        	newestList = contentMybatisDao.getNewest4Old(uid,sinceId,blacklistUids);
        }
        
        log.info("getNewest data success ");

        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> forwardTopicIdList = new ArrayList<Long>();
        for(Content idx : newestList){
            if(!uidList.contains(idx.getUid())){
                uidList.add(idx.getUid());
            }
            if(idx.getType() == Specification.ArticleType.LIVE.index
                    || idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国
                if(!topicIdList.contains(idx.getForwardCid())){
                    topicIdList.add(idx.getForwardCid());
                }
                if(idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    if(!forwardTopicIdList.contains(idx.getForwardCid())){
                        forwardTopicIdList.add(idx.getForwardCid());
                    }
                }
            }
        }
        
        //一次性获取王国的外露内容
        Map<String, List<Map<String, Object>>> topicOutDataMap = new HashMap<String, List<Map<String, Object>>>();
        String v = userService.getAppConfigByKey("KINGDOM_OUT_MINUTE");
        int limitMinute = 3;
        if(!StringUtils.isEmpty(v)){
        	limitMinute = Integer.valueOf(v).intValue();
        }
        List<Long> privateTopicIds = new ArrayList<Long>();
        String specialTopicIds = userService.getAppConfigByKey("SPECIAL_KINGDOM_IDS");
        if(!StringUtils.isEmpty(specialTopicIds)){
        	String[] tmp = specialTopicIds.split(",");
        	for(String a : tmp){
        		if(!StringUtils.isEmpty(a)){
        			privateTopicIds.add(Long.valueOf(a));
        		}
        	}
        }
        List<Map<String,Object>> topicOutList = liveForContentJdbcDao.getOutFragments(topicIdList, limitMinute, privateTopicIds);
        if(null != topicOutList && topicOutList.size() > 0){
        	Long topicId = null;
        	Long atUid = null;
        	Long fragmentUid = null;
        	List<Map<String, Object>> toList = null;
        	for(Map<String,Object> m : topicOutList){
        		topicId = (Long)m.get("topic_id");
        		toList = topicOutDataMap.get(topicId.toString());
        		if(null == toList){
        			toList = new ArrayList<Map<String, Object>>();
        			topicOutDataMap.put(topicId.toString(), toList);
        		}
        		toList.add(m);
        		atUid = (Long)m.get("at_uid");
        		if(null != atUid && atUid.longValue() > 0){
        			if(!uidList.contains(atUid)){
                        uidList.add(atUid);
                    }
        		}
        		fragmentUid = (Long)m.get("uid");
        		if(null != fragmentUid && fragmentUid.longValue() > 0){
        			if(!uidList.contains(fragmentUid)){
                        uidList.add(fragmentUid);
                    }
        		}
        	}
        }

        Map<String, Map<String, Object>> forwardTopicUserProfileMap = new HashMap<String, Map<String, Object>>();
        if(forwardTopicIdList.size() > 0){
            List<Map<String,Object>> topicUserProfileList = liveForContentJdbcDao.getTopicUserProfileByTopicIds(forwardTopicIdList);
            if(null != topicUserProfileList && topicUserProfileList.size() > 0){
                for(Map<String,Object> topicUserProfile : topicUserProfileList){
                    forwardTopicUserProfileMap.put(String.valueOf(topicUserProfile.get("id")), topicUserProfile);
                }
            }
        }

        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }

        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
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
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询王国订阅信息
        Map<String, String> liveFavouriteMap = new HashMap<String, String>();
        List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(uid, topicIdList);
        if(null != liveFavouriteList && liveFavouriteList.size() > 0){
            for(Map<String,Object> lf : liveFavouriteList){
                liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }

        UserProfile userProfile = null;
        Map<String, Object> topicUserProfile = null;
        List<Map<String, Object>> topicOutDataList = null;
        Map<String, Object> topicOutData = null;
        ShowNewestDto.OutDataElement outElement = null;
        UserProfile atUserProfile = null;
        UserProfile lastUserProfile = null;
        for(Content content : newestList){
            ShowNewestDto.ContentElement contentElement = ShowNewestDto.createElement();

            contentElement.setId(content.getId());
            contentElement.setUid(content.getUid());
            // 获取用户信息
            userProfile = profileMap.get(String.valueOf(content.getUid()));
            contentElement.setFinalUpdateTime(content.getUpdateTime());
            contentElement.setV_lv(userProfile.getvLv());
            contentElement.setLevel(userProfile.getLevel());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setCreateTime(content.getUpdateTime());
            String contentStr = content.getContent();
            if(contentStr.length() > 100){
                contentElement.setContent(contentStr.substring(0,100));
            }else{
                contentElement.setContent(contentStr);
            }
            contentElement.setType(content.getType());
            contentElement.setTitle(content.getTitle());
//            contentElement.setIsLike(isLike(content.getId(),uid));
            String cover = content.getConverImage();
            contentElement.setReviewCount(content.getReviewCount());
            contentElement.setReadCount(content.getReadCountDummy());
            contentElement.setRights(content.getRights());
            if(!StringUtils.isEmpty(cover)) {
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            contentElement.setForwardCid(content.getForwardCid());
            contentElement.setContentType(content.getContentType());
            contentElement.setFavoriteCount(content.getFavoriteCount()+1);
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }else if(content.getType() == Specification.ArticleType.LIVE.index
                    || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
            	if(null != liveFavouriteMap.get(content.getForwardCid().toString())){
                    contentElement.setFavorite(1);
                }else{
                    contentElement.setFavorite(0);
                }
                if(content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国转发UGC的，那么需要返回原作者UID和昵称
                    topicUserProfile = forwardTopicUserProfileMap.get(content.getForwardCid().toString());
                    if(null != topicUserProfile){
                        contentElement.setForwardUid((Long)topicUserProfile.get("uid"));
                        contentElement.setForwardNickName((String)topicUserProfile.get("nick_name"));
                    }
                }else{
                    if(null != topicMemberCountMap.get(content.getForwardCid().toString())){
                        contentElement.setFavoriteCount(topicMemberCountMap.get(content.getForwardCid().toString()).intValue()+1);
                    }else{
                        contentElement.setFavoriteCount(1);
                    }
                }

                //王国增加身份信息
                Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
                if(null != topic){
                    int internalStatust = this.getInternalStatus(topic, uid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    contentElement.setInternalStatus(internalStatust);
                    contentElement.setContentType((Integer) topic.get("type"));
                    if((Integer)topic.get("type") == 1000){
                        //查询聚合子王国
                        int acCount = liveForContentJdbcDao.getTopicAggregationCountByTopicId((Long) topic.get("id"));
                        contentElement.setAcCount(acCount);
                    }
                }
                contentElement.setLiveStatus(0);
                if(null != reviewCountMap.get(content.getForwardCid().toString())){
                    contentElement.setReviewCount(reviewCountMap.get(content.getForwardCid().toString()).intValue());
                }else{
                    contentElement.setReviewCount(0);
                }
                if(null != topicCountMap.get(content.getForwardCid().toString())){
                	contentElement.setTopicCount(topicCountMap.get(content.getForwardCid().toString()).intValue());
                }else{
                	contentElement.setTopicCount(0);
                }
                if(null != content.getUpdateTime()){
                	contentElement.setLastUpdateTime(content.getUpdateTime().getTime());
                }else{
                	contentElement.setLastUpdateTime(contentMybatisDao.getTopicLastUpdateTime(content.getForwardCid()));
                }
            }
            //判断人员是否关注
            if(null != followMap.get(uid+"_"+content.getUid())){
                contentElement.setIsFollowed(1);
            }else{
                contentElement.setIsFollowed(0);
            }
            if(null != followMap.get(content.getUid()+"_"+uid)){
                contentElement.setIsFollowMe(1);
            }else{
                contentElement.setIsFollowMe(0);
            }
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setForwardUrl(content.getForwardUrl());
            contentElement.setForwardTitle(content.getForwardTitle());
            if(vFlag>0){//3.0.0版本以上
            	contentElement.setLastUpdateTime(content.getUpdateId());
            }
            
            //增加王国外露内容
            if(content.getType().intValue() == Specification.ArticleType.LIVE.index){//王国才有外露
            	topicOutDataList = topicOutDataMap.get(content.getForwardCid().toString());
            	if(null != topicOutDataList && topicOutDataList.size() > 0){
            		//先判断是否UGC
            		//第一个如果是UGC则其他的不要了，如果不是，则后面如果有UGC则不要了
            		topicOutData = topicOutDataList.get(0);
            		//第一个需要是有头像的
            		lastUserProfile = profileMap.get(String.valueOf(topicOutData.get("uid")));
            		if(null != lastUserProfile){//这里放上最近发言的那个人的头像
            			contentElement.setUid(lastUserProfile.getUid());
            			contentElement.setNickName(lastUserProfile.getNickName());
            			contentElement.setV_lv(lastUserProfile.getvLv());
            			contentElement.setLevel(lastUserProfile.getLevel());
            			contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + lastUserProfile.getAvatar());
            			if(null != followMap.get(String.valueOf(topicOutData.get("uid"))+"_"+content.getUid())){
                            contentElement.setIsFollowed(1);
                        }else{
                            contentElement.setIsFollowed(0);
                        }
                        if(null != followMap.get(content.getUid()+"_"+String.valueOf(topicOutData.get("uid")))){
                            contentElement.setIsFollowMe(1);
                        }else{
                            contentElement.setIsFollowMe(0);
                        }
            		}
            		
            		int type = ((Integer)topicOutData.get("type")).intValue();
            		int contentType = ((Integer)topicOutData.get("content_type")).intValue();
            		if((type == 0 || type == 52) && contentType == 23){//第一个是UGC
            			outElement = new ShowNewestDto.OutDataElement();
            			outElement.setId((Long)topicOutData.get("id"));
            			outElement.setType((Integer)topicOutData.get("type"));
            			outElement.setContentType((Integer)topicOutData.get("content_type"));
            			outElement.setFragment((String)topicOutData.get("fragment"));
            			String fragmentImage = (String)topicOutData.get("fragment_image");
                        if (!StringUtils.isEmpty(fragmentImage)) {
                        	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                        }
            			outElement.setAtUid((Long)topicOutData.get("at_uid"));
            			if(outElement.getAtUid() > 0){
            				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
            				if(null != atUserProfile){
            					outElement.setAtNickName(atUserProfile.getNickName());
            				}
            			}
            			outElement.setExtra((String)topicOutData.get("extra"));
            			contentElement.getUgcData().add(outElement);
            		}else{//第一个不是UGC
            			for(int i=0;i<topicOutDataList.size();i++){
            				topicOutData = topicOutDataList.get(i);
            				type = ((Integer)topicOutData.get("type")).intValue();
            				contentType = ((Integer)topicOutData.get("content_type")).intValue();
            				if((type == 0 || type == 52) && contentType == 23){//UGC不要了
            					continue;
            				}else if((type == 0 || type == 55) && contentType == 0){//文本
            					if(contentElement.getTextData().size() == 0){
            						outElement = new ShowNewestDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			if(outElement.getAtUid() > 0){
                        				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
                        				if(null != atUserProfile){
                        					outElement.setAtNickName(atUserProfile.getNickName());
                        				}
                        			}
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getTextData().add(outElement);
            					}
            				}else if(type==13 || (type == 55 && contentType == 63)){//音频
            					if(contentElement.getAudioData().size() == 0){
            						outElement = new ShowNewestDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			if(outElement.getAtUid() > 0){
                        				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
                        				if(null != atUserProfile){
                        					outElement.setAtNickName(atUserProfile.getNickName());
                        				}
                        			}
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getAudioData().add(outElement);
            					}
            				}else{//图片区展示部分
            					if(contentElement.getAudioData().size() < 4){
            						outElement = new ShowNewestDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			if(outElement.getAtUid() > 0){
                        				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
                        				if(null != atUserProfile){
                        					outElement.setAtNickName(atUserProfile.getNickName());
                        				}
                        			}
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getImageData().add(outElement);
            					}
            				}
            			}
            		}
            	}
            }
            
            showNewestDto.getNewestData().add(contentElement);
        }
        return Response.success(showNewestDto);
    }

    @Override
    public Response Attention(int sinceId, long uid, int vFlag) {
        log.info("current sinceId is : " + sinceId);
        log.info("getAttention start ...");
        ShowAttentionDto showAttentionDto = new ShowAttentionDto();
        //获取此人关注的人是列表
        log.info("get user follow");
        List<Content> attentionList = contentMybatisDao.getAttention(sinceId ,uid, vFlag);
        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> forwardTopicIdList = new ArrayList<Long>();
        for(Content idx : attentionList){
            if(!uidList.contains(idx.getUid())){
                uidList.add(idx.getUid());
            }
            if(idx.getType() == Specification.ArticleType.LIVE.index
                    || idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国
                if(!topicIdList.contains(idx.getForwardCid())){
                    topicIdList.add(idx.getForwardCid());
                }
                if(idx.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    if(!forwardTopicIdList.contains(idx.getForwardCid())){
                        forwardTopicIdList.add(idx.getForwardCid());
                    }
                }
            }
        }
        log.info("getAttention data");
        
        //一次性获取王国的外露内容
        Map<String, List<Map<String, Object>>> topicOutDataMap = new HashMap<String, List<Map<String, Object>>>();
        String v = userService.getAppConfigByKey("KINGDOM_OUT_MINUTE");
        int limitMinute = 3;
        if(!StringUtils.isEmpty(v)){
        	limitMinute = Integer.valueOf(v).intValue();
        }
        List<Long> privateTopicIds = new ArrayList<Long>();
        String specialTopicIds = userService.getAppConfigByKey("SPECIAL_KINGDOM_IDS");
        if(!StringUtils.isEmpty(specialTopicIds)){
        	String[] tmp = specialTopicIds.split(",");
        	for(String a : tmp){
        		if(!StringUtils.isEmpty(a)){
        			privateTopicIds.add(Long.valueOf(a));
        		}
        	}
        }
        List<Map<String,Object>> topicOutList = liveForContentJdbcDao.getOutFragments(topicIdList, limitMinute, privateTopicIds);
        if(null != topicOutList && topicOutList.size() > 0){
        	Long topicId = null;
        	Long atUid = null;
        	Long fragmentUid = null;
        	List<Map<String, Object>> toList = null;
        	for(Map<String,Object> m : topicOutList){
        		topicId = (Long)m.get("topic_id");
        		toList = topicOutDataMap.get(topicId.toString());
        		if(null == toList){
        			toList = new ArrayList<Map<String, Object>>();
        			topicOutDataMap.put(topicId.toString(), toList);
        		}
        		toList.add(m);
        		atUid = (Long)m.get("at_uid");
        		if(null != atUid && atUid.longValue() > 0){
        			if(!uidList.contains(atUid)){
                        uidList.add(atUid);
                    }
        		}
        		fragmentUid = (Long)m.get("uid");
        		if(null != fragmentUid && fragmentUid.longValue() > 0){
        			if(!uidList.contains(fragmentUid)){
                        uidList.add(fragmentUid);
                    }
        		}
        	}
        }

        Map<String, Map<String, Object>> forwardTopicUserProfileMap = new HashMap<String, Map<String, Object>>();
        if(forwardTopicIdList.size() > 0){
            List<Map<String,Object>> topicUserProfileList = liveForContentJdbcDao.getTopicUserProfileByTopicIds(forwardTopicIdList);
            if(null != topicUserProfileList && topicUserProfileList.size() > 0){
                for(Map<String,Object> topicUserProfile : topicUserProfileList){
                    forwardTopicUserProfileMap.put(String.valueOf(topicUserProfile.get("id")), topicUserProfile);
                }
            }
        }

        Map<String, UserProfile> profileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                profileMap.put(String.valueOf(up.getUid()), up);
            }
        }
        
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
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
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询王国订阅信息
        Map<String, String> liveFavouriteMap = new HashMap<String, String>();
        List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(uid, topicIdList);
        if(null != liveFavouriteList && liveFavouriteList.size() > 0){
            for(Map<String,Object> lf : liveFavouriteList){
                liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }

        UserProfile userProfile = null;
        Map<String, Object> topicUserProfile = null;
        List<Map<String, Object>> topicOutDataList = null;
        Map<String, Object> topicOutData = null;
        ShowAttentionDto.OutDataElement outElement = null;
        UserProfile atUserProfile = null;
        UserProfile lastUserProfile = null;
        for(Content content : attentionList){
            ShowAttentionDto.ContentElement contentElement = ShowAttentionDto.createElement();
            contentElement.setId(content.getId());
            contentElement.setUid(content.getUid());
            // 获取用户信息
            userProfile = profileMap.get(String.valueOf(content.getUid()));
            contentElement.setV_lv(userProfile.getvLv());
            contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            contentElement.setNickName(userProfile.getNickName());
            contentElement.setLevel(userProfile.getLevel());
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
                if(content.getType() == Specification.ArticleType.FORWARD_ARTICLE.index
                        || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){
                    contentElement.setCoverImage(cover);
                }else {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
                }
            }
            contentElement.setTag(content.getFeeling());
            if(content.getType() == Specification.ArticleType.ORIGIN.index){
                //获取内容图片数量
                int imageCounts = contentMybatisDao.getContentImageCount(content.getId());
                contentElement.setImageCount(imageCounts);
            }
            //判断人员是否关注
            if(null != followMap.get(uid+"_"+content.getUid())){
                contentElement.setIsFollowed(1);
            }else{
                contentElement.setIsFollowed(0);
            }
            if(null != followMap.get(content.getUid()+"_"+uid)){
                contentElement.setIsFollowMe(1);
            }else{
                contentElement.setIsFollowMe(0);
            }
            contentElement.setLikeCount(content.getLikeCount());
            contentElement.setPersonCount(content.getPersonCount());
            contentElement.setFavoriteCount(content.getFavoriteCount()+1);
            contentElement.setForwardTitle(content.getForwardTitle());
            contentElement.setForwardUrl(content.getForwardUrl());
            if(content.getType() == Specification.ArticleType.LIVE.index
                    || content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国的话，获取身份信息
            	if(null != liveFavouriteMap.get(content.getForwardCid().toString())){
                    contentElement.setFavorite(1);
                }else{
                    contentElement.setFavorite(0);
                }
                if(content.getType() == Specification.ArticleType.FORWARD_LIVE.index){//王国转发UGC的，那么需要返回原作者UID和昵称
                    topicUserProfile = forwardTopicUserProfileMap.get(content.getForwardCid().toString());
                    if(null != topicUserProfile){
                        contentElement.setForwardUid((Long)topicUserProfile.get("uid"));
                        contentElement.setForwardNickName((String)topicUserProfile.get("nick_name"));
                    }
                }else{//王国的，需要实际的成员数
                    if(null != topicMemberCountMap.get(content.getForwardCid().toString())){
                        contentElement.setFavoriteCount(topicMemberCountMap.get(content.getForwardCid().toString()).intValue()+1);
                    }else{
                        contentElement.setFavoriteCount(1);
                    }
                }

                contentElement.setLiveStatus(0);
                if(null != reviewCountMap.get(content.getForwardCid().toString())){
                    contentElement.setReviewCount(reviewCountMap.get(content.getForwardCid().toString()).intValue());
                }else{
                    contentElement.setReviewCount(0);
                }
                if(null != topicCountMap.get(content.getForwardCid().toString())){
                	contentElement.setTopicCount(topicCountMap.get(content.getForwardCid().toString()).intValue());
                }else{
                	contentElement.setTopicCount(0);
                }
                Map<String, Object> topic = topicMap.get(String.valueOf(content.getForwardCid()));
                if(null != topic){
                	contentElement.setLastUpdateTime((Long)topic.get("long_time"));
                    int internalStatust = this.getInternalStatus(topic, uid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    contentElement.setInternalStatus(internalStatust);
                    contentElement.setContentType((Integer) topic.get("type"));
                    if((Integer) topic.get("type") == 1000){
                        //聚合王国子王国数量
                        contentElement.setAcCount(liveForContentJdbcDao.getTopicAggregationCountByTopicId(content.getForwardCid()));
                    }
                }
            }

            //增加王国外露内容
            if(content.getType().intValue() == Specification.ArticleType.LIVE.index){//王国才有外露
            	topicOutDataList = topicOutDataMap.get(content.getForwardCid().toString());
            	if(null != topicOutDataList && topicOutDataList.size() > 0){
            		//先判断是否UGC
            		//第一个如果是UGC则其他的不要了，如果不是，则后面如果有UGC则不要了
            		topicOutData = topicOutDataList.get(0);
            		lastUserProfile = profileMap.get(String.valueOf(topicOutData.get("uid")));
            		if(null != lastUserProfile){//这里放上最近发言的那个人的头像
            			contentElement.setUid(lastUserProfile.getUid());
            			contentElement.setNickName(lastUserProfile.getNickName());
            			contentElement.setV_lv(lastUserProfile.getvLv());
            			contentElement.setLevel(lastUserProfile.getLevel());
            			contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + lastUserProfile.getAvatar());
            			if(null != followMap.get(String.valueOf(topicOutData.get("uid"))+"_"+content.getUid())){
                            contentElement.setIsFollowed(1);
                        }else{
                            contentElement.setIsFollowed(0);
                        }
                        if(null != followMap.get(content.getUid()+"_"+String.valueOf(topicOutData.get("uid")))){
                            contentElement.setIsFollowMe(1);
                        }else{
                            contentElement.setIsFollowMe(0);
                        }
            		}
            		
            		int type = ((Integer)topicOutData.get("type")).intValue();
            		int contentType = ((Integer)topicOutData.get("content_type")).intValue();
            		if((type == 0 || type == 52) && contentType == 23){//第一个是UGC
            			outElement = new ShowAttentionDto.OutDataElement();
            			outElement.setId((Long)topicOutData.get("id"));
            			outElement.setType((Integer)topicOutData.get("type"));
            			outElement.setContentType((Integer)topicOutData.get("content_type"));
            			outElement.setFragment((String)topicOutData.get("fragment"));
            			String fragmentImage = (String)topicOutData.get("fragment_image");
                        if (!StringUtils.isEmpty(fragmentImage)) {
                        	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                        }
            			outElement.setAtUid((Long)topicOutData.get("at_uid"));
            			if(outElement.getAtUid() > 0){
            				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
            				if(null != atUserProfile){
            					outElement.setAtNickName(atUserProfile.getNickName());
            				}
            			}
            			outElement.setExtra((String)topicOutData.get("extra"));
            			contentElement.getUgcData().add(outElement);
            		}else{//第一个不是UGC
            			for(int i=0;i<topicOutDataList.size();i++){
            				topicOutData = topicOutDataList.get(i);
            				type = ((Integer)topicOutData.get("type")).intValue();
            				contentType = ((Integer)topicOutData.get("content_type")).intValue();
            				if((type == 0 || type == 52) && contentType == 23){//UGC不要了
            					continue;
            				}else if((type == 0 || type == 55) && contentType == 0){//文本
            					if(contentElement.getTextData().size() == 0){
            						outElement = new ShowAttentionDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			if(outElement.getAtUid() > 0){
                        				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
                        				if(null != atUserProfile){
                        					outElement.setAtNickName(atUserProfile.getNickName());
                        				}
                        			}
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getTextData().add(outElement);
            					}
            				}else if(type==13 || (type == 55 && contentType == 63)){//音频
            					if(contentElement.getAudioData().size() == 0){
            						outElement = new ShowAttentionDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			if(outElement.getAtUid() > 0){
                        				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
                        				if(null != atUserProfile){
                        					outElement.setAtNickName(atUserProfile.getNickName());
                        				}
                        			}
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getAudioData().add(outElement);
            					}
            				}else{//图片区展示部分
            					if(contentElement.getAudioData().size() < 4){
            						outElement = new ShowAttentionDto.OutDataElement();
                        			outElement.setId((Long)topicOutData.get("id"));
                        			outElement.setType((Integer)topicOutData.get("type"));
                        			outElement.setContentType((Integer)topicOutData.get("content_type"));
                        			outElement.setFragment((String)topicOutData.get("fragment"));
                        			String fragmentImage = (String)topicOutData.get("fragment_image");
                                    if (!StringUtils.isEmpty(fragmentImage)) {
                                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                    }
                        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                        			if(outElement.getAtUid() > 0){
                        				atUserProfile = profileMap.get(String.valueOf(outElement.getAtUid()));
                        				if(null != atUserProfile){
                        					outElement.setAtNickName(atUserProfile.getNickName());
                        				}
                        			}
                        			outElement.setExtra((String)topicOutData.get("extra"));
                        			contentElement.getImageData().add(outElement);
            					}
            				}
            			}
            		}
            	}
            }
            
            showAttentionDto.getAttentionData().add(contentElement);
//            List<ContentReview> contentReviewList = contentMybatisDao.getContentReviewTop3ByCid(content.getId());
//            log.info("getContentReviewTop3ByCid data success");
//            for(ContentReview contentReview : contentReviewList){
//                ShowAttentionDto.ContentElement.ReviewElement reviewElement = ShowAttentionDto.ContentElement.createElement();
//                reviewElement.setUid(contentReview.getUid());
//                UserProfile user = userService.getUserProfileByUid(contentReview.getUid());
//                reviewElement.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
//                reviewElement.setNickName(user.getNickName());
//                reviewElement.setCreateTime(contentReview.getCreateTime());
//                reviewElement.setReview(contentReview.getReview());
//                contentElement.getReviews().add(reviewElement);
//            }
        }
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FOLLOW_LIST.index,0,uid));
        log.info("monitor");
        return Response.success(showAttentionDto);
    }

    @Override
    public List<Content> getAttention(long sinceId, long uid, int vFlag) {
        List<Content> attentionList = contentMybatisDao.getAttention(sinceId ,uid, vFlag);
        return attentionList;
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

//        if (internalStatus == 0) {
//            internalStatus = userService.getUserInternalStatus(uid, (Long)topic.get("uid"));
//        }

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
    public List<Content> getContentsByTopicIds(List<Long> topicIds) {
        if(null == topicIds || topicIds.size() == 0){
            return null;
        }
        List<Content> list = contentMybatisDao.getContentByTopicIds(topicIds);
        return list;
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
//            list = contentMybatisDao.getArticleReviewByCid(cid,sinceId);
            list = new ArrayList<ContentReview>();
        }else{
//            list = contentMybatisDao.getContentReviewByCid(cid,sinceId);
            list = new ArrayList<ContentReview>();
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
            reviewElement.setLevel(userProfile.getLevel());
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

    public void addContentLikeByCid(long cid, long addNum){
        liveForContentJdbcDao.addContentLikeByCid(cid, addNum);
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
                    jsonObject.addProperty("cid",id);
                    String alias = String.valueOf(content.getUid());
                    userService.pushWithExtra(alias, "你发布的内容上热点啦！", JPushUtils.packageExtra(jsonObject));
                }
            }else if(content.getType() == Specification.ArticleType.LIVE.index){
                Map<String,Object> topic = liveForContentJdbcDao.getTopicListByCid(content.getForwardCid());
                JSONArray coreCircles = JSON.parseArray((String)topic.get("core_circle"));
                if(coreCircles!=null){
                    for(int i=0;i<coreCircles.size();i++){
                        if(!this.checkTopicPush(content.getForwardCid(), Long.valueOf(coreCircles.getString(i)))){
                            continue;
                        }
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("messageType",Specification.PushMessageType.LIVE_HOTTEST.index);
                        jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
                        jsonObject.addProperty("topicId",content.getForwardCid());
                        jsonObject.addProperty("contentType", (Integer)topic.get("type"));
                        jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//此处是核心圈的推送，所以直接设置核心圈
                        //这里是管理员将王国上热点。。这里没法设置操作人的身份了
                        String alias =coreCircles.getString(i);
                        userService.pushWithExtra(alias,"『"+content.getTitle()+ "』上热点啦！",JPushUtils.packageExtra(jsonObject));
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

    private boolean checkTopicPush(long topicId, long uid){
        Map<String,Object> tuc = liveForContentJdbcDao.getTopicUserConfig(topicId, uid);
        if(null != tuc){
            int pushType = (Integer)tuc.get("push_type");
            if(pushType == 1){
                return false;
            }
        }
        return true;
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
        List<String> ids = Lists.newArrayList();
        ids.add("110");
        ids.add("111");
        String result = Joiner.on(",").join(ids);
        System.out.println(result);
    }

    /**
     * 删除文章评论
     * 删除规则：--20161114
     * 1）管理员能删所有的评论
     * 2）非管理员只能删自己发的评论
     * @param delDTO
     */
    @Override
    public Response delArticleReview(ReviewDelDTO delDTO, boolean isSys) {
        ArticleReview review = contentMybatisDao.getArticleReviewById(delDTO.getRid());
        if(null == review){//可能是删除那些未发送成功的，所以直接置为成功
            log.info("文章评论["+delDTO.getRid()+"]不存在");
            return Response.success(ResponseStatus.REVIEW_DELETE_SUCCESS.status, ResponseStatus.REVIEW_DELETE_SUCCESS.message);
        }
        //判断当前用户是否有删除本条评论的权限
        boolean canDel = false;
        if(isSys){//是否系统内部删除
            canDel = true;
        }

        //判断是否是管理员，管理员啥都能删
        if(!canDel){
            if(userService.isAdmin(delDTO.getUid())){
                canDel = true;
            }
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
    public Response delContentReview(ReviewDelDTO delDTO, boolean isSys) {
        ContentReview review = contentMybatisDao.getContentReviewById(delDTO.getRid());
        if(null == review){//可能是删除那些未发送成功的，所以直接置为成功
            log.info("UGC评论["+delDTO.getRid()+"]不存在");
            return Response.success(ResponseStatus.REVIEW_DELETE_SUCCESS.status, ResponseStatus.REVIEW_DELETE_SUCCESS.message);
        }
        //判断当前用户是否有删除本条评论的权限
        boolean canDel = false;
        if(isSys){//是否系统内部删除
            canDel = true;
        }

        //判断是否是管理员，管理员啥都能删
        if(!canDel){
            if(userService.isAdmin(delDTO.getUid())){
                canDel = true;
            }
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

    @Override
    public Response searchUserContent(UserContentSearchDTO searchDTO) {
        int page = 1;
        if(searchDTO.getPage() > 1){
            page = searchDTO.getPage();
        }
        int pageSize = 10;
        if(searchDTO.getPageSize() > 0){
            pageSize = searchDTO.getPageSize();
        }
        int start = (page -1) * pageSize;

        ShowUserContentsDTO resultDTO = new ShowUserContentsDTO();
        resultDTO.setSearchType(searchDTO.getSearchType());
        resultDTO.setCurrentPage(page);

        if(searchDTO.getSearchType() == Specification.UserContentSearchType.ARTICLE_REVIEW.index){
            //文章评论查询
            int totalCount = contentMybatisDao.countArticleReviewPageByUid(searchDTO.getUid());
            resultDTO.setTotalCount(totalCount);
            resultDTO.setTotalPage(totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1);

            List<ArticleReview> list = contentMybatisDao.getArticleReviewPageByUid(searchDTO.getUid(), start, pageSize);
            if(null != list && list.size() > 0){
                ShowUserContentsDTO.UserArtcileReviewElement e = null;
                for(ArticleReview ar : list){
                    e = new ShowUserContentsDTO.UserArtcileReviewElement();
                    e.setId(ar.getId());
                    e.setArticleId(ar.getArticleId());
                    e.setAtUid(ar.getAtUid());
                    e.setCreateTime(ar.getCreateTime());
                    e.setReview(ar.getReview());
                    e.setStatus(ar.getStatus());
                    e.setUid(ar.getUid());
                    resultDTO.getResult().add(e);
                }
            }
        }else if(searchDTO.getSearchType() == Specification.UserContentSearchType.UGC.index){
            //UGC
            int totalCount = contentMybatisDao.countUgcPageByUid(searchDTO.getUid());
            resultDTO.setTotalCount(totalCount);
            resultDTO.setTotalPage(totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1);

            List<Content> list = contentMybatisDao.getUgcPageByUid(searchDTO.getUid(), start, pageSize);
            if(null != list && list.size() > 0){
                ShowUserContentsDTO.UserUgcElement e = null;
                for(Content c : list){
                    e = new ShowUserContentsDTO.UserUgcElement();
                    e.setContent(c.getContent());
                    e.setContentType(c.getContentType());
                    e.setConverImage(c.getConverImage());
                    e.setCreateTime(c.getCreateTime());
                    e.setFeeling(c.getFeeling());
                    e.setId(c.getId());
                    e.setIsTop(c.getIsTop());
                    e.setLikeCount(c.getLikeCount());
                    e.setReadCount(c.getReadCount());
                    e.setReadCountDummy(c.getReadCountDummy());
                    e.setReviewCount(c.getReviewCount());
                    e.setRights(c.getRights());
                    e.setStatus(c.getStatus());
                    e.setTitle(c.getTitle());
                    e.setType(c.getType());
                    e.setUid(c.getUid());
                    resultDTO.getResult().add(e);
                }
            }
        }else if(searchDTO.getSearchType() == Specification.UserContentSearchType.UGC_OR_PGC_REVIEW.index){
            //UGC评论
            int totalCount = contentMybatisDao.countContentReviewPageByUid(searchDTO.getUid());
            resultDTO.setTotalCount(totalCount);
            resultDTO.setTotalPage(totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1);

            List<ContentReview> list = contentMybatisDao.getContentReviewPageByUid(searchDTO.getUid(), start, pageSize);
            if(null != list && list.size() > 0){
                ShowUserContentsDTO.UserUgcReviewElement e = null;
                for(ContentReview cr : list){
                    e = new ShowUserContentsDTO.UserUgcReviewElement();
                    e.setAtUid(cr.getAtUid());
                    e.setCid(cr.getCid());
                    e.setCreateTime(cr.getCreateTime());
                    e.setExtra(cr.getExtra());
                    e.setId(cr.getId());
                    e.setReview(cr.getReview());
                    e.setStatus(cr.getStatus());
                    e.setUid(cr.getUid());
                    resultDTO.getResult().add(e);
                }
            }
        }else if(searchDTO.getSearchType() == Specification.UserContentSearchType.KINGDOM.index){
            //王国
            int totalCount = liveForContentJdbcDao.countUserTopicPageByUid(searchDTO.getUid());
            resultDTO.setTotalCount(totalCount);
            resultDTO.setTotalPage(totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1);

            List<Map<String,Object>> list = liveForContentJdbcDao.getUserTopicPageByUid(searchDTO.getUid(), start, pageSize);
            if(null != list && list.size() > 0){
                ShowUserContentsDTO.UserTopicElement e = null;
                for(Map<String,Object> t : list){
                    e = new ShowUserContentsDTO.UserTopicElement();
                    e.setCoreCircle((String)t.get("core_circle"));
                    e.setCreateTime((Date)t.get("create_time"));
                    e.setId((Long)t.get("id"));
                    e.setLiveImage((String)t.get("live_image"));
                    e.setStatus((Integer)t.get("status"));
                    e.setTitle((String)t.get("title"));
                    e.setUid((Long)t.get("uid"));
                    e.setUpdateTime((Date)t.get("update_time"));
                    resultDTO.getResult().add(e);
                }
            }
        }else if(searchDTO.getSearchType() == Specification.UserContentSearchType.KINGDOM_SPEAK.index){
            //王国发言/评论等
            int totalCount = liveForContentJdbcDao.countUserTopicFragmentPageByUid(searchDTO.getUid());
            resultDTO.setTotalCount(totalCount);
            resultDTO.setTotalPage(totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1);

            List<Map<String,Object>> list = liveForContentJdbcDao.getUserTopicFragmentPageByUid(searchDTO.getUid(), start, pageSize);
            if(null != list && list.size() > 0){
                ShowUserContentsDTO.UserTopicFragmentElement e = null;
                for(Map<String,Object> t : list){
                    e = new ShowUserContentsDTO.UserTopicFragmentElement();
                    e.setContentType((Integer)t.get("content_type"));
                    e.setCreateTime((Date)t.get("create_time"));
                    e.setExtra((String)t.get("extra"));
                    e.setFragment((String)t.get("fragment"));
                    e.setFragmentImage((String)t.get("fragment_image"));
                    e.setId((Long)t.get("id"));
                    e.setStatus((Integer)t.get("status"));
                    e.setTopicId((Long)t.get("topic_id"));
                    e.setType((Integer)t.get("type"));
                    e.setUid((Long)t.get("uid"));
                    resultDTO.getResult().add(e);
                }
            }
        }else{
            return Response.failure("非法的查询类型");
        }

        return Response.success(resultDTO);
    }

    @Override
    public Response delUserContent(int type, long id) {
        if(type == Specification.UserContentSearchType.ARTICLE_REVIEW.index){
            ReviewDelDTO delDTO = new ReviewDelDTO();
            delDTO.setRid(id);
            delDTO.setUid(-100);//运维人员
            return this.delArticleReview(delDTO, true);
        }else if(type == Specification.UserContentSearchType.UGC.index){
            return this.deleteContent(id, -100, true);
        }else if(type == Specification.UserContentSearchType.UGC_OR_PGC_REVIEW.index){
            ContentReview review = contentMybatisDao.getContentReviewById(id);
            if(null == review){
                log.info("UGC评论["+id+"]不存在");
                return Response.success(ResponseStatus.REVIEW_DELETE_SUCCESS.status, ResponseStatus.REVIEW_DELETE_SUCCESS.message);
            }
            ReviewDelDTO delDTO = new ReviewDelDTO();
            delDTO.setRid(id);
            delDTO.setUid(-100);//运维人员
            delDTO.setCid(review.getCid());
            return this.delContentReview(delDTO, true);
        }else if(type == Specification.UserContentSearchType.KINGDOM.index){
            List<Content> list = contentMybatisDao.getContentByTopicId(id);
            Content c = null;
            if(null != list && list.size() > 0){
                c = list.get(0);
            }
            if(null == c){
                log.info("王国["+id+"]不存在");
                return Response.success(ResponseStatus.CONTENT_DELETE_SUCCESS.status,ResponseStatus.CONTENT_DELETE_SUCCESS.message);
            }
            return this.deleteContent(c.getId(), -100, true);
        }else if(type == Specification.UserContentSearchType.KINGDOM_SPEAK.index){
            liveForContentJdbcDao.deleteTopicFragmentById(id);
            liveForContentJdbcDao.deleteTopicBarrageByFie(id);
            //记录下删除记录
            liveForContentJdbcDao.insertDeleteLog(Specification.DeleteObjectType.TOPIC_FRAGMENT.index, id, -100);
            return Response.success();
        }else{
            return Response.failure("非法的查询类型");
        }
    }

    @Override
    public Response hotList(long sinceId, long uid, int vflag){
        boolean isFirst = false;
        if(sinceId <= 0){
            isFirst = true;
            sinceId = Long.MAX_VALUE;
        }

        ShowHotListDTO result = new ShowHotListDTO();
        
        String key = KeysManager.SEVEN_DAY_REGISTER_PREFIX+uid;
        if(!StringUtils.isEmpty(cacheService.get(key))){
        	String bubblePositions=userService.getAppConfigByKey(Constant.HOTLIST_BUBBLE_POSITION_KEY);
            result.setBubblePositions(bubblePositions);	// 提示消息
        }
        
        int openPushPositions=0;
        String openPushPositionsStr = userService.getAppConfigByKey(Constant.OPEN_PUSH_POSITION);
        if(!StringUtils.isEmpty(openPushPositionsStr)){
        	openPushPositions = Integer.parseInt(openPushPositionsStr);
        }
        result.setOpenPushPositions(openPushPositions);	// 提示消息
        
        List<TagInfo> blackTags = topicTagMapper.getUserLikeTagAndSubTag(uid,0);		// 不喜欢的标签和子标签。
        
        List<Long> blacklistUids = liveForContentJdbcDao.getBlacklist(uid);
        List<ActivityWithBLOBs> activityList = null;
        List<UserFamous> userFamousList = null;
        List<Content2Dto> ceKingdomList = null;
        List<Map<String,Object>> listingKingdoms = null;
        if(isFirst){//第一次的话，还需要获取banner列表，名人堂列表，热点聚合王国列表
            //获取banner列表
            activityList = activityService.getHotActivity();
            //获取名人堂列表
            userFamousList = userService.getUserFamousPage(1, 30, blacklistUids);
            //获取热点聚合王国列表  ,已经不用了。
            //ceKingdomList = contentMybatisDao.getHotContentByType(sinceId, 1, 3);//只要3个热点聚合王国
            // 查上市价格, 获取30个上市王国
            listingKingdoms = liveForContentJdbcDao.getListingKingdoms(1, 30);
            List<String> blackTagNameList = new ArrayList<>();
            for(TagInfo info:blackTags){
            	blackTagNameList.add(info.getTagName());
            }
            result.setHotTagKingdomList(buildHotTagKingdoms(uid, blacklistUids,blackTagNameList));
        }

        List<String> redisIds = cacheService.lrange("HOT_TOP_KEY",0,-1);
        List<String> ids = Lists.newArrayList();
        long expireTime = Long.valueOf(userService.getAppConfigByKey("TOP_EXPIRED"))*1000;
        Map<String,Long> map = Maps.newConcurrentMap();
        for(String id : redisIds){
            Long startTime = Long.valueOf(id.split("@")[1]);
            String splitId = id.split("@")[0];
            map.put(splitId,startTime);
            if(System.currentTimeMillis()-startTime<=expireTime) {
                ids.add(splitId);
            }else {
                cacheService.lrem("HOT_TOP_KEY",0,id);

            }
        }
        List<Long> blackTagIdList = new ArrayList<>();
        for(TagInfo info:blackTags){
        	blackTagIdList.add(info.getTagId());
        }
        String blackTagIds = org.apache.commons.lang3.StringUtils.join(blackTagIdList,",");
        //String ids = null;
        List<Content2Dto> topList = Lists.newArrayList();
        if(!ObjectUtils.isEmpty(ids)) {
            topList = contentMybatisDao.getHotContentByRedis(uid,ids, blacklistUids,blackTagIds);
        }
        List<Content2Dto> contentList = contentMybatisDao.getHotContentByType(uid,sinceId, 0, 20,ids, blacklistUids,blackTagIds);//只要UGC+PGC+个人王国

        for(Content2Dto content2Dto : topList){
            content2Dto.setOperationTime(map.get(content2Dto.getHid()+""));
        }
        this.buildHotListDTO(uid, result, activityList, userFamousList, ceKingdomList, contentList,topList);
        // 随机显示喜欢不喜欢按钮
        int likeBtnRatio= userService.getIntegerAppConfigByKey("LIKE_BUTTON_APPEAR_RATIO");
        Set<Integer> rightDigs = new HashSet<>();
        while(likeBtnRatio>rightDigs.size()){		//随机序列。
        	rightDigs.add(RandomUtils.nextInt(0, 101));
        }
        for(HotContentElement ele:result.getTops()){
        	ele.setIsShowLikeButton(rightDigs.contains(RandomUtils.nextInt(0, 101))?1:0);
        }
        for(HotContentElement ele:result.getHottestContentData()){
        	ele.setIsShowLikeButton(rightDigs.contains(RandomUtils.nextInt(0, 101))?1:0);
        }
        // 排序topList
        if(null != listingKingdoms && listingKingdoms.size()>0){
            List<BasicKingdomInfo> listingKingdomList =kingdomBuider.buildKingdoms(listingKingdoms, uid);
            for(BasicKingdomInfo info:listingKingdomList){
                info.setShowPriceBrand(0);// 首页不显示米币吊牌。
            }
            result.setListingKingdoms(listingKingdomList);
        }else{
            result.setListingKingdoms(new ArrayList<>());
        }
        if(vflag == 0){
            if(result.getHottestCeKingdomData().size() > 0){
                for(ShowHotListDTO.HotCeKingdomElement e : result.getHottestCeKingdomData()){
                    e.setTags(null);
                }
            }
            if(result.getHottestContentData().size() > 0){
                for(ShowHotListDTO.HotContentElement e : result.getHottestContentData()){
                    e.setTags(null);
                }
            }
        }
        
        for(ShowHotListDTO.HotContentElement element : result.getTops()){
            element.setOperationTime(map.get(element.getHid()+""));
        }
        Collections.sort(result.getTops());
        return Response.success(result);
    }

    private void loadTopData() {
    }


    /**
     * 米汤币兑换人名币
     * @param price
     * @return
     */
    public double exchangeKingdomPrice(int price) {
        String  result =  userService.getAppConfigByKey("EXCHANGE_RATE");
        if(StringUtils.isEmpty(result)){
            return 0;
        }
        BigDecimal exchangeRate = new BigDecimal(result);
        return new BigDecimal(price).divide(exchangeRate, 2, RoundingMode.HALF_UP).doubleValue();
    }
    private List<HotTagElement> buildHotTagKingdoms(long uid, List<Long> blacklistUids,List<String> blackTags){
        List<com.me2me.content.dto.ShowHotListDTO.HotTagElement> dataList = new ArrayList<ShowHotListDTO.HotTagElement>();

        Set<String> allTags = new LinkedHashSet<>();
        
        //用户在首页的标签上点击了喜欢,注意在喜欢了之后,在推荐和查询的时候,连同下方的子标签也会一起加入展示,除非用户手动对子标签选择了不喜欢
        List<TagInfo> userLikeTagInfo= topicTagMapper.getUserLikeTagAndSubTag(uid,1);	// 白名单，推荐标签和子标签
        Set<String> userLikeTags = new LinkedHashSet<>();
        for(TagInfo info:userLikeTagInfo){
        	userLikeTags.add(info.getTagName());
        }
        allTags.addAll(userLikeTags);
        
        //除以上两种标签外,通过用户行为产生的排名最高的前5个"体系标签",且评分必须超过20
        List<String> favoTags = this.topicTagMapper.getUserFavoTags(uid,5);
        for(String favoTag:favoTags){
        	if(!blackTags.contains(favoTag)){
        		allTags.add(favoTag);
        	}
        }
        
        //除以上三种标签之外,随机从运营后台设定的"体系标签中"选出的标签,数量20个
        String strAdminTags = (String) userService.getAppConfigByKey("HOME_HOT_LABELS");
        List<String> adminTags = Arrays.asList(strAdminTags.split("\\n"));
        int n=0;
        for(String adminTag:adminTags){
        	if(n<20 && !blackTags.contains(adminTag)){	//除以上三种标签之外,随机从运营后台设定的"体系标签中"选出的标签,数量20个
        		allTags.add(adminTag);	
        		n++;
        	}
        }
       
        for(String label:allTags){
        	long tagId= topicTagMapper.getTagIdByTag(label);
            HotTagElement element = new HotTagElement();
           	element.setIsShowLikeButton(userLikeTags.contains(label)?0:1);
            
            
           /* List<Map<String,Object>> topicList = null;
            String ktKey = "OBJ:KINGDOMSBYTAG:"+label+"_new_1_4";
            Object tkRes = cacheService.getJavaObject(ktKey);
            if(null != tkRes){
            	topicList = (List<Map<String,Object>>)tkRes;
            }else{
            	topicList = this.topicTagMapper.getKingdomsByTag(uid,tagId,"new",1,4, blacklistUids);
            	List<Map<String,Object>> tkCacheObj = new ArrayList<Map<String,Object>>();
            	if(null != topicList && topicList.size() > 0){
            		Map<String,Object> t = null;
            		for(Map<String,Object> m : topicList){
            			t = new HashMap<String, Object>();
            			t.putAll(m);
            			tkCacheObj.add(t);
            		}
            	}
            	cacheService.cacheJavaObject(ktKey, tkCacheObj, 2*60*60);//缓存两小时
            }*/
           	List<Long> topicIds = topicTagMapper.getTopicIdsByTagAndSubTag(tagId);
           	List<Map<String,Object>> topicList = this.topicTagMapper.getKingdomsByTag(uid,topicIds,"new",1,4, blacklistUids);
            
            //List<Integer> topicIds = this.topicTagMapper.getTopicIdsByTag(label);
            Map<String,Object> totalPrice = null;
        	String cacheKey = "OBJ:TAGPRICEANDKINGDOMCOUNT:"+label;
        	Object tagRes = cacheService.getJavaObject(cacheKey);
        	if(null != tagRes){
        		log.info("从缓存里取到22");
        		totalPrice = (Map<String,Object>)tagRes;
        	}else{
        		log.info("查的数据库呀22");
        		totalPrice = topicTagMapper.getTagPriceAndKingdomCount(topicIds);
        		Map<String, Object> cacheObj = new HashMap<String, Object>();
        		if(null != totalPrice && totalPrice.size() > 0){
        			cacheObj.putAll(totalPrice);
        		}
        		cacheService.cacheJavaObject(cacheKey, cacheObj, 2*60*60);//缓存两小时
        	}

            if(topicList!=null && topicList.size()>0){
                List<BasicKingdomInfo> kingdoms =this.kingdomBuider.buildKingdoms(topicList, uid);
                element.setKingdomList(kingdoms);
            }
            int tagPersons=0;
            int tagPrice = 0;
            int kingdomCount = 0;
            if(totalPrice.containsKey("tagPersons")){
                tagPersons=((Number)totalPrice.get("tagPersons")).intValue();
            }
            if(totalPrice.containsKey("tagPrice")){
                tagPrice=((Number)totalPrice.get("tagPrice")).intValue();
            }
            if(totalPrice.containsKey("kingdomCount")){
                kingdomCount=((Number)totalPrice.get("kingdomCount")).intValue();
            }

            element.setKingdomCount(kingdomCount);
            element.setPersonCount(tagPersons);
            element.setTagName(label);
            element.setTagId(tagId);
            double rmbPrice = exchangeKingdomPrice(tagPrice);
            //element.setShowRMBBrand(rmbPrice>=TAG_SHOW_PRICE_BRAND_MIN?1:0); 首页不显示标签吊牌。
            element.setShowRMBBrand(0);
            element.setTagPrice(rmbPrice);
            dataList.add(element);
        }
        return dataList;
    }

    private void buildHotListDTO(
            long uid,
            ShowHotListDTO result,
            List<ActivityWithBLOBs> activityList,
            List<UserFamous> userFamousList,
            List<Content2Dto> ceKingdomList_no_use,
            List<Content2Dto> contentList,
            List<Content2Dto> topList){
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> ceTopicIdList = new ArrayList<Long>();
        List<Long> uidList = new ArrayList<Long>();
        //double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));
        double minRmb =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_RMB_BRAND_MIN"));
        if(null != activityList && activityList.size() > 0){
            for(ActivityWithBLOBs activity : activityList){
                if(activity.getTyp() == 2 && activity.getCid() > 0){
                    if(!topicIdList.contains(activity.getCid())){
                        topicIdList.add(activity.getCid());
                    }
                }
            }
        }
        if(null != userFamousList && userFamousList.size() > 0){
            for(UserFamous uf : userFamousList){
                if(!uidList.contains(uf.getUid())){
                    uidList.add(uf.getUid());
                }
            }
        }

//		if(null != ceKingdomList && ceKingdomList.size() > 0){
//			for(Content2Dto c : ceKingdomList){
//				if(!uidList.contains(c.getUid())){
//					uidList.add(c.getUid());
//				}
//				if(!topicIdList.contains(c.getForwardCid())){
//					topicIdList.add(c.getForwardCid());
//				}
//				if(!ceTopicIdList.contains(c.getForwardCid())){
//					ceTopicIdList.add(c.getForwardCid());
//				}
//			}
//		}

        if(null != contentList && contentList.size() > 0){
            for(Content2Dto c : contentList){
                if(!uidList.contains(c.getUid())){
                    uidList.add(c.getUid());
                }
                if(c.getType()==3 && !topicIdList.contains(c.getForwardCid())){
                    topicIdList.add(c.getForwardCid());
                }
            }
        }

        Map<String, List<Map<String, Object>>> acTopMap = new HashMap<String, List<Map<String, Object>>>();
        Map<String, List<Map<String, Object>>> membersMap = new HashMap<String, List<Map<String, Object>>>();
        if(ceTopicIdList.size() > 0){
            List<Map<String, Object>> acTopList = null;
            List<Map<String, Object>> membersLsit = null;
            for(Long ceId : ceTopicIdList){
                acTopList = liveForContentJdbcDao.getAcTopicListByCeTopicId(ceId, 0, 3);
                if(null != acTopList && acTopList.size() > 0){
                    acTopMap.put(ceId.toString(), acTopList);
                    for(Map<String, Object> acTopic : acTopList){
                        if(!topicIdList.contains((Long)acTopic.get("id"))){
                            topicIdList.add((Long)acTopic.get("id"));
                        }
                    }
                }
                membersLsit = liveForContentJdbcDao.getTopicMembersByTopicId(ceId, 0, 20);
                if(null != membersLsit && membersLsit.size() > 0){
                    membersMap.put(ceId.toString(), membersLsit);
                }
            }
        }


        //一次性查出所有王国详情
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
            Long topicId = null;
            for(Map<String,Object>  map : topicList){
                topicId = (Long)map.get("id");
                topicMap.put(topicId.toString(), map);
            }
        }
        //一次性查出所有王国对应的内容表
        Map<String, Content> topicContentMap = new HashMap<String, Content>();
        if(topicIdList.size() > 0){
            List<Content> topicContentList = contentMybatisDao.getContentByTopicIds(topicIdList);
            if(null != topicContentList && topicContentList.size() > 0){
                for(Content c : topicContentList){
                    topicContentMap.put(c.getForwardCid().toString(), c);
                }
            }
        }
        //一次性查出所有的用户信息
        Map<String, UserProfile> userProfileMap = new HashMap<String, UserProfile>();
        List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
        if(null != profileList && profileList.size() > 0){
            for(UserProfile up : profileList){
                userProfileMap.put(up.getUid().toString(), up);
            }
        }
        //一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
        List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
        if(null != userFollowList && userFollowList.size() > 0){
            for(UserFollow uf : userFollowList){
                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            }
        }
        //一次性查询王国订阅信息
        Map<String, String> liveFavouriteMap = new HashMap<String, String>();
        List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(uid, topicIdList);
        if(null != liveFavouriteList && liveFavouriteList.size() > 0){
            for(Map<String,Object> lf : liveFavouriteList){
                liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
            }
        }
        //一次性查询聚合王国的子王国数
        Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTopicIdList.size() > 0){
            List<Map<String,Object>> acCountList = liveForContentJdbcDao.getTopicAggregationAcCountByTopicIds(ceTopicIdList);
            if(null != acCountList && acCountList.size() > 0){
                for(Map<String,Object> a : acCountList){
                    acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
                }
            }
        }

        //一次性查询王国的最后一条更新记录
        Map<String, Map<String,Object>> lastFragmentMap = new HashMap<String, Map<String,Object>>();
        List<Map<String,Object>> lastFragmentList = liveForContentJdbcDao.getLastFragmentByTopicIds(topicIdList);
        if(null != lastFragmentList && lastFragmentList.size() > 0){
            for(Map<String,Object> lf : lastFragmentList){
                lastFragmentMap.put(((Long)lf.get("topic_id")).toString(), lf);
            }
        }
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
        if(null != tcList && tcList.size() > 0){
            for(Map<String, Object> m : tcList){
                topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
                reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            }
        }
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
        if(null == topicMemberCountMap){
            topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询王国的标签信息
        Map<String, String> topicTagMap = new HashMap<String, String>();
        List<Map<String, Object>> topicTagList = liveForContentJdbcDao.getTopicTagDetailListByTopicIds(topicIdList);
        if(null != topicTagList && topicTagList.size() > 0){
            long tid = 0;
            String tags = null;
            Long topicId = null;
            for(Map<String, Object> ttd : topicTagList){
                topicId = (Long)ttd.get("topic_id");
                if(topicId.longValue() != tid){
                    //先插入上一次
                    if(tid > 0 && !StringUtils.isEmpty(tags)){
                        topicTagMap.put(String.valueOf(tid), tags);
                    }
                    //再初始化新的
                    tid = topicId.longValue();
                    tags = null;
                }
                if(tags != null){
                    tags = tags + ";" + (String)ttd.get("tag");
                }else{
                    tags = (String)ttd.get("tag");
                }
            }
            if(tid > 0 && !StringUtils.isEmpty(tags)){
                topicTagMap.put(String.valueOf(tid), tags);
            }
        }

        Map<String, Object> topic = null;
        Content topicContent = null;
        UserProfile userProfile = null;
        Map<String,Object> lastFragment = null;

        if(null != activityList && activityList.size() > 0){
            ShowHotListDTO.HotActivityElement activityElement = null;
            for(ActivityWithBLOBs activity : activityList){
                activityElement = new ShowHotListDTO.HotActivityElement();
                activityElement.setId(activity.getId());
                activityElement.setTitle(activity.getActivityHashTitle());
                if(!StringUtils.isEmpty(activity.getActivityCover())) {
                    activityElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + activity.getActivityCover());
                }
                if(null != activity.getUpdateTime()){
                    activityElement.setUpdateTime(activity.getUpdateTime().getTime());
                }
                activityElement.setContentType(activity.getTyp());
                activityElement.setContentUrl(activity.getLinkUrl());
                activityElement.setType(4);//固定为4
                if(activity.getTyp() == 2){//王国类型的banner
                    activityElement.setTopicId(activity.getCid());
                    topic = topicMap.get(activity.getCid().toString());
                    topicContent = topicContentMap.get(activity.getCid().toString());
                    if(null != topic && null != topicContent){
                        activityElement.setCid(topicContent.getId());
                        activityElement.setTopicType((Integer)topic.get("type"));
                        int internalStatust = this.getInternalStatus(topic, uid);
                        if(internalStatust==Specification.SnsCircle.OUT.index){
                        	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                        		internalStatust=Specification.SnsCircle.IN.index;
                        	}
                        }
                        activityElement.setTopicInternalStatus(internalStatust);
                    }
                }
                activityElement.setLinkUrl("");//这个是兼容安卓222版本bug
                result.getActivityData().add(activityElement);
            }
        }

        if(null != userFamousList && userFamousList.size() > 0){
            ShowHotListDTO.HotFamousUserElement famousUserElement = null;
            for(UserFamous uf : userFamousList){
                famousUserElement = new ShowHotListDTO.HotFamousUserElement();
                famousUserElement.setUid(uf.getUid());
                userProfile = userProfileMap.get(uf.getUid().toString());

                if(null != userProfile){
                    famousUserElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                    famousUserElement.setNickName(userProfile.getNickName());
                    famousUserElement.setIntroduced(userProfile.getIntroduced());
                    famousUserElement.setV_lv(userProfile.getvLv());
                    famousUserElement.setLevel(userProfile.getLevel());
                }
                if(null != followMap.get(uid+"_"+uf.getUid())){
                    famousUserElement.setIsFollowed(1);
                }else{
                    famousUserElement.setIsFollowed(0);
                }
                if(null != followMap.get(uf.getUid()+"_"+uid)){
                    famousUserElement.setIsFollowMe(1);
                }else{
                    famousUserElement.setIsFollowMe(0);
                }

                result.getFamousUserData().add(famousUserElement);
            }
        }
		/*
		 * 聚合王国不处理
		List<Map<String, Object>> acTopList = null;
		List<Map<String, Object>> membersList = null;
		if(null != ceKingdomList && ceKingdomList.size() > 0){
			ShowHotListDTO.HotCeKingdomElement ceKingdomElement = null;
			ShowHotListDTO.AcTopElement acTopElement = null;
			ShowHotListDTO.MemberElement memberElement = null;
			for(Content2Dto ce : ceKingdomList){
				ceKingdomElement = new ShowHotListDTO.HotCeKingdomElement();
				ceKingdomElement.setUid(ce.getUid());
				userProfile = userProfileMap.get(ce.getUid().toString());
				ceKingdomElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
				ceKingdomElement.setNickName(userProfile.getNickName());
				ceKingdomElement.setV_lv(userProfile.getvLv());
                ceKingdomElement.setLevel(userProfile.getLevel());


				if(null != followMap.get(uid+"_"+ce.getUid())){
					ceKingdomElement.setIsFollowed(1);
				}else{
					ceKingdomElement.setIsFollowed(0);
				}
				if(null != followMap.get(ce.getUid()+"_"+uid)){
					ceKingdomElement.setIsFollowMe(1);
				}else{
					ceKingdomElement.setIsFollowMe(0);
				}
				if(null != liveFavouriteMap.get(ce.getForwardCid().toString())){
					ceKingdomElement.setFavorite(1);
				}else{
					ceKingdomElement.setFavorite(0);
				}
				ceKingdomElement.setTopicId(ce.getForwardCid());
				ceKingdomElement.setForwardCid(ce.getForwardCid());
				ceKingdomElement.setCid(ce.getId());
				ceKingdomElement.setId(ce.getId());
				topic = topicMap.get(ce.getForwardCid().toString());

				if(null != topic){
					ceKingdomElement.setTitle((String)topic.get("title"));
					ceKingdomElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
					ceKingdomElement.setCreateTime(((Date)topic.get("create_time")).getTime());
					ceKingdomElement.setUpdateTime((Long)topic.get("long_time"));
					ceKingdomElement.setLastUpdateTime((Long)topic.get("long_time"));
					ceKingdomElement.setContentType((Integer)topic.get("type"));
					ceKingdomElement.setInternalStatus(this.getInternalStatus(topic, uid));
					ceKingdomElement.setPrice((Integer)topic.get("price"));		//--2.2.7 王国价值
				}
				if(null == topicMemberCountMap.get(ce.getForwardCid().toString())){
					ceKingdomElement.setFavoriteCount(1);//默认只有国王一个成员
				}else{
					ceKingdomElement.setFavoriteCount(topicMemberCountMap.get(ce.getForwardCid().toString()).intValue()+1);
				}
				if(null != acCountMap.get(ce.getForwardCid().toString())){
					ceKingdomElement.setAcCount(acCountMap.get(ce.getForwardCid().toString()).intValue());
				}else{
					ceKingdomElement.setAcCount(0);
				}
				acTopList = acTopMap.get(ce.getForwardCid().toString());
				if(null != acTopList && acTopList.size() > 0){
					for(Map<String, Object> acTop : acTopList){
						acTopElement = new ShowHotListDTO.AcTopElement();
						acTopElement.setTopicId((Long)acTop.get("id"));
						topicContent = topicContentMap.get(((Long)acTop.get("id")).toString());
						if(null != topicContent){
							acTopElement.setCid(topicContent.getId());
						}
						acTopElement.setTitle((String)acTop.get("title"));
						acTopElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)acTop.get("live_image"));
						acTopElement.setContentType((Integer)acTop.get("type"));
						acTopElement.setInternalStatus(this.getInternalStatus(acTop, uid));
						ceKingdomElement.getAcTopList().add(acTopElement);
					}
				}
				membersList = membersMap.get(ce.getForwardCid().toString());
				if(null != membersList && membersList.size() > 0){
					for(Map<String, Object> members : membersList){
						memberElement = new ShowHotListDTO.MemberElement();
						memberElement.setUid((Long)members.get("uid"));
						memberElement.setNickName((String)members.get("nick_name"));
						memberElement.setAvatar(Constant.QINIU_DOMAIN + "/" + (String)members.get("avatar"));
						memberElement.setV_lv((Integer)members.get("v_lv"));
                        memberElement.setLevel((Integer)members.get("level"));
						ceKingdomElement.getMemberList().add(memberElement);
					}
				}
				if(null != topicTagMap.get(ce.getForwardCid().toString())){
					ceKingdomElement.setTags(topicTagMap.get(ce.getForwardCid().toString()));
	            }else{
	            	ceKingdomElement.setTags("");
	            }
				result.getHottestCeKingdomData().add(ceKingdomElement);
			}
		}
		*/
        if(null != contentList && contentList.size() > 0){
            ShowHotListDTO.HotContentElement contentElement = null;
            String lastFragmentImage = null;
            for(Content2Dto c : contentList){
                contentElement = new ShowHotListDTO.HotContentElement();
                contentElement.setSinceId(c.getUpdateTime().getTime());
                contentElement.setUid(c.getUid());
                userProfile = userProfileMap.get(c.getUid().toString());
                if(null != userProfile){
                    contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                    contentElement.setNickName(userProfile.getNickName());
                    contentElement.setV_lv(userProfile.getvLv());
                    contentElement.setLevel(userProfile.getLevel());
                }
                if(null != followMap.get(uid+"_"+c.getUid())){
                    contentElement.setIsFollowed(1);
                }else{
                    contentElement.setIsFollowed(0);
                }
                if(null != followMap.get(c.getUid()+"_"+uid)){
                    contentElement.setIsFollowMe(1);
                }else{
                    contentElement.setIsFollowMe(0);
                }
                contentElement.setType(c.getType());
                contentElement.setCreateTime(c.getCreateTime().getTime());
                contentElement.setUpdateTime(c.getUpdateTime().getTime());
                contentElement.setCid(c.getId());
                contentElement.setId(c.getId());
                contentElement.setTitle(c.getTitle());
                if(c.getConverImage()!=null && c.getConverImage().length()>0) {
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + c.getConverImage());
                }
                contentElement.setContent(c.getContent());
                contentElement.setReadCount(c.getReadCountDummy());
                contentElement.setLikeCount(c.getLikeCount());
                contentElement.setReviewCount(c.getReviewCount());
                contentElement.setFavoriteCount(c.getFavoriteCount());

                if(c.getType() == Specification.ArticleType.LIVE.index){
                    contentElement.setContent(c.getTitle());
                    contentElement.setTopicId(c.getForwardCid());
                    contentElement.setForwardCid(c.getForwardCid());
                    topic = topicMap.get(c.getForwardCid().toString());
                    if(null != topic){
                        contentElement.setContentType((Integer)topic.get("type"));
                        int internalStatust = this.getInternalStatus(topic, uid);
                        if(internalStatust==Specification.SnsCircle.OUT.index){
                        	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                        		internalStatust=Specification.SnsCircle.IN.index;
                        	}
                        }
                        contentElement.setInternalStatus(internalStatust);
                        contentElement.setPrice((Integer)topic.get("price"));
                        contentElement.setPriceRMB(exchangeKingdomPrice(contentElement.getPrice()));
                        contentElement.setShowPriceBrand(0);		//首页只显示RMB吊牌
                        contentElement.setShowRMBBrand(contentElement.getPriceRMB()>=minRmb?1:0);// 显示吊牌

                    }
                    lastFragment = lastFragmentMap.get(c.getForwardCid().toString());
                    if(null != lastFragment){
                        contentElement.setLastUpdateTime(((Date)lastFragment.get("create_time")).getTime());
                        contentElement.setLastType((Integer)lastFragment.get("type"));
                        contentElement.setLastContentType((Integer)lastFragment.get("content_type"));
                        contentElement.setLastFragment((String)lastFragment.get("fragment"));
                        lastFragmentImage = (String)lastFragment.get("fragment_image");
                        if (!StringUtils.isEmpty(lastFragmentImage)) {
                            contentElement.setLastFragmentImage(Constant.QINIU_DOMAIN + "/" + lastFragmentImage);
                        }
                        contentElement.setLastStatus((Integer)lastFragment.get("status"));
                        contentElement.setLastExtra((String)lastFragment.get("extra"));
                    }
                    if(null == topicMemberCountMap.get(c.getForwardCid().toString())){
                        contentElement.setFavoriteCount(1);//默认只有国王一个成员
                    }else{
                        contentElement.setFavoriteCount(topicMemberCountMap.get(c.getForwardCid().toString()).intValue()+1);
                    }
                    if(null != reviewCountMap.get(c.getForwardCid().toString())){
                        contentElement.setReviewCount(reviewCountMap.get(c.getForwardCid().toString()).intValue());
                    }else{
                        contentElement.setReviewCount(0);
                    }
                    if(null != liveFavouriteMap.get(c.getForwardCid().toString())){
                        contentElement.setFavorite(1);
                    }else{
                        contentElement.setFavorite(0);
                    }
                    if(null != topicTagMap.get(c.getForwardCid().toString())){
                        contentElement.setTags(topicTagMap.get(c.getForwardCid().toString()));
                    }else{
                        contentElement.setTags("");
                    }
                }

                result.getHottestContentData().add(contentElement);
            }
        }


        if(null != topList && topList.size() > 0){
            ShowHotListDTO.HotContentElement contentElement = null;
            String lastFragmentImage = null;
            for(Content2Dto c : topList){
                contentElement = new ShowHotListDTO.HotContentElement();
                contentElement.setSinceId(c.getHid());
                contentElement.setHid(c.getHid());
                contentElement.setOperationTime(c.getOperationTime());
                contentElement.setUid(c.getUid());
                userProfile = userProfileMap.get(c.getUid().toString());
                if (userProfile == null){
                    userProfile = userService.getUserProfileByUid(c.getUid());
                }

                if(null != userProfile){
                    contentElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                    contentElement.setNickName(userProfile.getNickName());
                    contentElement.setV_lv(userProfile.getvLv());
                    contentElement.setLevel(userProfile.getLevel());
                }
                if(null != followMap.get(uid+"_"+c.getUid())){
                    contentElement.setIsFollowed(1);
                }else{
                    contentElement.setIsFollowed(0);
                }
                if(null != followMap.get(c.getUid()+"_"+uid)){
                    contentElement.setIsFollowMe(1);
                }else{
                    contentElement.setIsFollowMe(0);
                }
                contentElement.setType(c.getType());
                contentElement.setCreateTime(c.getCreateTime().getTime());
                contentElement.setUpdateTime(c.getCreateTime().getTime());
                contentElement.setCid(c.getId());
                contentElement.setId(c.getId());
                contentElement.setTitle(c.getTitle());
                if(!StringUtils.isEmpty(c.getConverImage())){
                    contentElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + c.getConverImage());
                }
                contentElement.setContent(c.getContent());
                contentElement.setReadCount(c.getReadCountDummy());
                contentElement.setLikeCount(c.getLikeCount());
                contentElement.setReviewCount(c.getReviewCount());
                contentElement.setFavoriteCount(c.getFavoriteCount());

                if(c.getType() == Specification.ArticleType.LIVE.index){
                    contentElement.setContent(c.getTitle());
                    contentElement.setTopicId(c.getForwardCid());
                    contentElement.setForwardCid(c.getForwardCid());
                    topic = topicMap.get(c.getForwardCid().toString());
                    if(null != topic){
                        contentElement.setContentType((Integer)topic.get("type"));
                        int internalStatust = this.getInternalStatus(topic, uid);
                        if(internalStatust==Specification.SnsCircle.OUT.index){
                        	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                        		internalStatust=Specification.SnsCircle.IN.index;
                        	}
                        }
                        contentElement.setInternalStatus(internalStatust);
                        contentElement.setPrice((Integer)topic.get("price"));
                        contentElement.setPriceRMB(exchangeKingdomPrice(contentElement.getPrice()));
                        contentElement.setShowPriceBrand(0);		//首页只显示RMB吊牌
                        contentElement.setShowRMBBrand(contentElement.getPriceRMB()>=minRmb?1:0);// 显示吊牌

                    }
                    lastFragment = lastFragmentMap.get(c.getForwardCid().toString());
                    if(null != lastFragment){
                        contentElement.setLastUpdateTime(((Date)lastFragment.get("create_time")).getTime());
                        contentElement.setLastType((Integer)lastFragment.get("type"));
                        contentElement.setLastContentType((Integer)lastFragment.get("content_type"));
                        contentElement.setLastFragment((String)lastFragment.get("fragment"));
                        lastFragmentImage = (String)lastFragment.get("fragment_image");
                        if (!StringUtils.isEmpty(lastFragmentImage)) {
                            contentElement.setLastFragmentImage(Constant.QINIU_DOMAIN + "/" + lastFragmentImage);
                        }
                        contentElement.setLastStatus((Integer)lastFragment.get("status"));
                        contentElement.setLastExtra((String)lastFragment.get("extra"));
                    }
                    if(null == topicMemberCountMap.get(c.getForwardCid().toString())){
                        contentElement.setFavoriteCount(1);//默认只有国王一个成员
                    }else{
                        contentElement.setFavoriteCount(topicMemberCountMap.get(c.getForwardCid().toString()).intValue()+1);
                    }
                    if(null != reviewCountMap.get(c.getForwardCid().toString())){
                        contentElement.setReviewCount(reviewCountMap.get(c.getForwardCid().toString()).intValue());
                    }else{
                        contentElement.setReviewCount(0);
                    }
                    if(null != liveFavouriteMap.get(c.getForwardCid().toString())){
                        contentElement.setFavorite(1);
                    }else{
                        contentElement.setFavorite(0);
                    }
                    if(null != topicTagMap.get(c.getForwardCid().toString())){
                        contentElement.setTags(topicTagMap.get(c.getForwardCid().toString()));
                    }else{
                        contentElement.setTags("");
                    }
                }

                result.getTops().add(contentElement);
            }
        }
    }

    @Override
    public Response ceKingdomHotList(long sinceId, long uid, int vflag){
        if(sinceId <= 0){
            sinceId = Long.MAX_VALUE;
        }
        ShowHotCeKingdomListDTO result = new ShowHotCeKingdomListDTO();
        List<Long> blacklistUids = liveForContentJdbcDao.getBlacklist(uid);
        
        List<Content2Dto> ceKingdomList = contentMybatisDao.getHotContentByType(uid,sinceId, 1, 10,null,blacklistUids,"");
        //开始组装返回对象
        if(null != ceKingdomList && ceKingdomList.size() > 0){
            List<Long> uidList = new ArrayList<Long>();
            List<Long> topicIdList = new ArrayList<Long>();
            List<Long> subTopicIdList = new ArrayList<Long>();
            for(Content2Dto c : ceKingdomList){
                if(!uidList.contains(c.getUid())){
                    uidList.add(c.getUid());
                }
                if(!topicIdList.contains(c.getForwardCid())){
                    topicIdList.add(c.getForwardCid());
                }
            }

            //查出聚合王国的子王国和成员
            Map<String, List<Map<String, Object>>> acTopMap = new HashMap<String, List<Map<String, Object>>>();
            Map<String, List<Map<String, Object>>> membersMap = new HashMap<String, List<Map<String, Object>>>();
            if(topicIdList.size() > 0){
                List<Map<String, Object>> acTopList = null;
                List<Map<String, Object>> membersLsit = null;
                for(Long ceId : topicIdList){
                    acTopList = liveForContentJdbcDao.getAcTopicListByCeTopicId(ceId, 0, 3);
                    if(null != acTopList && acTopList.size() > 0){
                        acTopMap.put(ceId.toString(), acTopList);
                        for(Map<String, Object> acTopic : acTopList){
                            if(!subTopicIdList.contains((Long)acTopic.get("id"))){
                                subTopicIdList.add((Long)acTopic.get("id"));
                            }
                        }
                    }
                    membersLsit = liveForContentJdbcDao.getTopicMembersByTopicId(ceId, 0, 20);
                    if(null != membersLsit && membersLsit.size() > 0){
                        membersMap.put(ceId.toString(), membersLsit);
                    }
                }
            }
            //一次性查出子王国的内容表
            Map<String, Content> subTopicContentMap = new HashMap<String, Content>();
            if(topicIdList.size() > 0){
                List<Content> topicContentList = contentMybatisDao.getContentByTopicIds(subTopicIdList);
                if(null != topicContentList && topicContentList.size() > 0){
                    for(Content c : topicContentList){
                        subTopicContentMap.put(c.getForwardCid().toString(), c);
                    }
                }
            }
            //一次性查出所有王国详情
            Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
            List<Map<String,Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
            if(null != topicList && topicList.size() > 0){
                Long topicId = null;
                for(Map<String,Object>  map : topicList){
                    topicId = (Long)map.get("id");
                    topicMap.put(topicId.toString(), map);
                }
            }
            //一次性查出所有的用户信息
            Map<String, UserProfile> userProfileMap = new HashMap<String, UserProfile>();
            List<UserProfile> profileList = userService.getUserProfilesByUids(uidList);
            if(null != profileList && profileList.size() > 0){
                for(UserProfile up : profileList){
                    userProfileMap.put(up.getUid().toString(), up);
                }
            }
            //一次性查询关注信息
            Map<String, String> followMap = new HashMap<String, String>();
            List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
            if(null != userFollowList && userFollowList.size() > 0){
                for(UserFollow uf : userFollowList){
                    followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
                }
            }
            //一次性查询王国订阅信息
            Map<String, String> liveFavouriteMap = new HashMap<String, String>();
            List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(uid, topicIdList);
            if(null != liveFavouriteList && liveFavouriteList.size() > 0){
                for(Map<String,Object> lf : liveFavouriteList){
                    liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
                }
            }
            //一次性查询聚合王国的子王国数
            Map<String, Long> acCountMap = new HashMap<String, Long>();
            if(topicIdList.size() > 0){
                List<Map<String,Object>> acCountList = liveForContentJdbcDao.getTopicAggregationAcCountByTopicIds(topicIdList);
                if(null != acCountList && acCountList.size() > 0){
                    for(Map<String,Object> a : acCountList){
                        acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
                    }
                }
            }
            //一次性查询所有王国的成员数
            Map<String, Long> topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
            if(null == topicMemberCountMap){
                topicMemberCountMap = new HashMap<String, Long>();
            }
            //一次性查询王国的标签信息
            Map<String, String> topicTagMap = new HashMap<String, String>();
            List<Map<String, Object>> topicTagList = liveForContentJdbcDao.getTopicTagDetailListByTopicIds(topicIdList);
            if(null != topicTagList && topicTagList.size() > 0){
                long tid = 0;
                String tags = null;
                Long topicId = null;
                for(Map<String, Object> ttd : topicTagList){
                    topicId = (Long)ttd.get("topic_id");
                    if(topicId.longValue() != tid){
                        //先插入上一次
                        if(tid > 0 && !StringUtils.isEmpty(tags)){
                            topicTagMap.put(String.valueOf(tid), tags);
                        }
                        //再初始化新的
                        tid = topicId.longValue();
                        tags = null;
                    }
                    if(tags != null){
                        tags = tags + ";" + (String)ttd.get("tag");
                    }else{
                        tags = (String)ttd.get("tag");
                    }
                }
                if(tid > 0 && !StringUtils.isEmpty(tags)){
                    topicTagMap.put(String.valueOf(tid), tags);
                }
            }

            ShowHotCeKingdomListDTO.HotCeKingdomElement ceKingdomElement = null;
            ShowHotCeKingdomListDTO.AcTopElement acTopElement = null;
            ShowHotCeKingdomListDTO.MemberElement memberElement = null;
            List<Map<String, Object>> acTopList = null;
            List<Map<String, Object>> membersList = null;
            UserProfile userProfile = null;
            Map<String, Object> topic = null;
            Content subTopicContent = null;
            for(Content2Dto ce : ceKingdomList){
                ceKingdomElement = new ShowHotCeKingdomListDTO.HotCeKingdomElement();
                ceKingdomElement.setSinceId(ce.getHid());
                ceKingdomElement.setUid(ce.getUid());
                userProfile = userProfileMap.get(ce.getUid().toString());
                ceKingdomElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                ceKingdomElement.setNickName(userProfile.getNickName());
                ceKingdomElement.setV_lv(userProfile.getvLv());
                ceKingdomElement.setLevel(userProfile.getLevel());
                if(null != followMap.get(uid+"_"+ce.getUid())){
                    ceKingdomElement.setIsFollowed(1);
                }else{
                    ceKingdomElement.setIsFollowed(0);
                }
                if(null != followMap.get(ce.getUid()+"_"+uid)){
                    ceKingdomElement.setIsFollowMe(1);
                }else{
                    ceKingdomElement.setIsFollowMe(0);
                }
                if(null != liveFavouriteMap.get(ce.getForwardCid().toString())){
                    ceKingdomElement.setFavorite(1);
                }else{
                    ceKingdomElement.setFavorite(0);
                }
                ceKingdomElement.setTopicId(ce.getForwardCid());
                ceKingdomElement.setForwardCid(ce.getForwardCid());
                ceKingdomElement.setCid(ce.getId());
                ceKingdomElement.setId(ce.getId());
                topic = topicMap.get(ce.getForwardCid().toString());
                if(null != topic){
                    ceKingdomElement.setTitle((String)topic.get("title"));
                    ceKingdomElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
                    ceKingdomElement.setCreateTime(((Date)topic.get("create_time")).getTime());
                    ceKingdomElement.setUpdateTime((Long)topic.get("long_time"));
                    ceKingdomElement.setLastUpdateTime((Long)topic.get("long_time"));
                    ceKingdomElement.setContentType((Integer)topic.get("type"));
                    int internalStatust = this.getInternalStatus(topic, uid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    ceKingdomElement.setInternalStatus(internalStatust);
                }
                if(null != topicMemberCountMap.get(ce.getForwardCid().toString())){
                    ceKingdomElement.setFavoriteCount(topicMemberCountMap.get(ce.getForwardCid().toString()).intValue()+1);
                }else{
                    ceKingdomElement.setFavoriteCount(1);
                }

                if(null != acCountMap.get(ce.getForwardCid().toString())){
                    ceKingdomElement.setAcCount(acCountMap.get(ce.getForwardCid().toString()).intValue());
                }else{
                    ceKingdomElement.setAcCount(0);
                }
                acTopList = acTopMap.get(ce.getForwardCid().toString());
                if(null != acTopList && acTopList.size() > 0){
                    for(Map<String, Object> acTop : acTopList){
                        acTopElement = new ShowHotCeKingdomListDTO.AcTopElement();
                        acTopElement.setTopicId((Long)acTop.get("id"));
                        subTopicContent = subTopicContentMap.get(((Long)acTop.get("id")).toString());
                        if(null != subTopicContent){
                            acTopElement.setCid(subTopicContent.getId());
                        }
                        acTopElement.setTitle((String)acTop.get("title"));
                        acTopElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)acTop.get("live_image"));
                        acTopElement.setContentType((Integer)acTop.get("type"));
                        int internalStatust = this.getInternalStatus(acTop, uid);
                        if(internalStatust==Specification.SnsCircle.OUT.index){
                        	if( liveFavouriteMap.get(String.valueOf(acTop.get("id")))!=null){
                        		internalStatust=Specification.SnsCircle.IN.index;
                        	}
                        }
                        acTopElement.setInternalStatus(internalStatust);
                        ceKingdomElement.getAcTopList().add(acTopElement);
                    }
                }
                membersList = membersMap.get(ce.getForwardCid().toString());
                if(null != membersList && membersList.size() > 0){
                    for(Map<String, Object> members : membersList){
                        memberElement = new ShowHotCeKingdomListDTO.MemberElement();
                        memberElement.setUid((Long)members.get("uid"));
                        memberElement.setNickName((String)members.get("nick_name"));
                        memberElement.setAvatar(Constant.QINIU_DOMAIN + "/" + (String)members.get("avatar"));
                        memberElement.setV_lv((Integer)members.get("v_lv"));
                        memberElement.setLevel((Integer)members.get("level"));
                        ceKingdomElement.getMemberList().add(memberElement);
                    }
                }
                if(vflag > 0){
                    if(null != topicTagMap.get(ce.getForwardCid().toString())){
                        ceKingdomElement.setTags(topicTagMap.get(ce.getForwardCid().toString()));
                    }else{
                        ceKingdomElement.setTags("");
                    }
                }
                result.getHottestCeKingdomData().add(ceKingdomElement);
            }
        }

        return Response.success(result);
    }

    @Override
    public Response showBangDanList(long sinceId, int type,long currentUid, int vflag) {
    	List<Long> blacklistUids = liveForContentJdbcDao.getBlacklist(currentUid);
        if(null == blacklistUids){
        	blacklistUids = new ArrayList<Long>();
        }
    	
        BangDanDto bangDanDto = new BangDanDto();
        int searchType = 2;//找组织
        if(type == 1){
            searchType = 1;//找谁
        }
        double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));
        double minRmb =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_RMB_BRAND_MIN"));
        List<BillBoardDetails> showList = contentMybatisDao.getShowListPageByType((int)sinceId, searchType);
        if(null != showList && showList.size() > 0){
            //为减少在for循环里查询sql，这里统一将一些数据一次性查出使用
            List<Long> bidList = new ArrayList<Long>();
            for(BillBoardDetails bbd : showList){
                bidList.add(bbd.getBid());
            }
            List<Long> topicIdList = new ArrayList<Long>();
            List<Long> uidList = new ArrayList<Long>();
            List<Long> subBidList = new ArrayList<Long>();

            Map<String, List<BillBoardRelation>> relationMap = new HashMap<String, List<BillBoardRelation>>();
            //一次性查询所有榜单相关信息
            Map<String, BillBoard> bMap = new HashMap<String, BillBoard>();
            List<BillBoard> bList = contentMybatisDao.loadBillBoardByBids(bidList);
            if(null != bList && bList.size() > 0){
                List<BillBoardRelation> relationList = null;
                for(BillBoard bb : bList){
                    bMap.put(bb.getId().toString(), bb);

                    if(bb.getMode() == 0){
                        int pageSize = 0;//榜单集是所有
                        List<Long> noTargetIds = null;
                        if(bb.getType() == 1){
                            pageSize = 3;//王国
                            noTargetIds = liveForContentJdbcDao.getTopicIdsByUids(blacklistUids);
                        }else if(bb.getType() == 2){
                            pageSize = 5;//人
                            noTargetIds = blacklistUids;
                        }
                        
                        relationList = contentMybatisDao.getRelationListPage(bb.getId(), -1, pageSize, noTargetIds);
                        if(null != relationList && relationList.size() > 0){
                            relationMap.put(bb.getId().toString(), relationList);
                            for(BillBoardRelation bbr : relationList){
                                if(bbr.getType() == 1){//王国
                                    if(!topicIdList.contains(bbr.getTargetId())){
                                        topicIdList.add(bbr.getTargetId());
                                    }
                                }else if(bbr.getType() == 2){//人
                                    if(!uidList.contains(bbr.getTargetId())){
                                        uidList.add(bbr.getTargetId());
                                    }
                                }else if(bbr.getType() == 3){//榜单
                                    if(!subBidList.contains(bbr.getTargetId())){
                                        subBidList.add(bbr.getTargetId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //王国相关
            Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();//王国信息
            Map<String, String> liveFavouriteMap = new HashMap<String, String>();//王国订阅信息
            Map<String, Content> topicContentMap = new HashMap<String, Content>();//王国内容表信息
            Map<String, Long> reviewCountMap = new HashMap<String, Long>();//王国评论信息
            Map<String, Long> topicMemberCountMap = null;//王国成员数信息
            Map<String, String> topicTagMap = new HashMap<String, String>();//一次性查询王国的标签信息
            if(topicIdList.size() > 0){
                List<Map<String, Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
                if(null != topicList && topicList.size() > 0){
                    Long uid = null;
                    for(Map<String, Object> m : topicList){
                        topicMap.put(String.valueOf(m.get("id")), m);
                        uid = (Long)m.get("uid");
                        if(!uidList.contains(uid)){
                            uidList.add(uid);
                        }
                    }
                }
                List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
                if(null != liveFavouriteList && liveFavouriteList.size() > 0){
                    for(Map<String,Object> lf : liveFavouriteList){
                        liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
                    }
                }
                List<Content> topicContentList = contentMybatisDao.getContentByTopicIds(topicIdList);
                if(null != topicContentList && topicContentList.size() > 0){
                    for(Content c : topicContentList){
                        topicContentMap.put(c.getForwardCid().toString(), c);
                    }
                }
                List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
                if(null != tcList && tcList.size() > 0){
                    for(Map<String, Object> m : tcList){
                        reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
                    }
                }
                topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
                List<Map<String, Object>> topicTagList = liveForContentJdbcDao.getTopicTagDetailListByTopicIds(topicIdList);
                if(null != topicTagList && topicTagList.size() > 0){
                    long tid = 0;
                    String tags = null;
                    Long topicId = null;
                    for(Map<String, Object> ttd : topicTagList){
                        topicId = (Long)ttd.get("topic_id");
                        if(topicId.longValue() != tid){
                            //先插入上一次
                            if(tid > 0 && !StringUtils.isEmpty(tags)){
                                topicTagMap.put(String.valueOf(tid), tags);
                            }
                            //再初始化新的
                            tid = topicId.longValue();
                            tags = null;
                        }
                        if(tags != null){
                            tags = tags + ";" + (String)ttd.get("tag");
                        }else{
                            tags = (String)ttd.get("tag");
                        }
                    }
                    if(tid > 0 && !StringUtils.isEmpty(tags)){
                        topicTagMap.put(String.valueOf(tid), tags);
                    }
                }
            }
            if(null == topicMemberCountMap){
                topicMemberCountMap = new HashMap<String, Long>();
            }
            //人相关
            Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();//用户信息
            Map<String, String> followMap = new HashMap<String, String>();//关注信息
            if(uidList.size() > 0){
                List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
                if(null != userList && userList.size() > 0){
                    for(UserProfile u : userList){
                        userMap.put(u.getUid().toString(), u);
                    }
                }
                List<UserFollow> userFollowList = userService.getAllFollows(currentUid, uidList);
                if(null != userFollowList && userFollowList.size() > 0){
                    for(UserFollow uf : userFollowList){
                        followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
                    }
                }
            }
            //子榜单相关
            Map<String, BillBoard> subBillboardMap = new HashMap<String, BillBoard>();
            if(subBidList.size() > 0){
                List<BillBoard> subList = contentMybatisDao.loadBillBoardByBids(subBidList);
                if(null != subList && subList.size() > 0){
                    for(BillBoard bb : subList){
                        subBillboardMap.put(bb.getId().toString(), bb);
                    }
                }
            }

            BillBoard billBoard = null;
            BangDanDto.BangDanData bangDanData = null;
            BangDanDto.BangDanData.BangDanInnerData bangDanInnerData = null;
            List<BillBoardRelation> relationList = null;
            Map<String,Object> topic = null;
            UserProfile userProfile = null;
            Content topicContent = null;
            BillBoard subBillBoard = null;
            for(BillBoardDetails bbd : showList){
                billBoard = bMap.get(bbd.getBid().toString());
                if(null == billBoard){
                    continue;
                }
                bangDanData = new BangDanDto.BangDanData();
                bangDanData.setSummary(billBoard.getSummary());
                bangDanData.setTitle(billBoard.getName());
                bangDanData.setListId(billBoard.getId());
                if(!StringUtils.isEmpty(billBoard.getImage())){
                    bangDanData.setCoverImage(Constant.QINIU_DOMAIN + "/" + billBoard.getImage());
                }
                bangDanData.setIsShowName(billBoard.getShowName());
                bangDanData.setCoverWidth(billBoard.getImgWidth());
                bangDanData.setCoverHeight(billBoard.getImgHeight());
                bangDanData.setBgColor(billBoard.getBgColor());
                // 是否是榜单集合类型
                bangDanData.setType((billBoard.getType()==3)?2:1);
                bangDanData.setSinceId(bbd.getSort());
                bangDanData.setSubType(billBoard.getType());


                //获取榜单里的具体内容（王国3个，人5个，如果是榜单集则显示所有榜单）
                int pageSize = 0;//榜单集是所有
                if(billBoard.getType() == 1){
                    pageSize = 3;//王国
                }else if(billBoard.getType() == 2){
                    pageSize = 5;//人
                }
                if(billBoard.getMode() > 0){//自动榜单
                    this.buildAutoBillBoardSimple(bangDanData, billBoard.getId(), billBoard.getMode(), currentUid, billBoard.getType(), pageSize,blacklistUids);
                }else{//人工榜单
                    relationList = relationMap.get(billBoard.getId().toString());
                    if(null != relationList && relationList.size() > 0){
                        for(BillBoardRelation billBoardRelation : relationList){
                            bangDanInnerData = new BangDanDto.BangDanData.BangDanInnerData();
                            long targetId = billBoardRelation.getTargetId();
                            bangDanInnerData.setSubType(billBoardRelation.getType());
                            if(billBoardRelation.getType()==1){// 王国数据
                                bangDanInnerData.setSubListId(billBoard.getId());
                                topic = topicMap.get(String.valueOf(targetId));
                                if(null == topic){
                                    log.info("王国[id="+targetId+"]不存在");
                                    continue;
                                }
                                long uid = (Long)topic.get("uid");
                                bangDanInnerData.setUid(uid);
                                userProfile = userMap.get(String.valueOf(uid));
                                if(null == userProfile){
                                    log.info("用户[uid="+uid+"]不存在");
                                    continue;
                                }
                                bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                                bangDanInnerData.setNickName(userProfile.getNickName());
                                bangDanInnerData.setV_lv(userProfile.getvLv());
                                bangDanInnerData.setLevel(userProfile.getLevel());
                                if(null != followMap.get(currentUid+"_"+uid)){
                                    bangDanInnerData.setIsFollowed(1);
                                }else{
                                    bangDanInnerData.setIsFollowed(0);
                                }
                                if(null != followMap.get(uid+"_"+currentUid)){
                                    bangDanInnerData.setIsFollowMe(1);
                                }else{
                                    bangDanInnerData.setIsFollowMe(0);
                                }
                                bangDanInnerData.setContentType((Integer)topic.get("type"));
                                if(null != liveFavouriteMap.get(String.valueOf(targetId))){
                                    bangDanInnerData.setFavorite(1);
                                }else{
                                    bangDanInnerData.setFavorite(0);
                                }
                                topicContent = topicContentMap.get(String.valueOf(targetId));
                                if(null == topicContent){
                                    continue;
                                }
                                bangDanInnerData.setPrice((Integer)topic.get("price"));

                                bangDanInnerData.setPriceRMB(exchangeKingdomPrice(bangDanInnerData.getPrice()));
                                bangDanInnerData.setShowPriceBrand(bangDanInnerData.getPrice()!=null && bangDanInnerData.getPrice()>=minPrice?1:0);
                                bangDanInnerData.setShowRMBBrand(0);// 显示吊牌不显示RMB吊牌。

                                bangDanInnerData.setId(topicContent.getId());
                                bangDanInnerData.setCid(topicContent.getId());
                                bangDanInnerData.setTopicId(targetId);
                                bangDanInnerData.setForwardCid(targetId);
                                bangDanInnerData.setTitle((String)topic.get("title"));
                                bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
                                int internalStatust = this.getInternalStatus(topic, currentUid);
                                if(internalStatust==Specification.SnsCircle.OUT.index){
                                	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                                		internalStatust=Specification.SnsCircle.IN.index;
                                	}
                                }
                                bangDanInnerData.setInternalStatus(internalStatust);
                                if(null != topicMemberCountMap.get(String.valueOf(targetId))){
                                    bangDanInnerData.setFavoriteCount(topicMemberCountMap.get(String.valueOf(targetId)).intValue() + 1);
                                }else{
                                    bangDanInnerData.setFavoriteCount(1);
                                }
                                bangDanInnerData.setReadCount(topicContent.getReadCountDummy());
                                bangDanInnerData.setLikeCount(topicContent.getLikeCount());
                                if(null != reviewCountMap.get(String.valueOf(targetId))){
                                    bangDanInnerData.setReviewCount(reviewCountMap.get(String.valueOf(targetId)).intValue());
                                }else{
                                    bangDanInnerData.setReviewCount(0);
                                }
                                if(null != topicTagMap.get(String.valueOf(targetId))){
                                    bangDanInnerData.setTags(topicTagMap.get(String.valueOf(targetId)));
                                }else{
                                    bangDanInnerData.setTags("");
                                }
                            }else if(billBoardRelation.getType()==2){// 人
                                bangDanInnerData.setSubListId(billBoard.getId());
                                bangDanInnerData.setUid(targetId);
                                userProfile = userMap.get(String.valueOf(targetId));
                                if(null == userProfile){
                                    log.info("用户[uid="+targetId+"]不存在");
                                    continue;
                                }
                                bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                                bangDanInnerData.setNickName(userProfile.getNickName());
                                bangDanInnerData.setV_lv(userProfile.getvLv());
                                bangDanInnerData.setLevel(userProfile.getLevel());
                                if(null != followMap.get(currentUid+"_"+targetId)){
                                    bangDanInnerData.setIsFollowed(1);
                                }else{
                                    bangDanInnerData.setIsFollowed(0);
                                }
                                if(null != followMap.get(targetId+"_"+currentUid)){
                                    bangDanInnerData.setIsFollowMe(1);
                                }else{
                                    bangDanInnerData.setIsFollowMe(0);
                                }
                                bangDanInnerData.setIntroduced(userProfile.getIntroduced());
                            }else if(billBoardRelation.getType()==3){// 榜单集合
                                subBillBoard = subBillboardMap.get(String.valueOf(targetId));
                                if(null == subBillBoard){
                                    continue;
                                }
                                bangDanInnerData.setSubListId(subBillBoard.getId());
                                if(!StringUtils.isEmpty(subBillBoard.getImage())){
                                    bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + subBillBoard.getImage());
                                }
                                bangDanInnerData.setTitle(subBillBoard.getName());
                            }
                            bangDanData.getSubList().add(bangDanInnerData);
                        }
                    }
                }

                //榜单集合
                List<Map<String,Object>> topicList = this.liveForContentJdbcDao.getTopPricedKingdomList( 1, 3, blacklistUids);
                List<BasicKingdomInfo> buildResult = this.kingdomBuider.buildKingdoms(topicList, currentUid);
                bangDanDto.setListPricedTopic(buildResult);

                bangDanDto.getListData().add(bangDanData);
            }
        }

        if(vflag == 0){
            if(bangDanDto.getListData().size() > 0){
                for(BangDanDto.BangDanData bdd : bangDanDto.getListData()){
                    if(bdd.getSubList().size() > 0){
                        for(BangDanDto.BangDanData.BangDanInnerData data : bdd.getSubList()){
                            data.setTags(null);
                        }
                    }
                }
            }
        }

        return Response.success(bangDanDto);
    }

    public Response showBangDanList2(int type,long currentUid) {
        BangDanDto bangDanDto = new BangDanDto();
        List<BillBoardRelation> billBoardRelations = contentMybatisDao.loadBillBoardRelations(type);
        List<Long> bids = contentMybatisDao.loadBillBoardCover(type);
        List<BillBoard> data = contentMybatisDao.loadBillBoardByBids(bids);
        for(BillBoard billBoard : data){
            BangDanDto.BangDanData bangDanData = new BangDanDto.BangDanData();
            bangDanData.setSummary(billBoard.getSummary());
            bangDanData.setTitle(billBoard.getName());
            bangDanData.setListId(billBoard.getId());
            bangDanData.setCoverImage(Constant.QINIU_DOMAIN + "/" + billBoard.getImage());
            bangDanData.setIsShowName(billBoard.getShowName());
            bangDanData.setCoverWidth(billBoard.getImgWidth());
            bangDanData.setCoverHeight(billBoard.getImgHeight());
            bangDanData.setBgColor(billBoard.getBgColor());
            // 是否是榜单集合类型
            bangDanData.setType((billBoard.getType()==3)?2:1);
            bangDanData.setSinceId(0);
            bangDanData.setSubType(billBoard.getType());
            for(BillBoardRelation billBoardRelation : billBoardRelations){
                // 关系对应
                if(billBoard.getId()==billBoardRelation.getSourceId()){
                    BangDanDto.BangDanData.BangDanInnerData bangDanInnerData = new BangDanDto.BangDanData.BangDanInnerData();
                    long targetId = billBoardRelation.getTargetId();
                    bangDanInnerData.setSubType(billBoardRelation.getType());
                    if(billBoardRelation.getType()==1){
                        // 王国数据
                        Map map = billBoardJdbcDao.getTopicById(targetId);
                        String title = map.get("title").toString();
                        long uid = Long.valueOf(map.get("uid").toString());
                        int contentType = Integer.valueOf(map.get("type").toString());
                        String liveImage = map.get("live_image").toString();
                        bangDanInnerData.setSubListId(billBoardRelation.getId());
                        bangDanInnerData.setUid(uid);
                        UserProfile userProfile = userService.getUserProfileByUid(uid);
                        bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                        bangDanInnerData.setNickName(userProfile.getNickName());
                        bangDanInnerData.setV_lv(userProfile.getvLv());
                        bangDanInnerData.setLevel(userProfile.getLevel());
                        int isFollowed = userService.isFollow(uid,currentUid);
                        bangDanInnerData.setIsFollowed(isFollowed);
                        int isFollowMe = userService.isFollow(currentUid,uid);
                        bangDanInnerData.setIsFollowMe(isFollowMe);
                        bangDanInnerData.setContentType(contentType);
                        bangDanInnerData.setFavorite(contentMybatisDao.isFavorite(targetId,currentUid));
                        Content content = com.me2me.common.utils.Lists.getSingle(contentMybatisDao.getContentByTopicId(targetId));
                        bangDanInnerData.setId(content.getId());
                        bangDanInnerData.setCid(content.getId());
                        bangDanInnerData.setTopicId(targetId);
                        bangDanInnerData.setForwardCid(targetId);
                        bangDanInnerData.setTitle(title);
                        bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + liveImage);
                        bangDanInnerData.setInternalStatus(getInternalStatus(map,currentUid));
                        bangDanInnerData.setFavoriteCount(content.getFavoriteCount()+1);
                        bangDanInnerData.setReadCount(content.getReadCountDummy());
                        bangDanInnerData.setLikeCount(content.getLikeCount());
                        bangDanInnerData.setReviewCount(content.getReviewCount());
                    }else if(billBoardRelation.getType()==2){
                        // 人
                        bangDanInnerData.setUid(targetId);
                        UserProfile userProfile = userService.getUserProfileByUid(targetId);
                        bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                        bangDanInnerData.setNickName(userProfile.getNickName());
                        bangDanInnerData.setV_lv(userProfile.getvLv());
                        bangDanInnerData.setLevel(userProfile.getLevel());
                        int isFollowed = userService.isFollow(targetId,currentUid);
                        bangDanInnerData.setIsFollowed(isFollowed);
                        int isFollowMe = userService.isFollow(currentUid,targetId);
                        bangDanInnerData.setIsFollowMe(isFollowMe);
                        bangDanInnerData.setId(billBoardRelation.getId());
                        bangDanInnerData.setIntroduced(userProfile.getIntroduced());
                    }else if(billBoardRelation.getType()==3){
                        // 榜单集合
                        BillBoard bb = contentMybatisDao.loadBillBoardById(targetId);
                        bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + billBoard.getImage());
                        bangDanInnerData.setId(bb.getId());
                        bangDanInnerData.setTitle(bb.getName());
                        bangDanInnerData.setSubListId(bb.getId());
                    }
                    bangDanData.getSubList().add(bangDanInnerData);
                }
            }
            bangDanDto.getListData().add(bangDanData);
        }
        return Response.success(bangDanDto);
    }

    @Override
    public List<Map<String, Object>> queryEvery(String sql){
        sql = sql.trim();
        if(null == sql || "".equals(sql)
                || !sql.startsWith("select")){
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> list = liveForContentJdbcDao.queryBySql(sql);
        if(null != list && list.size() > 0){
            Map<String, Object> map = null;
            for(Map<String, Object> m : list){
                map = new HashMap<String, Object>();
                for(Map.Entry<String, Object> entry : m.entrySet()){
                    map.put(entry.getKey(), entry.getValue());
                }
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public void executeSql(String sql){
        liveForContentJdbcDao.executeSql(sql);
    }

    @Override
    public void insertBillboardList(List<BillBoardList> insertList, String key){
        if(null == insertList || insertList.size() == 0 || StringUtils.isEmpty(key)){
            return;
        }
        //先删掉原有的
        contentMybatisDao.deleteBillBoardListByKey(key);
        //再插入新的数据
        for(BillBoardList bbl : insertList){
            contentMybatisDao.insertBillBoardList(bbl);
        }
    }

    @Override
    public Response showListDetail(long currentUid, long bid,long sinceId, int vflag) {
        BillBoardDetailsDto billBoardDetailsDto = new BillBoardDetailsDto();

        List<Long> blacklistUids = liveForContentJdbcDao.getBlacklist(currentUid);
        if(null == blacklistUids){
        	blacklistUids = new ArrayList<Long>();
        }
        
        BillBoard billBoard = contentMybatisDao.loadBillBoardById(bid);
        if(null == billBoard){
            return Response.failure(ResponseStatus.DATA_DOES_NOT_EXIST.status, ResponseStatus.DATA_DOES_NOT_EXIST.message);
        }

        double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));

        // 加载榜单基本信息
        billBoardDetailsDto.setSummary(billBoard.getSummary());
        billBoardDetailsDto.setTitle(billBoard.getName());
        billBoardDetailsDto.setListId(billBoard.getId());
        if(!StringUtils.isEmpty(billBoard.getImage())){
            billBoardDetailsDto.setCoverImage(Constant.QINIU_DOMAIN + "/" + billBoard.getImage());
        }
        billBoardDetailsDto.setCoverWidth(billBoard.getImgWidth());
        billBoardDetailsDto.setCoverHeight(billBoard.getImgHeight());
        billBoardDetailsDto.setBgColor(billBoard.getBgColor());
        billBoardDetailsDto.setType(billBoard.getType()==3?2:1);
        billBoardDetailsDto.setSubType(billBoard.getType());

        if(billBoard.getMode() > 0){//自动榜单
            this.buildAutoBillBoardDetails(billBoardDetailsDto, billBoard.getMode(), sinceId, currentUid, billBoard.getType(), blacklistUids);
        }else{//人工榜单
            // 记载榜单旗下的列表数据
        	List<Long> noTargetIds = null;
        	if(billBoard.getType() == 1){//王国
        		noTargetIds = liveForContentJdbcDao.getTopicIdsByUids(blacklistUids);
        	}else if(billBoard.getType() == 2){//人
        		noTargetIds = blacklistUids;
        	}
            List<BillBoardRelation> data =  contentMybatisDao.loadBillBoardRelationsBySinceId(sinceId,bid,noTargetIds);
            if(null != data && data.size() > 0){
                //尽量不再循环里查sql，故将所需sql在循环外统一查询出来 -- modify by zcl
                List<Long> uidList = new ArrayList<Long>();//人
                List<Long> topicIdList = new ArrayList<Long>();//王国
                if(billBoard.getType() == 1){//王国
                    for(BillBoardRelation bbr : data){
                        if(!topicIdList.contains(bbr.getTargetId())){
                            topicIdList.add(bbr.getTargetId());
                        }
                    }
                }else if(billBoard.getType() == 2){//人
                    for(BillBoardRelation bbr : data){
                        if(!uidList.contains(bbr.getTargetId())){
                            uidList.add(bbr.getTargetId());
                        }
                    }
                }
                //王国相关
                Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();//王国信息
                Map<String, String> liveFavouriteMap = new HashMap<String, String>();//王国订阅信息
                Map<String, Content> topicContentMap = new HashMap<String, Content>();//王国内容表信息
                Map<String, Long> reviewCountMap = new HashMap<String, Long>();//王国评论信息
                Map<String, Long> topicMemberCountMap = null;//王国成员信息
                Map<String, String> topicTagMap = new HashMap<String, String>();
                if(topicIdList.size() > 0){
                    List<Map<String, Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
                    if(null != topicList && topicList.size() > 0){
                        Long uid = null;
                        for(Map<String, Object> m : topicList){
                            topicMap.put(String.valueOf(m.get("id")), m);
                            uid = (Long)m.get("uid");
                            if(!uidList.contains(uid)){
                                uidList.add(uid);
                            }
                        }
                    }
                    List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
                    if(null != liveFavouriteList && liveFavouriteList.size() > 0){
                        for(Map<String,Object> lf : liveFavouriteList){
                            liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
                        }
                    }
                    List<Content> topicContentList = contentMybatisDao.getContentByTopicIds(topicIdList);
                    if(null != topicContentList && topicContentList.size() > 0){
                        for(Content c : topicContentList){
                            topicContentMap.put(c.getForwardCid().toString(), c);
                        }
                    }
                    List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
                    if(null != tcList && tcList.size() > 0){
                        for(Map<String, Object> m : tcList){
                            reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
                        }
                    }
                    topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
                    List<Map<String, Object>> topicTagList = liveForContentJdbcDao.getTopicTagDetailListByTopicIds(topicIdList);
                    if(null != topicTagList && topicTagList.size() > 0){
                        long tid = 0;
                        String tags = null;
                        Long topicId = null;
                        for(Map<String, Object> ttd : topicTagList){
                            topicId = (Long)ttd.get("topic_id");
                            if(topicId.longValue() != tid){
                                //先插入上一次
                                if(tid > 0 && !StringUtils.isEmpty(tags)){
                                    topicTagMap.put(String.valueOf(tid), tags);
                                }
                                //再初始化新的
                                tid = topicId.longValue();
                                tags = null;
                            }
                            if(tags != null){
                                tags = tags + ";" + (String)ttd.get("tag");
                            }else{
                                tags = (String)ttd.get("tag");
                            }
                        }
                        if(tid > 0 && !StringUtils.isEmpty(tags)){
                            topicTagMap.put(String.valueOf(tid), tags);
                        }
                    }
                }
                if(null == topicMemberCountMap){
                    topicMemberCountMap = new HashMap<String, Long>();
                }
                //人相关
                Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();//用户信息
                Map<String, String> followMap = new HashMap<String, String>();//关注信息
                if(uidList.size() > 0){
                    List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
                    if(null != userList && userList.size() > 0){
                        for(UserProfile u : userList){
                            userMap.put(u.getUid().toString(), u);
                        }
                    }
                    List<UserFollow> userFollowList = userService.getAllFollows(currentUid, uidList);
                    if(null != userFollowList && userFollowList.size() > 0){
                        for(UserFollow uf : userFollowList){
                            followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
                        }
                    }
                }

                Map<String, Object> topic = null;
                UserProfile userProfile = null;
                Content topicContent = null;
                for(BillBoardRelation billBoardRelation : data){
                    BillBoardDetailsDto.InnerDetailData bangDanInnerData = new BillBoardDetailsDto.InnerDetailData();
                    long targetId = billBoardRelation.getTargetId();
                    int type = billBoardRelation.getType();
                    bangDanInnerData.setSubType(type);
                    bangDanInnerData.setSinceId(billBoardRelation.getSort());
                    if(type==1){// 王国
                        topic = topicMap.get(String.valueOf(targetId));
                        if(null == topic){
                            log.info("王国[id="+targetId+"]不存在");
                            continue;
                        }
                        long uid = Long.valueOf(topic.get("uid").toString());
                        bangDanInnerData.setUid(uid);
                        userProfile = userMap.get(String.valueOf(uid));
                        if(null == userProfile){
                            log.info("用户[uid="+uid+"]不存在");
                            continue;
                        }
                        bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                        bangDanInnerData.setNickName(userProfile.getNickName());
                        bangDanInnerData.setV_lv(userProfile.getvLv());
                        bangDanInnerData.setLevel(userProfile.getLevel());
                        if(null != followMap.get(currentUid+"_"+uid)){
                            bangDanInnerData.setIsFollowed(1);
                        }else{
                            bangDanInnerData.setIsFollowed(0);
                        }
                        if(null != followMap.get(uid+"_"+currentUid)){
                            bangDanInnerData.setIsFollowMe(1);
                        }else{
                            bangDanInnerData.setIsFollowMe(0);
                        }
                        bangDanInnerData.setContentType((Integer)topic.get("type"));
                        bangDanInnerData.setPrice((Integer)topic.get("price"));		//2.2.7王国价值
                        if(null != liveFavouriteMap.get(String.valueOf(targetId))){
                            bangDanInnerData.setFavorite(1);
                        }else{
                            bangDanInnerData.setFavorite(0);
                        }
                        topicContent = topicContentMap.get(String.valueOf(targetId));
                        bangDanInnerData.setId(topicContent.getId());
                        bangDanInnerData.setCid(topicContent.getId());
                        bangDanInnerData.setTopicId(targetId);
                        bangDanInnerData.setForwardCid(targetId);
                        bangDanInnerData.setTitle((String)topic.get("title"));
                        bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
                        int internalStatust = this.getInternalStatus(topic,currentUid);
                        if(internalStatust==Specification.SnsCircle.OUT.index){
                        	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                        		internalStatust=Specification.SnsCircle.IN.index;
                        	}
                        }
                        bangDanInnerData.setInternalStatus(internalStatust);
                        if(null != topicMemberCountMap.get(String.valueOf(targetId))){
                            bangDanInnerData.setFavoriteCount(topicMemberCountMap.get(String.valueOf(targetId)).intValue()+1);
                        }else{
                            bangDanInnerData.setFavoriteCount(1);
                        }
                        bangDanInnerData.setReadCount(topicContent.getReadCountDummy());
                        bangDanInnerData.setLikeCount(topicContent.getLikeCount());
                        if(null != reviewCountMap.get(String.valueOf(targetId))){
                            bangDanInnerData.setReviewCount(reviewCountMap.get(String.valueOf(targetId)).intValue());
                        }else{
                            bangDanInnerData.setReviewCount(0);
                        }
                        if(null != topicTagMap.get(String.valueOf(targetId))){
                            bangDanInnerData.setTags(topicTagMap.get(String.valueOf(targetId)));
                        }else{
                            bangDanInnerData.setTags("");
                        }
                        bangDanInnerData.setPrice((Integer)topic.get("price"));
                        bangDanInnerData.setPriceRMB(exchangeKingdomPrice(bangDanInnerData.getPrice()));
                        bangDanInnerData.setShowPriceBrand(bangDanInnerData.getPrice()!=null && bangDanInnerData.getPrice()>=minPrice?1:0);
                        bangDanInnerData.setShowRMBBrand(0);// 显示吊牌不显示RMB吊牌。
                    }else if(type==2){//人
                        bangDanInnerData.setUid(targetId);
                        userProfile = userMap.get(String.valueOf(targetId));
                        if(null == userProfile){
                        	log.info("用户[uid="+targetId+"]不存在");
                            continue;
                        }
                        bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                        bangDanInnerData.setNickName(userProfile.getNickName());
                        bangDanInnerData.setV_lv(userProfile.getvLv());
                        bangDanInnerData.setLevel(userProfile.getLevel());
                        if(null != followMap.get(currentUid+"_"+targetId)){
                            bangDanInnerData.setIsFollowed(1);
                        }else{
                            bangDanInnerData.setIsFollowed(0);
                        }
                        if(null != followMap.get(targetId+"_"+currentUid)){
                            bangDanInnerData.setIsFollowMe(1);
                        }else{
                            bangDanInnerData.setIsFollowMe(0);
                        }
                        bangDanInnerData.setIntroduced(userProfile.getIntroduced());
                    }else if(type==3){// 榜单
                        BillBoard bb = contentMybatisDao.loadBillBoardById(targetId);
                        if(!StringUtils.isEmpty(bb.getImage())){
                            bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + bb.getImage());
                        }
                        bangDanInnerData.setId(bb.getId());
                        bangDanInnerData.setTitle(bb.getName());
                    }
                    billBoardDetailsDto.getSubList().add(bangDanInnerData);
                }
            }
        }

        if(vflag == 0){
            if(billBoardDetailsDto.getSubList().size() > 0){
                for(BillBoardDetailsDto.InnerDetailData data : billBoardDetailsDto.getSubList()){
                    data.setTags(null);
                }
            }
        }

        return Response.success(billBoardDetailsDto);
    }

    private List<BillBoardListDTO> getAutoBillBoardList(int mode, long sinceId, int pageSize, List<Long> blacklistUids){
        List<BillBoardListDTO> result = null;
        String currentCacheKey = null;
        List<BillBoardList> bbList = null;
        List<Long> topicIds = null;
        switch(mode){
            case 1://最活跃的米汤新鲜人
                //实时统计
                if(sinceId < 0){
                    sinceId = Long.MAX_VALUE;
                }
                result = liveForContentJdbcDao.getActiveUserBillboard(sinceId, pageSize, blacklistUids);
                break;
            case 2://最受追捧的米汤大咖
                currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_POPULAR_PEOPLE);
                if(StringUtils.isEmpty(currentCacheKey)){
                    currentCacheKey = Constant.BILLBOARD_KEY_TARGET1;
                }
                bbList = contentMybatisDao.getBillBoardListPage(Constant.BILLBOARD_KEY_POPULAR_PEOPLE+currentCacheKey, (int)sinceId, pageSize, blacklistUids);
                result = this.genBBLDto(bbList);
                break;
            case 3://最爱叨逼叨的话痨王国
                currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_JAY_PEOPLE);
                if(StringUtils.isEmpty(currentCacheKey)){
                    currentCacheKey = Constant.BILLBOARD_KEY_TARGET1;
                }
                bbList = contentMybatisDao.getBillBoardListPage(Constant.BILLBOARD_KEY_JAY_PEOPLE+currentCacheKey, (int)sinceId, pageSize, blacklistUids);
                result = this.genBBLDto(bbList);
                break;
            case 4://这里的互动最热闹
                //实时统计
                if(sinceId < 0){
                    sinceId = Long.MAX_VALUE;
                }
                result = liveForContentJdbcDao.getInteractionHottestKingdomBillboard(sinceId, pageSize, blacklistUids);
                break;
            case 5://最丰富多彩的王国
                currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_COLOURFUL_KINGDOM);
                if(StringUtils.isEmpty(currentCacheKey)){
                    currentCacheKey = Constant.BILLBOARD_KEY_TARGET1;
                }
                topicIds = liveForContentJdbcDao.getTopicIdsByUids(blacklistUids);
                bbList = contentMybatisDao.getBillBoardListPage(Constant.BILLBOARD_KEY_COLOURFUL_KINGDOM+currentCacheKey, (int)sinceId, pageSize, topicIds);
                result = this.genBBLDto(bbList);
                break;
            case 6://求安慰的孤独王国
                currentCacheKey = cacheService.get(Constant.BILLBOARD_KEY_LONELY_KINGDOM);
                if(StringUtils.isEmpty(currentCacheKey)){
                    currentCacheKey = Constant.BILLBOARD_KEY_TARGET1;
                }
                topicIds = liveForContentJdbcDao.getTopicIdsByUids(blacklistUids);
                bbList = contentMybatisDao.getBillBoardListPage(Constant.BILLBOARD_KEY_LONELY_KINGDOM+currentCacheKey, (int)sinceId, pageSize, topicIds);
                result = this.genBBLDto(bbList);
                break;
            case 7://最新更新的王国
                //实时统计
                if(sinceId < 0){
                    sinceId = Long.MAX_VALUE;
                }
                result = liveForContentJdbcDao.getLivesByUpdateTime(sinceId, pageSize, blacklistUids);
                break;
            case 8://新注册的帅哥
                //实时统计
                if(sinceId < 0){
                    sinceId = Long.MAX_VALUE;
                }
                result = liveForContentJdbcDao.getNewPeople(1, sinceId, pageSize, blacklistUids);
                break;
            case 9://新注册的美女
                //实时统计
                if(sinceId < 0){
                    sinceId = Long.MAX_VALUE;
                }
                result = liveForContentJdbcDao.getNewPeople(0, sinceId, pageSize, blacklistUids);
                break;
            case 10://新注册的用户（无所谓有没有王国）
                //实时统计
                if(sinceId < 0){
                    sinceId = Long.MAX_VALUE;
                }
                result = liveForContentJdbcDao.getNewRegisterUsers(sinceId, pageSize, blacklistUids);
                break;
            case 11://炙手可热的米汤红人
                //实时统计
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.fansBillboard(sinceId, pageSize, blacklistUids);
                break;
            case 12://王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.kingdomPriceList(sinceId, pageSize, blacklistUids);
                break;
            case 13://王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.kingdomIncrPriceList(sinceId, pageSize, blacklistUids);
                break;
            case 14://标签[运动的时候最性感]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("运动的时候最性感", sinceId, pageSize, blacklistUids);
                break;
            case 15://标签[运动的时候最性感]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("运动的时候最性感", sinceId, pageSize, blacklistUids);
                break;
            case 16://标签[非典型性话唠]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("非典型性话唠", sinceId, pageSize, blacklistUids);
                break;
            case 17://标签[非典型性话唠]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("非典型性话唠", sinceId, pageSize, blacklistUids);
                break;
            case 18://标签[声音与光影]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("声音与光影", sinceId, pageSize, blacklistUids);
                break;
            case 19://标签[声音与光影]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("声音与光影", sinceId, pageSize, blacklistUids);
                break;
            case 20://标签[建筑不止是房子]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("建筑不止是房子", sinceId, pageSize, blacklistUids);
                break;
            case 21://标签[建筑不止是房子]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("建筑不止是房子", sinceId, pageSize, blacklistUids);
                break;
            case 22://标签[寰球动漫游戏世界]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("寰球动漫游戏世界", sinceId, pageSize, blacklistUids);
                break;
            case 23://标签[寰球动漫游戏世界]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("寰球动漫游戏世界", sinceId, pageSize, blacklistUids);
                break;
            case 24://标签[玩物不丧志]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("玩物不丧志", sinceId, pageSize, blacklistUids);
                break;
            case 25://标签[玩物不丧志]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("玩物不丧志", sinceId, pageSize, blacklistUids);
                break;
            case 26://标签[铲屎官的日常]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("铲屎官的日常", sinceId, pageSize, blacklistUids);
                break;
            case 27://标签[铲屎官的日常]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("铲屎官的日常", sinceId, pageSize, blacklistUids);
                break;
            case 28://标签[旅行是我的态度]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("旅行是我的态度", sinceId, pageSize, blacklistUids);
                break;
            case 29://标签[旅行是我的态度]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("旅行是我的态度", sinceId, pageSize, blacklistUids);
                break;
            case 30://标签[深夜食堂]王国价值最高
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomPriceList("深夜食堂", sinceId, pageSize, blacklistUids);
                break;
            case 31://标签[深夜食堂]王国价值增长最快
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.tagKingdomIncrPriceList("深夜食堂", sinceId, pageSize, blacklistUids);
                break;
            case 32://个人米汤币排行榜
            	//实时统计
                if(sinceId < 0){
                    sinceId = 0l;
                }
                result = liveForContentJdbcDao.userCoinList(sinceId, pageSize, blacklistUids);
                break;
            case 33://对外分享次数用户榜单
            	if(sinceId < 0){
                    sinceId = 0l;
                }
            	result = liveForContentJdbcDao.shareUserList(sinceId, pageSize, blacklistUids);
            	break;
            case 34://外部阅读次数王国榜单
            	if(sinceId < 0){
                    sinceId = 0l;
                }
            	result = liveForContentJdbcDao.outReadKingdomList(sinceId, pageSize, blacklistUids);
            	break;
            case 35://正在抽奖的王国
            	if(sinceId < 0){
                    sinceId = 0l;
                }
            	result = liveForContentJdbcDao.kingdomLotteryList(sinceId, pageSize, blacklistUids);
            	break;
            default:
                break;
        }

        return result;
    }

    private List<BillBoardListDTO> genBBLDto(List<BillBoardList> list){
        List<BillBoardListDTO> result = new ArrayList<BillBoardListDTO>();
        if(null != list && list.size() > 0){
            BillBoardListDTO dto = null;
            for(BillBoardList bbl : list){
                dto = new BillBoardListDTO();
                dto.setTargetId(bbl.getTargetId());
                dto.setType(bbl.getType());
                dto.setSinceId(bbl.getSinceId());
                result.add(dto);
            }
        }
        return result;
    }

    private void buildAutoBillBoardSimple(BangDanDto.BangDanData bangDanData, long bid, int mode, long currentUid, int type, int pageSize, List<Long> blacklistUids){
        List<BillBoardListDTO> result = this.getAutoBillBoardList(mode, -1, pageSize,blacklistUids);

        double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));

        if(null != result && result.size() > 0){
            List<Long> topicIdList = new ArrayList<Long>();
            List<Long> uidList = new ArrayList<Long>();
            if(type == 1){//王国
                for(BillBoardListDTO bbl : result){
                    if(!topicIdList.contains(bbl.getTargetId())){
                        topicIdList.add(bbl.getTargetId());
                    }
                }
            }else if(type == 2){//人
                for(BillBoardListDTO bbl : result){
                    if(!uidList.contains(bbl.getTargetId())){
                        uidList.add(bbl.getTargetId());
                    }
                }
            }

            Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
            Map<String, String> liveFavouriteMap = new HashMap<String, String>();
            Map<String, Content> topicContentMap = new HashMap<String, Content>();
            Map<String, Long> reviewCountMap = new HashMap<String, Long>();
            Map<String, Long> topicMemberCountMap = null;
            Map<String, String> topicTagMap = new HashMap<String, String>();
            if(topicIdList.size() > 0){
                List<Map<String, Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
                if(null != topicList && topicList.size() > 0){
                    Long uid = null;
                    for(Map<String, Object> m : topicList){
                        topicMap.put(String.valueOf(m.get("id")), m);
                        uid = (Long)m.get("uid");
                        if(!uidList.contains(uid)){
                            uidList.add(uid);
                        }
                    }
                }
                List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
                if(null != liveFavouriteList && liveFavouriteList.size() > 0){
                    for(Map<String,Object> lf : liveFavouriteList){
                        liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
                    }
                }
                List<Content> topicContentList = contentMybatisDao.getContentByTopicIds(topicIdList);
                if(null != topicContentList && topicContentList.size() > 0){
                    for(Content c : topicContentList){
                        topicContentMap.put(c.getForwardCid().toString(), c);
                    }
                }
                List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
                if(null != tcList && tcList.size() > 0){
                    for(Map<String, Object> m : tcList){
                        reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
                    }
                }
                topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
                List<Map<String, Object>> topicTagList = liveForContentJdbcDao.getTopicTagDetailListByTopicIds(topicIdList);
                if(null != topicTagList && topicTagList.size() > 0){
                    long tid = 0;
                    String tags = null;
                    Long topicId = null;
                    for(Map<String, Object> ttd : topicTagList){
                        topicId = (Long)ttd.get("topic_id");
                        if(topicId.longValue() != tid){
                            //先插入上一次
                            if(tid > 0 && !StringUtils.isEmpty(tags)){
                                topicTagMap.put(String.valueOf(tid), tags);
                            }
                            //再初始化新的
                            tid = topicId.longValue();
                            tags = null;
                        }
                        if(tags != null){
                            tags = tags + ";" + (String)ttd.get("tag");
                        }else{
                            tags = (String)ttd.get("tag");
                        }
                    }
                    if(tid > 0 && !StringUtils.isEmpty(tags)){
                        topicTagMap.put(String.valueOf(tid), tags);
                    }
                }
            }
            if(null == topicMemberCountMap){
                topicMemberCountMap = new HashMap<String, Long>();
            }
            Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
            //一次性查询关注信息
            Map<String, String> followMap = new HashMap<String, String>();
            if(uidList.size() > 0){
                List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
                if(null != userList && userList.size() > 0){
                    for(UserProfile u : userList){
                        userMap.put(u.getUid().toString(), u);
                    }
                }
                List<UserFollow> userFollowList = userService.getAllFollows(currentUid, uidList);
                if(null != userFollowList && userFollowList.size() > 0){
                    for(UserFollow uf : userFollowList){
                        followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
                    }
                }
            }

            BangDanDto.BangDanData.BangDanInnerData bangDanInnerData = null;
            Map<String,Object> topic = null;
            UserProfile userProfile = null;
            Content topicContent = null;
            for(BillBoardListDTO bbl : result){
                bangDanInnerData = new BangDanDto.BangDanData.BangDanInnerData();
                bangDanInnerData.setSubType(type);
                if(type==1){// 王国数据
                    bangDanInnerData.setSubListId(bid);
                    topic = topicMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == topic){
                        log.info("王国[id="+bbl.getTargetId()+"]不存在");
                        continue;
                    }
                    long uid = (Long)topic.get("uid");
                    bangDanInnerData.setUid(uid);
                    userProfile = userMap.get(String.valueOf(uid));
                    if(null == userProfile){
                        log.info("用户[uid="+uid+"]不存在");
                        continue;
                    }
                    bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                    bangDanInnerData.setNickName(userProfile.getNickName());
                    bangDanInnerData.setV_lv(userProfile.getvLv());
                    bangDanInnerData.setLevel(userProfile.getLevel());
                    if(null != followMap.get(currentUid+"_"+uid)){
                        bangDanInnerData.setIsFollowed(1);
                    }else{
                        bangDanInnerData.setIsFollowed(0);
                    }
                    if(null != followMap.get(uid+"_"+currentUid)){
                        bangDanInnerData.setIsFollowMe(1);
                    }else{
                        bangDanInnerData.setIsFollowMe(0);
                    }
                    bangDanInnerData.setContentType((Integer)topic.get("type"));
                    if(liveFavouriteMap.get(String.valueOf(bbl.getTargetId())) != null){
                        bangDanInnerData.setFavorite(1);
                    }else{
                        bangDanInnerData.setFavorite(0);
                    }
                    topicContent = topicContentMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == topicContent){
                        continue;
                    }
                    bangDanInnerData.setId(topicContent.getId());
                    bangDanInnerData.setCid(topicContent.getId());
                    bangDanInnerData.setTopicId(bbl.getTargetId());
                    bangDanInnerData.setForwardCid(bbl.getTargetId());
                    bangDanInnerData.setTitle((String)topic.get("title"));
                    bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + topic.get("live_image").toString());
                    int internalStatust = this.getInternalStatus(topic, currentUid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    bangDanInnerData.setInternalStatus(internalStatust);
                    if(null != topicMemberCountMap.get(String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setFavoriteCount(topicMemberCountMap.get(String.valueOf(bbl.getTargetId())).intValue()+1);
                    }else{
                        bangDanInnerData.setFavoriteCount(1);
                    }
                    bangDanInnerData.setReadCount(topicContent.getReadCountDummy());
                    bangDanInnerData.setLikeCount(topicContent.getLikeCount());
                    if(null != reviewCountMap.get(String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setReviewCount(reviewCountMap.get(String.valueOf(bbl.getTargetId())).intValue());
                    }else{
                        bangDanInnerData.setReviewCount(0);
                    }
                    if(null != topicTagMap.get(String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setTags(topicTagMap.get(String.valueOf(bbl.getTargetId())));
                    }else{
                        bangDanInnerData.setTags("");
                    }
                    bangDanInnerData.setPrice((Integer)topic.get("price"));
                    bangDanInnerData.setPriceRMB(exchangeKingdomPrice(bangDanInnerData.getPrice()));
                    bangDanInnerData.setShowPriceBrand(bangDanInnerData.getPrice()!=null && bangDanInnerData.getPrice()>=minPrice?1:0);
                    bangDanInnerData.setShowRMBBrand(0);// 显示吊牌不显示RMB吊牌。
                }else if(type==2){// 人
                    bangDanInnerData.setSubListId(bid);
                    bangDanInnerData.setUid(bbl.getTargetId());
                    userProfile = userMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == userProfile){
                        log.info("用户[uid="+bbl.getTargetId()+"]不存在");
                        continue;
                    }
                    bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                    bangDanInnerData.setNickName(userProfile.getNickName());
                    bangDanInnerData.setV_lv(userProfile.getvLv());
                    bangDanInnerData.setLevel(userProfile.getLevel());
                    if(null != followMap.get(currentUid+"_"+bbl.getTargetId())){
                        bangDanInnerData.setIsFollowed(1);
                    }else{
                        bangDanInnerData.setIsFollowed(0);
                    }
                    if(null != followMap.get(bbl.getTargetId()+"_"+currentUid)){
                        bangDanInnerData.setIsFollowMe(1);
                    }else{
                        bangDanInnerData.setIsFollowMe(0);
                    }
                    bangDanInnerData.setIntroduced(userProfile.getIntroduced());
                }
                bangDanData.getSubList().add(bangDanInnerData);
            }
        }
    }

    /**
     * 处理自动榜单详情
     * @param billBoardDetailsDto
     * @param mode
     * @return
     */
    private void buildAutoBillBoardDetails(BillBoardDetailsDto billBoardDetailsDto, int mode, long sinceId, long currentUid, int type, List<Long> blacklistUids){
        List<BillBoardListDTO> result = this.getAutoBillBoardList(mode, sinceId, 20, blacklistUids);

        double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));

        if(null != result && result.size() > 0){
            List<Long> topicIdList = new ArrayList<Long>();
            List<Long> uidList = new ArrayList<Long>();
            if(type == 1){//王国
                for(BillBoardListDTO bbl : result){
                    if(!topicIdList.contains(bbl.getTargetId())){
                        topicIdList.add(bbl.getTargetId());
                    }
                }
            }else if(type == 2){//人
                for(BillBoardListDTO bbl : result){
                    if(!uidList.contains(bbl.getTargetId())){
                        uidList.add(bbl.getTargetId());
                    }
                }
            }

            Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
            Map<String, String> liveFavouriteMap = new HashMap<String, String>();
            Map<String, Content> topicContentMap = new HashMap<String, Content>();
            Map<String, Long> reviewCountMap = new HashMap<String, Long>();
            Map<String, Long> topicMemberCountMap = null;
            Map<String, String> topicTagMap = new HashMap<String, String>();
            if(topicIdList.size() > 0){
                List<Map<String, Object>> topicList = liveForContentJdbcDao.getTopicListByIds(topicIdList);
                if(null != topicList && topicList.size() > 0){
                    Long uid = null;
                    for(Map<String, Object> m : topicList){
                        topicMap.put(String.valueOf(m.get("id")), m);
                        uid = (Long)m.get("uid");
                        if(!uidList.contains(uid)){
                            uidList.add(uid);
                        }
                    }
                }
                List<Map<String,Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
                if(null != liveFavouriteList && liveFavouriteList.size() > 0){
                    for(Map<String,Object> lf : liveFavouriteList){
                        liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
                    }
                }
                List<Content> topicContentList = contentMybatisDao.getContentByTopicIds(topicIdList);
                if(null != topicContentList && topicContentList.size() > 0){
                    for(Content c : topicContentList){
                        topicContentMap.put(c.getForwardCid().toString(), c);
                    }
                }
                List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
                if(null != tcList && tcList.size() > 0){
                    for(Map<String, Object> m : tcList){
                        reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
                    }
                }
                topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
                List<Map<String, Object>> topicTagList = liveForContentJdbcDao.getTopicTagDetailListByTopicIds(topicIdList);
                if(null != topicTagList && topicTagList.size() > 0){
                    long tid = 0;
                    String tags = null;
                    Long topicId = null;
                    for(Map<String, Object> ttd : topicTagList){
                        topicId = (Long)ttd.get("topic_id");
                        if(topicId.longValue() != tid){
                            //先插入上一次
                            if(tid > 0 && !StringUtils.isEmpty(tags)){
                                topicTagMap.put(String.valueOf(tid), tags);
                            }
                            //再初始化新的
                            tid = topicId.longValue();
                            tags = null;
                        }
                        if(tags != null){
                            tags = tags + ";" + (String)ttd.get("tag");
                        }else{
                            tags = (String)ttd.get("tag");
                        }
                    }
                    if(tid > 0 && !StringUtils.isEmpty(tags)){
                        topicTagMap.put(String.valueOf(tid), tags);
                    }
                }
            }
            if(null == topicMemberCountMap){
                topicMemberCountMap = new HashMap<String, Long>();
            }
            Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
            //一次性查询关注信息
            Map<String, String> followMap = new HashMap<String, String>();
            if(uidList.size() > 0){
                List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
                if(null != userList && userList.size() > 0){
                    for(UserProfile u : userList){
                        userMap.put(u.getUid().toString(), u);
                    }
                }
                List<UserFollow> userFollowList = userService.getAllFollows(currentUid, uidList);
                if(null != userFollowList && userFollowList.size() > 0){
                    for(UserFollow uf : userFollowList){
                        followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
                    }
                }
            }

            BillBoardDetailsDto.InnerDetailData bangDanInnerData = null;
            Map<String, Object> topic = null;
            Content topicContent = null;
            UserProfile userProfile = null;
            for(BillBoardListDTO bbl : result){
                bangDanInnerData = new BillBoardDetailsDto.InnerDetailData();
                bangDanInnerData.setSubType(type);
                bangDanInnerData.setSinceId(bbl.getSinceId());
                if(type==1){// 王国
                    topic = topicMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == topic){
                        log.info("王国[id="+bbl.getTargetId()+"]不存在");
                        continue;
                    }
                    long uid = Long.valueOf(topic.get("uid").toString());
                    bangDanInnerData.setUid(uid);
                    userProfile = userMap.get(String.valueOf(uid));
                    if(null == userProfile){
                        log.info("用户[uid="+uid+"]不存在");
                        continue;
                    }
                    bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                    bangDanInnerData.setNickName(userProfile.getNickName());
                    bangDanInnerData.setV_lv(userProfile.getvLv());
                    bangDanInnerData.setLevel(userProfile.getLevel());
                    if(null != followMap.get(currentUid+"_"+uid)){
                        bangDanInnerData.setIsFollowed(1);
                    }else{
                        bangDanInnerData.setIsFollowed(0);
                    }
                    if(null != followMap.get(uid+"_"+currentUid)){
                        bangDanInnerData.setIsFollowMe(1);
                    }else{
                        bangDanInnerData.setIsFollowMe(0);
                    }
                    bangDanInnerData.setContentType((Integer)topic.get("type"));
                    if(null != liveFavouriteMap.get(String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setFavorite(1);
                    }else{
                        bangDanInnerData.setFavorite(0);
                    }
                    topicContent = topicContentMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == topicContent){
                        continue;
                    }
                    bangDanInnerData.setPrice((Integer)topic.get("price"));
                    bangDanInnerData.setPriceRMB(exchangeKingdomPrice(bangDanInnerData.getPrice()));
                    bangDanInnerData.setShowPriceBrand(bangDanInnerData.getPrice()!=null && bangDanInnerData.getPrice()>=minPrice?1:0);
                    bangDanInnerData.setShowRMBBrand(0);// 显示吊牌不显示RMB吊牌。
                    bangDanInnerData.setId(topicContent.getId());
                    bangDanInnerData.setCid(topicContent.getId());
                    bangDanInnerData.setTopicId(bbl.getTargetId());
                    bangDanInnerData.setForwardCid(bbl.getTargetId());
                    bangDanInnerData.setTitle((String)topic.get("title"));
                    bangDanInnerData.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
                    int internalStatust = this.getInternalStatus(topic, currentUid);
                    if(internalStatust==Specification.SnsCircle.OUT.index){
                    	if( liveFavouriteMap.get(String.valueOf(topic.get("id")))!=null){
                    		internalStatust=Specification.SnsCircle.IN.index;
                    	}
                    }
                    bangDanInnerData.setInternalStatus(internalStatust);
                    if(null != topicMemberCountMap.get(String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setFavoriteCount(topicMemberCountMap.get(String.valueOf(bbl.getTargetId())).intValue()+1);
                    }else{
                        bangDanInnerData.setFavoriteCount(1);
                    }
                    bangDanInnerData.setReadCount(topicContent.getReadCountDummy());
                    bangDanInnerData.setLikeCount(topicContent.getLikeCount());
                    if(null != reviewCountMap.get(String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setReviewCount(reviewCountMap.get(String.valueOf(bbl.getTargetId())).intValue());
                    }else{
                        bangDanInnerData.setReviewCount(0);
                    }
                    if(null != topicTagMap.get(String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setTags(topicTagMap.get(String.valueOf(bbl.getTargetId())));
                    }else{
                        bangDanInnerData.setTags("");
                    }
                    billBoardDetailsDto.getSubList().add(bangDanInnerData);
                }else if(type==2){//人
                    bangDanInnerData.setUid(bbl.getTargetId());
                    userProfile = userMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == userProfile){
                        log.info("用户[uid="+bbl.getTargetId()+"]不存在");
                        continue;
                    }
                    bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                    bangDanInnerData.setNickName(userProfile.getNickName());
                    bangDanInnerData.setV_lv(userProfile.getvLv());
                    bangDanInnerData.setLevel(userProfile.getLevel());
                    if(null != followMap.get(currentUid+"_"+String.valueOf(bbl.getTargetId()))){
                        bangDanInnerData.setIsFollowed(1);
                    }else{
                        bangDanInnerData.setIsFollowed(0);
                    }
                    if(null != followMap.get(String.valueOf(bbl.getTargetId())+"_"+currentUid)){
                        bangDanInnerData.setIsFollowMe(1);
                    }else{
                        bangDanInnerData.setIsFollowMe(0);
                    }
                    bangDanInnerData.setIntroduced(userProfile.getIntroduced());
                    billBoardDetailsDto.getSubList().add(bangDanInnerData);
                }
            }
        }
    }

    @Override
    public List<BillBoard> getAllBillBoard() {
        return contentMybatisDao.loadBillBoard();
    }

    @Override
    public void updateBillBoard(BillBoard bb) {
        contentMybatisDao.updateBillBoard(bb);
    }

    @Override
    public void deleteBillBoardById(long id) {
        //删除榜单对应数据
        List<BillBoardRelation> relationList = contentMybatisDao.loadBillBoardRelation(id);
        for(BillBoardRelation br :relationList){
            contentMybatisDao.delBillBoardRelationById(br.getId());
        }
        //删除对应的上线配置项
        contentMybatisDao.deleteBillBoardDetailByBId(id);
        //删除榜单。
        contentMybatisDao.deleteBillBoardByKey(id);
    }

    @Override
    public BillBoard getBillBoardById(long id) {
        return contentMybatisDao.loadBillBoardById(id);
    }

    @Override
    public void addBillBoard(BillBoard bb) {
        contentMybatisDao.insertBillBoard(bb);
    }

    @Override
    public List<BillBoardRelation> getBillBoardRelationByBid(long bid){
        return contentMybatisDao.loadBillBoardRelation(bid);
    }

    @Override
    public List<BillBoardRelationDto> getRelationsByBillBoardId(long id) {
        BillBoard bb = contentMybatisDao.loadBillBoardById(id);
        if(null != bb && bb.getMode().intValue() > 0){
            return this.getRelationsByMode(bb.getMode().intValue());
        }

        List<BillBoardRelation> relationList = contentMybatisDao.loadBillBoardRelation(id);
        List<BillBoardRelationDto> retList = new ArrayList<>();
        for(BillBoardRelation billBoardRelation :relationList){
            BillBoardRelationDto bangDanInnerData = new BillBoardRelationDto();
            long targetId = billBoardRelation.getTargetId();
            int type = billBoardRelation.getType();
            BeanUtils.copyProperties(billBoardRelation, bangDanInnerData);
            if(type==1){  // 王国
                Map map = billBoardJdbcDao.getTopicById(targetId);
                if(map==null){
                    continue;
                }
                String title = (String)map.get("title");
                long uid = Long.valueOf(map.get("uid").toString());
                int contentType = Integer.valueOf(map.get("type").toString());
                String liveImage = map.get("live_image").toString();
                bangDanInnerData.setTitle(title);
                bangDanInnerData.setCover(liveImage);
                bangDanInnerData.setTopicId(targetId);
                bangDanInnerData.setAggregation((Integer) map.get("type"));
            }else if(type==2){	// 人
                bangDanInnerData.setUid(targetId);
                UserProfile userProfile = userService.getUserProfileByUid(targetId);
                bangDanInnerData.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                bangDanInnerData.setNickName(userProfile.getNickName());
                bangDanInnerData.setvLv(userProfile.getvLv());
                bangDanInnerData.setUserRegDate(userProfile.getCreateTime());
            }else if(type==3){// 榜单
                BillBoard billBoard = contentMybatisDao.loadBillBoardById(targetId);
                bangDanInnerData.setRankingCover(Constant.QINIU_DOMAIN + "/" + billBoard.getImage());
                bangDanInnerData.setRankingId(billBoard.getId());
                bangDanInnerData.setRankingName(billBoard.getName());
                bangDanInnerData.setRankingType(billBoard.getType());
            }
            retList.add(bangDanInnerData);
        }
        return retList;
    }

    private List<BillBoardRelationDto> getRelationsByMode(int mode){
        List<BillBoardRelationDto> result = Lists.newArrayList();

        int type = 1;//默认王国
        if(mode == 1 || mode == 2 || mode == 3 || mode == 8 || mode == 9 || mode == 10 || mode == 11 || mode == 32 || mode == 33){
            type = 2;
        }

        List<BillBoardListDTO> list = this.getAutoBillBoardList(mode, -1, 100, null);
        if(null != list && list.size() > 0){
            List<Long> idList = Lists.newArrayList();
            for(BillBoardListDTO bbl : list){
                if(!idList.contains(bbl.getTargetId())){
                    idList.add(bbl.getTargetId());
                }
            }

            Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
            Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
            if(type == 1){//王国
                List<Map<String, Object>> topicList = liveForContentJdbcDao.getTopicListByIds(idList);
                if(null != topicList && topicList.size() > 0){
                    for(Map<String, Object> m : topicList){
                        topicMap.put(String.valueOf(m.get("id")), m);
                    }
                }
            }else{//人
                List<UserProfile> userList = userService.getUserProfilesByUids(idList);
                if(null != userList && userList.size() > 0){
                    for(UserProfile u : userList){
                        userMap.put(u.getUid().toString(), u);
                    }
                }
            }
            BillBoardRelationDto bbrdto = null;
            UserProfile userProfile = null;
            Map<String, Object> topic = null;
            for(BillBoardListDTO bbl : list){
                bbrdto = new BillBoardRelationDto();
                if(type == 1){//王国属性
                    topic = topicMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == topic){
                        continue;
                    }
                    bbrdto.setTopicId((Long)topic.get("id"));
                    bbrdto.setTitle((String)topic.get("title"));
                    bbrdto.setCover((String)topic.get("live_image"));
                    bbrdto.setAggregation((Integer)topic.get("type"));
                }else{//人属性
                    userProfile = userMap.get(String.valueOf(bbl.getTargetId()));
                    if(null == userProfile){
                        continue;
                    }
                    bbrdto.setNickName(userProfile.getNickName());
                    bbrdto.setUid(userProfile.getUid());
                    bbrdto.setUserRegDate(userProfile.getCreateTime());
                    bbrdto.setvLv(userProfile.getvLv());
                    bbrdto.setAvatar(userProfile.getAvatar());
                }
                result.add(bbrdto);
            }
        }

        return result;
    }

    @Override
    public void addRelationToBillBoard(BillBoardRelation br) {
        // 防重复
        if(br.getTargetId()==0||br.getSourceId()==0||br.getType()==0){
            throw new RuntimeException("数据不完整");
        }
        boolean exists = contentMybatisDao.existsBillBoardRelation(br);
        if(!exists){
            contentMybatisDao.insertBillBoardRelation(br);
        }
    }

    @Override
    public void delBillBoardRelationById(long rid) {
        contentMybatisDao.delBillBoardRelationById(rid);
    }

    @Override
    public void updateBillBoardRelation(BillBoardRelation br) {
        contentMybatisDao.updateBillBoardRelation(br);
    }

    @Override
    public List<OnlineBillBoardDto> getOnlineBillBoardListByType(int type) {
        List<BillBoardDetails> detailList = contentMybatisDao.getBillBoardDetailsByType(type);
        List<OnlineBillBoardDto> dtoList = new ArrayList<>();
        for(BillBoardDetails detail:detailList){
            OnlineBillBoardDto dto= new OnlineBillBoardDto();
            BillBoard billbord=contentMybatisDao.loadBillBoardById(detail.getBid());
            dto.setDetail(detail);
            dto.setBillbord(billbord);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public void addOnlineBillBoard(BillBoardDetails br) {
        List<BillBoardDetails> list = contentMybatisDao.getBillBoardDetailByBidAndType(br.getBid(),br.getType());
        if(list==null || list.isEmpty()){
            contentMybatisDao.insertBillBoardDetail(br);
        }
    }

    @Override
    public void delOnlineBillBoardById(long rid) {
        contentMybatisDao.delBillBoardDetailById(rid);
    }

    @Override
    public void updateOnlineBillBoard(BillBoardDetails br) {
        contentMybatisDao.updateBillBoardDetailById(br);
    }

    @Override
    public Integer addEmotionPack(EmotionPack pack) {
        return emotionPackMapper.insertSelective(pack);
    }

    @Override
    public void deleteEmotionPackByKey(Integer id) {
        EmotionPackDetailExample example = new EmotionPackDetailExample();
        example.createCriteria().andPackIdEqualTo(id);
        emotionPackDetailMapper.deleteByExample(example);
        emotionPackMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateEmotionPackByKey(EmotionPack pack) {
        emotionPackMapper.updateByPrimaryKeySelective(pack);

    }

    @Override
    public EmotionPack getEmotionPackByKey(Integer id) {
        return emotionPackMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer addEmotionPackDetail(EmotionPackDetail detail) {
        return emotionPackDetailMapper.insertSelective(detail);
    }

    @Override
    public void deleteEmotionPackDetailByKey(Integer id) {
        emotionPackDetailMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateEmotionPackDetailByKey(EmotionPackDetail detail) {
        emotionPackDetailMapper.updateByPrimaryKeySelective(detail);
    }

    @Override
    public EmotionPackDetail getEmotionPackDetailByKey(Integer id) {
        return emotionPackDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageBean<EmotionPack> getEmotionPackPage(PageBean<EmotionPack> page, Map<String, Object> conditions) {
        EmotionPackExample example = new EmotionPackExample();
        int count = emotionPackMapper.countByExample(example);
        example.setOrderByClause("order_num desc limit "+((page.getCurrentPage()-1)*page.getPageSize())+","+page.getPageSize());
        List<EmotionPack> packList=  emotionPackMapper.selectByExample(example);
        page.setPageSize(count);
        page.setDataList(packList);
        return page;
    }

    @Override
    public PageBean<EmotionPackDetail> getEmotionPackDetailPage(PageBean<EmotionPackDetail> page,
                                                                Map<String, Object> conditions) {
        EmotionPackDetailExample example = new EmotionPackDetailExample();
        example.createCriteria().andPackIdEqualTo((Integer)conditions.get("packId"));
        int count = emotionPackDetailMapper.countByExample(example);
        example.setOrderByClause("order_num asc limit "+((page.getCurrentPage()-1)*page.getPageSize())+","+page.getPageSize());
        List<EmotionPackDetail> packList=  emotionPackDetailMapper.selectByExample(example);
        page.setPageSize(count);
        page.setDataList(packList);
        return page;
    }

    @Override
    public List<EmotionPackDetail> getEmotionPackDetailBig() {
        List<EmotionPackDetail> list = new ArrayList<EmotionPackDetail>();
        EmotionPackExample example = new EmotionPackExample();
        example.createCriteria().andEmojiTypeEqualTo(1);
        List<EmotionPack> packList=  emotionPackMapper.selectByExample(example);
        EmotionPack emotionPack = new EmotionPack();
        List<Integer> ilist = new ArrayList<Integer>();
        if(packList.size()>0){
            for (int i = 0; i < packList.size(); i++) {
                ilist.add(packList.get(i).getId());
            }
        }else{
            return list;
        }
        EmotionPackDetailExample example1 = new EmotionPackDetailExample();
        example1.createCriteria().andPackIdIn(ilist);
        list=  emotionPackDetailMapper.selectByExample(example1);
        return list;
    }

    @Override
    public Response emojiPackageQuery() {
        EmojiPackDto dto = new EmojiPackDto();
        EmotionPackExample example = new EmotionPackExample();
        example.createCriteria().andIsValidEqualTo(1);
        example.setOrderByClause("order_num desc");
        List<EmotionPack> packList = emotionPackMapper.selectByExample(example);
        EmojiPackDto.PackageData pdata = null;
        for(EmotionPack pack:packList){
            pdata = new EmojiPackDto.PackageData();
            pdata.setCover(Constant.QINIU_DOMAIN + "/" + pack.getCover());
            pdata.setEmojiType(pack.getEmojiType());
            pdata.setExtra(pack.getExtra());
            pdata.setId(pack.getId());
            pdata.setName(pack.getName());
            pdata.setPVersion(pack.getpVersion());
            pdata.setVersion(pack.getVersion());
            dto.getPackageData().add(pdata);
        }
        return Response.success(dto);
    }

    @Override
    public Response emojiPackageDetail(int packageId) {
        EmotionPack  pack=	emotionPackMapper.selectByPrimaryKey(packageId);
        if(null == pack){
            return Response.failure(500, "表情包不存在");
        }

        EmojiPackDetailDto dto = new EmojiPackDetailDto();
        dto.setPackageId(pack.getId());
        dto.setEmojiType(pack.getEmojiType());
        dto.setPackageName(pack.getName());
        dto.setPackageCover(Constant.QINIU_DOMAIN + "/" + pack.getCover());
        dto.setPackageVersion(pack.getVersion());
        dto.setPackagePversion(pack.getpVersion());

        EmotionPackDetailExample example = new EmotionPackDetailExample();
        example.createCriteria().andPackIdEqualTo(packageId);
        example.setOrderByClause("order_num asc");
        List<EmotionPackDetail> detailList = emotionPackDetailMapper.selectByExample(example);
        EmojiPackDetailDto.PackageDetailData data = null;
        for(EmotionPackDetail detail:detailList){
            data = new EmojiPackDetailDto.PackageDetailData();
            data.setExtra(detail.getExtra());
            data.setH(detail.getH());
            data.setId(detail.getId());
            data.setImage(Constant.QINIU_DOMAIN + "/" + detail.getImage());
            data.setThumb(Constant.QINIU_DOMAIN + "/" + detail.getThumb());
            data.setThumb_h(detail.getThumbH());
            data.setThumb_w(detail.getThumbW());
            data.setTitle(detail.getTitle());
            data.setContent(detail.getExtra());
            data.setW(detail.getW());
            data.setEmojiType(pack.getEmojiType());
            dto.getEmojiData().add(data);
        }
        return Response.success(dto);
    }

    @Override
    public List<Long> getBillboardTopicIds4kingdomPushTask(){
        List<Long> result = new ArrayList<Long>();

        //查询所有上线的、王国榜单
        List<Long> bidList = new ArrayList<Long>();
        List<BillBoardDetails> list = contentMybatisDao.getBillBoardDetailsByStatusAndType(1, 2);
        if(null != list && list.size() > 0){
            for(BillBoardDetails bbd : list){
                bidList.add(bbd.getBid());
            }
        }

        List<BillBoard> bList = contentMybatisDao.loadBillBoardByBidsAndType(bidList, 1);
        if(null != bList && bList.size() > 0){
            List<Long> bids = new ArrayList<Long>();
            for(BillBoard b : bList){
                bids.add(b.getId());
            }
            List<BillBoardRelation> rList = contentMybatisDao.getBillBoardRelationsByBidsAndType(bids, 1);
            if(null != rList && rList.size() > 0){

                for(BillBoardRelation r : rList){
                    if(!result.contains(r.getTargetId())){
                        result.add(r.getTargetId());
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<BillBoard> getBillBoardList4kingdomPushTask(){
        List<Long> bidList = new ArrayList<Long>();
        List<BillBoardDetails> list = contentMybatisDao.getBillBoardDetailsByStatusAndType(1, 2);
        if(null != list && list.size() > 0){
            for(BillBoardDetails bbd : list){
                bidList.add(bbd.getBid());
            }
        }
        if(null != bidList && bidList.size() > 0){
            return contentMybatisDao.loadBillBoardByBidsAndType(bidList, 1);
        }
        return null;
    }
    @Override
    public Response getEmotionInfoByValue(int happyValue,int freeValue) {
        EmotionInfo  emotionInfo=	userService.getEmotionInfoByValue(happyValue, freeValue);
        if(null == emotionInfo){
            return Response.failure(500, "未匹配到情绪信息");
        }
        EmotionInfoDto dto = new EmotionInfoDto();
        dto.setEmotionName(emotionInfo.getEmotionname());
        dto.setFreeMax(emotionInfo.getFreemax());
        dto.setFreeMin(emotionInfo.getFreemin());
        dto.setHappyMax(emotionInfo.getHappymax());
        dto.setHappyMin(emotionInfo.getHappymin());
        dto.setId(emotionInfo.getId());
        dto.setTopicId(emotionInfo.getTopicid());
        EmotionPackDetail  detail=	emotionPackDetailMapper.selectByPrimaryKey(Integer.valueOf(emotionInfo.getEmotionpackid()+""));
        if(null == detail){
            return Response.failure(500, "表情不存在");
        }
        EmotionInfoDto.EmotionPack ep = EmotionInfoDto.createEmotionPack();
        ep.setExtra(detail.getExtra());
        ep.setH(detail.getH());
        ep.setId(detail.getId());
        ep.setImage(Constant.QINIU_DOMAIN + "/" + detail.getImage());
        ep.setThumb(Constant.QINIU_DOMAIN + "/" + detail.getThumb());
        ep.setThumb_h(detail.getThumbH());
        ep.setThumb_w(detail.getThumbW());
        ep.setTitle(detail.getTitle());
        ep.setContent(detail.getExtra());
        ep.setW(detail.getW());
        ep.setEmojiType(1);
        dto.setEmotionPack(ep);
        return Response.success(dto);
    }
    @Override
    public Response getLastEmotionInfo(long uid) {
        try {
            LastEmotionInfoDto  lastEmotionInfoDto = new LastEmotionInfoDto();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date);
            int n = -1;
            if(cal1.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
                n=-2;
            }
            String monday;
            cal1.add(Calendar.DATE, n*7);
            cal1.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            monday = sdf1.format(cal1.getTime());
            Date mondayDate=null;

            mondayDate = sdf1.parse(monday);


            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date);
            int m = 0;
            if(cal2.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
                m=-1;
            }
            String sunday;
            cal2.add(Calendar.DATE, m*7);
            cal2.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
            sunday = sdf2.format(cal2.getTime());
            Date sundayDate = sdf2.parse(sunday);


            SimpleDateFormat dsdf = new SimpleDateFormat("M月dd日");
            String dateStr = dsdf.format(mondayDate)+"-"+dsdf.format(sundayDate);
            lastEmotionInfoDto.setDateStr(dateStr);
            //判断是否周总结
            if(userService.exsitEmotionRecord(uid, mondayDate, sundayDate)){
                EmotionSummaryModel EmotionSummaryModel =  new EmotionSummaryModel(sdf.format(mondayDate),uid, "0");
                String isSummaryStr = cacheService.hGet(EmotionSummaryModel.getKey(), EmotionSummaryModel.getField());
                if (!StringUtils.isEmpty(isSummaryStr)) {
                    lastEmotionInfoDto.setIsSummary(0);
                } else {
                    lastEmotionInfoDto.setIsSummary(1);
                }
            }else{
                lastEmotionInfoDto.setIsSummary(0);
            }
            EmotionRecord emotionRecord =   userService.getLastEmotionRecord(uid);
            if(emotionRecord==null){
                lastEmotionInfoDto.setId(0);
            }else{
                EmotionInfo emotionInfo = userService.getEmotionInfoByKey(emotionRecord.getEmotionid());
                if (emotionInfo == null) {
                    return Response.failure(500, "没有该情绪信息！");
                }
                EmotionPackDetail emotionPackDetail = emotionPackDetailMapper.selectByPrimaryKey(Integer.valueOf(emotionInfo.getEmotionpackid() + ""));
                if (emotionPackDetail == null) {
                    return Response.failure(500, "没有该情绪大表情信息！");
                }
                lastEmotionInfoDto.setId(emotionInfo.getId());
                lastEmotionInfoDto.setEmotionName(emotionInfo.getEmotionname());
                lastEmotionInfoDto.setHappyValue(emotionRecord.getHappyvalue());
                lastEmotionInfoDto.setFreeValue(emotionRecord.getFreevalue());
                lastEmotionInfoDto.setTopicId(emotionInfo.getTopicid());
                int recordCount = userService.getEmotionRecordCount(uid);
                lastEmotionInfoDto.setRecordCount(recordCount);
                lastEmotionInfoDto.setCreateTime(emotionRecord.getCreateTime());
                LastEmotionInfoDto.EmotionPack ep =LastEmotionInfoDto.createEmotionPack();
                ep.setId(emotionPackDetail.getId());
                ep.setTitle(emotionPackDetail.getTitle());
                ep.setContent(emotionPackDetail.getExtra());
                ep.setImage(Constant.QINIU_DOMAIN + "/" + emotionPackDetail.getImage());
                ep.setThumb(Constant.QINIU_DOMAIN + "/" + emotionPackDetail.getThumb());
                ep.setW(emotionPackDetail.getW());
                ep.setH(emotionPackDetail.getH());
                ep.setThumb_w(emotionPackDetail.getThumbW());
                ep.setThumb_h(emotionPackDetail.getThumbH());
                ep.setExtra(emotionPackDetail.getExtra());
                ep.setEmojiType(1);
                lastEmotionInfoDto.setEmotionPack(ep);
                long timeInterval = (date.getTime()-emotionRecord.getCreateTime().getTime())/1000;
                lastEmotionInfoDto.setTimeInterval(timeInterval);
            }
            return  Response.success(lastEmotionInfoDto);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return  Response.failure(500,"时间转换错误");
        }
    }

    @Override
    public Response shareRecord(long uid, int type, long cid, String shareAddr){
        ContentShareHistory csh = new ContentShareHistory();
        csh.setCid(cid);
        csh.setCreateTime(new Date());
        csh.setShareAddr(shareAddr);
        csh.setType(type);
        csh.setUid(uid);
        contentMybatisDao.saveContentShareHistory(csh);
        ModifyUserCoinDto modifyUserCoinDto = userService.coinRule(uid, userService.getCoinRules().get(Rules.SHARE_KING_KEY));
        Response response = Response.success(Response.success(ResponseStatus.OPERATION_SUCCESS.status, ResponseStatus.OPERATION_SUCCESS.message));
        response.setData(modifyUserCoinDto);
        if(csh.getType()==1){
	        // 记录操作日志
	        this.addUserOprationLog(uid, USER_OPRATE_TYPE.SHARE_KINGDOM,cid);
        }
        return response;
    }


    @Override
    public Response<PricedKingdomDto> getPricedKingdomList(int page, int pageSize,long currentUid) {
    	List<Long> blacklistUids = liveForContentJdbcDao.getBlacklist(currentUid);
        List<Map<String,Object>> topicList = this.liveForContentJdbcDao.getTopPricedKingdomList( page, pageSize, blacklistUids);
        List<BasicKingdomInfo> buildResult = this.kingdomBuider.buildKingdoms(topicList, currentUid);
        PricedKingdomDto result = new PricedKingdomDto();
        for(BasicKingdomInfo info:buildResult){
            PricedKingdomDto.TopicData topicData = new PricedKingdomDto.TopicData();
            BeanUtils.copyProperties(info, topicData);
            result.getListData().add(topicData);
        }
        return Response.success(result);
    }

    @Override
    public int getTopicMembersCount(long topicId){
        List<Long> topicIdList = new ArrayList<Long>();
        topicIdList.add(topicId);
        Map<String, Long> map=  liveForContentJdbcDao.getTopicMembersCount(topicIdList);
        if(map==null){
            return 0;
        }else{
            return map.get(""+topicId)==null?0:Integer.parseInt(map.get(""+topicId).toString());
        }
    }
    @Override
    public int getTopicShareCount(long topicId){
        return contentMybatisDao.getTopicShareCount(topicId);
    }
    @Override
    public void updateContentUid(long newUid,long topicId){
        liveForContentJdbcDao.updateContentUid(newUid,topicId);
    }

    @Override
    public Response<TagKingdomDto> tagKingdomList(String tagName, String order, int page, int pageSize, long uid) {
    	List<Long> blacklistUids = liveForContentJdbcDao.getBlacklist(uid);
    	long tagId = topicTagMapper.getTagIdByTag(tagName);
        List<Map<String,Object>> topics = null;
     /*   String ktKey = "OBJ:KINGDOMSBYTAG:"+tagName+"_"+order+"_"+page+"_"+pageSize;
        Object tkRes = cacheService.getJavaObject(ktKey);
        if(null != tkRes){
        	topics = (List<Map<String,Object>>)tkRes;
        }else{
        	
        	List<Map<String,Object>> tkCacheObj = new ArrayList<Map<String,Object>>();
        	if(null != topics && topics.size() > 0){
        		Map<String,Object> t = null;
        		for(Map<String,Object> m : topics){
        			t = new HashMap<String, Object>();
        			t.putAll(m);
        			tkCacheObj.add(t);
        		}
        	}
        	cacheService.cacheJavaObject(ktKey, tkCacheObj, 2*60*60);//缓存两小时
        }*/
        List<Long> topicIds = topicTagMapper.getTopicIdsByTagAndSubTag(tagId);
        topics = topicTagMapper.getKingdomsByTag(uid,topicIds, order, page, pageSize, blacklistUids);
        
        List<BasicKingdomInfo> kingdoms = this.kingdomBuider.buildKingdoms(topics, uid);
        TagKingdomDto dto = new TagKingdomDto();
        dto.setKingdomList(kingdoms);

        if(page==1){
        	Map<String,Object> totalPrice = null;
        	String cacheKey = "OBJ:TAGPRICEANDKINGDOMCOUNT:"+tagName;
        	Object tagRes = cacheService.getJavaObject(cacheKey);
        	if(null != tagRes){
        		log.info("从缓存里取到11");
        		totalPrice = (Map<String,Object>)tagRes;
        	}else{
        		log.info("查的数据库呀11");
        		totalPrice = topicTagMapper.getTagPriceAndKingdomCount(topicIds);
        		Map<String, Object> cacheObj = new HashMap<String, Object>();
        		if(null != totalPrice && totalPrice.size() > 0){
        			cacheObj.putAll(totalPrice);
        		}
        		cacheService.cacheJavaObject(cacheKey, cacheObj, 2*60*60);//缓存两小时
        	}
            
            int tagPersons=0;
            //int tagPrice=(Integer)totalPrice.get("tagPrice");
            int kingdomCount =0;
            if(totalPrice.containsKey("tagPersons")){
                tagPersons=((Number)totalPrice.get("tagPersons")).intValue();
            }
            if(totalPrice.containsKey("kingdomCount")){
                kingdomCount =((Number)totalPrice.get("kingdomCount")).intValue();
            }

            dto.setKingdomCount(kingdomCount);
            dto.setPersonCount(tagPersons);
            dto.setTagName(tagName);

            // 取topic tags 取所有的体系标签， 排序规则：1 运营指定顺序 2 用户喜好 3 标签价值
            
            List<String> subSysTagList = topicTagMapper.getSubSysTags(tagId);		//	只取当前标签的子标签
            dto.setHotTagList(subSysTagList);
            /* 与运营协商,推荐的标签也没几个，此段代码很影响性能，暂时去掉。20170901
	            //先从缓存里取
	            List<Map<String,Object>> sysTagList = null;
	            Object res = cacheService.getJavaObject("OBJ:SYSTAGCOUNTINFO");
	            if(null != res){
	            	log.info("从缓存里取到");
	            	sysTagList = (List<Map<String,Object>>)res;
	            }else{
	            	log.info("查的数据库呀");
	            	sysTagList = topicTagMapper.getSysTagCountInfo();		
	            }
           
             if(sysTagList!=null && !sysTagList.isEmpty()){
                int size= sysTagList==null?0:sysTagList.size();
                String[] recommendTags = new String[size];

                // 运营指定顺序
                Iterator<Map<String,Object>> it = sysTagList.iterator();
                while(it.hasNext()){
                    Map<String,Object> tagInfo= it.next();
                    if(tagInfo.get("order_num")!=null){
                        int pos = (Integer)tagInfo.get("order_num")-1;		// 手动序号
                        if(pos>=0 && pos < size){	// 防爆。
                            recommendTags[pos]=(String)tagInfo.get("tag");
                            it.remove();
                        }
                    }
                }

                List<Integer> userHobbyList= topicTagMapper.getUserHobbyIdsByUid(uid);

                // 用户兴趣爱好,查标签对应的子标签，从集合里面判断是否包含用户喜好
                for(int i=0;i<recommendTags.length && userHobbyList.size()>0;i++){
                    if(recommendTags[i]==null){
                        it = sysTagList.iterator();
                        while(it.hasNext()){
                            Map<String,Object> tagInfo= it.next();
                            long tagId = (Long)tagInfo.get("id");
                            String strTagName = (String)tagInfo.get("tag");
                            // 主标签匹配
                            String tagHobby = (String)tagInfo.get("user_hobby_ids");
                            if(tagHobby!=null){
                                if(CollectionUtils.contains(userHobbyList, tagHobby.split(","),true)){
                                    recommendTags[i]=strTagName;
                                    it.remove();
                                    continue;
                                };
                            }
                            // 子标签匹配
                            List<Map<String,Object>> subTags = topicTagMapper.getSubTagsByParentTagId(tagId);
                            for(Map<String,Object> subTag:subTags){
                                String subTaghobby = (String)subTag.get("user_hobby_ids");
                                if(subTaghobby!=null){
                                    if(CollectionUtils.contains(userHobbyList, subTaghobby.split(","),true)){
                                        recommendTags[i]=strTagName;
                                        it.remove();
                                        break;
                                    };
                                }
                            }
                        }

                    }
                }
                // 价值排序
                sysTagList.sort(new Comparator<Map<String,Object>>() {
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        int p1=0,p2=0;
                        if(o1.get("price")!=null){
                            p1=((Number)o1.get("price")).intValue();
                        }
                        if(o2.get("price")!=null){
                            p2=((Number)o2.get("price")).intValue();
                        }
                        return p1<p2?1:-1;
                    }
                });
                it=sysTagList.iterator();
                for(int i=0;i<recommendTags.length;i++){
                    if(recommendTags[i]==null && it.hasNext()){
                        recommendTags[i]=(String) it.next().get("tag");
                    }
                }


                // 去掉当前标签
                List<String> tagList = new ArrayList<>();
                for(String x:recommendTags){
                    if(x!=null && !tagName.equals(x)){
                        tagList.add(x);
                    }
                }
                dto.setHotTagList(tagList);
            }*/
            
        }
        // 记录操作日志
        this.addUserOprationLog(uid, USER_OPRATE_TYPE.HIT_TAG,tagName);
        return Response.success(dto);
    }

    @Override
    public Response initSquareUpdateId() {
        List<Map<String,Object>> list = billBoardJdbcDao.getAllContent();
        log.info("共["+list.size()+"]个内容");
        int i=0;
        int l = list.size();
        for(Map map : list){
        	i++;
            long id = Long.valueOf(map.get("id").toString());
            long updateId = cacheService.incr("UPDATE_ID");
            billBoardJdbcDao.setUpdateId(updateId,id);
            if(i%2000 == 0){
            	l = l - i;
            	log.info("本批次处理了["+i+"]个，还剩["+l+"]个");
            	i = 0;
            }
        }
        if(i>0){
        	l = l - i;
        	log.info("本批次处理了["+i+"]个，还剩["+l+"]个");
        }
        return Response.success();
    }

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object... params) {
		return jdbc.queryForList(sql, params);
	}

	@Override
	public Map<String, Object> queryForMap(String sql, Object... args) {
		return jdbc.queryForMap(sql,args);
	}

	@Override
	public <T> T queryForObject(String sql, Class<T> cls, Object... args) {
		return jdbc.queryForObject(sql, cls,args);
	}

	@Override
	public void update(String sql, Object... args) {
		jdbc.update(sql,args);
		
	}
	/**以事务方式运行，可以返回ID**/
	@Override
	public int insert(String sql, Object... args) {
		jdbc.update(sql,args);
		return jdbc.queryForObject("select @@IDENTITY",Integer.class);
	}

	@Override
	public void addUserOprationLog(long uid, USER_OPRATE_TYPE action, long topicId) {
		UserVisitLog record = new UserVisitLog();
		record.setAction(action.toString());
		record.setCreateTime(new Date());
		record.setExtra(null);
		record.setTopicId(topicId);
		record.setUid(uid);
		userVisitLogMapper.insertSelective(record);
		
	}

	@Override
	public void addUserOprationLog(long uid, USER_OPRATE_TYPE action, String extra) {
		UserVisitLog record = new UserVisitLog();
		record.setAction(action.toString());
		record.setCreateTime(new Date());
		record.setExtra(extra);
		record.setTopicId(-1L);
		record.setUid(uid);
		userVisitLogMapper.insertSelective(record);
	}

}
