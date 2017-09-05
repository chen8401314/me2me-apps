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
@RequestMapping("/giftInfo")
@Slf4j
public class GiftInfoController {
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LiveService liveService;
	
	@Autowired
	private LocalConfigService config;
	
	@Autowired
	private FileTransferService fileTransferService;
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping(value = "/list_gift")
	@SystemControllerLog(description = "礼物列表")
	public String list_gift(HttpServletRequest request) throws Exception {
		List<GiftInfo> datas = liveService.getGiftInfoList();
		EmotionInfoListDto dto = new EmotionInfoListDto();
		request.setAttribute("data",datas);
		return "gift/list_gift";
	}
	
	@RequestMapping(value = "/add_emotion")
	@SystemControllerLog(description = "添加情绪")
	public String add_emotion(HttpServletRequest request,DatatablePage dpage) throws Exception {
		List<EmotionPackDetail> list =contentService.getEmotionPackDetailBig();
		request.setAttribute("dataList2",list);
		return "emotionInfo/add_emotion";
	}
	
	@RequestMapping(value = "/modify_emotion")
	@SystemControllerLog(description = "修改情绪")
	public String modify_tease(HttpServletRequest request,DatatablePage dpage) throws Exception {
		String id = request.getParameter("id");
		EmotionInfo item= userService.getEmotionInfoByKey(Long.valueOf(id));
		request.setAttribute("item",item);
	    EmotionPackDetail epd  = contentService.getEmotionPackDetailByKey(Integer.valueOf(item.getEmotionpackid()+"") );
		request.setAttribute("epd",epd);
		List<EmotionPackDetail> list =contentService.getEmotionPackDetailBig();
		request.setAttribute("dataList2",list);
		Topic topic = liveService.getTopicById(item.getTopicid());
		request.setAttribute("topicTitle",topic.getTitle());
		return "emotionInfo/add_emotion";
	}
	@RequestMapping(value = "/doSaveEmotion")
	@SystemControllerLog(description = "保存情绪")
	public String doSaveEmotion(EmotionInfo tpl,HttpServletRequest mrequest,@RequestParam("file")MultipartFile file) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		tpl.setTopiccoverphoto(imgName);
			}
			if(tpl.getId()!=null){
				userService.updateEmotionInfoByKey(tpl); 
			}else{
				userService.addEmotionInfo(tpl);
			}
			return "redirect:./list_emotion";
		}catch(Exception e){
			e.printStackTrace();
			mrequest.setAttribute("item",tpl);
			return "redirect:./add_emotion";
		}
	}
	@RequestMapping(value = "/delete_emotion")
	@SystemControllerLog(description = "删除情绪")
	public String deleteEmotion(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		EmotionInfo emotionInfo  = new EmotionInfo();
		emotionInfo.setId(Long.valueOf(id));
		emotionInfo.setStatus(0);
		userService.updateEmotionInfoByKey(emotionInfo);
		return "redirect:./list_emotion";
	}
	@RequestMapping(value = "/up_emotion")
	@SystemControllerLog(description = "上架情绪")
	public String upEmotion(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		EmotionInfo emotionInfo  = new EmotionInfo();
		emotionInfo.setId(Long.valueOf(id));
		emotionInfo.setStatus(1);
		userService.updateEmotionInfoByKey(emotionInfo);
		return "redirect:./list_emotion";
	}
	@RequestMapping(value = "/off_emotion")
	@SystemControllerLog(description = "下架情绪")
	public String offEmotion(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		EmotionInfo emotionInfo  = new EmotionInfo();
		emotionInfo.setId(Long.valueOf(id));
		emotionInfo.setStatus(2);
		userService.updateEmotionInfoByKey(emotionInfo);
		return "redirect:./list_emotion";
	}
	@RequestMapping(value = "/existsEmotionInfoByName")
	@ResponseBody
	public String existsEmotionInfoByName(EmotionInfo tpl,HttpServletRequest mrequest) throws Exception {
			if(userService.existsEmotionInfoByName(tpl)){
				return "1";
			}else{
				return "0";
			}
	}

}
