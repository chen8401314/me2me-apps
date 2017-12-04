package com.me2me.live.service;

import com.me2me.common.web.Response;
import com.me2me.live.model.TopicCategory;

/**
 * liveService 太大了，这个是拆分扩展方法。
 * @author zhangjiwei
 * @date Sep 19, 2017
 */
public interface LiveExtService {
	/**
	 * 查询所有分类。
	 * @author zhangjiwei
	 * @date Sep 19, 2017
	 * @return
	 */
	Response category();
	/**
	 * 查询王国分类分页
	 * @author zhangjiwei
	 * @date Sep 19, 2017
	 * @param uid
	 * @param kcid 分类ID
	 * @param page 分页
	 * @return
	 */
	Response kingdomByCategory(long uid,int kcid,int page);
	
	void addCategory(TopicCategory category);
	
	void updateCategory(TopicCategory category);
	TopicCategory getCategoryById(int id);
	
	/**
	 * 王国图库左右滑动图片获取接口
	 * @param uid
	 * @param topicId
	 * @param fid
	 * @param imageName
	 * @param type
	 * @return
	 */
	Response getKingdomImage(long uid,long topicId,long fid,String imageName,int type);
	
	/**
	 * 王国图库月份列表接口
	 * @param uid
	 * @param topicId
	 * @param fid
	 * @return
	 */
	Response kingdomImageMonth(long uid, long topicId, long fid);
	
	/**
	 * 王国图库按月份查询接口
	 * @param uid
	 * @param topicId
	 * @param month
	 * @return
	 */
	Response kingdomImageList(long uid, long topicId, String month);
	
	/**
	 * 分享卡片信息获取接口
	 * @param targetUid
	 * @param topicId
	 * @param fid
	 * @return
	 */
	Response shareImgInfo(long uid, long targetUid, long topicId, long fid);
	
	/**
	 * 王国图片视频点赞接口
	 * @param uid
	 * @param fid
	 * @param imageName
	 * @param action
	 * @return
	 */
	Response fragmentLike(long uid, long topicId, long fid, String imageName, int action);
}
