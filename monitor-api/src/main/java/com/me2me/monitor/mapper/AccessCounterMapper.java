package com.me2me.monitor.mapper;

import com.me2me.monitor.model.AccessCounter;
import com.me2me.monitor.model.AccessCounterExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AccessCounterMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int countByExample(AccessCounterExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int deleteByExample(AccessCounterExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int insert(AccessCounter record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int insertSelective(AccessCounter record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    List<AccessCounter> selectByExample(AccessCounterExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    AccessCounter selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int updateByExampleSelective(@Param("record") AccessCounter record, @Param("example") AccessCounterExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int updateByExample(@Param("record") AccessCounter record, @Param("example") AccessCounterExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int updateByPrimaryKeySelective(AccessCounter record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table access_counter
     *
     * @mbggenerated Mon Jun 13 20:16:07 CST 2016
     */
    int updateByPrimaryKey(AccessCounter record);
}