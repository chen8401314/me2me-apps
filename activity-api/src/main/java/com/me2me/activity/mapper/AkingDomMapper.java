package com.me2me.activity.mapper;

import com.me2me.activity.dto.BlurSearchDto;
import com.me2me.activity.model.AkingDom;
import com.me2me.activity.model.AkingDomExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface AkingDomMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int countByExample(AkingDomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int deleteByExample(AkingDomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int insert(AkingDom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int insertSelective(AkingDom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    List<AkingDom> selectByExample(AkingDomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    AkingDom selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int updateByExampleSelective(@Param("record") AkingDom record, @Param("example") AkingDomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int updateByExample(@Param("record") AkingDom record, @Param("example") AkingDomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int updateByPrimaryKeySelective(AkingDom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_kingdom
     *
     * @mbggenerated Fri Jan 06 16:58:28 CST 2017
     */
    int updateByPrimaryKey(AkingDom record);

    List<BlurSearchDto> getAllNewYearLive(Map map);

    List<AkingDom> getActualList(Map map);

    int getRanksAkingDom(Map map);

}