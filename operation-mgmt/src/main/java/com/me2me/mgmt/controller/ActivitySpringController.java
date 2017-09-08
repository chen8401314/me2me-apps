package com.me2me.mgmt.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import com.me2me.activity.dto.ShowMiliDatasDTO;
import com.me2me.activity.model.AactivityStage;
import com.me2me.activity.model.AmiliData;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.ActivityInfoDTO;
import com.me2me.mgmt.request.MiliDataQueryDTO;
import com.me2me.mgmt.request.StageItem;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.sms.service.SmsService;

@Controller
@RequestMapping("/spring")
public class ActivitySpringController {

	private static final Logger logger = LoggerFactory.getLogger(ActivitySpringController.class);
	
	@Autowired
    private ActivityService activityService;
	@Autowired
	private SmsService smsService;
	
	@RequestMapping(value="/getActivityInfo")
	public ModelAndView getActivityInfo(){
		ActivityInfoDTO dto = new ActivityInfoDTO();
		dto.setActivityInfo(activityService.getAactivityById(2));
		List<AactivityStage> list = activityService.getAllStage(2);
		List<StageItem> stageList = new ArrayList<StageItem>();
		if(null != list && list.size() > 0){
			StageItem item = null;
			for(AactivityStage stage : list){
				item = new StageItem();
				item.setId(stage.getId());
				item.setName(getStageNameByStage(stage.getStage()));
				item.setStartTime(DateUtil.date2string(stage.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
				item.setEndTime(DateUtil.date2string(stage.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
				item.setStatus(stage.getType());
				stageList.add(item);
			}
		}
		
		dto.setStageList(stageList);
		
		ModelAndView view = new ModelAndView("spring/stage");
		view.addObject("dataObj",dto);
		
		return view;
	}
	
	private String getStageNameByStage(int stage){
		switch(stage){
		case 1:
			return "预热阶段";
		case 2:
			return "活动阶段";
		case 3:
			return "结束阶段";
		default:
			return "不支持的stage";
		}
	}
	
	@RequestMapping(value="/stage/f/{id}")
	public ModelAndView getStage(@PathVariable long id){
		ModelAndView view = new ModelAndView("spring/stageEdit");
		
		AactivityStage stage = activityService.getAactivityStageById(id);
		if(null != stage){
			StageItem item = new StageItem();
			item.setId(stage.getId());
			item.setName(getStageNameByStage(stage.getStage()));
			item.setStartTime(DateUtil.date2string(stage.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			item.setEndTime(DateUtil.date2string(stage.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			item.setStatus(stage.getType());
			view.addObject("dataObj",item);
		}
		
		return view;
	}
	
	@RequestMapping(value="/stage/update")
	@SystemControllerLog(description = "春节活动更新阶段")
	public ModelAndView updateStage(StageItem item) throws ParseException{
		AactivityStage stage = activityService.getAactivityStageById(item.getId());
		stage.setStartTime(DateUtil.string2date(item.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		stage.setEndTime(DateUtil.string2date(item.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		stage.setType(item.getStatus());
		
		activityService.updateAactivityStage(stage);
		
		ModelAndView view = new ModelAndView("redirect:/spring/getActivityInfo");
		return view;
	}
	
	@RequestMapping(value="/control/index")
	public ModelAndView controlIndex(){
		ModelAndView view = new ModelAndView("spring/control");
		return view;
	}
	
	//@RequestMapping(value="/control/startNotice")
	//@ResponseBody
	public String msgStartNotice(){
		//先获取所有手机用户手机号
    	List<String> mobileList = activityService.getAllUserMobilesInApp();
    	if(null == mobileList || mobileList.size() == 0){
    		logger.info("没有系统手机用户");
    		mobileList = new ArrayList<String>();
    	}else{
    		logger.info("共["+mobileList.size()+"]个系统手机用户");
    	}
    	//获取7天活动所有报名手机号
    	List<String> sdayMobileList = activityService.getAll7DayMobiles();
    	if(null != sdayMobileList && sdayMobileList.size() > 0){
    		logger.info("共["+sdayMobileList.size()+"]个七天报名用户");
    		for(String m : sdayMobileList){
    			if(!mobileList.contains(m)){
    				mobileList.add(m);
    			}
    		}
    	}else{
    		logger.info("没有七天报名用户");
    	}
    	
    	logger.info("共["+mobileList.size()+"]个手机号待发送（这里包含马甲号，下面会去除）");
    	
    	long total = 0;
    	List<String> sendList = new ArrayList<String>();
    	for(String mobile : mobileList){
    		if(checkMobile(mobile)){
    			total++;
    			sendList.add(mobile);
    			if(sendList.size() >= 180){
    				smsService.send7dayCommon("150472", sendList, null);
                    logger.info("send [" + sendList.size() + "] user!");
                    sendList.clear();
    			}
    		}
    	}
    	if(sendList.size() > 0){
    		smsService.send7dayCommon("150472", sendList, null);
            logger.info("send [" + sendList.size() + "] user!");
            sendList.clear();
    	}
    	logger.info("共["+total+"]个手机号发送了消息");
		
		return "0";
	}
	
	private boolean checkMobile(String mobile){
    	if(!StringUtils.isEmpty(mobile)){
    		if(!mobile.startsWith("100") && !mobile.startsWith("111")
    				&& !mobile.startsWith("123") && !mobile.startsWith("1666")
    				&& !mobile.startsWith("180000") && !mobile.startsWith("18888888")
    				&& !mobile.startsWith("18900") && !mobile.startsWith("19000")
    				&& !mobile.startsWith("2") && !mobile.startsWith("8")){
    			return true;
    		}
    	}
    	return false;
    }
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/milidata/query")
	public ModelAndView miliDataQuery(MiliDataQueryDTO dto){
		ModelAndView view = new ModelAndView("spring/milidata");
		
		Response resp = activityService.searchMiliDatas(dto.getMkey(), 2, dto.getPage(), dto.getPageSize());
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
		Response resp = activityService.searchMiliDatas(dto.getMkey(), 2, dto.getPage(), dto.getPageSize());
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
		ModelAndView view = new ModelAndView("spring/milidataEdit");
		
		AmiliData data = activityService.getAmiliDataById(id);
		view.addObject("dataObj",data);
		
		return view;
	}
	
	@RequestMapping(value="/milidata/update")
	@SystemControllerLog(description = "春节活动更新米粒块")
	public ModelAndView updateMiliData(AmiliData data){
		ModelAndView view = null;
		if(StringUtils.isEmpty(data.getMkey()) || StringUtils.isEmpty(data.getContent())){
			view = new ModelAndView("spring/milidataEdit");
			view.addObject("dataObj",data);
			view.addObject("errMsg","米粒内容不能为空");
			return view;
		}
		
		activityService.updateAmiliData(data);
		
		view = new ModelAndView("redirect:/spring/milidata/query");
		return view;
	}
}
