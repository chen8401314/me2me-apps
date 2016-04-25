package com.me2me.live.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.me2me.common.web.Specification;
import com.me2me.live.mapper.LiveCpiMapper;
import com.me2me.live.mapper.LiveFavoriteMapper;
import com.me2me.live.mapper.TopicFragmentMapper;
import com.me2me.live.mapper.TopicMapper;
import com.me2me.live.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    private LiveCpiMapper liveCpiMapper;

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

    public TopicFragment getLastTopicFragment(long topicId ){
        TopicFragmentExample example = new TopicFragmentExample();
        TopicFragmentExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
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
        topicMapper.updateByPrimaryKey(topic);
    }

    public List<Topic> getMyLives(long uid ,long sinceId ,List<Long> topics){
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andIdLessThan(sinceId);
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        TopicExample.Criteria criteriaOr = example.createCriteria();
        if(topics != null && topics.size() > 0) {
            example.or(criteriaOr.andIdIn(topics));
        }
        example.setOrderByClause("id desc,status asc limit 10" );
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
        criteria.andStatusNotEqualTo(Specification.LiveStatus.REMOVE.index);
        example.setOrderByClause("id desc,status asc limit 10");
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

    public LiveCpi getLiveCpi(long topicId){
        LiveCpiExample example = new LiveCpiExample();
        LiveCpiExample.Criteria criteria = example.createCriteria();
        criteria.andTopicIdEqualTo(topicId);
        List<LiveCpi> liveCpiList = liveCpiMapper.selectByExample(example);
        return (liveCpiList != null && liveCpiList.size() > 0) ? liveCpiList.get(0) : null;
    }
}
