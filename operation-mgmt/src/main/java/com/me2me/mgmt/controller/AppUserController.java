package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.AppGagUserQueryDTO;
import com.me2me.mgmt.request.AppUserDTO;
import com.me2me.mgmt.request.AppUserQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.sms.service.SmsService;
import com.me2me.user.dto.SearchUserProfileDto;
import com.me2me.user.dto.ShowUsergagDto;
import com.me2me.user.dto.UserSignUpDto;
import com.me2me.user.model.UserGag;
import com.me2me.user.service.UserService;


@Controller
@RequestMapping("/appuser")
public class AppUserController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);
	
	@Autowired
    private UserService userService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private SmsService smsService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/query")
	@SystemControllerLog(description = "APP用户查询")
	public ModelAndView query(AppUserQueryDTO dto) {
		ModelAndView view = new ModelAndView("appuser/userList");
		int vLv = -1;
		if(dto.getIsV() == 1){
			vLv = 1;
		}else if(dto.getIsV() == 2){
			vLv = 0;
		}
		
		int status = -1;
		if(dto.getStatus() == 1){
			status = 0;
		}else if(dto.getStatus() == 2){
			status = 1;
		}
		
		Response resp = userService.searchUserPage(dto.getNickName(), dto.getMobile(), vLv, status, dto.getStartTime(), dto.getEndTime(), 1, 200);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			SearchUserProfileDto data = (SearchUserProfileDto)resp.getData();
			dto.setData(data);
		}
		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value="/option/vlv")
	@SystemControllerLog(description = "上大V或取消大V操作")
    public ModelAndView optionVlv(HttpServletRequest req){
    	int action = Integer.valueOf(req.getParameter("a"));
    	long uid = Long.valueOf(req.getParameter("i"));
    	String nickName = req.getParameter("m");
    	if(null == nickName){
    		nickName = "";
    	}
    	if(action == 1){
    		userService.optionV(1, uid);
    	}else{
    		userService.optionV(2, uid);
    	}
    	ModelAndView view = new ModelAndView("redirect:/appuser/query");
    	view.addObject("nickName",nickName);
    	return view;
    }
	
	@RequestMapping(value="/option/status")
	@SystemControllerLog(description = "禁止或恢复用户")
	public ModelAndView optionStatus(HttpServletRequest req){
		int action = Integer.valueOf(req.getParameter("a"));
    	long uid = Long.valueOf(req.getParameter("i"));
    	String nickName = req.getParameter("m");
    	if(null == nickName){
    		nickName = "";
    	}
    	if(action == 1){
    		userService.optionDisableUser(1, uid);
    	}else{
    		userService.optionDisableUser(2, uid);
    	}
    	ModelAndView view = new ModelAndView("redirect:/appuser/query");
    	view.addObject("nickName",nickName);
		return view;
	}
	
	@RequestMapping(value="/createUser")
	@SystemControllerLog(description = "创建马甲号")
	public ModelAndView createUser(AppUserDTO dto){
		if(dto.getCount() > 0 && dto.getMobile() > 0 && StringUtils.isNotBlank(dto.getPwd())){
			UserSignUpDto userSignUpDto = new UserSignUpDto();
			userSignUpDto.setGender(0);//默认女的
			long m = dto.getMobile();
			for(int i=0;i<dto.getCount();i++){
				long mobile = m + i;
				userSignUpDto.setMobile(String.valueOf(mobile));
				userSignUpDto.setNickName(userSignUpDto.getMobile());
				userSignUpDto.setEncrypt(dto.getPwd());
				userService.signUp(userSignUpDto);
			}
		}
		ModelAndView view = new ModelAndView("redirect:/appuser/query");
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/gaguser/query")
	@SystemControllerLog(description = "APP禁言用户查询")
	public ModelAndView gagUserQuery(AppGagUserQueryDTO dto){
		long uid = 0;
		if(StringUtils.isNotBlank(dto.getUid())){
			uid = Long.valueOf(dto.getUid());
		}
		
		Response resp = userService.getGagUserPageByTargetUid(uid, 1, 200);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowUsergagDto data = (ShowUsergagDto)resp.getData();
			if(null != data.getResult() && data.getResult().size() > 0){
				for(ShowUsergagDto.UsergagElement e : data.getResult()){
					if(e.getUid() == 0){
						e.setUserName("运营管理员");
					}
				}
			}
			
			dto.setData(data);
		}
		ModelAndView view = new ModelAndView("appuser/gagList");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value="/gaguser/add/{uid}")
	@SystemControllerLog(description = "添加APP禁言用户")
	public ModelAndView addGagUser(@PathVariable long uid){
		if(uid > 0){
			UserGag gag = new UserGag();
			gag.setCid(Long.valueOf(0));
			gag.setGagLevel(0);//后台设置的默认为全部
			gag.setTargetUid(uid);
			gag.setType(0);//后台设置的默认为全部
			gag.setUid(Long.valueOf(0));//默认运营管理员
			userService.addGagUser(gag);
		}
		
		ModelAndView view = new ModelAndView("redirect:/appuser/gaguser/query");
		return view;
	}
	
	@RequestMapping(value="/gaguser/remove/{gid}")
	@SystemControllerLog(description = "取消APP禁言用户")
	public ModelAndView delGagUser(@PathVariable long gid){
		if(gid > 0){
			userService.deleteGagUserById(gid);
		}
		
		ModelAndView view = new ModelAndView("redirect:/appuser/gaguser/query");
		return view;
	}
	
	@RequestMapping(value="/msgsender")
	public ModelAndView msgsender(){
		ModelAndView view = new ModelAndView("/appuser/smsConsole");
		return view;
	}
	
	@RequestMapping(value = "/sendMsg")
	@ResponseBody
	public String sendMsg(@RequestParam("id")String msgId, 
			@RequestParam("m")int mode, @RequestParam("ms")String mobiles){
		if(StringUtils.isBlank(msgId)){
			return "请输入正确的短信模板ID";
		}
		if(mode == 1){
			if(StringUtils.isBlank(mobiles)){
				return "请指定手机号";
			}
			logger.info("指定手机号发送，指定的手机号为：【"+mobiles+"】");
			String[] tmp = mobiles.split(",");
			if(null != tmp && tmp.length > 0){
				long total = 0;
				List<String> sendList = new ArrayList<String>();
				for(String t : tmp){
					if(StringUtils.isNotBlank(t)){
						total++;
						sendList.add(t);
						if(sendList.size() >= 180){
		    				smsService.send7dayCommon(msgId, sendList, null);
		                    logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
		                    sendList.clear();
		    			}
					}
				}
				if(sendList.size() > 0){
		    		smsService.send7dayCommon(msgId, sendList, null);
		            logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
		            sendList.clear();
		    	}
				logger.info("共["+total+"]个手机号发送了消息");
			}else{
				logger.info("没有手机号需要发送");
			}
		}else if(mode == 2){
			logger.info("全部注册手机号发送");
			//先获取所有手机用户手机号
	    	List<String> mobileList = activityService.getAllUserMobilesInApp();
	    	if(null == mobileList){
	    		mobileList = new ArrayList<String>();
	    	}
	    	logger.info("共["+mobileList.size()+"]个手机号待发送（这里包含马甲号，下面会去除）");
	    	
	    	long total = 0;
	    	List<String> sendList = new ArrayList<String>();
	    	for(String mobile : mobileList){
	    		if(checkMobile(mobile)){
	    			total++;
	    			sendList.add(mobile);
	    			if(sendList.size() >= 180){
	    				smsService.send7dayCommon(msgId, sendList, null);
	                    logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
	                    sendList.clear();
	    			}
	    		}
	    	}
	    	if(sendList.size() > 0){
	    		smsService.send7dayCommon(msgId, sendList, null);
	            logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
	            sendList.clear();
	    	}
	    	logger.info("共["+total+"]个手机号发送了消息");
		}else{
			return "不支持的发送模式";
		}
		
		return "执行完成";
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
}
