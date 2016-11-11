package com.me2me.mgmt.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.ActiveStatDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.monitor.dto.LoadReportDto;
import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.service.MonitorService;

/**
 * Created by 王一武 on 2016/9/20.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
	
	@Autowired
	private MonitorService monitorService;

    @SuppressWarnings("rawtypes")
	@RequestMapping("")
    @SystemControllerLog(description = "用户活跃度查询")
    public ModelAndView dashboard(){
    	ActiveStatDTO dto = new ActiveStatDTO();
    	
    	Date today = new Date();
    	//today
    	MonitorReportDto monitorReportDto = new MonitorReportDto();
        monitorReportDto.setChannel(0);
        monitorReportDto.setType(2);
        monitorReportDto.setStartDate(DateUtil.date2string(today, "yyyy-MM-dd"));
        monitorReportDto.setEndDate(DateUtil.date2string(DateUtil.addDay(today, 1), "yyyy-MM-dd"));
        monitorReportDto.setActionType(0);
        Response resp = monitorService.loadActivityReport(monitorReportDto);
        if(null != resp && null != resp.getData() && resp.getCode()==200){
        	dto.setTodayCount(((LoadReportDto)resp.getData()).getCounter());
        }
    	//yesterday
        monitorReportDto.setStartDate(DateUtil.date2string(DateUtil.addDay(today, -1), "yyyy-MM-dd"));
        monitorReportDto.setEndDate(DateUtil.date2string(today, "yyyy-MM-dd"));
        resp = monitorService.loadActivityReport(monitorReportDto);
        if(null != resp && null != resp.getData() && resp.getCode()==200){
        	dto.setYesterdayCount(((LoadReportDto)resp.getData()).getCounter());
        }
        //threeday
        monitorReportDto.setStartDate(DateUtil.date2string(DateUtil.addDay(today, -3), "yyyy-MM-dd"));
        monitorReportDto.setEndDate(DateUtil.date2string(today, "yyyy-MM-dd"));
        resp = monitorService.loadActivityReport(monitorReportDto);
        if(null != resp && null != resp.getData() && resp.getCode()==200){
        	dto.setThreedayCount(((LoadReportDto)resp.getData()).getCounter());
        }
        //sevenday
        monitorReportDto.setStartDate(DateUtil.date2string(DateUtil.addDay(today, -7), "yyyy-MM-dd"));
        monitorReportDto.setEndDate(DateUtil.date2string(today, "yyyy-MM-dd"));
        resp = monitorService.loadActivityReport(monitorReportDto);
        if(null != resp && null != resp.getData() && resp.getCode()==200){
        	dto.setSevendayCount(((LoadReportDto)resp.getData()).getCounter());
        }
    	
        ModelAndView view = new ModelAndView("Dashboard");
        view.addObject("dataObj",dto);
        return view;
    }
}
