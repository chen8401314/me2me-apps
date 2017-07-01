package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.common.web.Response;
import com.me2me.live.dto.SearchTopicListedListDto;
import com.me2me.live.model.TopicListed;
import com.me2me.live.service.LiveService;
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
	
	@RequestMapping(value = "/topicListed")
	public String topicListed(HttpServletRequest request) throws Exception {
		return "topicListed/list_topicListed";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadTopicListed")
	public DatatablePage ajaxLoadUsers(HttpServletRequest request,DatatablePage page) throws Exception {
		SearchTopicListedListDto dto = new SearchTopicListedListDto();
		Response resp = liveService.searchTopicListedPage(0, page.getStart(), page.getLength());
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
		SearchTopicListedListDto dto = new SearchTopicListedListDto();
		Response resp = liveService.searchTopicListedPage(2, page.getStart(), page.getLength());
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
	
}
