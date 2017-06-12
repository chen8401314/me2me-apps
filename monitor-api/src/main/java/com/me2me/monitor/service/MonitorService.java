package com.me2me.monitor.service;

import com.me2me.common.web.Response;
import com.me2me.monitor.dto.AccessLoggerDto;
import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.monitor.model.AccessTrack;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/13.
 */
public interface MonitorService{

    void mark(AccessTrack accessTrack);

    void post(MonitorEvent monitorEvent);

    Response loadBootReport(MonitorReportDto monitorReportDto);

    Response loadActionReport(MonitorReportDto monitorReportDto);

    Response loadActivityReport(MonitorReportDto monitorReportDto);

    void saveAccessLog(AccessLoggerDto accessLoggerDto);
}
