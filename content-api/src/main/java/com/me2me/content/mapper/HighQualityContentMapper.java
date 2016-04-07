package com.me2me.content.mapper;

import com.me2me.content.model.HighQualityContent;
import com.me2me.content.model.HighQualityContentExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HighQualityContentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int countByExample(HighQualityContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int deleteByExample(HighQualityContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int insert(HighQualityContent record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int insertSelective(HighQualityContent record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    List<HighQualityContent> selectByExample(HighQualityContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    HighQualityContent selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int updateByExampleSelective(@Param("record") HighQualityContent record, @Param("example") HighQualityContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int updateByExample(@Param("record") HighQualityContent record, @Param("example") HighQualityContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int updateByPrimaryKeySelective(HighQualityContent record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table high_quality_content
     *
     * @mbggenerated Thu Apr 07 14:52:39 CST 2016
     */
    int updateByPrimaryKey(HighQualityContent record);
}