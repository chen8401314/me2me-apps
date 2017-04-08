package com.me2me.search.db.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.me2me.search.esmapping.SearchHistoryEsMapping;
import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UgcEsMapping;
import com.me2me.search.esmapping.UserEsMapping;
import com.me2me.search.model.SearchHotKeyword;


/**
 * 搜索相关数据操作。
 * @author zhangjiwei
 * @date Apr 5, 2017
 */
public interface SearchMapper  {
	/**
	 * 获取指定数量的热门关键字。
	 * @author zhangjiwei
	 * @date Apr 5, 2017
	 * @param maxCount
	 * @return
	 */
	public List<SearchHotKeyword> getHotKeywords(int maxCount);
	/**
	 * 查询UGC
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param dateBegin
	 * @param dateEnd
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<UserEsMapping> getUserPageByUpdateDate(
			@Param("dateBegin")String dateBegin,
			@Param("dateEnd")String dateEnd,
			@Param("skip")int skip,
			@Param("limit")int limit);
	/**
	 * 查询UGC
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param dateBegin
	 * @param dateEnd
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<UgcEsMapping> getUgcPageByUpdateDate(
			@Param("dateBegin")String dateBegin,
			@Param("dateEnd")String dateEnd,
			@Param("skip")int skip,
			@Param("limit")int limit);
	/**
	 * 查询王国。
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param dateBegin
	 * @param dateEnd
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<TopicEsMapping> getKingdomPageByUpdateDate(
			@Param("dateBegin")String dateBegin,
			@Param("dateEnd")String dateEnd,
			@Param("skip")int skip,
			@Param("limit")int limit);
	
	/**
	 * 查询指定王国的所有评论。
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param topicId
	 * @return
	 */
	public List<String> getKingdomFragmentsByTopicId(int topicId);
	
    /**
     * 按日期查询关键字排行 。
     * @author zhangjiwei
     * @date Apr 7, 2017
     * @param dateBegin
     * @param dateEnd
     * @param skip
     * @param limit
     * @return
     */
    @ResultType(SearchHistoryEsMapping.class)
    @Select("select * from search_history_count where DATE_FORMAT(last_query_date,'%Y-%m-%d') >= #{0} and DATE_FORMAT(last_query_date,'%Y-%m-%d') <= #{1} limit #{2},#{3}")
    List<SearchHistoryEsMapping> getSearchHistoryPageByDate(String dateBegin,String dateEnd,int skip,int limit);
}