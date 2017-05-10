package com.me2me.mgmt.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.me2me.common.page.PageBean;
import com.me2me.common.security.SecurityUtils;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.model.TeaseInfo;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.services.LocalConfigService;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;
/**
 * 逗一逗管理
 * @author chenxiang
 * @date 2017-05-08
 */
@Controller  
@RequestMapping("/tease")
@Slf4j
public class TeaseController {
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LiveService liveService;
	
	@Autowired
	private LocalConfigService config;
	@Autowired
	private FileTransferService fileTransferService;
	
	@RequestMapping(value = "/list_tease")
	@SystemControllerLog(description = "逗一逗列表")
	public String list_tease(HttpServletRequest request,DatatablePage dpage) throws Exception {
		Map<String,Object> param = new HashMap<>();
		PageBean page = dpage.toPageBean();
		page.setPageSize(99999);
		PageBean<TeaseInfo> list =liveService.getTeaseInfoPage(page, param);
		request.setAttribute("dataList2",list.getDataList());
		return "tease/list_tease";
	}
	
	@RequestMapping(value = "/add_tease")
	@SystemControllerLog(description = "添加逗一逗")
	public String add_tease(HttpServletRequest request) throws Exception {
		return "tease/add_tease";
	}
	
	@RequestMapping(value = "/modify_tease")
	@SystemControllerLog(description = "修改逗一逗")
	public String modify_tease(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		TeaseInfo item= liveService.getTeaseInfoByKey(Long.valueOf(id));
		request.setAttribute("item",item);
		return "tease/add_tease";
	}
	@RequestMapping(value = "/doSavetease")
	@SystemControllerLog(description = "保存逗一逗")
	public String doSavetease(TeaseInfo tpl,@RequestParam("file")MultipartFile file,@RequestParam("file1")MultipartFile file1,HttpServletRequest mrequest) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		tpl.setImage(imgName);
			}
			if(file1!=null && StringUtils.isNotEmpty(file1.getOriginalFilename()) && file1.getSize()>0){
				String audioName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file1.getBytes(), audioName);
	    		tpl.setAudio(audioName);
			}
			if(tpl.getId()!=null){
				liveService.updateTeaseInfoByKey(tpl); 
			}else{
				liveService.addTeaseInfo(tpl);
			}
			return "redirect:./list_tease";
		}catch(Exception e){
			e.printStackTrace();
			mrequest.setAttribute("item",tpl);
			return add_tease(mrequest);
		}
	}
	@RequestMapping(value = "/delete_tease")
	@SystemControllerLog(description = "删除逗一逗")
	public String deletepack(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		TeaseInfo teaseInfo  = new TeaseInfo();
		teaseInfo.setId(Long.valueOf(id));
		teaseInfo.setStatus(0);
		liveService.updateTeaseInfoByKey(teaseInfo);
		return "redirect:./list_tease";
	}
	
	

}
