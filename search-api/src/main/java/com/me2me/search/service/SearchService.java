package com.me2me.search.service;

import java.util.List;
import java.util.Map;

import com.me2me.common.web.Response;
import com.me2me.search.dto.RecommendTagDto;
import com.me2me.search.model.SearchHotKeyword;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
public interface SearchService {

    Response search(String keyword,int page,int pageSize,long uid,int isSearchFans);

    Response assistant(String keyword);
    
    Response associatedWord(String keyword);

    Response allSearch(long uid, String keyword, int searchType, int contentType, int page, int pageSize);
 
    Response recWord();
    
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
	 * @throws Exception
	 */
	int indexSearchHistory(boolean fully) throws Exception;
	/**
	 * 搜索结果包装成json,仅供后台使用。
	 * @author zhangjiwei
	 * @date Apr 8, 2017
	 * @param key
	 * @param type
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String searchForJSON(String key,String type,int contentType,int page,int pageSize);
	/**
	 * 所有热门词
	 * @author zhangjiwei
	 * @date Apr 8, 2017
	 * @return
	 */
	List<SearchHotKeyword> getAllHotKeyword();
	
	SearchHotKeyword getHotKeywordById(int id);
	
	void addHotKeyword(SearchHotKeyword hk);
	
	void updateHotKeyword(SearchHotKeyword hk);
	
	void delHotKeyword(int id);
	/**
	 * 推荐用户
	 * @author zhangjiwei
	 * @date Apr 20, 2017
	 * @param uid 推荐与此用户相关的用户。
	 * @param page
	 * @param pageSize
	 * @return
	 */
	 public Response recommendUser(long uid,int page,int pageSize);

	 public Response recommendIndex(long uid,int page, String token, String version);
/**
 * 推王国
 * @author zhangjiwei
 * @date Apr 21, 2017
 * @param uid
 * @param page
 * @param pageSize
 * @return
 */
	Response recommendKingdom(long uid,int page,int pageSize);
	
	Response recContentDislike(long uid, long cid, int type);
	
	List<Map<String, Object>> topicAtUserList(String keyword, long searchUid);
	/**
	 * 索引标签样本 。
	 * @author zhangjiwei
	 * @date Jun 30, 2017
	 * @param fully
	 * @return
	 * @throws Exception
	 */
	int indexTagSample() throws Exception;
	
	Response<RecommendTagDto> recommendTags(String content,int count);
}
