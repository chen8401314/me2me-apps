package com.me2me.mgmt.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.mgmt.Utils.DateUtil;
import com.me2me.mgmt.dal.entity.MgmtSysLog;
import com.me2me.mgmt.manager.MgmtSysLogManager;
import com.me2me.mgmt.request.SystemLogQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;

@Controller
@RequestMapping("/system")
public class SystemController {
	
	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

	@Autowired
	private MgmtSysLogManager mgmtSysLogManager;
	
	@RequestMapping(value = "/log/query")
	@SystemControllerLog(description = "系统操作日志查询")
	public ModelAndView querySysLog(SystemLogQueryDTO dto){
		if(null == dto){
			dto = new SystemLogQueryDTO();
		}
		Date now = new Date();
		if(StringUtils.isBlank(dto.getStartDate())){
			dto.setStartDate(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		if(StringUtils.isBlank(dto.getEndDate())){
			dto.setEndDate(DateUtil.date2string(DateUtil.addDay(now, 1), "yyyy-MM-dd"));
		}
		
		ModelAndView view = new ModelAndView("system/syslogList");
		try{
			Date startTime = DateUtil.string2date(dto.getStartDate(), "yyyy-MM-dd");
			Date endTime = DateUtil.string2date(dto.getEndDate(), "yyyy-MM-dd");
			List<MgmtSysLog> list = mgmtSysLogManager.queryPageByDescAndTime(dto.getOptDesc(), startTime, endTime, 1, 200);
			dto.setResult(list);
		}catch(Exception e){
			logger.error("查询失败", e);
		}
		view.addObject("dataObj",dto);
		
		return view;
	}
}
