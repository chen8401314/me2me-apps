package com.me2me.activity.mapper;

import com.me2me.activity.model.AdoubleTopicApply;
import com.me2me.activity.model.AdoubleTopicApplyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdoubleTopicApplyMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int countByExample(AdoubleTopicApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int deleteByExample(AdoubleTopicApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int insert(AdoubleTopicApply record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int insertSelective(AdoubleTopicApply record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    List<AdoubleTopicApply> selectByExample(AdoubleTopicApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    AdoubleTopicApply selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int updateByExampleSelective(@Param("record") AdoubleTopicApply record, @Param("example") AdoubleTopicApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int updateByExample(@Param("record") AdoubleTopicApply record, @Param("example") AdoubleTopicApplyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int updateByPrimaryKeySelective(AdoubleTopicApply record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_double_topic_apply
     *
     * @mbggenerated Thu Dec 08 20:49:19 CST 2016
     */
    int updateByPrimaryKey(AdoubleTopicApply record);
}