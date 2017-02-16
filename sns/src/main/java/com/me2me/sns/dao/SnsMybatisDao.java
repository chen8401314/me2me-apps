package com.me2me.sns.dao;

import com.me2me.common.utils.Lists;
import com.me2me.sns.dto.GetSnsCircleDto;
import com.me2me.sns.dto.SnsCircleDto;
import com.me2me.sns.mapper.SnsCircleMapper;
import com.me2me.sns.model.SnsCircle;
import com.me2me.sns.model.SnsCircleExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :14:41
 */
@Repository
public class SnsMybatisDao {

    @Autowired
    private SnsCircleMapper snsCircleMapper;

    public List<SnsCircle> getSnsCircle(long uid,long topicId,int type){
        SnsCircleExample example = new SnsCircleExample();
        SnsCircleExample.Criteria criteria = example.createCriteria();
        criteria.andOwnerEqualTo(uid);
        criteria.andInternalStatusEqualTo(type);
        return snsCircleMapper.selectByExample(example);
    }

    public List<SnsCircleDto> getSnsCircle(GetSnsCircleDto getSnsCircleDto){
        return snsCircleMapper.getSnsCircle(getSnsCircleDto);

    }

    public int getSnsCircleCount(GetSnsCircleDto getSnsCircleDto){
        return snsCircleMapper.getSnsCircleCount(getSnsCircleDto);

    }

    public void createSnsCircle(long uid ,long owner,int internalStatus){
        SnsCircleExample example = new SnsCircleExample();
        SnsCircleExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andOwnerEqualTo(owner);
        List<SnsCircle> list = snsCircleMapper.selectByExample(example);
        SnsCircle snsCircle = Lists.getSingle(list);
        if(snsCircle != null){
            snsCircle.setInternalStatus(internalStatus);
            snsCircleMapper.updateByPrimaryKeySelective(snsCircle);
            return;
        }
        snsCircle = new SnsCircle();
        snsCircle.setUid(uid);
        snsCircle.setInternalStatus(internalStatus);
        snsCircle.setOwner(owner);
        snsCircleMapper.insertSelective(snsCircle);
    }

    public void deleteSnsCircle(long uid ,long owner){
        SnsCircleExample example = new SnsCircleExample();
        SnsCircleExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andOwnerEqualTo(owner);
        snsCircleMapper.deleteByExample(example);
    }

    public void updateSnsCircle(long uid ,long owner ,int internalStatus){
        SnsCircleExample example = new SnsCircleExample();
        SnsCircleExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andOwnerEqualTo(owner);
        List<SnsCircle> list = snsCircleMapper.selectByExample(example);
        SnsCircle snsCircle = Lists.getSingle(list);
        if(snsCircle != null) {
            snsCircle.setInternalStatus(internalStatus);
            snsCircleMapper.updateByPrimaryKeySelective(snsCircle);
        }else{
            snsCircle = new SnsCircle();
            snsCircle.setInternalStatus(internalStatus);
            snsCircle.setUid(uid);
            snsCircle.setOwner(owner);
            snsCircleMapper.insertSelective(snsCircle);
        }
    }

    public List<SnsCircleDto> getSnsCircleMember(GetSnsCircleDto getSnsCircleDto){
        return snsCircleMapper.getSnsCircleMember(getSnsCircleDto);
    }

    public SnsCircle getMySnsCircle(long uid,long topicId,long owner,int type){
        SnsCircleExample example = new SnsCircleExample();
        SnsCircleExample.Criteria criteria = example.createCriteria();
        criteria.andOwnerEqualTo(owner);
        criteria.andUidEqualTo(uid);
        criteria.andInternalStatusEqualTo(type);
        List<SnsCircle> list = snsCircleMapper.selectByExample(example);
        return Lists.getSingle(list);
    }

}
