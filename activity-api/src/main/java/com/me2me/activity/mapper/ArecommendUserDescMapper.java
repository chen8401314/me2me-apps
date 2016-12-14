package com.me2me.activity.mapper;

import com.me2me.activity.model.ArecommendUserDesc;
import com.me2me.activity.model.ArecommendUserDescExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArecommendUserDescMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int countByExample(ArecommendUserDescExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int deleteByExample(ArecommendUserDescExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int insert(ArecommendUserDesc record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int insertSelective(ArecommendUserDesc record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    List<ArecommendUserDesc> selectByExample(ArecommendUserDescExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    ArecommendUserDesc selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int updateByExampleSelective(@Param("record") ArecommendUserDesc record, @Param("example") ArecommendUserDescExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int updateByExample(@Param("record") ArecommendUserDesc record, @Param("example") ArecommendUserDescExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int updateByPrimaryKeySelective(ArecommendUserDesc record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table a_recommend_user_desc
     *
     * @mbggenerated Wed Dec 14 15:38:13 CST 2016
     */
    int updateByPrimaryKey(ArecommendUserDesc record);
}