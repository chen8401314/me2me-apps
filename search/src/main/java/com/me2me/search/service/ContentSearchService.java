package com.me2me.search.service;
import java.util.List;

import org.springframework.data.elasticsearch.core.FacetedPage;

import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UgcEsMapping;
import com.me2me.search.esmapping.UserEsMapping;

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
}
