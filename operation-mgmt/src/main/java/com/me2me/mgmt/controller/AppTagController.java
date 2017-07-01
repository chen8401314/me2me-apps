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
import com.me2me.live.model.TopicTag;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.request.AppTagQueryDTO;
import com.me2me.mgmt.request.AppTagTopicListQueryDTO;
import com.me2me.mgmt.services.LocalConfigService;
import com.me2me.mgmt.syslog.SystemControllerLog;

@Controller
@RequestMapping("/tag")
public class AppTagController {

	private static final Logger logger = LoggerFactory.getLogger(AppTagController.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private LiveService liveService;
	@Autowired
	private LocalConfigService config;
	
	@RequestMapping(value = "/query")
	public ModelAndView tagQuery(AppTagQueryDTO dto){
		
		this.getTagInfo(dto);
		
		
		
		ModelAndView view = new ModelAndView("tag/tagList");
		view.addObject("dataObj", dto);
		//查体系标签
		String sql = "select * from topic_tag where is_sys=1";
		view.addObject("sysTagList",contentService.queryEvery(sql));
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
		if(dto.getPid()!=null){
			countSql.append(" and t.pid=").append(dto.getPid());
		}
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
		querySql.append("max(t.order_num) as ordernum,");
		querySql.append("max(t.is_sys) as issys,max(t.status) as status");
		querySql.append(" from topic_tag t left join topic_tag_detail d");
		querySql.append(" on t.id=d.tag_id and d.status=0");
		querySql.append(" where 1=1");
		if(dto.getPid()!=null){
			querySql.append(" and t.pid=").append(dto.getPid());
		}
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
				item.setOrderNum((Integer)m.get("ordernum"));
				item.setTopicCount((Long)m.get("kcount"));
				dto.getResult().add(item);
			}
		}
	}
	
	@RequestMapping(value="/f/{id}")
	public ModelAndView getTag(@PathVariable long id){
		TopicTag topicTag = liveService.getTopicTagById(id);
		
		
		ModelAndView view = new ModelAndView("tag/tagEdit");
		view.addObject("dataObj",topicTag);
		//查体系标签
		String sql = "select * from topic_tag where is_sys=1";
		view.addObject("sysTagList",contentService.queryEvery(sql));
		
		view.addObject("userHobbyList",contentService.queryEvery("select * from dictionary where tid=3"));
		
		return view;
	}
	
	@RequestMapping(value="/updateTag")
	@SystemControllerLog(description = "更新标签")
	public ModelAndView updateTag(TopicTag topicTag){
		ModelAndView view = null;
		TopicTag dbTag = liveService.getTopicTagByTag(topicTag.getTag());
		if(null != dbTag && dbTag.getId().longValue() != topicTag.getId().longValue()){
			view = new ModelAndView("tag/tagEdit");
			view.addObject("dataObj",topicTag);
			view.addObject("errMsg","标签名已经存在");
			return view;
		}
		liveService.updateTopicTag(topicTag);
		
		view = new ModelAndView("redirect:/tag/query");
		return view;
	}
	@RequestMapping(value="/tagNew")
	@SystemControllerLog(description = "更新标签")
	public ModelAndView tagNew(TopicTag topicTag){
		//查体系标签
		String sql = "select * from topic_tag where is_sys=1";
		
		ModelAndView view = new ModelAndView("/tag/tagNew");
		view.addObject("sysTagList",contentService.queryEvery(sql));
		view.addObject("userHobbyList",contentService.queryEvery("select * from dictionary where tid=3"));
		return view;
	}
	
	@RequestMapping(value="/createTag")
	@SystemControllerLog(description = "新建标签")
	public ModelAndView createTag(TopicTag topicTag){
		ModelAndView view = null;
		TopicTag dbTag = liveService.getTopicTagByTag(topicTag.getTag());
		if(null != dbTag){
			view = new ModelAndView("tag/tagNew");
			view.addObject("dataObj",topicTag);
			view.addObject("errMsg","标签名已经存在");
			return view;
		}
		liveService.createTopicTag(topicTag);
		view = new ModelAndView("redirect:/tag/query");
		return view;
	}
	
	@RequestMapping(value="/topicList/query")
	public ModelAndView getTagTopicListQuery(AppTagTopicListQueryDTO dto){
		this.getTagTopicListInfo(dto);
		
		ModelAndView view = new ModelAndView("tag/tagTopicList");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value="/topicList/page")
	@ResponseBody
	public String getTagTopicListpage(AppTagTopicListQueryDTO dto){
		this.getTagTopicListInfo(dto);
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	private void getTagTopicListInfo(AppTagTopicListQueryDTO dto){
		int pageSize = dto.getPageSize();
		int start = (dto.getPage()-1)*dto.getPageSize();
		
		StringBuilder countSql = new StringBuilder();
		countSql.append("select count(1) as cc from topic_tag_detail d,topic t");
		countSql.append(" where d.topic_id=t.id and d.status=0 and d.tag_id=").append(dto.getTagId());
		
		StringBuilder querySql = new StringBuilder();
		querySql.append("select t.*,d.id as did from topic_tag_detail d,topic t");
		querySql.append(" where d.topic_id=t.id and d.status=0");
		querySql.append(" and d.tag_id=").append(dto.getTagId());
		querySql.append(" order by d.create_time desc,id");
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
			List<Long> tidList = new ArrayList<Long>();
			for(Map<String, Object> m : list){
				tidList.add((Long)m.get("id"));
			}
			Map<String, String> topicTagMap = new HashMap<String, String>();
			if(tidList.size() > 0){
				StringBuilder tagsSql = new StringBuilder();
				tagsSql.append("select * from topic_tag_detail d where d.status=0");
				tagsSql.append(" and d.topic_id in (");
		    	for(int i=0;i<tidList.size();i++){
		    		if(i>0){
		    			tagsSql.append(",");
		    		}
		    		tagsSql.append(tidList.get(i).longValue());
		    	}
		    	tagsSql.append(") order by topic_id asc,id asc");
		    	
		    	List<Map<String, Object>> topicTagList = null;
		    	try{
		    		topicTagList = contentService.queryEvery(tagsSql.toString());
				}catch(Exception e){
					logger.error("查询出错", e);
				}
		    	
		    	if(null != topicTagList && topicTagList.size() > 0){
	            	long tid = 0;
	            	String tags = null;
	            	Long topicId = null;
	            	for(Map<String, Object> ttd : topicTagList){
	            		topicId = (Long)ttd.get("topic_id");
	            		if(topicId.longValue() != tid){
	            			//先插入上一次
	            			if(tid > 0 && !StringUtils.isEmpty(tags)){
	            				topicTagMap.put(String.valueOf(tid), tags);
	            			}
	            			//再初始化新的
	            			tid = topicId.longValue();
	            			tags = null;
	            		}
	            		if(tags != null){
	            			tags = tags + ";" + (String)ttd.get("tag");
	            		}else{
	            			tags = (String)ttd.get("tag");
	            		}
	            	}
	            	if(tid > 0 && !StringUtils.isEmpty(tags)){
	            		topicTagMap.put(String.valueOf(tid), tags);
	            	}
	            }
		    	
			}
			AppTagTopicListQueryDTO.Item item = null;
			for(Map<String, Object> m : list){
				item = new AppTagTopicListQueryDTO.Item();
				item.setTagTopicId((Long)m.get("did"));
				item.setTopicId((Long)m.get("id"));
				item.setCreateTime((Date)m.get("create_time"));
				item.setH5url(config.getWebappUrl() + item.getTopicId());
				long lut = (Long)m.get("long_time");
				item.setLastUpdateTime(new Date(lut));
				item.setTitle((String)m.get("title"));
				if(null != topicTagMap.get(String.valueOf(item.getTopicId()))){
					item.setTags(topicTagMap.get(String.valueOf(item.getTopicId())));
	            }else{
	            	item.setTags("");
	            }
				dto.getResult().add(item);
			}
		}
	}
	
	@RequestMapping(value="/deltopic/{tagId}/{tagTopicId}")
	public ModelAndView delTagTopic(@PathVariable long tagId, @PathVariable long tagTopicId){
		liveService.delTagTopic(tagTopicId);
		
		ModelAndView view = new ModelAndView("redirect:/tag/topicList/query?tagId="+tagId);
		return view;
	}
	
	@RequestMapping(value="/addTagTopic")
	@ResponseBody
	public String addTagTopic(String topidIds, String tagId){
		List<Long> topicIdList = new ArrayList<Long>();
		if(StringUtils.isNotBlank(topidIds)){
			String[] tmp = topidIds.split(",");
			if(tmp.length > 0){
				for(String t : tmp){
					if(StringUtils.isNotBlank(t)){
						topicIdList.add(Long.valueOf(t.trim()));
					}
				}
			}
		}
		Long tid = Long.valueOf(tagId.trim());
		
		liveService.addTagTopics(tid, topicIdList);
		
		return "0";
	}
}
