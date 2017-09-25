package com.me2me.mgmt.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.me2me.common.page.PageBean;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.content.dto.SearchAdBannerListDto;
import com.me2me.content.dto.SearchAdInfoListDto;
import com.me2me.content.model.AdBanner;
import com.me2me.content.model.AdInfo;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.model.Topic;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.vo.DatatablePage;

@Controller
@RequestMapping("/ad")
public class AdController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdController.class);
	
	@Autowired
    private ContentService contentService;
	
	@Autowired
    private LiveService liverService;
	
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
			logger.error("保存广告位失败", e);
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
			logger.error("删除广告位失败", e);
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
			logger.error("删除失败", e);
			return "0";
		}
	}
	@RequestMapping(value = "/getAdInfo")
	@ResponseBody
	public AdInfo getAdInfo(long id,HttpServletRequest mrequest) throws Exception {
		try {
			return contentService.getAdInfoById(id);
		} catch (Exception e) {
			return null;
		}
	}
	@RequestMapping(value = "/addAdInfo")
	@ResponseBody
	public Map<String,String> addAdInfo(MultipartHttpServletRequest mrequest) throws Exception {
		Map<String,String> map  = new HashMap<String,String>();
		map.put("result","0");
		try{
			AdInfo adInfo = new AdInfo();
			adInfo.setId(StringUtils.isEmpty(mrequest.getParameter("id"))?0:Long.parseLong(mrequest.getParameter("id")));
			adInfo.setAdTitle(StringUtils.isEmpty(mrequest.getParameter("adTitle"))?null:mrequest.getParameter("adTitle"));
			adInfo.setAdCoverWidth(StringUtils.isEmpty(mrequest.getParameter("adCoverWidth"))?null:Integer.parseInt(mrequest.getParameter("adCoverWidth")));
			adInfo.setAdCoverHeight(StringUtils.isEmpty(mrequest.getParameter("adCoverHeight"))?null:Integer.parseInt(mrequest.getParameter("adCoverHeight")));
			adInfo.setEffectiveTime(StringUtils.isEmpty(mrequest.getParameter("effectiveTime"))?null:new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mrequest.getParameter("effectiveTime")));
			adInfo.setDisplayProbability(StringUtils.isEmpty(mrequest.getParameter("displayProbability"))?null:Integer.parseInt(mrequest.getParameter("displayProbability")));
			adInfo.setType(StringUtils.isEmpty(mrequest.getParameter("type"))?null:Integer.parseInt(mrequest.getParameter("type")));
			adInfo.setTopicId(StringUtils.isEmpty(mrequest.getParameter("topicId"))?0:Long.parseLong(mrequest.getParameter("topicId")));
			adInfo.setAdUrl(StringUtils.isEmpty(mrequest.getParameter("adUrl"))?null:mrequest.getParameter("adUrl"));
			adInfo.setBannerId(StringUtils.isEmpty(mrequest.getParameter("bannerId"))?0:Long.parseLong(mrequest.getParameter("bannerId")));
			if(adInfo.getType()==1){
			Topic topic = 	liverService.getTopicById(adInfo.getTopicId());
				if(topic == null){
					map.put("result","0");
					map.put("msg", "找不到该王国");
					return map;
				}
			}
			MultipartFile file = mrequest.getFile("file");
			if(file!=null && !StringUtils.isEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		adInfo.setAdCover(imgName);
			}
			if(adInfo.getId()!=0){
				contentService.updateAdInfo(adInfo);
			}else{
				contentService.saveAdInfo(adInfo);
			}
			map.put("result","1");
			return map;
		}catch(Exception e){
			e.printStackTrace();
			logger.error("保存广告失败", e);
			map.put("result","0");
			map.put("msg",e.getMessage());
			return map;
		}
	}
	@RequestMapping(value = "/getTimeInterval")
	@ResponseBody
	public Map<String,String> getTimeInterval(HttpServletRequest mrequest) throws Exception {
		Map<String,String> dateMap  =new HashMap<String,String>();
		try{
			Date today = new Date();
			Date effectiveTime = DateUtil.addDay(today,1);
			dateMap.put("data", "1");
			dateMap.put("today",DateUtil.date2string(today, "yyyy-MM-dd HH:mm:ss"));
			dateMap.put("effectiveTime",DateUtil.date2string(effectiveTime, "yyyy-MM-dd HH:mm:ss"));
			return dateMap;
		}catch(Exception e){
			dateMap.put("data","0");
			return dateMap;
		}
	}
	
}
