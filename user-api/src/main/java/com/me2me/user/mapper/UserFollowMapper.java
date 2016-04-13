package com.me2me.user.mapper;

import com.me2me.user.dto.FansParamsDto;
import com.me2me.user.dto.FollowParamsDto;
import com.me2me.user.dto.UserFansDto;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserFollowExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserFollowMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int countByExample(UserFollowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int deleteByExample(UserFollowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int insert(UserFollow record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int insertSelective(UserFollow record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    List<UserFollow> selectByExample(UserFollowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    UserFollow selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int updateByExampleSelective(@Param("record") UserFollow record, @Param("example") UserFollowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int updateByExample(@Param("record") UserFollow record, @Param("example") UserFollowExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int updateByPrimaryKeySelective(UserFollow record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_follow
     *
     * @mbggenerated Wed Apr 13 15:53:14 CST 2016
     */
    int updateByPrimaryKey(UserFollow record);

    List<UserFansDto> getFans(FansParamsDto fansParamsDto);

    List<UserFansDto> getFollows(FollowParamsDto followParamsDto);
}