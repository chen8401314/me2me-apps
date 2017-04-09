package com.me2me.mgmt.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.request.AppTagQueryDTO;

@Controller
@RequestMapping("/tag")
public class AppTagController {

	private static final Logger logger = LoggerFactory.getLogger(AppTagController.class);
	
	@Autowired
    private ContentService contentService;
	
	@RequestMapping(value = "/query")
	public ModelAndView tagQuery(AppTagQueryDTO dto){
		
		this.getTagInfo(dto);
		
		ModelAndView view = new ModelAndView("tag/tagList");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value="/page")
	@ResponseBody
	public String tagPage(AppTagQueryDTO dto){
		this.getTagInfo(dto);
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	private void getTagInfo(AppTagQueryDTO dto){
		int pageSize = dto.getPageSize();
		int start = (dto.getPage()-1)*dto.getPageSize();
		
		StringBuilder countSql = new StringBuilder();
		countSql.append("select count(1) as cc from (");
		countSql.append("select t.id,count(d.topic_id) as kcount,");
		countSql.append("max(t.create_time) as createtime,");
		countSql.append("max(t.tag) as tag,");
		countSql.append("max(t.is_rec) as isrec,");
		countSql.append("max(t.is_sys) as issys,");
		countSql.append("max(t.status) as status");
		countSql.append(" from topic_tag t left join topic_tag_detail d");
		countSql.append(" on t.id=d.tag_id and d.status=0");
		countSql.append(" where 1=1");
		if(StringUtils.isNotBlank(dto.getStartTime())){
			countSql.append(" and t.create_time>='").append(dto.getStartTime());
			countSql.append(" 00:00:00'");
		}
		if(StringUtils.isNotBlank(dto.getEndTime())){
			countSql.append(" and t.create_time<='").append(dto.getEndTime());
			countSql.append(" 23:59:59'");
		}
		if(StringUtils.isNotBlank(dto.getTagName())){
			countSql.append(" and t.tag like '%").append(dto.getTagName()).append("%'");
		}
		if(dto.getIsRec() >= 0){
			countSql.append(" and t.is_rec=").append(dto.getIsRec());
		}
		if(dto.getIsSys() >= 0){
			countSql.append(" and t.is_sys=").append(dto.getIsSys());
		}
		countSql.append(" group by t.id");
		if(StringUtils.isNotBlank(dto.getTopicCountStart()) 
				&& StringUtils.isNotBlank(dto.getTopicCountEnd())){
			countSql.append(" having kcount>=").append(dto.getTopicCountStart());
			countSql.append(" and kcount<=").append(dto.getTopicCountEnd());
		}else if(StringUtils.isNotBlank(dto.getTopicCountStart())){
			countSql.append(" having kcount>=").append(dto.getTopicCountStart());
		}else if(StringUtils.isNotBlank(dto.getTopicCountEnd())){
			countSql.append(" having kcount<=").append(dto.getTopicCountEnd());
		}
		countSql.append(") m");
		
		StringBuilder querySql = new StringBuilder();
		querySql.append("select t.id,count(d.topic_id) as kcount,");
		querySql.append("max(t.create_time) as createtime,");
		querySql.append("max(t.tag) as tag,max(t.is_rec) as isrec,");
		querySql.append("max(t.is_sys) as issys,max(t.status) as status");
		querySql.append(" from topic_tag t left join topic_tag_detail d");
		querySql.append(" on t.id=d.tag_id and d.status=0");
		querySql.append(" where 1=1");
		if(StringUtils.isNotBlank(dto.getStartTime())){
			querySql.append(" and t.create_time>='").append(dto.getStartTime());
			querySql.append(" 00:00:00'");
		}
		if(StringUtils.isNotBlank(dto.getEndTime())){
			querySql.append(" and t.create_time<='").append(dto.getEndTime());
			querySql.append(" 23:59:59'");
		}
		if(StringUtils.isNotBlank(dto.getTagName())){
			querySql.append(" and t.tag like '%").append(dto.getTagName()).append("%'");
		}
		if(dto.getIsRec() >= 0){
			querySql.append(" and t.is_rec=").append(dto.getIsRec());
		}
		if(dto.getIsSys() >= 0){
			querySql.append(" and t.is_sys=").append(dto.getIsSys());
		}
		querySql.append(" group by t.id");
		if(StringUtils.isNotBlank(dto.getTopicCountStart()) 
				&& StringUtils.isNotBlank(dto.getTopicCountEnd())){
			querySql.append(" having kcount>=").append(dto.getTopicCountStart());
			querySql.append(" and kcount<=").append(dto.getTopicCountEnd());
		}else if(StringUtils.isNotBlank(dto.getTopicCountStart())){
			querySql.append(" having kcount>=").append(dto.getTopicCountStart());
		}else if(StringUtils.isNotBlank(dto.getTopicCountEnd())){
			querySql.append(" having kcount<=").append(dto.getTopicCountEnd());
		}
		querySql.append(" order by createtime desc");
		querySql.append(" limit ").append(start).append(",").append(pageSize);
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> countList = null;
		try{
			countList = contentService.queryEvery(countSql.toString());
			list = contentService.queryEvery(querySql.toString());
		}catch(Exception e){
			logger.error("查询出错", e);
		}

		if(null != countList && countList.size() > 0){
			Map<String, Object> count = countList.get(0);
			long totalCount = (Long)count.get("cc");
			int totalPage = totalCount%pageSize==0?(int)totalCount/pageSize:((int)totalCount/pageSize)+1;
			dto.setTotalCount((int)totalCount);
			dto.setTotalPage(totalPage);
		}
		
		if(null != list && list.size() > 0){
			AppTagQueryDTO.Item item = null;
			for(Map<String, Object> m : list){
				item = new AppTagQueryDTO.Item();
				item.setId((Long)m.get("id"));
				item.setCreateTime((Date)m.get("createtime"));
				item.setIsRec((Integer)m.get("isrec"));
				item.setIsSys((Integer)m.get("issys"));
				item.setStatus((Integer)m.get("status"));
				item.setTagName((String)m.get("tag"));
				item.setTopicCount((Long)m.get("kcount"));
				dto.getResult().add(item);
			}
		}
	}
}
