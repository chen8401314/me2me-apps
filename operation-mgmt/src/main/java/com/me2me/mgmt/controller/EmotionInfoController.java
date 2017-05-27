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
 * 情绪管理
 * @author chenxiang
 * @date 2017-05-23
 */
@Controller  
@RequestMapping("/emotionInfo")
@Slf4j
public class EmotionInfoController {
	
	
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
	
	@RequestMapping(value = "/list_emotion")
	@SystemControllerLog(description = "情绪列表")
	public String list_emotion(HttpServletRequest request) throws Exception {
		List<EmotionInfo> datas = userService.getEmotionInfoList();
		EmotionInfoListDto dto = new EmotionInfoListDto();
		for (int i = 0; i < datas.size(); i++) {
			EmotionInfoListDto.EmotionInfoElement ee = EmotionInfoListDto.createEmotionInfoElement();
			Map<String,Object> map = new HashMap<String,Object>();
			EmotionInfo emotionInfo  =datas.get(i);
			ee.setId(emotionInfo.getId());
			ee.setEmotionName(emotionInfo.getEmotionname());
			ee.setHappyMin(emotionInfo.getHappymin());
			ee.setHappyMax(emotionInfo.getHappymax());
			ee.setFreeMin(emotionInfo.getFreemin());
			ee.setFreeMax(emotionInfo.getFreemax());
			if(emotionInfo.getTopicid()!=null){
			ee.setTopicId(emotionInfo.getTopicid());
			Topic topic =  liveService.getTopicById(emotionInfo.getTopicid());
			if(topic!=null){
				ee.setTopicTitle(topic.getTitle());
			}
			}
			EmotionPackDetail epd = contentService.getEmotionPackDetailByKey(Integer.valueOf(emotionInfo.getEmotionpackid()+""));
			EmotionInfoListDto.EmotionPack ep = EmotionInfoListDto.createEmotionPack();
			ep.setId(epd.getId());
			ep.setTitle(epd.getTitle());
			ep.setContent(epd.getExtra());
			ep.setImage(epd.getImage());
			ep.setThumb(epd.getThumb());
			ep.setW(epd.getW());
			ep.setH(epd.getH());
			ep.setThumb_w(epd.getThumbW());
			ep.setThumb_h(epd.getThumbH());
			ep.setExtra(epd.getExtra());
			ep.setEmojiType(1);
			ee.setEmotionPack(ep);
			dto.getEmotionInfoData().add(ee);
		}
		request.setAttribute("data",dto);

		
		return "emotionInfo/list_emotion";
	}
	
	@RequestMapping(value = "/add_emotion")
	@SystemControllerLog(description = "添加情绪")
	public String add_emotion(HttpServletRequest request,DatatablePage dpage) throws Exception {

		Map<String,Object> param = new HashMap<>();
		param.put("packId", 1);
		PageBean page = dpage.toPageBean();
		page.setPageSize(99999);
		PageBean<EmotionPackDetail> list =contentService.getEmotionPackDetailPage(page, param);
		request.setAttribute("dataList2",list.getDataList());
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
		Map<String,Object> param = new HashMap<>();
		param.put("packId", 1);
		PageBean page = dpage.toPageBean();
		page.setPageSize(99999);
		PageBean<EmotionPackDetail> list =contentService.getEmotionPackDetailPage(page, param);
		request.setAttribute("dataList2",list.getDataList());
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
