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
import com.me2me.content.model.EmotionPack;
import com.me2me.content.model.EmotionPackDetail;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.services.LocalConfigService;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;
/**
 * 表情包管理
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
@Controller  
@RequestMapping("/emotion")
@Slf4j
public class EmotionController {
	
	@Autowired
    private ContentService contentService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LiveService liveService;
	
	@Autowired
	private LocalConfigService config;
	@Autowired
	private FileTransferService fileTransferService;
	
	@RequestMapping(value = "/list_pack")
	@SystemControllerLog(description = "表情包列表")
	public String list_emotion_packs(HttpServletRequest request,DatatablePage dpage) throws Exception {
		Map<String,Object> param = new HashMap<>();
		PageBean page = dpage.toPageBean();
		page.setPageSize(99999);
		PageBean<EmotionPack> list =contentService.getEmotionPackPage(page, param);
		request.setAttribute("dataList2",list.getDataList());
		return "emotions/list_pack";
	}
	
	@RequestMapping(value = "/add_pack")
	@SystemControllerLog(description = "添加表情包")
	public String add_pack(HttpServletRequest request) throws Exception {
		return "emotions/add_pack";
	}
	
	@RequestMapping(value = "/modify_pack")
	@SystemControllerLog(description = "修改表情包")
	public String modify_pack(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		EmotionPack item= contentService.getEmotionPackByKey(Integer.parseInt(id));
		request.setAttribute("item",item);
		return "emotions/add_pack";
	}
	@RequestMapping(value = "/doSavepack")
	@SystemControllerLog(description = "保存表情包")
	public String doSavepack(EmotionPack tpl,@RequestParam("file")MultipartFile file,HttpServletRequest mrequest) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		tpl.setCover(imgName);
			}
			if(tpl.getId()!=null){
				contentService.updateEmotionPackByKey(tpl); 
			}else{
				contentService.addEmotionPack(tpl);
			}
			return "redirect:./list_pack";
		}catch(Exception e){
			e.printStackTrace();
			mrequest.setAttribute("item",tpl);
			return add_pack(mrequest);
		}
	}
	@RequestMapping(value = "/delete_pack")
	@SystemControllerLog(description = "删除表情包")
	public String deletepack(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		contentService.deleteEmotionPackByKey(Integer.parseInt(id));
		return "redirect:./list_pack";
	}
	
	
	//===================== 上线表情包管理==============================================
	
	@RequestMapping(value = "/list_pack_detail")
	@SystemControllerLog(description = "表情包列表")
	public String list_pack_detail(HttpServletRequest request,DatatablePage dpage,@RequestParam("packId")Integer packId) throws Exception {
		Map<String,Object> param = new HashMap<>();
		param.put("packId", packId);
		PageBean page = dpage.toPageBean();
		page.setPageSize(99999);
		PageBean<EmotionPackDetail> list =contentService.getEmotionPackDetailPage(page, param);
		request.setAttribute("dataList2",list.getDataList());
		return "emotions/list_pack_detail";
	}
	
	@RequestMapping(value = "/add_pack_detail")
	@SystemControllerLog(description = "添加表情包")
	public String add_pack_detail(HttpServletRequest request) throws Exception {
		String packId= request.getParameter("packId");
		request.setAttribute("packId", packId);
		return "emotions/add_pack_detail";
	}
	
	@RequestMapping(value = "/modify_pack_detail")
	@SystemControllerLog(description = "修改表情包")
	public String modify_pack_detail(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		EmotionPackDetail item= contentService.getEmotionPackDetailByKey(Integer.parseInt(id));
		request.setAttribute("packId", item.getPackId());
		request.setAttribute("item",item);
		return "emotions/add_pack_detail";
	}
	@RequestMapping(value = "/doSavepack_detail")
	@SystemControllerLog(description = "保存表情包")
	public String doSavepack_detail(EmotionPackDetail tpl,@RequestParam("file")MultipartFile file,HttpServletRequest mrequest) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		tpl.setImage(imgName);
	    		tpl.setThumb(imgName);
	    		tpl.setThumbH(tpl.getH());
	    		tpl.setThumbW(tpl.getW());
			}
			if(tpl.getId()!=null){
				contentService.updateEmotionPackDetailByKey(tpl);
			}else{
				contentService.addEmotionPackDetail(tpl);
			}
			return "redirect:./list_pack_detail?packId="+tpl.getPackId();
		}catch(Exception e){
			mrequest.setAttribute("item",tpl);
			return add_pack(mrequest);
		}
	}
	@RequestMapping(value = "/delete_pack_detail")
	@SystemControllerLog(description = "删除表情包")
	public String deletepack_detail(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		String packid = request.getParameter("packId");
		contentService.deleteEmotionPackDetailByKey(Integer.parseInt(id));
		return "redirect:./list_pack_detail?packId="+packid;
	}

}
