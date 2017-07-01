package com.me2me.mgmt.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.search.model.SearchHotKeyword;
import com.me2me.search.service.SearchService;
import com.plusnet.sso.api.vo.JsonResult;

@Controller  
@RequestMapping("/search")
public class SearchController {
		
	@Autowired
	private  SearchService searchService;
	
	@RequestMapping("/console")
	public String console(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String keyword = request.getParameter("keyword");
		request.setAttribute("keyword", keyword);
		if(keyword!=null){
			String type= request.getParameter("type");
			request.setAttribute("type", type);
			
			String contentType = request.getParameter("contentType");
			int cType = -1;
			if(StringUtils.isNotBlank(contentType)){
				int ct = Integer.valueOf(contentType);
				if(ct == 1){
					cType = 0;
				}else if(ct == 2){
					cType = 1000;
				}
			}
			
			String json=searchService.searchForJSON(keyword,type,cType, 1, 20);
			JSONObject obj = JSON.parseObject(json);
			JSONArray arr = obj.getJSONArray("content");
			request.setAttribute("dataList", arr);
		}
		return "search/console";
	}
	
	@ResponseBody
	@RequestMapping("/startTask")
	public JsonResult startTask(HttpServletRequest request,HttpServletResponse response){
		try{
			String task= request.getParameter("task");
			if("ugc".equals(task)){
				searchService.indexUgcData(true);
			}else if("kingdom".equals(task)){
				searchService.indexKingdomData(true);
			}else if("user".equals(task)){
				searchService.indexUserData(true);
			}else if("history".equals(task)){
				searchService.indexSearchHistory(true);
			}else if("tagSamples".equals(task)){
				searchService.indexTagSample();
			}
		}catch (Exception e) {
			e.printStackTrace();
			return JsonResult.error(e.getMessage());
		}
		return JsonResult.success();
	}
	
	
	
	@RequestMapping(value = "/hotkeywordList")
	@SystemControllerLog(description = "热词列表")
	public String hotkeywordList(HttpServletRequest request) throws Exception {
		String queryStr = request.getParameter("query");
		if(!StringUtils.isEmpty(queryStr)){
			queryStr=new String(queryStr.getBytes("iso-8859-1"),"utf-8");
		}
		List<SearchHotKeyword> list =searchService.getAllHotKeyword();
		request.setAttribute("dataList",list);
		return "search/hotkeywordList";
	}
	@RequestMapping(value = "/addHotKeyword")
	@SystemControllerLog(description = "添加热词")
	public String addHotKeyword(HttpServletRequest request) throws Exception {
		return "search/addHotKeyword";
	}
	@RequestMapping(value = "/modifyHotKeyword")
	@SystemControllerLog(description = "修改热词")
	public String modifyHotKeyword(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		SearchHotKeyword item =searchService.getHotKeywordById(Integer.parseInt(id));
		request.setAttribute("item",item);
		return "search/addHotKeyword";
	}
	
	@RequestMapping(value = "/doSaveHotKeyword")
	@SystemControllerLog(description = "保存热词")
	public String doSaveHotKeyword(HttpServletRequest request,SearchHotKeyword tpl) throws Exception {
		try{
			if(tpl.getId()!=null){
				searchService.updateHotKeyword(tpl);
			}else{
				tpl.setIsValid(1);
				tpl.setCreationDate(new Date());
				searchService.addHotKeyword(tpl);
			}
			return "redirect:./hotkeywordList";
		}catch(Exception e){
			request.setAttribute("item",tpl);
			return addHotKeyword(request);
		}
	}
	@ResponseBody
	@RequestMapping(value = "/updateHotKeyword")
	@SystemControllerLog(description = "修改热词顺序")
	public JsonResult updateHotKeyword(HttpServletRequest request) throws Exception{
		String json = request.getParameter("json");
		try{
			List<SearchHotKeyword> details = JSON.parseArray(json, SearchHotKeyword.class);
			for(SearchHotKeyword detail:details){
				searchService.updateHotKeyword(detail);
			}
			return JsonResult.success();
		}catch(Exception e){
			e.printStackTrace();
			return JsonResult.error("保存排序错误。");
		}
	}
	@ResponseBody
	@RequestMapping(value = "/deleteHotKeyword")
	@SystemControllerLog(description = "删除热词")
	public JsonResult deleteHotKeyword(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		searchService.delHotKeyword(Integer.parseInt(id));
		return JsonResult.success();
	}
}
