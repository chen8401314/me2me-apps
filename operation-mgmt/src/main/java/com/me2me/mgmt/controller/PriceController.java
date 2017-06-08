package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.request.KingdomBusinessDTO;
import com.me2me.mgmt.request.KingdomDTO;
import com.me2me.mgmt.request.KingdomQueryDTO;
import com.me2me.mgmt.request.SearchUserDTO;

@Controller
@RequestMapping("/price")
public class PriceController {

	private static final Logger logger = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
    private ContentService contentService;
	
	@RequestMapping(value = "/kingdomQuery")
	public ModelAndView kingdomQuery(KingdomQueryDTO dto){
		
		if(StringUtils.isBlank(dto.getOrderbyParam())){
			dto.setOrderbyParam("create_time");
		}
		if(StringUtils.isBlank(dto.getOrderby())){
			dto.setOrderby("desc");
		}
		
		this.getkingdomList(dto);
		
		ModelAndView view = new ModelAndView("price/kingdomList");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value="/kingdomPage")
	@ResponseBody
	public String kingdomPage(KingdomQueryDTO dto){
		this.getkingdomList(dto);
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	private void getkingdomList(KingdomQueryDTO dto){
		int page = dto.getPage();
		if(page <= 0){
			page = 1;
		}
		int pageSize = dto.getPageSize();
		if(pageSize < 1){
			pageSize = 10;
		}
		
		int start = (page-1)*pageSize;
		StringBuilder sb = new StringBuilder();
		sb.append("select * from topic t");
		if(StringUtils.isNotBlank(dto.getTitle())){
			sb.append(" where t.title like '%").append(dto.getTitle()).append("%'");
		}
		sb.append(" order by ").append(dto.getOrderbyParam()).append(" ").append(dto.getOrderby());
		sb.append(" limit ").append(start).append(",").append(pageSize);
		String querySql = sb.toString();
		
		StringBuilder c = new StringBuilder();
		c.append("select count(1) as cc from topic t");
		if(StringUtils.isNotBlank(dto.getTitle())){
			c.append(" where t.title like '%").append(dto.getTitle()).append("%'");
		}
		String countSql = c.toString();
		
		int total = 0;
		List<Map<String, Object>> countList = contentService.queryEvery(countSql);
		if(null != countList && countList.size() > 0){
			Map<String, Object> count = countList.get(0);
			total = ((Long)count.get("cc")).intValue();
		}
		int totalPage = (total%pageSize==0)?(total/pageSize):(total/pageSize+1);
		dto.setTotalPage(totalPage);
		
		List<Map<String, Object>> queryList = contentService.queryEvery(querySql);
		if(null != queryList && queryList.size() > 0){
			List<Long> uidList = new ArrayList<Long>();
			Long uid = null;
			for(Map<String, Object> m : queryList){
				uid = (Long)m.get("uid");
				if(!uidList.contains(uid)){
					uidList.add(uid);
				}
			}
			
			StringBuilder userSql = new StringBuilder();
			userSql.append("select u.uid,u.nick_name from user_profile u where u.uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					userSql.append(",");
				}
				userSql.append(uidList.get(i));
			}
			userSql.append(")");
			List<Map<String, Object>> userList = contentService.queryEvery(userSql.toString());
			Map<String, String> userNameMap = new HashMap<String, String>();
			if(null != userList && userList.size() > 0){
				for(Map<String, Object> u : userList){
					userNameMap.put(String.valueOf(u.get("uid")), (String)u.get("nick_name"));
				}
			}
			
			KingdomQueryDTO.Item item = null;
			for(Map<String, Object> m : queryList){
				item = new KingdomQueryDTO.Item();
				item.setCreateTime((Date)m.get("create_time"));
				item.setId((Long)m.get("id"));
				item.setUid((Long)m.get("uid"));
				item.setNickName(userNameMap.get(String.valueOf(item.getUid())));
				item.setPrice((Integer)m.get("price"));
				item.setTitle((String)m.get("title"));
				item.setType((Integer)m.get("type"));
				item.setUpdateTime((Date)m.get("update_time"));
				dto.getResult().add(item);
			}
		}
	}
	
	@RequestMapping(value = "/kingdom/{id}")
	public ModelAndView getKingdom(@PathVariable long id){
		StringBuilder sb = new StringBuilder();
		sb.append("select t.id,t.uid,u.nick_name,t.title,t.price");
		sb.append(" from topic t,user_profile u");
		sb.append(" where t.uid=u.uid and t.id=").append(id);
		
		ModelAndView view = new ModelAndView("price/business");
		
		List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			Map<String, Object> map = list.get(0);
			KingdomDTO dto = new KingdomDTO();
			dto.setTopicId((Long)map.get("id"));
			dto.setTitle((String)map.get("title"));
			dto.setUid((Long)map.get("uid"));
			dto.setNickName((String)map.get("nick_name"));
			dto.setPrice((Integer)map.get("price"));
			
			view.addObject("dataObj",dto);
		}else{
			view.addObject("errMsg","当前王国有问题");
		}
		
		return view;
	}
	
	@RequestMapping(value="/searchUser")
	@ResponseBody
	public String searchUser(SearchUserDTO dto){
		if(StringUtils.isNotBlank(dto.getNickName()) || dto.getUid() > 0){
			StringBuilder sb = new StringBuilder();
			sb.append("select u.uid,u.nick_name from user_profile u");
			if(dto.getUid() > 0){
				sb.append(" where u.uid=").append(dto.getUid());
			}else{
				sb.append(" where u.nick_name like '%").append(dto.getNickName()).append("%'");
			}
			List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
			if(null != list && list.size() > 0){
				SearchUserDTO.Item item = null;
				for(Map<String, Object> m : list){
					item = new SearchUserDTO.Item();
					item.setUid((Long)m.get("uid"));
					item.setNickName((String)m.get("nick_name"));
					dto.getResult().add(item);
				}
			}
		}
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	@RequestMapping(value="/business")
	@ResponseBody
	public String business(KingdomBusinessDTO dto){
		return "0";
	}
}
