package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.web.Response;
import com.me2me.mgmt.request.AppUserDTO;
import com.me2me.mgmt.request.AppUserQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.user.dto.SearchUserProfileDto;
import com.me2me.user.dto.UserSignUpDto;
import com.me2me.user.service.UserService;

@Controller
@RequestMapping("/appuser")
public class AppUserController {
	
	@Autowired
    private UserService userService;
	
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
		
		Response resp = userService.searchPageByNickNameAndvLv(dto.getNickName(), dto.getMobile(), vLv, 1, 200);
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
		String nickName = req.getParameter("s");
		String isVString = req.getParameter("v");
		String mobile = req.getParameter("m");
		int isV = 0;
		if(StringUtils.isNotBlank(isVString)){
			isV = Integer.valueOf(isVString);
		}
		
    	int action = Integer.valueOf(req.getParameter("a"));
    	long uid = Long.valueOf(req.getParameter("i"));
    	if(action == 1){
    		userService.optionV(1, uid);
    	}else{
    		userService.optionV(2, uid);
    	}
    	ModelAndView view = new ModelAndView("redirect:/appuser/query");
    	//设置查询参数，以此来保持页面查询显示
    	view.addObject("nickName", nickName);
    	view.addObject("isV", isV);
    	view.addObject("mobile", mobile);
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
}
