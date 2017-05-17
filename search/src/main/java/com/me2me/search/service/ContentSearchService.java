package com.me2me.search.service;
import java.util.List;

import org.springframework.data.elasticsearch.core.FacetedPage;

import com.me2me.search.dto.RecommendKingdom;
import com.me2me.search.dto.RecommendUser;
import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UgcEsMapping;
import com.me2me.search.esmapping.UserEsMapping;
import com.me2me.user.model.UserProfile;

/**
 * 内容搜索服务，提供UGC、王国、文章库的搜索
 * 带热词推荐 
 * @author zhangjiwei
 * @date 2016年10月10日
 *
 */
public interface ContentSearchService {
	/**
	 * 搜索ugc内容
	 * @param title
	 * @return
	 */
	FacetedPage<UgcEsMapping> queryUGC(String content,int page,int pageSize);
	/**
	 * 搜索王国
	 * @param content
	 * @param contentType 王国类型 0全部，1个人王国，2聚合王国
	 * @param page
	 * @param pageSize
	 * @return
	 */
	FacetedPage<TopicEsMapping> queryKingdom(String content, int contentType, int page,int pageSize);
		
	/**
	 * 搜人
	 * @param content
	 * @param page
	 * @param pageSize
	 * @return
	 */
	FacetedPage<UserEsMapping> queryUsers(String content,int page,int pageSize);
	
	/**
	 * 给王国at用户使用的搜人
	 * @param content
	 * @param page
	 * @param pageSize
	 * @return
	 */
	FacetedPage<UserEsMapping> queryUsers4AtUserList(String content,int page,int pageSize, long searchUid);
	/**
	 * 保存搜索记录，为热门词推荐提供基础依据
	 * @param userId
	 * @param searchContent
	 */
	void addSearchHistory(String searchContent);
	/**
	 * 根据输入联想，类似百度的搜索框
	 * @param key 用户输入的关键字
	 * @param count 推荐数量。
	 * @return
	 */
	List<String> associateKeywordList(String key,int count);
	/**
	 * 取热门词,自带缓存1分钟。
	 * @param dbCount 从数据库取热词数量，此内容是小编维护的
	 * @param esCount 从ES中取出按热度排序的搜索关键字数量。
	 * @return
	 */
	List<String> getHotKeywordList(int dbCount,int esCount);
	
	 /**
     * 索引用户数据
     * @author zhangjiwei
     * @date Apr 7, 2017
     * @param fully 是否全量
     * @return 索引过的数据量
     */
	int indexUserData(boolean fully) throws Exception;
	/**
	 * 索引UGC数据
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param fully 是否全量
	 * @return 索引过的数据量
	 */
	int indexUgcData(boolean fully) throws Exception;
	/**
	 * 索引王国数据。
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param fully 是否全量
	 * @return 索引过的数据量
	 */
	int indexKingdomData(boolean fully) throws Exception;
	/**
	 * 索引搜索历史
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param fully
	 * @return
	 */
	int indexSearchHistory(boolean fully) throws Exception;
	/**
	 * 获取与本用户相似的用户列表。
	 * @author zhangjiwei
	 * @date Apr 18, 2017
	 * @param uid
	 * @return
	 */
	public List<RecommendUser> getRecommendUserList(long uid,int page,int pageSize, List<Long> noUids);
	
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
	public List<RecommendKingdom>  getRecommendKingdomList(long uid,int page,int pageSize);
	
	/**
	 * 获取推荐的王国列表（搜索引擎中的数据）
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<TopicEsMapping> getTopicEsMappingList(long uid,List<Long> noIds,int page,int pageSize);
	
	/**
	 * 推文章。
	 * @author zhangjiwei
	 * @date May 17, 2017
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<RecommendKingdom>  getRecommendArticleList(long uid,int page,int pageSize);
}
