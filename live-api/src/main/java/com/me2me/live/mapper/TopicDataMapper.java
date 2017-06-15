package com.me2me.live.mapper;

import com.me2me.live.model.TopicData;
import com.me2me.live.model.TopicDataExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TopicDataMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int countByExample(TopicDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int deleteByExample(TopicDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int insert(TopicData record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int insertSelective(TopicData record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    List<TopicData> selectByExample(TopicDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    TopicData selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int updateByExampleSelective(@Param("record") TopicData record, @Param("example") TopicDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int updateByExample(@Param("record") TopicData record, @Param("example") TopicDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int updateByPrimaryKeySelective(TopicData record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table topic_data
     *
     * @mbggenerated Thu Jun 15 09:18:03 CST 2017
     */
    int updateByPrimaryKey(TopicData record);
}