package com.me2me.mgmt.controller;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
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

import com.alibaba.fastjson.JSON;
import com.me2me.common.page.PageBean;
import com.me2me.common.security.SecurityUtils;
import com.me2me.content.dto.BillBoardRelationDto;
import com.me2me.content.dto.OnlineBillBoardDto;
import com.me2me.content.model.BillBoard;
import com.me2me.content.model.BillBoardDetails;
import com.me2me.content.model.BillBoardRelation;
import com.me2me.content.service.ContentService;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.dto.SearchTopicDto;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.dal.utils.HttpUtils;
import com.me2me.mgmt.services.LocalConfigService;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.dto.SearchUserDto;
import com.me2me.user.service.UserService;
import com.plusnet.sso.api.vo.JsonResult;

import lombok.extern.slf4j.Slf4j;
/**
 * 榜单管理
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
@Controller  
@RequestMapping("/ranking")
@Slf4j
public class RankingController {
	
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
	
	
	@RequestMapping(value = "/list_ranking")
	@SystemControllerLog(description = "榜单列表")
	public String list_ranking(HttpServletRequest request) throws Exception {
		List<BillBoard> list =contentService.getAllBillBoard();
		request.setAttribute("dataList",list);
		return "ranking_list/list_ranking";
	}
	@RequestMapping(value = "/add_ranking")
	@SystemControllerLog(description = "添加榜单")
	public String add_ranking(HttpServletRequest request) throws Exception {
		return "ranking_list/add_ranking";
	}
	
	@RequestMapping(value = "/modify_ranking")
	@SystemControllerLog(description = "修改榜单")
	public String modify_ranking(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		BillBoard item= contentService.getBillBoardById(Long.parseLong(id));
		request.setAttribute("item",item);
		return "ranking_list/add_ranking";
	}
	@RequestMapping(value = "/doSaveRanking")
	@SystemControllerLog(description = "保存榜单")
	public String doSaveRanking(BillBoard tpl,@RequestParam("image2")MultipartFile file,HttpServletRequest mrequest) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis(), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		tpl.setImage(imgName);
			}
			if(tpl.getId()!=null){
				contentService.updateBillBoard(tpl);
			}else{
				
				contentService.addBillBoard(tpl);
			}
			return "redirect:./list_ranking";
		}catch(Exception e){
			mrequest.setAttribute("item",tpl);
			return add_ranking(mrequest);
		}
	}
	@RequestMapping(value = "/deleteRanking")
	@SystemControllerLog(description = "删除榜单")
	public String deleteRanking(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		contentService.deleteBillBoardById(Long.parseLong(id));
		return "redirect:./list_ranking";
	}
	
	
	@RequestMapping(value = "/ranking_data_mgr")
	@SystemControllerLog(description = "榜单数据管理")
	public String ranking_data_mgr(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		BillBoard item= contentService.getBillBoardById(Long.parseLong(id));
		String baseKingdomURL = config.getWebappUrl();
		request.setAttribute("baseKingdomURL", baseKingdomURL);
		request.setAttribute("item",item);
		return "ranking_list/ranking_data_mgr";
	}
	@RequestMapping("listUsers")
	public String listUsers(HttpServletRequest request){
		return "ranking_list/ajax_list_user";
	}
	@RequestMapping("listKingdoms")
	public String lisKingdoms(HttpServletRequest request){
		String baseKingdomURL = config.getWebappUrl();
		request.setAttribute("baseKingdomURL", baseKingdomURL);
		return "ranking_list/ajax_list_kingdom";
	}
	@RequestMapping("listRankings")
	public String listRankings(HttpServletRequest request){
		List<BillBoard> list =contentService.getAllBillBoard();
		request.setAttribute("dataList",list);
		return "ranking_list/ajax_list_ranking";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadUsers")
	@SystemControllerLog(description = "载入系统用户")
	public DatatablePage ajaxLoadUsers(HttpServletRequest request,DatatablePage page) throws Exception {
		Map<String,Object> map = new HashMap<>();
		Enumeration<String> nameeum = request.getParameterNames();
		while(nameeum.hasMoreElements()){
			String name = nameeum.nextElement();
			String val = request.getParameter(name);
			
			if(!name.contains("][") && !StringUtils.isEmpty(val)){
				if(name.equals("nick_name")){
					val =HttpUtils.toUTF8(val);
					val = "%"+val+"%";
				}
				
				map.put(name, val);
			}
			
		}
		Map<String,String> colMap = new HashMap<>();
		colMap.put("userProfile.nickName", "nick_name");
		colMap.put("userProfile.uid", "uid");
		colMap.put("userProfile.createTime", "create_time");
		colMap.put("userProfile.vLv", "v_lv");
		
		// order
		String orderBy =request.getParameter("order[0][column]");
		String order =request.getParameter("order[0][dir]");
		if(orderBy!=null && order!=null){
			String canOrder = request.getParameter("columns["+orderBy+"][orderable]");
			if("true".equals(canOrder)){
				orderBy = request.getParameter("columns["+orderBy+"][data]");
				String orderBy2 = colMap.get(orderBy);
				if(orderBy2!=null){
					orderBy=orderBy2;
				}
				map.put("orderBy", orderBy);
				map.put("order", order);
			}
		}
		PageBean pb = page.toPageBean();
		PageBean<SearchUserDto> list = userService.searchUserPage(pb, map);
		page.setData(list.getDataList());
		page.setRecordsTotal((int)list.getTotalRecords());
		return page;
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadKingdoms")
	@SystemControllerLog(description = "载入系统王国")
	public DatatablePage ajaxLoadKingdoms(HttpServletRequest request,DatatablePage page) throws Exception {
		Map<String,Object> map = new HashMap<>();
		Enumeration<String> nameeum = request.getParameterNames();
		while(nameeum.hasMoreElements()){
			String name = nameeum.nextElement();
			String val = request.getParameter(name);
			if(!name.contains("][") && !StringUtils.isEmpty(val)){
				map.put(name, val);
			}
		}
		
		if(map.containsKey("title")){
			map.put("title","%"+HttpUtils.toUTF8(request.getParameter("title")+"%"));
		}
		if(map.containsKey("nick_name")){
			map.put("nick_name", "%"+HttpUtils.toUTF8(request.getParameter("nick_name"))+"%");
		}
		Map<String,String> colMap = new HashMap<>();
		colMap.put("reviewCount", "review_count");
		colMap.put("readCountDummy", "read_count_dummy");
		colMap.put("likeCount", "like_count");
		colMap.put("favoriteCount", "favorite_count");
		colMap.put("topicId", "topic_id");
		colMap.put("vLv", "v_lv");
		colMap.put("updateTime", "update_time");
		
		// order
		String orderBy =request.getParameter("order[0][column]");
		String order =request.getParameter("order[0][dir]");
		if(orderBy!=null && order!=null){
			String canOrder = request.getParameter("columns["+orderBy+"][orderable]");
			if("true".equals(canOrder)){
				
				orderBy = request.getParameter("columns["+orderBy+"][data]");
				String orderBy2 = colMap.get(orderBy);
				if(orderBy2!=null){
					orderBy=orderBy2;
				}
				map.put("orderBy", orderBy);
				map.put("order", order);
			}
		}
		PageBean pb = page.toPageBean();
		PageBean<SearchTopicDto> list =liveService.getTopicPage(pb, map);
		page.setData(list.getDataList());
		page.setRecordsTotal((int) list.getTotalRecords());
		return page;
		
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateRankingData")
	@SystemControllerLog(description = "修改排行数据")
	public JsonResult updateRankingData(HttpServletRequest request) throws Exception{
		String json = request.getParameter("json");
		try{
			List<BillBoardRelation> details = JSON.parseArray(json, BillBoardRelation.class);
			for(BillBoardRelation detail:details){
				contentService.updateBillBoardRelation(detail);
			}
			return JsonResult.success();
		}catch(Exception e){
			e.printStackTrace();
			return JsonResult.error("保存排序错误。");
		}
	}
	@ResponseBody
	@RequestMapping(value = "/doSaveRankingData")
	@SystemControllerLog(description = "保存排行数据")
	public JsonResult doSaveRankingData(HttpServletRequest request) throws Exception {
		try{
			String json = request.getParameter("json");
			List<BillBoardRelation> details = JSON.parseArray(json, BillBoardRelation.class);
			for(BillBoardRelation detail:details){
				contentService.addRelationToBillBoard(detail);
			}
			
			return JsonResult.success();
		}catch(Exception e){
			return JsonResult.error();
		}
	}
	@ResponseBody
	@RequestMapping(value = "/deleteRankingData")
	@SystemControllerLog(description = "删除排行数据")
	public JsonResult deleteRankingData(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		contentService.delBillBoardRelationById(Integer.parseInt(id));
		return JsonResult.success();
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadRankingData")
	@SystemControllerLog(description = "载入排行榜数据")
	public DatatablePage ajaxLoadRankingData(HttpServletRequest request,DatatablePage page) throws Exception {
		String id = request.getParameter("id");
		List<BillBoardRelationDto> dataList =  contentService.getRelationsByBillBoardId(Integer.parseInt(id));
		request.setAttribute("dataList",dataList);
		page.setData(dataList);
		page.setRecordsTotal(dataList.size());
		return page;
	}
	
	
	//===================== 上线榜单管理==============================================
	
	@RequestMapping(value = "/list_online_ranking")
	@SystemControllerLog(description = "上线榜单列表")
	public String list_online_ranking(HttpServletRequest request) throws Exception {
		String type = request.getParameter("type");
		List<BillBoard> list =contentService.getAllBillBoard();
		request.setAttribute("dataList",list);
		
		List<OnlineBillBoardDto> myDataList =contentService.getOnlineBillBoardListByType(Integer.parseInt(type));
		request.setAttribute("myDataList",myDataList);
		return "ranking_list/list_online_ranking";
	}
	
	@ResponseBody
	@RequestMapping(value = "/doSaveOnlineRanking")
	@SystemControllerLog(description = "保存上线榜单")
	public JsonResult doSaveOnlineRanking(HttpServletRequest request,BillBoardDetails tpl) throws Exception {
		try{
			contentService.addOnlineBillBoard(tpl);
			return JsonResult.success();
		}catch(Exception e){
			return JsonResult.error();
		}
	}
	@ResponseBody
	@RequestMapping(value = "/deleteOnlineRanking")
	@SystemControllerLog(description = "删除上线榜单")
	public JsonResult deleteOnlineRanking(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		contentService.delOnlineBillBoardById(Long.parseLong(id));
		return JsonResult.success();
	}
	@ResponseBody
	@RequestMapping(value = "/updateOnlineRanking")
	@SystemControllerLog(description = "修改上线榜单")
	public JsonResult updateOnlineRanking(HttpServletRequest request) throws Exception{
		String json = request.getParameter("json");
		try{
			List<BillBoardDetails> details = JSON.parseArray(json, BillBoardDetails.class);
			for(BillBoardDetails detail:details){
				contentService.updateOnlineBillBoard(detail);
			}
			return JsonResult.success();
		}catch(Exception e){
			e.printStackTrace();
			return JsonResult.error("保存排序错误。");
		}
	}
}
