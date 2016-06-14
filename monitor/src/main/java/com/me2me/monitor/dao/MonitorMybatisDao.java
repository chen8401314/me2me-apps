package com.me2me.monitor.dao;

import com.me2me.monitor.mapper.AccessTrackMapper;
import com.me2me.monitor.model.AccessTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/13.
 */
@Repository
public class MonitorMybatisDao {

    @Autowired
    private AccessTrackMapper accessTrackMapper;

    public void save(AccessTrack accessTrack){
        accessTrackMapper.insertSelective(accessTrack);
    }

}
