package com.me2me.search.service;
import java.util.List;

import com.me2me.search.dto.SearchResult;
import com.me2me.search.dto.SearchResultArticle;
import com.me2me.search.dto.SearchResultKingdom;
import com.me2me.search.dto.SearchResultUGC;
import com.me2me.search.dto.SearchResultUser;

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
	public SearchResult<SearchResultUGC> queryUGC(String content,int page,int pageSize);
	/**
	 * 搜索王国
	 * @param title
	 * @return
	 */
	public SearchResult<SearchResultKingdom> queryKingdom(String content,int page,int pageSize);
	/**
	 * 搜索爬虫文章库
	 * @param content
	 * @param beginPos
	 * @param pageSize
	 * @return
	 */
	public SearchResult<SearchResultArticle> queryArticle(String content,int page,int pageSize);
	
	/**
	 * 搜人
	 * @param content
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public SearchResult<SearchResultUser> queryUsers(String content,int page,int pageSize);
	/**
	 * 保存搜索记录，为热门词推荐提供基础依据
	 * @param userId
	 * @param searchContent
	 */
	public void logSearchHistory(String searchContent);
	/**
	 * 根据输入推荐热词，类似百度的搜索框
	 * @param key 用户输入的关键字
	 * @param isPinYing 是否是拼音输入，如果是拼音，那么自动推荐拼音关联的关键字。
	 * @param count 推荐数量。
	 * @return
	 */
	public List<String> recommendKeywordList(String key,boolean isPinYing,int count);
	/**
	 * 取热门词,自带缓存1分钟。
	 * @param count
	 * @return
	 */
	public List<String> getHotKeywordList(int count);
	
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
	 * 搜索历史索引
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param fully
	 * @return
	 */
	public int indexSearchHistory(boolean fully) throws Exception;
}
