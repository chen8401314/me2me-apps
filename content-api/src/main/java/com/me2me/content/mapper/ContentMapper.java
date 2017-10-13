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
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int countByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int deleteByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int insert(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int insertSelective(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    List<Content> selectByExampleWithBLOBs(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    List<Content> selectByExample(ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    Content selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int updateByExampleSelective(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int updateByExampleWithBLOBs(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int updateByExample(@Param("record") Content record, @Param("example") ContentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int updateByPrimaryKeySelective(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
     */
    int updateByPrimaryKeyWithBLOBs(Content record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table content
     *
     * @mbggenerated Tue Aug 08 20:09:05 CST 2017
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

    List<Content> loadNewestContent4Old(@Param("uid") long uid,@Param("sinceId") long sinceId, @Param("blacklistUids") List<Long> blacklistUids);
    
    List<Content> loadNewestContent(@Param("uid") long uid,@Param("sinceId") long sinceId, @Param("blacklistUids") List<Long> blacklistUids);

    int countFragment(CountFragmentDto countFragmentDto);

    List<ResultKingTopicDto> kingTopic(KingTopicDto kingTopic);

    int getTopicCount(long topicId);

    Long getTopicLastUpdateTime(long topicId);

    List<Content> loadMyPublishDataByType(MyPublishDto dto);
    
    List<Content> loadMyKingdom(MyPublishDto dto);
    
    List<Content> getMyOwnKingdom(MyPublishDto dto);
    
    List<Content> loadMyJoinKingdom(MyPublishDto dto);
    
    int countMyJoinKingdom(MyPublishDto dto);

    int countMyPublishByType(MyPublishDto dto);

    int countMyKingdom(MyPublishDto dto);
    
    void clearData();

    /**
     * 
     * @param sinceId
     * @param flag  0 2.2.0版本前
     * @return
     */
    List<Content2Dto> loadHottestContentByUpdateTime(@Param("sinceId") long sinceId, @Param("flag") int flag);


    List<Content2Dto> getHotContentByType(@Param("hq") HotQueryDto hotQueryDto);

    List<Content2Dto> getHotContentListByType(@Param("hq") HotQueryDto hotQueryDto);

    List<Content2Dto> getHotContentByRedis(@Param("uid") long uid,@Param("ids") List<String> ids, @Param("blacklistUids") List<Long> blacklistUids,@Param("blackTagIds")String blackTagIds);


    List<Content> loadMyPublishUgcData(Map map);

    List<Content> loadMyPublishLiveData(Map map);

    List<Content> loadMyPublishLiveData2(Map map);

    List<Content> getAttention(Map map);
    
    List<Content> getTagTopicList(@Param("tagId") long tagId, @Param("blacklistUids") List<Long> blacklistUids,@Param("page") int page,@Param("pageSize") int pageSize);
}