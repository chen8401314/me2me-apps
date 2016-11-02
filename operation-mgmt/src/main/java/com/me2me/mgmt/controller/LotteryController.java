package com.me2me.mgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.dto.ShowLuckActsDTO;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.LotteryOptDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;

@Controller
@RequestMapping("/lottery")
public class LotteryController {

	@Autowired
    private ActivityService activityService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/winnerQuery")
	@SystemControllerLog(description = "抽奖活动获奖用户查询查询")
	public ModelAndView winnerQuery(LotteryOptDTO dto) {
		ModelAndView view = new ModelAndView("lottery/winnerList");
		if(dto.getActive() <= 0){
			dto.setActive(1);//默认为小米活动
		}
		
		Response resp = activityService.getWinners(dto.getActive());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowLuckActsDTO data = (ShowLuckActsDTO)resp.getData();
			dto.setData(data);
		}
		view.addObject("dataObj",dto);
		
		return view;
	}
}
