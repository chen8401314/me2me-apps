package com.me2me.live.mapper;

import com.me2me.live.model.TopicFragment;
import com.me2me.live.model.TopicFragmentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TopicFragmentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int countByExample(TopicFragmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int deleteByExample(TopicFragmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int insert(TopicFragment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int insertSelective(TopicFragment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    List<TopicFragment> selectByExampleWithBLOBs(TopicFragmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    List<TopicFragment> selectByExample(TopicFragmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    TopicFragment selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int updateByExampleSelective(@Param("record") TopicFragment record, @Param("example") TopicFragmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int updateByExampleWithBLOBs(@Param("record") TopicFragment record, @Param("example") TopicFragmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int updateByExample(@Param("record") TopicFragment record, @Param("example") TopicFragmentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int updateByPrimaryKeySelective(TopicFragment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int updateByPrimaryKeyWithBLOBs(TopicFragment record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_fragment
     *
     * @mbggenerated Mon Apr 11 17:47:00 CST 2016
     */
    int updateByPrimaryKey(TopicFragment record);
}