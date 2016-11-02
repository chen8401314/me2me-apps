package com.me2me.activity.mapper;

import com.me2me.activity.model.LuckWinners;
import com.me2me.activity.model.LuckWinnersExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LuckWinnersMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int countByExample(LuckWinnersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int deleteByExample(LuckWinnersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int insert(LuckWinners record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int insertSelective(LuckWinners record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    List<LuckWinners> selectByExample(LuckWinnersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    LuckWinners selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int updateByExampleSelective(@Param("record") LuckWinners record, @Param("example") LuckWinnersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int updateByExample(@Param("record") LuckWinners record, @Param("example") LuckWinnersExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int updateByPrimaryKeySelective(LuckWinners record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table luck_winners
     *
     * @mbggenerated Wed Nov 02 15:55:58 CST 2016
     */
    int updateByPrimaryKey(LuckWinners record);
}