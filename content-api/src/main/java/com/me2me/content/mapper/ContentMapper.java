package com.me2me.content.mapper;

import com.me2me.content.dto.Content2Dto;
import com.me2me.content.dto.CountFragmentDto;
import com.me2me.content.dto.IsFavoriteDto;
import com.me2me.content.dto.KingTopicDto;
import com.me2me.content.dto.MyPublishDto;
import com.me2me.content.dto.ResultKingTopicDto;
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
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int countByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int deleteByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int insert(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int insertSelective(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    List<Content> selectByExampleWithBLOBs(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    List<Content> selectByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    Content selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int updateByExampleSelective(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int updateByExampleWithBLOBs(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int updateByExample(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int updateByPrimaryKeySelective(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
     */
    int updateByPrimaryKeyWithBLOBs(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Thu Jul 06 11:33:57 CST 2017
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

    List<Content> loadHottestTopsContent(@Param("flag") int flag);

    List<Content> loadNewestContent(@Param("sinceId") int sinceId, @Param("flag") int flag);

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
    List<Content2Dto> loadHottestContentByUpdateTime(@Param("sinceId") long sinceId, @Param("flag") int flag);

    /**
     * 
     * @param sinceId
     * @param type   0 ugc+个人王国   1 聚合王国
     * @return
     */
    List<Content2Dto> getHotContentByType(@Param("sinceId") long sinceId, @Param("type") int type, @Param("pageSize") int pageSize,@Param("ids") String ids);


    List<Content2Dto> getHotContentByRedis(@Param("ids") String ids);


    List<Content> loadMyPublishUgcData(Map map);

    List<Content> loadMyPublishLiveData(Map map);

    List<Content> loadMyPublishLiveData2(Map map);

    List<Content> getAttention(Map map);
}