package com.me2me.content.service;

import java.util.List;
import java.util.Map;

import com.me2me.common.web.Response;
import com.me2me.content.dto.*;
import com.me2me.content.model.*;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
public interface ContentService{

    Response recommend(long uid,String emotion);

    /**
     * 精选接口
     * @return
     */
    Response highQuality(int sinceId,long uid);

    /**
     * 广场列表
     * @param sinceId
     * @return
     */
    Response square(int sinceId,long uid);

    /**
     * 发布接口
     * @param contentDto
     * @return
     */
    Response publish(ContentDto contentDto);

    /**
     * 点赞接口
     * @return
     */
    Response like(LikeDto likeDto);

    /**
     * 打标签接口
     * @return
     */
    Response writeTag(WriteTagDto writeTagDto);

    /**
     * 用户发布内容删除
     * @param id
     * @param uid
     * @return
     */
    Response deleteContent(long id, long uid, boolean isSys);

    /**
     * 获取内容详情
     * @param id
     * @return
     */
    Response contentDetail(long id,long uid);

    /**
     * 我发布的内容列表
     * @param uid
     * @param updateTime
     * @return
     */
    Response myPublish(long uid ,long updateTime ,int type ,int sinceId ,int newType,int vFlag);

    /**
     * 内容所有感受列表
     * @param cid
     * @return
     */
   // Response getContentFeeling(long cid, int sinceId);

    /**
     * 根据内容id，返回内容信息给H5
     * @param id
     */
    ContentH5Dto contentH5(long id);

    /**
     *
     * @param targetUid
     * @param sourceUid
     * @return
     */
    Response UserData(long targetUid ,long sourceUid);

    Response UserData2(long targetUid ,long sourceUid,int vFlag);

    /**
     * 小编发布接口
     * @param contentDto
     * @return
     */
    Response editorPublish(ContentDto contentDto);

    /**
     * 小编精选
     * @param sinceId
     * @return
     */
    Response SelectedData(int sinceId,long uid);

    /**
     * 精选首页
     * @return
     */
    Response highQualityIndex(int sinceId,long uid);

    /**
     * 修改内容权限
     * @param rights
     * @param cid
     * @param uid
     * @return
     */
    Response modifyRights(int rights,long cid,long uid);

    Response showContents(EditorContentDto editorContentDto);

    Response Activities(int sinceId,long uid);

    Response getHottest(int sinceId,long uid);

    Response Newest(int sinceId,long uid, int vFlag);

    Response Attention(int sinceId,long uid, int vFlag);

    Response createReview(ReviewDto reviewDto);

    Response option(long id, int optionAction, int action);

    Content getContentByTopicId(long topicId);
    
    List<Content> getContentsByTopicIds(List<Long> topicIds);

    Response showUGCDetails(long id);

    Response reviewList(long cid,long sinceId,int type);

    void updateContentById(Content content);
    
    void addContentLikeByCid(long cid, long addNum);

    int isLike(long cid,long uid);

    int countFragment(long topicId,long uid);

    Response publish2(ContentDto contentDto);

    void createTag(ContentDto contentDto, Content content);

    void createContent(Content content);

    void createContentImage(ContentImage contentImage);

    Content getContentById(long id);

    ContentLikesDetails getContentLikesDetails(ContentLikesDetails contentLikesDetails);

    void createContentLikesDetails(ContentLikesDetails contentLikesDetails);

    void remind(Content content ,long uid ,int type,String arg);

    void remind(Content content ,long uid ,int type,String arg,long atUid);

    void deleteContentLikesDetails(ContentLikesDetails contentLikesDetails);

    Response like2(LikeDto likeDto);

    void createArticleLike(LikeDto likeDto);

    void createArticleReview(ReviewDto reviewDto);

    void createReview2(ReviewDto review);

    Response getArticleComments(long uid,long id);

    Response getArticleReview(long id, long sinceId);

    void createTag(ContentTags contentTags);

    void createContentTagsDetails(ContentTagsDetails contentTagsDetails);

    void createContentArticleDetails(ArticleTagsDetails articleTagsDetails);

    Response writeTag2(WriteTagDto writeTagDto);

    Response modifyPGC(ContentDto contentDto);

    void robotLikes(LikeDto likeDto);

    Response kingTopic(KingTopicDto kingTopic);

    Response myPublishByType(long uid ,int sinceId ,int type,long updateTime,long currentUid,int vFlag);

    void clearData();

    Response Hottest2(int sinceId,long uid, int flag);

    int getUgcCount(long uid);

    int getLiveCount(long uid);
    
    Response deleteReview(ReviewDelDTO delDTO);
    
    Response delArticleReview(ReviewDelDTO delDTO, boolean isSys);

    Response delContentReview(ReviewDelDTO delDTO, boolean isSys);
    
    Response searchUserContent(UserContentSearchDTO searchDTO);
    
    Response delUserContent(int type, long id);
    
    Response hotList(long sinceId, long uid);
    
    Response ceKingdomHotList(long sinceId, long uid);

    /**
     * 榜单列表
     * @return
     */
    Response showBangDanList(int type,long currentUid);

    /**
     * 给IMS系统开的后门，直接通过sql查询结果
     * 其他地方不建议调用本方法
     * @param sql
     * @return
     */
    List<Map<String, Object>> queryEvery(String sql);

    /**
     * 自动榜单列表插入方法(供IMS系统调用)
     * @param insertList
     * @param key
     */
    void insertBillboardList(List<BillBoardList> insertList, String key);

    /**
     * 榜单详情接口
     * @param currentUid
     * @param bid
     * @return
     */
    Response showListDetail(long currentUid, long bid,long sinceId);
}
