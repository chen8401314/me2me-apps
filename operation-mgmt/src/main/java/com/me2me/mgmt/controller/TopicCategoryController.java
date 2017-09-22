package com.me2me.mgmt.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.me2me.common.security.SecurityUtils;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.model.TopicCategory;
import com.me2me.live.service.LiveExtService;
import com.me2me.mgmt.vo.DatatablePage;
import com.plusnet.sso.api.vo.JsonResult;

import lombok.extern.slf4j.Slf4j;
/**
 * 榜单管理
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
@Controller  
@RequestMapping("/topicCategory")
@Slf4j
public class TopicCategoryController {
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private FileTransferService fileTransferService;
	@Autowired
	private LiveExtService liveExtService;
	
	@RequestMapping(value = "/listCategory")
	public String list(HttpServletRequest request) throws Exception {
		List<Map<String,Object>> dataList =jdbc.queryForList("select * from topic_category order by order_num asc");
		request.setAttribute("dataList", dataList);
		return "topicCategory/list_category";
	}
	@RequestMapping(value = "/addCategory")
	public String addCategory(HttpServletRequest request,Integer id) throws Exception {
		return "topicCategory/add_category";
	}
	@RequestMapping(value = "/modifyCategory")
	public String modifyCategory(HttpServletRequest request,Integer id) throws Exception {
		Map<String,Object> data =jdbc.queryForMap("select * from topic_category where id=?",id);
		request.setAttribute("item", data);
		return "topicCategory/add_category";
	}
	@RequestMapping(value = "/doSave")
	public String doSave(@RequestParam("image_cover")MultipartFile file,@RequestParam("image_icon")MultipartFile iconFile,HttpServletRequest mrequest,TopicCategory category) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis()+RandomUtils.nextInt(1111111, 9999999), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		category.setCoverImg(imgName);
			}
			if(iconFile!=null && StringUtils.isNotEmpty(iconFile.getOriginalFilename()) && iconFile.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis()+RandomUtils.nextInt(1111111, 9999999), "1");
	    		fileTransferService.upload(iconFile.getBytes(), imgName);
	    		category.setIcon(imgName);
			}
		}catch(Exception e){
		}
		if(category.getId()==null){
			liveExtService.addCategory(category);
		}else{
			liveExtService.updateCategory(category);
		}
		return "redirect:./listCategory";
	}
	
	@RequestMapping(value = "/deleteCategory")
	public String deleteCategory(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		try{
			Map<String,Object> item = jdbc.queryForMap("select * from topic_category where id=?",Integer.parseInt(id));
			String cover = (String) item.get("cover_img");
			String icon = (String) item.get("icon");
			if(!org.apache.commons.lang3.StringUtils.isEmpty(cover)){
				fileTransferService.deleteQiniuResource(cover);
			}
			if(!org.apache.commons.lang3.StringUtils.isEmpty(icon)){
				fileTransferService.deleteQiniuResource(icon);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		jdbc.update("delete from topic_category where id=?",Integer.parseInt(id));
		return "redirect:./listCategory";
	}
	@RequestMapping(value = "/categoryTopicList")
	public String categoryTopicList(HttpServletRequest request) throws Exception {
		//查询所有分类
		List<Map<String,Object>> types = jdbc.queryForList("select * from topic_category order by order_num asc");
		request.setAttribute("types", types);
		String category = request.getParameter("category");
		for(Map<String,Object> type:types){
			if(category.equals(type.get("id").toString())){
				request.setAttribute("curType", type);
			}
		}
		return "topicCategory/category_topic_list";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxCategoryTopicList")
	public DatatablePage ajaxCategoryTopicList(HttpServletRequest request,DatatablePage page) throws Exception {
		String category= request.getParameter("category");
		String title = request.getParameter("title");
		//title= new String(title.getBytes("iso8859-1"),"utf-8");
		String where = "";
		if("".equals(category)){
			where= " (category_id is null or category_id=0) ";
		}else{
			where= " category_id="+category;
		}
		if(!StringUtils.isEmpty(title)){
			where+=" and title like '%"+title+"%' ";
		}
		int count = jdbc.queryForObject("select count(1) from topic where "+where,Integer.class);
		int skip= page.getStart();
		int limit= page.getLength();
		List<Map<String,Object>> dataList =jdbc.queryForList("select * from topic where "+where+" order by update_time desc limit ?,?",skip,limit);
		page.setData(dataList);
		page.setRecordsTotal(count);
		page.setLength(limit);
		return page;
	}
	@ResponseBody
	@RequestMapping(value = "/addTopic2Category")
	public JsonResult addTopic2Category(HttpServletRequest request) throws Exception {
		String ids = request.getParameter("ids");
		String toCategory= request.getParameter("toCategory");
		if(!StringUtils.isEmpty(ids) && toCategory!=null){
			if(toCategory.equals("")){
				jdbc.update("update topic set category_id=0 where id in("+ids+")");
			}else{
				jdbc.update("update topic set category_id=? where id in("+ids+")",toCategory);
			}
		}
		return JsonResult.success();
	}
}
