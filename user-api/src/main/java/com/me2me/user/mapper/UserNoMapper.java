package com.me2me.user.mapper;

import com.me2me.user.model.UserNo;
import com.me2me.user.model.UserNoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserNoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int countByExample(UserNoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int deleteByExample(UserNoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int deleteByPrimaryKey(Long uid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int insert(UserNo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int insertSelective(UserNo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    List<UserNo> selectByExample(UserNoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    UserNo selectByPrimaryKey(Long uid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int updateByExampleSelective(@Param("record") UserNo record, @Param("example") UserNoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int updateByExample(@Param("record") UserNo record, @Param("example") UserNoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int updateByPrimaryKeySelective(UserNo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_no
     *
     * @mbggenerated Tue May 10 11:38:43 CST 2016
     */
    int updateByPrimaryKey(UserNo record);
}