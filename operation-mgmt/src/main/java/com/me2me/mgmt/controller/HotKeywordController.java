package com.me2me.mgmt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.me2me.live.model.TopicFragmentTemplate;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.syslog.SystemControllerLog;
/**
 * 热词管理
 * @author zhangjiwei
 * @date Apr 5, 2017
 */
@Controller
@RequestMapping("/hotkeyword")
public class HotKeywordController {
	
	@Autowired
    private LiveService liveService;
	
	
	@RequestMapping(value = "/query")
	@SystemControllerLog(description = "热词列表")
	public String query(HttpServletRequest request) throws Exception {
		String queryStr = request.getParameter("query");
		if(!StringUtils.isEmpty(queryStr)){
			queryStr=new String(queryStr.getBytes("iso-8859-1"),"utf-8");
		}
		List<TopicFragmentTemplate> list =liveService.getFragmentTplList(queryStr);
		request.setAttribute("dataList",list);
		return "topicFragmentTemplate/query";
	}
	@RequestMapping(value = "/add")
	@SystemControllerLog(description = "添加热词")
	public String add(HttpServletRequest request) throws Exception {
		return "topicFragmentTemplate/add";
	}
	@RequestMapping(value = "/modify")
	@SystemControllerLog(description = "添加热词")
	public String modify(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		TopicFragmentTemplate item =liveService.getFragmentTplById(Long.parseLong(id));
		request.setAttribute("item",item);
		return "topicFragmentTemplate/add";
	}
	
	@RequestMapping(value = "/doSave")
	@SystemControllerLog(description = "保存热词")
	public String doSave(HttpServletRequest request,TopicFragmentTemplate tpl) throws Exception {
		try{
			if(tpl.getId()!=null){
				liveService.updateFragmentTpl(tpl);
			}else{
				liveService.addFragmentTpl(tpl);
			}
			return "redirect:./query";
		}catch(Exception e){
			request.setAttribute("item",tpl);
			return add(request);
		}
	}
	@RequestMapping(value = "/delete")
	@SystemControllerLog(description = "删除热词")
	public String delete(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		liveService.deleteFragmentTpl(Long.parseLong(id));
		return "redirect:./query";
	}
	
}
