package com.me2me.content.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * 标签相关
 * @author zhangjiwei
 * @date Jun 30, 2017
 */
public interface TopicTagSearchMapper {
	  /**
     * 按标签查询王国
     * @author zhangjiwei
	 * @param uid 
     * @date Jun 29, 2017
     * @param order 排序规则
     * @param page 当前页
     * @param pageSize 页大小
     * @return
     */
	public List<Map<String, Object>> getKingdomsByTag(
			@Param("uid") long uid,
			@Param("tag") String tag,
			@Param("order")String order,
			@Param("page")int page, 
			@Param("pageSize")int pageSize,
			@Param("blacklistUids")List<Long> blacklistUids);
	/**
	 * 获取单个标签的价值，米汤币，人数信息
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @return
	 */
	public Map<String,Object> getTagPriceAndKingdomCount(@Param("tag")String tag);
	/**
	 * 获取系统标签统计信息（总价值，对应的用户喜好）
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @return
	 */
	public List<Map<String,Object>> getSysTagCountInfo();
	/**
	 * 取父标签的子标签。
	 * @author zhangjiwei
	 * @date Jun 29, 2017
	 * @param pid
	 * @return
	 */
	@Select("select * from topic_tag where status=0 and pid=#{pid}")
	public List<Map<String, Object>> getSubTagsByParentTagId(@Param("pid")long pid);
	/**
	 * 获取用户爱好
	 * @author zhangjiwei
	 * @date Jun 30, 2017
	 * @param uid
	 * @return
	 */
	@ResultType(Integer.class)
	@Select("select hobby from user_hobby where uid=#{uid}")
	public List<Integer> getUserHobbyIdsByUid(@Param("uid")long uid);
	/**
	 * 获取TAG和子TAG的所有王国ID
	 * @author zhangjiwei
	 * @date Jul 7, 2017
	 * @param tag
	 * @return
	 */
	public List<Integer> getTopicIdsByTag(@Param("tag")String tag);
	/**
	 * 取用户感兴趣的标签，根据用户行为习惯，后台统计标签分数。
	 * @author zhangjiwei
	 * @date Aug 10, 2017
	 * @param favoScore 用户爱好对应的预设分数
	 * @param uid
	 * @return
	 */
	public List<String> getUserFavoTags(@Param("uid")long uid,@Param("count")int count);
	/**
	 * 取标签的子系统标签
	 * @author zhangjiwei
	 * @date Sep 1, 2017
	 * @param tagName
	 * @return
	 */
	@ResultType(String.class)
	@Select("select tag from topic_tag where status=0 and is_sys=1 and pid=(select id from topic_tag where tag=#{0})")
	public List<String> getSubSysTags(String tagName);
	
	public List<String> getTagAndSubTag(@Param("pids")List<String> userNotLike);
	/**
	 * 取用户标签喜好，包括子标签。
	 * @author zhangjiwei
	 * @date Sep 4, 2017
	 * @param uid
	 * @param isLike 1喜欢 0不喜欢。
	 * @return
	 */
	public List<String> getUserLikeTagAndSubTag(@Param("uid")long uid,@Param("isLike")int isLike);

}
