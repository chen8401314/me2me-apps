package com.me2me.content.mapper;

import com.me2me.content.dto.*;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ContentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int countByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int deleteByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int insert(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int insertSelective(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    List<Content> selectByExampleWithBLOBs(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    List<Content> selectByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    Content selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int updateByExampleSelective(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int updateByExampleWithBLOBs(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int updateByExample(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int updateByPrimaryKeySelective(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int updateByPrimaryKeyWithBLOBs(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Mon Oct 31 17:56:58 CST 2016
     */
    int updateByPrimaryKey(Content record);

    List<Content> loadSquareData(int sinceId);

    List<Content> loadHighQualityData(int sinceId);

    List<Content> loadMyPublishData(Map map);

    List<Content> loadSelectedData(int sinceId);

    List<Content> loadActivityData(int sinceId);

    Integer getTopicStatus(long topicId);

    void deleteTopicById(long topicId);

    int isFavorite(IsFavoriteDto isFavoriteDto);

    List<Content> loadHottestContent(int sinceId);

    List<Content> loadHottestTopsContent(int flag);

    List<Content> loadNewestContent(int sinceId, int flag);

    int countFragment(CountFragmentDto countFragmentDto);

    List<ResultKingTopicDto> kingTopic(KingTopicDto kingTopic);

    int getTopicCount(long topicId);

    Long getTopicLastUpdateTime(long topicId);

    List<Content>loadMyPublishDataByType(MyPublishDto dto);

    int countMyPublishByType(MyPublishDto dto);

    void clearData();

    /**
     * 
     * @param sinceId
     * @param flag  0 2.2.0版本前
     * @return
     */
    List<Content2Dto> loadHottestContentByUpdateTime(long sinceId, int flag);

    List<Content> loadMyPublishUgcData(Map map);

    List<Content> loadMyPublishLiveData(Map map);

    List<Content> loadMyPublishLiveData2(Map map);

    List<Content> getAttention(Map map);
}