package com.me2me.monitor.dao;

import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.mapper.AccessTrackMapper;
import com.me2me.monitor.model.AccessTrack;
import com.me2me.monitor.model.AccessTrackExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/13.
 */
@Repository
public class MonitorMybatisDao {

    @Autowired
    private AccessTrackMapper accessTrackMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(AccessTrack accessTrack){
        accessTrackMapper.insertSelective(accessTrack);
    }

    public int getReport(MonitorReportDto monitorReportDto){
        if(monitorReportDto.getType()!=0) {
            // 日活规则
            List<Map<String,Object>> counter = jdbcTemplate.queryForList(
                    "select count(distinct uid) as counter from access_track " +
                            "where create_time > ? and create_time < ? " +
                            "and type = ? and channel = ? "
                    ,monitorReportDto.getStartDate(),monitorReportDto.getEndDate(),monitorReportDto.getType(),monitorReportDto.getChannel());
            return Integer.valueOf(counter.get(0).get("counter").toString());
        }else{
            // 启动次数
            List<Map<String,Object>> counter = jdbcTemplate.queryForList(
                    "select count(1) as counter from access_track " +
                            "where create_time > ? and create_time < ? and type = ? and channel = ? "
                    ,monitorReportDto.getStartDate(),monitorReportDto.getEndDate(),monitorReportDto.getType(),monitorReportDto.getChannel());
            return Integer.valueOf(counter.get(0).get("counter").toString());
        }
    }

}
