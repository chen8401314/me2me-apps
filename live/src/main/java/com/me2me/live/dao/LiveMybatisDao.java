package com.me2me.live.dao;

import com.google.common.collect.Lists;
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
        example.setOrderByClause("id asc limit 10");
        return topicFragmentMapper.selectByExample(example);
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

    public List<Topic> getMyLives(long uid){
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andStatusEqualTo(0);
        List<Long> topicList = getTopicId(uid);
        if(topicList != null && topicList.size() >0) {
            example.or(criteria.andIdIn(topicList));
        }
        return topicMapper.selectByExample(example);
    }

    public List<Long> getTopicId(long uid){
        List result = Lists.newArrayList();
        LiveFavoriteExample example = new LiveFavoriteExample();
        LiveFavoriteExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<LiveFavorite> liveFavoriteList = liveFavoriteMapper.selectByExample(example);
        for(LiveFavorite liveFavorite : liveFavoriteList){
            result.add(liveFavorite.getId());
        }
        return result;
    }

    public List<Topic> getLives(){
        TopicExample example = new TopicExample();
        TopicExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
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
}
