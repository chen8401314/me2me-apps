package com.me2me.content.service;

import com.me2me.common.web.Response;
import com.me2me.content.dto.*;
import com.me2me.content.model.Content;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
public interface ContentService {

    Response recommend(long uid);

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
     * @return
     */
    Response deleteContent(long id);

    /**
     * 获取内容详情
     * @param id
     * @return
     */
    Response getContentDetail(long id,long uid);

    /**
     * 我发布的内容列表
     * @param uid
     * @param sinceId
     * @return
     */
    Response myPublish(long uid ,int sinceId);

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
    ContentH5Dto getContent(long id);

    /**
     *
     * @param targetUid
     * @param sourceUid
     * @return
     */
    Response getUserData(long targetUid ,long sourceUid);

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
    Response getSelectedData(int sinceId,long uid);

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

    Response getActivities(int sinceId,long uid);

    Response getHottest(int sinceId,long uid);

    Response getNewest(int sinceId,long uid);

    Response getAttention(int sinceId,long uid);

    Response createReview(ReviewDto reviewDto);

    Response option(long id, int optionAction, int action);

    Content getContentByTopicId(long topicId);

    Response showUGCDetails(long id);

    Response reviewList(long cid,long sinceId);

    void updateContentById(Content content);

    int isLike(long cid,long uid);

}
