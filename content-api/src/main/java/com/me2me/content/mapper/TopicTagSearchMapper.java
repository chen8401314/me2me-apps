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
     * @date Jun 29, 2017
     * @param order 排序规则
     * @param page 当前页
     * @param pageSize 页大小
     * @return
     */
	public List<Map<String, Object>> getKingdomsByTag(
			@Param("tag") String tag,
			@Param("order")String order,
			@Param("page")int page, 
			@Param("pageSize")int pageSize);
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
}
