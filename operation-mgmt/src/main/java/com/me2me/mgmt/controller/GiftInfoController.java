package com.me2me.mgmt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.me2me.common.page.PageBean;
import com.me2me.common.security.SecurityUtils;
import com.me2me.content.model.EmotionPackDetail;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.dto.GiftInfoListDto;
import com.me2me.live.model.GiftInfo;
import com.me2me.live.model.Topic;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.services.LocalConfigService;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.dto.EmotionInfoListDto;
import com.me2me.user.model.EmotionInfo;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;
/**
 * 礼物管理
 * @author chenxiang
 * @date 2017-09-1
 */
@Controller  
@RequestMapping("/gift")
@Slf4j
public class GiftInfoController {
	
	
	@Autowired
	private LiveService liveService;
	
	
	@Autowired
	private FileTransferService fileTransferService;
	
	@RequestMapping(value = "/list_gift")
	@SystemControllerLog(description = "礼物列表")
	public String list_gift(HttpServletRequest request) throws Exception {
		List<GiftInfo> datas = liveService.getGiftInfoList();
		GiftInfoListDto dto = new GiftInfoListDto();
		for (GiftInfo giftInfo : datas) {
			GiftInfoListDto.GiftInfoElement e = GiftInfoListDto.createGiftInfoElement();
			e.setId(giftInfo.getId());
			e.setName(giftInfo.getName());
			e.setImage(giftInfo.getImage());
			e.setPrice(giftInfo.getPrice());
			e.setAddPrice(giftInfo.getAddPrice());
			e.setImageWidth(giftInfo.getImageWidth());
			e.setImageHeight(giftInfo.getImageHeight());
			e.setGifImage(giftInfo.getGifImage());
			e.setPlayTime(giftInfo.getPlayTime());
			e.setSortNumber(giftInfo.getSortNumber());
			e.setStatus(giftInfo.getStatus());
			dto.getGiftInfoElementData().add(e);
		}
		request.setAttribute("data",dto.getGiftInfoElementData());
		return "gift/list_gift";
	}
	
	@RequestMapping(value = "/add_gift")
	@SystemControllerLog(description = "添加礼物")
	public String add_gift(HttpServletRequest request,DatatablePage dpage) throws Exception {
		return "gift/add_gift";
	}
	
	@RequestMapping(value = "/modify_gift")
	@SystemControllerLog(description = "修改礼物")
	public String modify_gift(HttpServletRequest request,DatatablePage dpage) throws Exception {
		String id = request.getParameter("id");
		GiftInfo item= liveService.getGiftInfoById(Long.valueOf(id));
		request.setAttribute("item",item);
		return "gift/add_gift";
	}
	@RequestMapping(value = "/doSaveGiftInfo")
	@SystemControllerLog(description = "保存礼物")
	public String doSaveGiftInfo(GiftInfo tpl,HttpServletRequest mrequest,@RequestParam("file")MultipartFile file,@RequestParam("file1")MultipartFile file1) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		tpl.setImage(imgName);
			}
			if(file1!=null && StringUtils.isNotEmpty(file1.getOriginalFilename()) && file1.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file1.getBytes(), imgName);
	    		tpl.setGifImage(imgName);
			}
			if(tpl.getId()!=null){
				liveService.updateGiftInfo(tpl); 
			}else{
				liveService.saveGiftInfo(tpl);
			}
			return "redirect:./list_gift";
		}catch(Exception e){
			e.printStackTrace();
			mrequest.setAttribute("item",tpl);
			return "redirect:./add_gift";
		}
	}
	@RequestMapping(value = "/delete_gift")
	@SystemControllerLog(description = "删除礼物")
	public String delete_gift(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		GiftInfo giftInfo  = new GiftInfo();
		giftInfo.setId(Long.valueOf(id));
		giftInfo.setStatus(-1);
		liveService.updateGiftInfo(giftInfo); 
		return "redirect:./list_gift";
	}

}
