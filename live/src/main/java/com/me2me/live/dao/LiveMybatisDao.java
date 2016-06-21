package com.me2me.live.dao;

import com.google.common.collect.Lists;
import com.me2me.common.web.Specification;
import com.me2me.live.mapper.*;
import com.me2me.live.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
@Repository
public class LiveMybatisDao {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicFragmentMapper topicFragmentMapper;

    @Autowired
    private LiveFavoriteMapper liveFavoriteMapper;

    @Autowired
    private LiveReadHistoryMapper liveReadHistoryMapper;

    @Autowired
    private TopicBarrageMapper topicBarrageMapper;



    public void createTopic(Topic topic){
        topicMapper.insertSelective(topic);
    }

    public Topic getTopicById(long topicId){
        return topicMapper.selectByPrimaryKey(topicId);
    }

    public List<TopicFragment> getTopicFragment(long topicId,long sinceId ){
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdGreaterThan(sinceId);
        example.setOrderByClause("id asc limit 10 "  );
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }

    public List<TopicFragment> getPrevTopicFragment(long topicId,long sinceId ){
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andIdLessThan(sinceId);
        example.setOrderByClause("id desc limit 10 "  );
        return topicFragmentMapper.selectByExampleWithBLOBs(example);
    }

    public TopicFragment getLastTopicFragment(long topicId,long uid ){
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        example.setOrderByClause("id desc limit 1 ");
        List<TopicFragment> topicFragmentList = topicFragmentMapper.selectByExampleWithBLOBs(example);
        return (topicFragmentList != null && topicFragmentList.size() > 0) ? topicFragmentList.get(0) : null;
    }

    public void createTopicFragment(TopicFragment topicFragment){
        topicFragmentMapper.insertSelective(topicFragment);
    }

    public Topic getTopic(long uid,long topicId){
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andIdEqualTo(topicId);
        List<Topic> list = topicMapper.selectByExample(example);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }
    public void updateTopic(Topic topic){
        topicMapper.updateByPrimaryKeySelective(topic);
    }

    public List<Topic> getMyLives(long uid ,long sinceId ,List<Long> topics){
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andIdLessThan(sinceId);
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if(topics != null && topics.size() > 0) {
            criteriaOr.andIdLessThan(sinceId);
            criteriaOr.andUidNotEqualTo(uid);
            criteriaOr.andIdIn(topics);
            example.or(criteriaOr);
        }
        example.setOrderByClause("id desc, status asc limit 10" );
        return topicMapper.selectByExample(example);
    }

    public List<Long> getTopicId(long uid){
        List result = Lists.newArrayList();
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<LiveFavorite> liveFavoriteList = liveFavoriteMapper.selectByExample(example);
        for(LiveFavorite liveFavorite : liveFavoriteList){
            result.add(liveFavorite.getTopicId());
        }
        return result;
    }

    public List<Topic> getLives(long sinceId){
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andIdLessThan(sinceId);
        criteria.andStatusEqualTo(Specification.LiveStatus.LIVING.index);
        example.setOrderByClause(" id desc limit 10 ");
        return topicMapper.selectByExample(example);
    }

    public List<Topic> getLivesByUpdateTime(long updateTime){
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andLongTimeLessThan(updateTime);
        criteria.andStatusEqualTo(Specification.LiveStatus.LIVING.index);
        example.setOrderByClause(" long_time desc limit 10 ");
        return topicMapper.selectByExample(example);
    }

    public LiveFavorite getLiveFavorite(long uid, long topicId){
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andTopicIdEqualTo(topicId);
        List<LiveFavorite> liveFavoriteList = liveFavoriteMapper.selectByExample(example);
        return  (liveFavoriteList != null && liveFavoriteList.size() > 0) ? liveFavoriteList.get(0) : null;
    }

    public void createLiveFavorite(LiveFavorite liveFavorite){
        liveFavoriteMapper.insertSelective(liveFavorite);
    }

    public void deleteLiveFavorite(LiveFavorite liveFavorite){
        liveFavoriteMapper.deleteByPrimaryKey(liveFavorite.getId());
    }

    public int countFragment(long topicId,long uid ){
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidNotEqualTo(uid);
        return topicFragmentMapper.countByExample(example);
    }

    public int countFragmentByUid(long topicId,long uid ){
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        return topicFragmentMapper.countByExample(example);
    }

     public List<LiveFavorite> getFavoriteList(long topicId){
         LiveFavoriteExample example = new LiveFavoriteExample();
         LiveFavoriteExample.Criteria criteria = example.createCriteria();
         criteria.andTopicIdEqualTo(topicId);
         example.setOrderByClause(" id asc limit 20");
         return liveFavoriteMapper.selectByExample(example);
     }

    public LiveReadHistory getLiveReadHistory(long topicId,long uid){
        LiveReadHistoryExample example = new LiveReadHistoryExample();
        LiveReadHistoryExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andUidEqualTo(uid);
        List<LiveReadHistory> liveReadHistories =  liveReadHistoryMapper.selectByExample(example);
        return (liveReadHistories != null && liveReadHistories.size() > 0) ?liveReadHistories.get(0) : null ;
    }

    public void createLiveReadHistory(long topicId,long uid){
        LiveReadHistory liveReadHistory = new LiveReadHistory();
        liveReadHistory.setTopicId(topicId);
        liveReadHistory.setUid(uid);
        liveReadHistoryMapper.insertSelective(liveReadHistory);

    }

    public void createTopicBarrage(TopicBarrage topicBarrage){
        topicBarrageMapper.insertSelective(topicBarrage);
    }

    public List<TopicBarrage> getBarrage(long topicId,long sinceId, long topId ,long bottomId ){
        TopicBarrage topicBarrage = new TopicBarrage();
        topicBarrage.setTopId(topicId);
        topicBarrage.setTopId(topId);
        topicBarrage.setBottomId(bottomId);
        topicBarrage.setId(sinceId);
        return topicBarrageMapper.selectByExampleWithBLOBsDistinct(topicBarrage);
    }

    public TopicBarrage getBarrage(long topicId, long topId ,long bottomId, int type ,long uid ){
        TopicBarrageExample example = new TopicBarrageExample();
        TopicBarrageExample.Criteria criteria =example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        criteria.andTopIdEqualTo(topId);
        criteria.andBottomIdEqualTo(bottomId);
        criteria.andTypeEqualTo(type);
        criteria.andUidEqualTo(uid);
        List<TopicBarrage> topicBarrages = topicBarrageMapper.selectByExampleWithBLOBs(example);
        return com.me2me.common.utils.Lists.getSingle(topicBarrages);
    }

}
