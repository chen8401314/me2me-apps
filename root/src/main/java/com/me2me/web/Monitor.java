package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.service.MonitorService;
import com.me2me.web.request.MonitorReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/14.
 */
@Controller
@RequestMapping(value = "/api/monitor")
public class Monitor extends BaseController {

    @Autowired
    private MonitorService monitorService;

    /**
     * 启动次数
     * @param monitorReportRequest
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/report",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response report(MonitorReportRequest monitorReportRequest){
        MonitorReportDto monitorReportDto = new MonitorReportDto();
        monitorReportDto.setChannel(Integer.valueOf(monitorReportRequest.getChannel()));
        monitorReportDto.setType(monitorReportRequest.getType());
        monitorReportDto.setStartDate(monitorReportRequest.getStartDate());
        monitorReportDto.setEndDate(monitorReportRequest.getEndDate());
        monitorReportDto.setActionType(monitorReportRequest.getActionType());
        if(monitorReportDto.getType()== 0) {
            return monitorService.loadBootReport(monitorReportDto);
        }else if(monitorReportDto.getType() == 1) {
            return monitorService.loadActionReport(monitorReportDto);
        }else if(monitorReportDto.getType() == 2){
            return monitorService.loadActivityReport(monitorReportDto);
        }else{
            return Response.failure("参数非法...");
        }
    }
}
