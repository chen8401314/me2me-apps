package com.me2me.mgmt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.me2me.common.page.PageBean;
import com.me2me.live.dto.SearchDropAroundTopicDto;
import com.me2me.live.model.TopicDroparound;
import com.me2me.live.model.TopicFragmentTemplate;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.vo.DatatablePage;
import com.plusnet.sso.api.vo.JsonResult;

@Controller
@RequestMapping("/topicFragmentTemplate")
public class TopicFragmentTemplateController {
	
	@Autowired
    private LiveService liveService;
	
	
	@RequestMapping(value = "/query")
	@SystemControllerLog(description = "足迹语言模板列表")
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
	@SystemControllerLog(description = "添加语言模板")
	public String add(HttpServletRequest request) throws Exception {
		return "topicFragmentTemplate/add";
	}
	@RequestMapping(value = "/modify")
	@SystemControllerLog(description = "添加语言模板")
	public String modify(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		TopicFragmentTemplate item =liveService.getFragmentTplById(Long.parseLong(id));
		request.setAttribute("item",item);
		return "topicFragmentTemplate/add";
	}
	
	@RequestMapping(value = "/doSave")
	@SystemControllerLog(description = "保存足迹语言模板")
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
	@SystemControllerLog(description = "删除足迹语言模板")
	public String delete(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		liveService.deleteFragmentTpl(Long.parseLong(id));
		return "redirect:./query";
	}
	@RequestMapping(value = "/dropAroundKingdomMgr")
	@SystemControllerLog(description = "串门王国管理")
	public String dropAroundKingdomMgr(HttpServletRequest request) throws Exception {
		return "topicFragmentTemplate/dropAroundKingdomMgr";
	}
	
	@ResponseBody
	@RequestMapping("addDropAroundKingdom")
	@SystemControllerLog(description = "添加串门王国")
	public JsonResult addDropAroundKingdom(HttpServletRequest request){
		try{
			String json = request.getParameter("data");
			JSONArray kingdoms = JSON.parseArray(json);
			for(int i=0;i<kingdoms.size();i++){
				Integer topicId = kingdoms.getInteger(i);
				this.liveService.copyKingdomToDropAroundKingdom(topicId,-1);
			}
			return JsonResult.success();
		}catch(Exception e){
			e.printStackTrace();
			return JsonResult.error("添加王国失败");
		}
	}
	@ResponseBody
	@RequestMapping("delMyKingdom")
	@SystemControllerLog(description = "删除串门王国")
	public JsonResult delMyKingdom(HttpServletRequest request){
		try{
			String id = request.getParameter("id");
			this.liveService.delDropAroundKingdom(Integer.parseInt(id));
			return JsonResult.success();
		}catch(Exception e){
			e.printStackTrace();
			return JsonResult.error("删除王国失败");
		}
	}
	// 修改顺序。
	@ResponseBody
	@RequestMapping("updateDropAroundKingdomOrder")
	@SystemControllerLog(description = "修改王国管理")
	public JsonResult updateDropAroundKingdomOrder(HttpServletRequest request,TopicDroparound td){
		try{
			this.liveService.updateDropAroundKingdom(td);
			return JsonResult.success();
		}catch(Exception e){
			return JsonResult.error("添加王国失败");
		}
	}
	@ResponseBody
	@RequestMapping("/loadSourceKingdomPage")
	public DatatablePage loadSourceKingdomPage(DatatablePage dpage, HttpServletRequest request) throws Exception {
		PageBean page = dpage.toPageBean();
		String keyword =  request.getParameter("keyword");
		if(keyword!=null){
			keyword=new String(keyword.getBytes("iso-8859-1"),"utf-8");
		}
		PageBean<SearchDropAroundTopicDto>  result = liveService.getTopicPage(page, keyword);
		dpage.setData(result.getDataList());
		dpage.setRecordsTotal((int)result.getTotalRecords());
		return dpage;
	}
	
	@ResponseBody
	@RequestMapping("loadMyKingdomPage")
	public DatatablePage loadMyKingdomPage(DatatablePage dpage, HttpServletRequest request) throws Exception{
		
		String keyword=request.getParameter("keyword");
		if(keyword!=null){
			keyword=new String(keyword.getBytes("iso-8859-1"),"utf-8");
		}
		PageBean page2= this.liveService.getDropAroundKingdomPage(dpage.toPageBean(),keyword);
		dpage.setData(page2.getDataList());
		dpage.setRecordsTotal((int)page2.getTotalRecords());
		return dpage;
	}
}
