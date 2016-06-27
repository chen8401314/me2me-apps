package com.me2me.sns.mapper;

import com.me2me.sns.model.SnsCircle;
import com.me2me.sns.model.SnsCircleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SnsCircleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int countByExample(SnsCircleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int deleteByExample(SnsCircleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int insert(SnsCircle record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int insertSelective(SnsCircle record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    List<SnsCircle> selectByExample(SnsCircleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    SnsCircle selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int updateByExampleSelective(@Param("record") SnsCircle record, @Param("example") SnsCircleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int updateByExample(@Param("record") SnsCircle record, @Param("example") SnsCircleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int updateByPrimaryKeySelective(SnsCircle record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sns_circle
     *
     * @mbggenerated Mon Jun 27 15:11:26 CST 2016
     */
    int updateByPrimaryKey(SnsCircle record);
}