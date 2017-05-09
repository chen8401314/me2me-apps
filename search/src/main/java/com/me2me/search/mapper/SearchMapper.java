package com.me2me.search.mapper;

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
	 * 查询指定王国的所有评论。
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param topicId
	 * @return
	 */
	@ResultType(Map.class)
	@Select("select topic_id, fragment from topic_fragment where topic_id in(${ids}) and status=1 and type=0 and content_type=0 order by id desc")
	public List<Map<String,Object>> getKingdomFragmentsByTopicIds(@Param("ids")String topicIds);
	
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
    @Select("select * from search_history_count where DATE_FORMAT(last_query_date,'%Y-%m-%d %T') >= #{0} and DATE_FORMAT(last_query_date,'%Y-%m-%d %T') <= #{1} limit #{2},#{3}")
    List<SearchHistoryEsMapping> getSearchHistoryPageByDate(String dateBegin,String dateEnd,int skip,int limit);
    
    
    /**
     * 取用户的偏好。
     * @author zhangjiwei
     * @date Apr 19, 2017
     * @param uid
     * @return
     */
    @ResultType(String.class)
    @Select("select d.value from user_hobby h left join dictionary d on h.hobby=d.id where h.uid=#{0}")
    List<String> getUserHobby(long uid);
    
    /**
     * 获取用户的爱好ID
     * @param uid
     * @return
     */
    @ResultType(Long.class)
    @Select("select h.hobby from user_hobby h where h.uid=#{0}")
    List<Long> getUserHobbyIds(long uid);
    
    /**
     * 取用户的偏好。
     * @author zhangjiwei
     * @date Apr 19, 2017
     * @param uid
     * @return
     */
    @ResultType(Map.class)
    @Select("select h.uid,d.value from user_hobby h left join dictionary d on h.hobby=d.id where h.uid in (${ids})")
    List<Map<String,Object>> getUserHobbyByUids(@Param("ids")String uids);
}