package com.me2me.mgmt.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.mgmt.Utils.DateUtil;
import com.me2me.mgmt.dal.entity.MgmtSysLog;
import com.me2me.mgmt.dal.entity.MgmtUser;
import com.me2me.mgmt.manager.MgmtSysLogManager;
import com.me2me.mgmt.manager.MgmtUserManager;
import com.me2me.mgmt.request.SystemLogQueryDTO;
import com.me2me.mgmt.request.SystemUserDTO;
import com.me2me.mgmt.request.SystemUserQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;

@Controller
@RequestMapping("/system")
public class SystemController {
	
	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

	@Autowired
	private MgmtSysLogManager mgmtSysLogManager;
	@Autowired
	private MgmtUserManager mgmtUserManager;
	
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
	
	@RequestMapping(value = "/user/query")
	@SystemControllerLog(description = "系统用户查询")
	public ModelAndView querySysUser(SystemUserQueryDTO dto){
		if(null == dto){
			dto = new SystemUserQueryDTO();
		}
		ModelAndView view = new ModelAndView("system/sysUserList");
		try{
			List<MgmtUser> list = mgmtUserManager.getPageByName(dto.getUname(), 1, 200);
			dto.setResult(list);
		}catch(Exception e){
			logger.error("查询失败", e);
		}
		view.addObject("dataObj",dto);
		
		return view;
	}
	
	@RequestMapping(value = "/user/create")
	@SystemControllerLog(description = "创建运营用户")
	public ModelAndView createSysUser(SystemUserDTO dto){
		ModelAndView view = null;
		if(dto.getUuid() < 1 || StringUtils.isBlank(dto.getUname()) || dto.getAppUid() < 1){
			view = new ModelAndView("system/sysUserNew");
			view.addObject("errMsg","参数不能为空或统一账号ID和APPUID不能小于1");
			return view;
		}
		MgmtUser user = mgmtUserManager.getByUuid(String.valueOf(dto.getUuid()));
		if(null != user){
			view = new ModelAndView("system/sysUserNew");
			view.addObject("errMsg","统一账号["+dto.getUuid()+"]不能重复绑定");
			return view;
		}
		
		user = mgmtUserManager.getByAppUid(String.valueOf(dto.getAppUid()));
		if(null != user){
			view = new ModelAndView("system/sysUserNew");
			view.addObject("errMsg","APP UID["+dto.getAppUid()+"]不能重复绑定");
			return view;
		}
		
		user = new MgmtUser();
		user.setUuid(String.valueOf(dto.getUuid()));
		user.setName(dto.getUname());
		user.setAppUid(String.valueOf(dto.getAppUid()));
		user.setType(2);
		mgmtUserManager.saveMgmtUser(user);
		
		view = new ModelAndView("redirect:/system/user/query");
		return view;
	}
	
	@RequestMapping(value = "/user/find/{uid}")
	@SystemControllerLog(description = "查询运营用户详细")
	public ModelAndView findSysUser(@PathVariable long uid){
		ModelAndView view = null;
		if(uid < 1){
			logger.warn("请求参数用户ID不正确");
			view = new ModelAndView("redirect:/system/user/query");
			return view;
		}
		MgmtUser user = mgmtUserManager.getById(uid);
		if(null == user){
			logger.warn("用户[id="+uid+"]不存在");
			view = new ModelAndView("redirect:/system/user/query");
			return view;
		}
		view = new ModelAndView("system/sysUserEdit");
		SystemUserDTO dto = new SystemUserDTO();
		dto.setAppUid(Long.valueOf(user.getAppUid()));
		dto.setId(user.getId());
		dto.setUname(user.getName());
		dto.setUuid(Long.valueOf(user.getUuid()));
		view.addObject("dataObj",dto);
		
		return view;
	}
	
	@RequestMapping(value = "/user/editSave")
	@SystemControllerLog(description = "更新运营用户")
	public ModelAndView editSaveSysUser(SystemUserDTO dto){
		ModelAndView view = new ModelAndView("system/sysUserEdit");
		if(dto.getAppUid() < 1){
			view.addObject("errMsg","APP UID 不能小于1");
			view.addObject("dataObj",dto);
			return view;
		}
		MgmtUser user = mgmtUserManager.getById(dto.getId());
		if(null == user){
			view.addObject("errMsg","当前用户不存在");
			view.addObject("dataObj",dto);
			return view;
		}
		if(String.valueOf(dto.getAppUid()).equals(user.getAppUid())){//未更改，则直接提示更新成功
			view.addObject("errMsg","更新成功");
			view.addObject("dataObj",dto);
			return view;
		}
		
		MgmtUser checkUser = mgmtUserManager.getByAppUid(String.valueOf(dto.getAppUid()));
		if(null != checkUser){
			view.addObject("errMsg","APP UID不能重复绑定");
			view.addObject("dataObj",dto);
			return view;
		}
		
		user.setAppUid(String.valueOf(dto.getAppUid()));
		mgmtUserManager.updateMgmtUser(user);
		
		view.addObject("errMsg","更新成功");
		view.addObject("dataObj",dto);
		return view;
	}
}
