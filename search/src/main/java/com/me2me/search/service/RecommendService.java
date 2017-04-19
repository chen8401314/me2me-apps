package com.me2me.search.service;
import java.util.List;

import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UserFeatureMapping;

/**
 * 内容搜索服务，提供UGC、王国、文章库的搜索
 * 带热词推荐 
 * @author zhangjiwei
 * @date 2016年10月10日
 *
 */
public interface RecommendService {
	/**
	 * 获取与本用户相似的用户列表。
	 * @author zhangjiwei
	 * @date Apr 18, 2017
	 * @param uid
	 * @return
	 */
	public List<UserFeatureMapping> getRecommendUserList(int uid);
	
	/**
	 * 依据内容推荐本内容的标签。
	 * @author zhangjiwei
	 * @date Apr 18, 2017
	 * @param conentet
	 * @return
	 */
	public List<String> getRecommendTagList(String content);
	/**
	 * 获取推荐的王国列表。
	 * @author zhangjiwei
	 * @date Apr 18, 2017
	 * @param uid
	 * @return
	 */
	public List<TopicEsMapping> getRecommendKingomList(int uid);
	
	/**
	 * 索引用户画像
	 * @author zhangjiwei
	 * @date Apr 18, 2017
	 */
	void indexUserFeature(boolean fully);
}
