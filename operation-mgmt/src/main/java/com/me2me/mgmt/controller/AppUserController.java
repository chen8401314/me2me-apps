package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.web.Response;
import com.me2me.mgmt.request.AppGagUserQueryDTO;
import com.me2me.mgmt.request.AppUserDTO;
import com.me2me.mgmt.request.AppUserQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.user.dto.SearchUserProfileDto;
import com.me2me.user.dto.ShowUsergagDto;
import com.me2me.user.dto.UserSignUpDto;
import com.me2me.user.model.UserGag;
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
}
