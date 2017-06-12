package com.me2me.monitor.service;

import com.me2me.common.web.Response;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.monitor.dao.MonitorMybatisDao;
import com.me2me.monitor.dto.AccessLoggerDto;
import com.me2me.monitor.dto.LoadReportDto;
import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.monitor.model.AccessTrack;
import com.me2me.monitor.model.HttpAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/12.
 */
@Service
@Slf4j
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private MonitorMybatisDao monitorMybatisDao;

    @Autowired
    private ApplicationEventBus applicationEventBus;

    @Override
    public void mark(AccessTrack accessTrack) {
        monitorMybatisDao.save(accessTrack);
    }

    @Override
    public void post(MonitorEvent monitorEvent) {
        applicationEventBus.post(monitorEvent);
    }

    @Override
    public Response loadBootReport(MonitorReportDto monitorReportDto) {
        int counter = monitorMybatisDao.getBootReport(monitorReportDto);
        LoadReportDto loadReportDto = new LoadReportDto();
        loadReportDto.setCounter(counter);
        return Response.success(loadReportDto);
    }

    @Override
    public Response loadActionReport(MonitorReportDto monitorReportDto) {
        int counter;
        if(monitorReportDto.getActionType()==2){
            counter = monitorMybatisDao.getRegisterReport(monitorReportDto);
        }else {
            counter = monitorMybatisDao.getActionReport(monitorReportDto);
        }
        LoadReportDto loadReportDto = new LoadReportDto();
        loadReportDto.setCounter(counter);
        return Response.success(loadReportDto);
    }

    @Override
    public Response loadActivityReport(MonitorReportDto monitorReportDto) {
        int counter = monitorMybatisDao.getActivityReport(monitorReportDto);
        LoadReportDto loadReportDto = new LoadReportDto();
        loadReportDto.setCounter(counter);
        return Response.success(loadReportDto);
    }

    @Override
    public void saveAccessLog(AccessLoggerDto accessLoggerDto) {
        HttpAccess httpAccess = new HttpAccess();
        httpAccess.setUid(accessLoggerDto.getUid());
        httpAccess.setHttpHeaders(accessLoggerDto.getHeaders());
        httpAccess.setHttpRequestMethod(accessLoggerDto.getMethod());
        httpAccess.setHttpRequestParams(accessLoggerDto.getParams());
        httpAccess.setHttpUri(accessLoggerDto.getUri());
        monitorMybatisDao.saveHttpAccess(httpAccess);
    }

}
