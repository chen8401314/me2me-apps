package com.me2me.mgmt.controller;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.me2me.content.dto.OnlineBillBoardDto;
import com.me2me.content.model.BillBoard;
import com.me2me.content.model.BillBoardDetails;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.user.service.UserService;
import com.plusnet.sso.api.vo.JsonResult;
/**
 * 榜单管理
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
@Controller  
@RequestMapping("/ranking")
public class RankingController {
	
	@Autowired
    private ContentService contentService;
	
	@Autowired
	private UserService userService;
	
	
	@RequestMapping(value = "/list_ranking")
	@SystemControllerLog(description = "榜单列表")
	public String list_ranking(HttpServletRequest request) throws Exception {
		List<BillBoard> list =contentService.getAllBillBoard();
		request.setAttribute("dataList",list);
		return "ranking_list/list_ranking";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadUsers")
	@SystemControllerLog(description = "载入系统用户")
	public String ajaxLoadUsers(HttpServletRequest request) throws Exception {
		List<BillBoard> list =contentService.getAllBillBoard();
		request.setAttribute("dataList",list);
		return "ranking_list/list_ranking";
	}
	@ResponseBody
	@RequestMapping(value = "/ajaxLoadKingdoms")
	@SystemControllerLog(description = "载入系统王国")
	public String ajaxLoadKingdoms(HttpServletRequest request) throws Exception {
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
	@RequestMapping(value = "/ranking_data_mgr")
	@SystemControllerLog(description = "榜单数据管理")
	public String ranking_data_mgr(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		BillBoard item= contentService.getBillBoardById(Long.parseLong(id));
		request.setAttribute("item",item);
		return "ranking_list/ranking_data_mgr";
	}
	
	@RequestMapping(value = "/doSaveRanking")
	@SystemControllerLog(description = "保存榜单")
	public String doSaveRanking(HttpServletRequest request,BillBoard tpl) throws Exception {
		try{
			if(tpl.getId()!=null){
				contentService.updateBillBoard(tpl);
			}else{
				contentService.addBillBoard(tpl);
			}
			return "redirect:./list_ranking";
		}catch(Exception e){
			request.setAttribute("item",tpl);
			return add_ranking(request);
		}
	}
	@RequestMapping(value = "/deleteRanking")
	@SystemControllerLog(description = "删除榜单")
	public String deleteRanking(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
		contentService.deleteBillBoardById(Long.parseLong(id));
		return "redirect:./list_ranking";
	}
	
	
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
