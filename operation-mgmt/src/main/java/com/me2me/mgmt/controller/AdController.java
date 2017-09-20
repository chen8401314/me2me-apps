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
import com.me2me.content.dto.SearchAdBannerListDto;
import com.me2me.content.service.ContentService;
import com.me2me.live.dto.SearchTopicListedListDto;
import com.me2me.live.model.TopicListed;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.dal.utils.HttpUtils;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.model.EmotionInfo;
import com.plusnet.sso.api.vo.JsonResult;


@Controller
@RequestMapping("/ad")
public class AdController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdController.class);
	
	@Autowired
    private ContentService contentService;
	
	
	@RequestMapping(value = "/adBanner")
	public String topicListed(HttpServletRequest request) throws Exception {
		return "ad/list_adBanner";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxAdBannerList")
	public DatatablePage ajaxLoadUsers(HttpServletRequest request,DatatablePage page) throws Exception {
		int status = -1;
		SearchAdBannerListDto dto = new SearchAdBannerListDto();
		PageBean pb = page.toPageBean();
		Response resp = contentService.searchAdBannerListPage(status,pb.getCurrentPage(),pb.getPageSize());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto = (SearchAdBannerListDto)resp.getData();
		}
		page.setData(dto.getResult());
		page.setRecordsTotal(dto.getTotalRecord());
		return page;
	}
}
