package com.me2me.mgmt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.user.model.MbtiMapping;
import com.me2me.user.service.UserService;

@Controller
@RequestMapping("/mbtiMapping")
public class MBTIMappingController {
	
	@Autowired
    private UserService userService;
	
	
	@RequestMapping(value = "/query")
	@SystemControllerLog(description = "MBTI映射列表")
	public String query(HttpServletRequest request) throws Exception {
		
		List<MbtiMapping> list =userService.getMBTIMappingPage();
		request.setAttribute("dataList",list);
		return "mbtiMapping/query";
	}
	@RequestMapping(value = "/add")
	@SystemControllerLog(description = "添加MBTI映射")
	public String add(HttpServletRequest request) throws Exception {
		return "mbtiMapping/add";
	}
	@RequestMapping(value = "/modify")
	@SystemControllerLog(description = "添加MBTI映射")
	public String modify(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		MbtiMapping item =userService.getMBTIMappingById(Long.parseLong(id));
		request.setAttribute("item",item);
		return "mbtiMapping/add";
	}
	
	@RequestMapping(value = "/doSave")
	@SystemControllerLog(description = "保存MBTI映射")
	public String doSave(HttpServletRequest request,MbtiMapping tpl) throws Exception {
		try{
			if(tpl.getId()!=null){
				userService.modifyMBTIMapping(tpl);
			}else{
				userService.addMBTIMapping(tpl);
			}
			return "redirect:./query";
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("item",tpl);
			return add(request);
		}
	}
	@RequestMapping(value = "/delete")
	@SystemControllerLog(description = "删除MBTI映射")
	public String delete(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		userService.delMBTIMapping(Long.parseLong(id));
		return "redirect:./query";
	}

}
