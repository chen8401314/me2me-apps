package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.cache.service.CacheService;
import com.me2me.common.page.PageBean;
import com.me2me.common.web.Response;
import com.me2me.live.dto.SearchTopicListedListDto;
import com.me2me.live.model.TopicListed;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.dal.utils.HttpUtils;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.model.EmotionInfo;
import com.plusnet.sso.api.vo.JsonResult;


@Controller
@RequestMapping("/topicListed")
public class TopicListedController {
	
	private static final Logger logger = LoggerFactory.getLogger(TopicListedController.class);
	
	@Autowired
    private LiveService liveService;
	
	@Autowired
    private CacheService cacheService;
	
	@RequestMapping(value = "/topicListed")
	public String topicListed(HttpServletRequest request) throws Exception {
		String startTime="";
		String endTime ="";
	    String restStrDateBegin =cacheService.get("REST_LISTED_START_DATE");
	    String restStrDateEnd =cacheService.get("REST_LISTED_END_DATE");
	    if(!StringUtils.isEmpty(restStrDateBegin)){
	    	startTime = restStrDateBegin;
	    }
	    if(!StringUtils.isEmpty(restStrDateEnd)){
	    	endTime = restStrDateEnd;
	    }
		request.setAttribute("startTime",startTime);
		request.setAttribute("endTime",endTime);
		return "topicListed/list_topicListed";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadTopicListed")
	public DatatablePage ajaxLoadUsers(HttpServletRequest request,DatatablePage page) throws Exception {
		String title = request.getParameter("title");
		String statusStr = request.getParameter("status");
		int status = -1;
		if(!StringUtils.isEmpty(statusStr)){
			status = Integer.parseInt(statusStr);
		}
		SearchTopicListedListDto dto = new SearchTopicListedListDto();
		PageBean pb = page.toPageBean();
		Response resp = liveService.searchTopicListedPage(status,title, pb.getCurrentPage(),pb.getPageSize());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto = (SearchTopicListedListDto)resp.getData();
		}
		page.setData(dto.getResult());
		page.setRecordsTotal(dto.getTotalRecord());
		return page;
	}
	@RequestMapping(value = "/handleTopicListed")
	@ResponseBody
	public String handleTopicListed(TopicListed topicListed,HttpServletRequest mrequest) throws Exception {
		try {
			liveService.updateTopicListedStatus(topicListed);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	@RequestMapping(value = "/topicListedPending")
	public String topicListedPending(HttpServletRequest request) throws Exception {
		return "topicListed/list_topicListedPending";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadTopicListedPending")
	public DatatablePage ajaxLoadTopicListedPending(HttpServletRequest request,DatatablePage page) throws Exception {
		String title = request.getParameter("title");
		SearchTopicListedListDto dto = new SearchTopicListedListDto();
		PageBean pb = page.toPageBean();
		Response resp = liveService.searchTopicListedPage(2,title, pb.getCurrentPage(),pb.getPageSize());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto = (SearchTopicListedListDto)resp.getData();
		}
		page.setData(dto.getResult());
		page.setRecordsTotal(dto.getTotalRecord());
		return page;
	}
	@RequestMapping(value = "/handleTransaction")
	@ResponseBody
	public JsonResult handleTransaction(Long id,Long meNumber,HttpServletRequest mrequest) throws Exception {
			try {
				String result= liveService.handleTransaction(id, meNumber);
				if("0".equals(result)){
					return JsonResult.success();
				}else{
					return JsonResult.error(result);
				}
			} catch (Exception e) {
				return JsonResult.error(e.getMessage());
			}
	}
	@RequestMapping(value = "/setRestTime")
	@ResponseBody
	public String setRestTime(String startTime,String endTime,HttpServletRequest mrequest) throws Exception {
		try {
		   cacheService.set("REST_LISTED_START_DATE",startTime);
		   cacheService.set("REST_LISTED_END_DATE",endTime);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
}
