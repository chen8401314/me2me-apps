package com.me2me.mgmt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.me2me.activity.dto.ShowActivity7DayUserStatDTO;
import com.me2me.activity.dto.ShowActivity7DayUsersDTO;
import com.me2me.activity.service.ActivityService;
import com.me2me.mgmt.request.StatUserDTO;

@Controller
@RequestMapping("/7day")
public class Activity7dayController {

	private static final Logger logger = LoggerFactory.getLogger(Activity7dayController.class);
	
	@Autowired
    private ActivityService activityService;
	
	@RequestMapping(value="/stat/user")
	public ModelAndView statUser(StatUserDTO dto){
		ModelAndView view = new ModelAndView("7day/statUser");
		//先查询出统计信息
		ShowActivity7DayUserStatDTO userStat = activityService.get7dayUserStat(dto.getChannel(), dto.getCode(), dto.getStartTime(), dto.getEndTime());
		dto.setUserStatDTO(userStat);
		
		if(userStat.getTotalUser()%10 == 0){
			dto.setTotalPage((int)userStat.getTotalUser()/10);
		}else{
			dto.setTotalPage((int)userStat.getTotalUser()/10 + 1);
		}
		
		//再查询分页用户信息
		ShowActivity7DayUsersDTO users = activityService.get7dayUsers(dto.getChannel(), dto.getCode(), dto.getStartTime(), dto.getEndTime(), 1, 10);
		dto.setUserDTO(users);
		
		view.addObject("dataObj",dto);
		
		return view;
	}
	
	@RequestMapping(value="/stat/user/query")
	public String statUserQuery(StatUserDTO dto){
		ShowActivity7DayUsersDTO users = activityService.get7dayUsers(dto.getChannel(), dto.getCode(), dto.getStartTime(), dto.getEndTime(), dto.getPage(), dto.getPageSize());
		JSONObject obj = (JSONObject)JSON.toJSON(users);
		return obj.toJSONString();
	}
}
