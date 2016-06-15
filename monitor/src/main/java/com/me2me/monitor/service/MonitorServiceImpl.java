package com.me2me.monitor.service;

import com.me2me.common.web.Response;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.monitor.dao.MonitorMybatisDao;
import com.me2me.monitor.dto.LoadReportDto;
import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.monitor.model.AccessTrack;
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
    public Response loadReport(MonitorReportDto monitorReportDto) {
        int counter = monitorMybatisDao.getReport(monitorReportDto);
        LoadReportDto loadReportDto = new LoadReportDto();
        loadReportDto.setCounter(counter);
        return Response.success(loadReportDto);
    }
}
