package com.me2me.content.service;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.DeleteContentDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.WriteTagDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
public interface ContentService {


    /**
     * 精选接口
     * @return
     */
    Response highQuality(int sinceId);

    /**
     * 广场列表
     * @param sinceId
     * @return
     */
    Response square(int sinceId);

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
     * 是否点赞
     * @param uid
     * @param cid
     * @return
     */
    int isLike(long uid,long cid);

    /**
     * 获取内容详情
     * @param id
     * @return
     */
    Response getContentDetail(long id);

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
     * @param sinceId
     * @return
     */
    Response getContentFeeling(long cid,int sinceId);
}
