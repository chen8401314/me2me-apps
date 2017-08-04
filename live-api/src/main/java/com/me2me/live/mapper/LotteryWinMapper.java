package com.me2me.live.mapper;

import com.me2me.live.model.LotteryWin;
import com.me2me.live.model.LotteryWinExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LotteryWinMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int countByExample(LotteryWinExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int deleteByExample(LotteryWinExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int insert(LotteryWin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int insertSelective(LotteryWin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    List<LotteryWin> selectByExample(LotteryWinExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    LotteryWin selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int updateByExampleSelective(@Param("record") LotteryWin record, @Param("example") LotteryWinExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int updateByExample(@Param("record") LotteryWin record, @Param("example") LotteryWinExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int updateByPrimaryKeySelective(LotteryWin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lottery_win
     *
     * @mbggenerated Fri Aug 04 15:07:45 CST 2017
     */
    int updateByPrimaryKey(LotteryWin record);
}