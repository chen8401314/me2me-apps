package com.me2me.mgmt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.model.Tchannel;
import com.me2me.activity.service.ActivityService;
import com.me2me.mgmt.request.AppChannelItem;
import com.me2me.mgmt.request.ChannelQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;

@Controller
@RequestMapping("/appchannel")
public class AppChannelController {
	
	@Autowired
    private ActivityService activityService;

	@RequestMapping(value = "/query")
	public ModelAndView channelQuery(ChannelQueryDTO dto){
		List<Tchannel> list = activityService.getAppChannel(dto.getChannelCode());
		dto.resultClear();
		if(null != list && list.size() > 0){
			AppChannelItem item = null;
			for(Tchannel c : list){
				item = new AppChannelItem();
				item.setId(c.getId());
				item.setName(c.getName());
				item.setCode(c.getCode());
				dto.getResult().add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("appchannel/list");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value = "/find/{cid}")
	public ModelAndView channelFind(@PathVariable long cid){
		Tchannel channel = activityService.getTchannelById(cid);
		
		AppChannelItem item = new AppChannelItem();
		if(null != channel){
			item.setCode(channel.getCode());
			item.setId(channel.getId());
			item.setName(channel.getName());
		}
		
		ModelAndView view = new ModelAndView("appchannel/edit");
		view.addObject("dataObj", item);
		return view;
	}
	
	@RequestMapping(value = "/edit/save")
	@SystemControllerLog(description = "更新APP渠道")
	public ModelAndView channelEditSave(AppChannelItem item){
		Tchannel channel = activityService.getTchannelByCode(item.getCode());
		if(null != channel && channel.getId().longValue() != item.getId()){
			ModelAndView view = new ModelAndView("appchannel/edit");
			view.addObject("errMsg", "重复的渠道标识");
			view.addObject("dataObj", item);
			return view;
		}
		
		channel = new Tchannel();
		channel.setId(item.getId());
		channel.setName(item.getName());
		channel.setCode(item.getCode());
		activityService.updateTchannel(channel);
		
		return new ModelAndView("redirect:/appchannel/query");
	}
	
	@RequestMapping(value = "/create")
	@SystemControllerLog(description = "新建APP渠道")
	public ModelAndView channelCreate(AppChannelItem item){
		Tchannel channel = activityService.getTchannelByCode(item.getCode());
		if(null != channel){
			ModelAndView view = new ModelAndView("appchannel/new");
			view.addObject("errMsg", "重复的渠道标识");
			view.addObject("dataObj", item);
			return view;
		}
		
		channel = new Tchannel();
		channel.setName(item.getName());
		channel.setCode(item.getCode());
		activityService.saveTchannel(channel);
		
		return new ModelAndView("redirect:/appchannel/query");
	}
	
	@RequestMapping(value = "/delete/{cid}")
	public ModelAndView channelDelete(@PathVariable long cid){
		activityService.deleteTchannel(cid);
		
		return new ModelAndView("redirect:/appchannel/query");
	}
}
