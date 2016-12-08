package com.me2me.activity.mapper;

import com.me2me.activity.dto.BlurSearchDto;
import com.me2me.activity.model.Atopic;
import com.me2me.activity.model.AtopicExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface AtopicMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int countByExample(AtopicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int deleteByExample(AtopicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int insert(Atopic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int insertSelective(Atopic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    List<Atopic> selectByExample(AtopicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    Atopic selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int updateByExampleSelective(@Param("record") Atopic record, @Param("example") AtopicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int updateByExample(@Param("record") Atopic record, @Param("example") AtopicExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int updateByPrimaryKeySelective(Atopic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_topic
     *
     * @mbggenerated Wed Dec 07 17:44:28 CST 2016
     */
    int updateByPrimaryKey(Atopic record);

    int updateAtopicStatus(Map map);

    List<BlurSearchDto> getTopicByBoy(Map map);

    List<BlurSearchDto> getTopicByGirl(Map map);

}