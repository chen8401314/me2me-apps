package com.me2me.live.dao;

import com.me2me.live.mapper.TopicFragmentMapper;
import com.me2me.live.mapper.TopicMapper;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicExample;
import com.me2me.live.model.TopicFragment;
import com.me2me.live.model.TopicFragmentExample;
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

    public void finishMyLive(long topicId){
        topicMapper.deleteByPrimaryKey(topicId);
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
}
