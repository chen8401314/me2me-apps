package com.me2me.mgmt.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.me2me.common.security.SecurityUtils;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.services.LocalConfigService;
import com.me2me.mgmt.task.billboard.KingdomCountDayTask;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.service.UserService;
import com.plusnet.sso.api.vo.JsonResult;

import lombok.extern.slf4j.Slf4j;
/**
 * 榜单管理
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
@Controller  
@RequestMapping("/topicCover")
@Slf4j
public class TopicCoverController {
	
	@Autowired
    private ContentService contentService;
	
	@Autowired
	private FileTransferService fileTransferService;
	
	
	@RequestMapping(value = "/list")
	public String list_ranking(HttpServletRequest request,DatatablePage dp) throws Exception {
		return "topicCover/list";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadPics")
	public DatatablePage ajaxLoadPics(HttpServletRequest request,DatatablePage dp) throws Exception {
		List<Map<String,Object>> ret = contentService.queryForList("select * from topic_preset_pic order by id desc");
		dp.setRecordsTotal(ret.size());
		dp.setData(ret);
		return dp;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/doSave")
	public JsonResult doSave(@RequestParam("file")MultipartFile file,HttpServletRequest mrequest) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis()+RandomUtils.nextInt(1111111, 9999999), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		contentService.update("insert into topic_preset_pic(pic,create_time) values(?,now())", imgName);
	    		return JsonResult.success();
			}
		}catch(Exception e){
			return JsonResult.error("上传失败");
		}
		return JsonResult.error("上传失败");
	}
	
	@ResponseBody
	@RequestMapping(value = "/delete")
	public JsonResult deleteRanking(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		try{
			String key = contentService.queryForObject("select pic from topic_preset_pic where id=?",String.class, Integer.parseInt(id));
			fileTransferService.deleteQiniuResource(key);
		}catch(Exception e){
			e.printStackTrace();
		}
		contentService.update("delete from topic_preset_pic where id=?",Integer.parseInt(id));
		return JsonResult.success();
	}
	
	
}
