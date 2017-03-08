package com.me2me.mgmt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.model.Tchannel;
import com.me2me.activity.service.ActivityService;
import com.me2me.mgmt.request.ChannelQueryDTO;

@Controller
@RequestMapping("/appchannel")
public class AppChannelController {
	
	@Autowired
    private ActivityService activityService;

	@RequestMapping(value = "/query")
	public ModelAndView channelQuery(ChannelQueryDTO dto){
//		List<Tchannel> list = 
		
		ModelAndView view = new ModelAndView("appchannel/list");
		view.addObject("dataObj", dto);
		return view;
	}
}
