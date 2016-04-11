package com.me2me.user.mapper;

import com.me2me.user.model.User;
import com.me2me.user.model.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int countByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int deleteByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int deleteByPrimaryKey(Long uid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    List<User> selectByExample(UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    User selectByPrimaryKey(Long uid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbggenerated Mon Feb 29 16:27:09 CST 2016
     */
    int updateByPrimaryKey(User record);
}