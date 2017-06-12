package com.me2me.monitor.dao;

import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.mapper.AccessTrackMapper;
import com.me2me.monitor.mapper.HttpAccessMapper;
import com.me2me.monitor.model.AccessTrack;
import com.me2me.monitor.model.HttpAccess;
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
    private HttpAccessMapper httpAccessMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(AccessTrack accessTrack){
        accessTrackMapper.insertSelective(accessTrack);
    }


    public int getBootReport(MonitorReportDto monitorReportDto){
        List<Map<String,Object>> counter = jdbcTemplate.queryForList(
                "select count(1) as counter from access_track " +
                        "where create_time > ? and create_time < ? " +
                        "and type = ? and "+(monitorReportDto.getChannel()==0 ? "1=?" : "channel = ?"),
                monitorReportDto.getStartDate(),
                monitorReportDto.getEndDate(),
                monitorReportDto.getType(),
                monitorReportDto.getChannel()==0?1:monitorReportDto.getChannel());
        return Integer.valueOf(counter.get(0).get("counter").toString());
    }

    public int getActionReport(MonitorReportDto monitorReportDto){
        List<Map<String,Object>> counter = jdbcTemplate.queryForList(
                "select count(distinct uid) as counter from access_track " +
                        "where create_time > ? and create_time < ? and type = ? and "+(monitorReportDto.getChannel()==0 ? "1=?" : "channel = ? ")+" and action_type = ? "
                ,monitorReportDto.getStartDate(),
                monitorReportDto.getEndDate(),
                monitorReportDto.getType(),
                monitorReportDto.getChannel()==0?1:monitorReportDto.getChannel(),
                monitorReportDto.getActionType());
        return Integer.valueOf(counter.get(0).get("counter").toString());
    }

    public int getRegisterReport(MonitorReportDto monitorReportDto){
        List<Map<String,Object>> counter = jdbcTemplate.queryForList(
                "select count(uid) as counter from access_track " +
                        "where create_time > ? and create_time < ? and type = ? and "+(monitorReportDto.getChannel()==0 ? "1=?" : "channel = ? ")+" and action_type = ? "
                ,monitorReportDto.getStartDate(),
                monitorReportDto.getEndDate(),
                monitorReportDto.getType(),
                monitorReportDto.getChannel()==0?1:monitorReportDto.getChannel(),
                monitorReportDto.getActionType());
        return Integer.valueOf(counter.get(0).get("counter").toString());
    }

    public int getActivityReport(MonitorReportDto monitorReportDto){
        List<Map<String,Object>> counter = jdbcTemplate.queryForList(
                "select count(distinct uid) as counter from access_track " +
                        "where create_time > ? and create_time < ? and type <> 0 and "+(monitorReportDto.getChannel()==0 ? "1=?" : "channel = ? ")+""
                ,monitorReportDto.getStartDate(),
                monitorReportDto.getEndDate(),
                monitorReportDto.getChannel()==0?1:monitorReportDto.getChannel());
        return Integer.valueOf(counter.get(0).get("counter").toString());
    }

    public void saveHttpAccess(HttpAccess httpAccess){
        httpAccessMapper.insertSelective(httpAccess);
    }
}
