package com.me2me.mgmt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.me2me.activity.dto.ShowActivity7DayUserStatDTO;
import com.me2me.activity.dto.ShowActivity7DayUsersDTO;
import com.me2me.activity.dto.ShowMiliDatasDTO;
import com.me2me.activity.model.AmiliData;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.MiliDataQueryDTO;
import com.me2me.mgmt.request.StatUserDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;

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
	@ResponseBody
	public String statUserQuery(StatUserDTO dto){
		ShowActivity7DayUsersDTO users = activityService.get7dayUsers(dto.getChannel(), dto.getCode(), dto.getStartTime(), dto.getEndTime(), dto.getPage(), dto.getPageSize());
		JSONObject obj = (JSONObject)JSON.toJSON(users);
		return obj.toJSONString();
	}
	
	@RequestMapping(value="/control/index")
	public ModelAndView controlIndex(){
		ModelAndView view = new ModelAndView("7day/control");
		return view;
	}
	
	@RequestMapping(value="/control/auditSuccess")
	@ResponseBody
	@SystemControllerLog(description = "七天活动一键审核通过")
	public String auditSuccess(){
		//一键审核通过
		activityService.oneKeyAudit();
		
		return "0";
	}
	
	@RequestMapping(value="/control/noticeBind")
	@ResponseBody
	@SystemControllerLog(description = "七天活动一键通知绑定")
	public String noticeBind(){
		//一键通知绑定
		activityService.bindNotice();
		return "0";
	}
	
	@RequestMapping(value="/control/activityStartNotice")
	@ResponseBody
	@SystemControllerLog(description = "七天活动一键通知活动开始")
	public String activityStartNotice(){
		//一键通知活动进行
		activityService.noticeActivityStart();
		
		return "0";
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/milidata/query")
	public ModelAndView miliDataQuery(MiliDataQueryDTO dto){
		ModelAndView view = new ModelAndView("7day/milidata");
		
		Response resp = activityService.searchMiliDatas(dto.getMkey(), dto.getPage(), dto.getPageSize());
		if(null != resp && resp.getCode() == 200){
			dto.setData((ShowMiliDatasDTO)resp.getData());
			if(null != dto.getData() && null != dto.getData().getResult() && dto.getData().getResult().size() > 0){
				for(ShowMiliDatasDTO.MiliDataElement e : dto.getData().getResult()){
					e.setContent(e.getContent().replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;"));
				} 
			}
		}
		
		view.addObject("dataObj",dto);
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/milidata/queryJson")
	@ResponseBody
	public String miliDataQueryJson(MiliDataQueryDTO dto){
		Response resp = activityService.searchMiliDatas(dto.getMkey(), dto.getPage(), dto.getPageSize());
		if(null != resp && resp.getCode() == 200){
			if(null != resp.getData()){
				ShowMiliDatasDTO sddto = (ShowMiliDatasDTO)resp.getData();
				if(null != sddto.getResult() && sddto.getResult().size() > 0){
					for(ShowMiliDatasDTO.MiliDataElement e : sddto.getResult()){
						e.setContent(e.getContent().replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;"));
					}
				}
			}
		}
		
		JSONObject obj = (JSONObject)JSON.toJSON(resp);
		return obj.toJSONString();
	}
	
	@RequestMapping(value="/milidata/f/{id}")
	public ModelAndView getMiliData(@PathVariable long id){
		ModelAndView view = new ModelAndView("7day/milidataEdit");
		
		AmiliData data = activityService.getAmiliDataById(id);
		view.addObject("dataObj",data);
		
		return view;
	}
	
	@RequestMapping(value="/milidata/update")
	@SystemControllerLog(description = "七天活动更新米粒块")
	public ModelAndView updateMiliData(AmiliData data){
		ModelAndView view = null;
		if(StringUtils.isEmpty(data.getMkey()) || StringUtils.isEmpty(data.getContent())){
			view = new ModelAndView("7day/milidataEdit");
			view.addObject("dataObj",data);
			view.addObject("errMsg","米粒内容不能为空");
			return view;
		}
		
		activityService.updateAmiliData(data);
		
		view = new ModelAndView("redirect:/7day/milidata/query");
		return view;
	}
	
	@RequestMapping(value="/milidata/save")
	@SystemControllerLog(description = "七天活动新增米粒块")
	public ModelAndView saveMiliData(AmiliData data){
		ModelAndView view = null;
		if(StringUtils.isEmpty(data.getMkey()) || StringUtils.isEmpty(data.getContent())){
			view = new ModelAndView("7day/milidataNew");
			view.addObject("dataObj",data);
			view.addObject("errMsg","米粒内容不能为空");
			return view;
		}
		
		activityService.saveAmiliData(data);
		view = new ModelAndView("redirect:/7day/milidata/query");
		return view;
	}
}
