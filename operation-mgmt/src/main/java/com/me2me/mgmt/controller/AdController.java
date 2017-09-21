package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.me2me.cache.service.CacheService;
import com.me2me.common.page.PageBean;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Response;
import com.me2me.content.dto.SearchAdBannerListDto;
import com.me2me.content.dto.SearchAdInfoListDto;
import com.me2me.content.model.AdBanner;
import com.me2me.content.model.AdInfo;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.dto.SearchTopicListedListDto;
import com.me2me.live.model.GiftInfo;
import com.me2me.live.model.QuotationInfo;
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
	
	@Autowired
	private FileTransferService fileTransferService;
	
	@RequestMapping(value = "/adBanner")
	public String adBanner(HttpServletRequest request) throws Exception {
		return "ad/list_adBanner";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxAdBannerList")
	public DatatablePage ajaxAdBannerList(HttpServletRequest request,DatatablePage page) throws Exception {
		int status = 0;
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
	
	@ResponseBody
	@RequestMapping(value = "/ajaxAllAdBannerList")
	public List<AdBanner> ajaxAllAdBannerList(HttpServletRequest request,DatatablePage page) throws Exception {
		int status = 0;
		return contentService.getAllAdBannerList(status);
	}
	@RequestMapping(value = "/addAdBanner")
	@ResponseBody
	public String addAdBanner(AdBanner adBanner,HttpServletRequest mrequest) throws Exception {
		try {
		    if(adBanner.getId()==0){
		    	contentService.saveAdBanner(adBanner);
		    }else{
		    	contentService.updateAdBanner(adBanner);
		    }
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	@RequestMapping(value = "/delAdBanner")
	@ResponseBody
	public String delAdBanner(long id,HttpServletRequest mrequest) throws Exception {
		try {
			AdBanner adBanner = new AdBanner();
			adBanner.setId(id);
			adBanner.setStatus(1);
			contentService.updateAdBanner(adBanner);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	@RequestMapping(value = "/getAdBanner")
	@ResponseBody
	public AdBanner getAdBanner(long id,HttpServletRequest mrequest) throws Exception {
		try {
			return contentService.getAdBannerById(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	@RequestMapping(value = "/adInfo")
	public String adInfo(HttpServletRequest request) throws Exception {
		return "ad/list_adInfo";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxAdInfoList")
	public DatatablePage ajaxAdInfoList(long bannerId,HttpServletRequest request,DatatablePage page) throws Exception {
		int status = 0;
		SearchAdInfoListDto dto = new SearchAdInfoListDto();
		PageBean pb = page.toPageBean();
		Response resp = contentService.searchAdInfoListPage(status,bannerId,pb.getCurrentPage(),pb.getPageSize());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			dto = (SearchAdInfoListDto)resp.getData();
		}
		page.setData(dto.getResult());
		page.setRecordsTotal(dto.getTotalRecord());
		return page;
	}
	@RequestMapping(value = "/delAdInfo")
	@ResponseBody
	public String delAdInfo(long id,HttpServletRequest mrequest) throws Exception {
		try {
			AdInfo adInfo = new AdInfo();
			adInfo.setId(id);
			adInfo.setStatus(1);
			contentService.updateAdInfo(adInfo);
			return "1";
		} catch (Exception e) {
			return "0";
		}
	}
	@RequestMapping(value = "/getAdInfo")
	@ResponseBody
	public AdBanner getAdInfo(long id,HttpServletRequest mrequest) throws Exception {
		try {
			return contentService.getAdInfoById(id);
		} catch (Exception e) {
			return null;
		}
	}
	@RequestMapping(value = "/addAdInfo")
	@SystemControllerLog(description = "保存广告信息")
	public String addAdInfo(AdInfo adInfo,HttpServletRequest mrequest,@RequestParam("file")MultipartFile file) throws Exception {
		try{
			if(file!=null && !StringUtils.isEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		adInfo.setAdCover(imgName);
			}
			if(adInfo.getId()!=null || adInfo.getId()!=0){
				contentService.updateAdInfo(adInfo);
			}else{
				contentService.saveAdInfo(adInfo);
			}
			return "1";
		}catch(Exception e){
			return "0";
		}
	}
	
}
