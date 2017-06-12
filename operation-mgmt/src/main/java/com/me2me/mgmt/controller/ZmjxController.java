package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.model.AcommonList;
import com.me2me.activity.service.ActivityService;
import com.me2me.cache.CacheConstant;
import com.me2me.cache.service.CacheService;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.request.ActivitySpecialKingdomDTO;
import com.me2me.mgmt.request.KingdomDTO;
import com.me2me.mgmt.request.ZmjxConfingDTO;
import com.me2me.mgmt.services.LocalConfigService;

/**
 * 最美家乡活动Controller
 * @author pc340
 *
 */
@Controller
@RequestMapping("/zmjx")
public class ZmjxController {

	private static final Logger logger = LoggerFactory.getLogger(ZmjxController.class);

	@Autowired
    private ActivityService activityService;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private LocalConfigService localConfigService;
	
	/**
	 * 查看配置的王国
	 * @return
	 */
	@RequestMapping(value="/config/kingdomQuery")
	public ModelAndView kingdomQuery(ActivitySpecialKingdomDTO dto){
		Set<String> kingdoms = cacheService.smembers(CacheConstant.ACTIVITY_CORECIRCLE_SPECIAL_TOPIC_LIST_KEY);
		
		List<Long> tidList = new ArrayList<Long>();
		if(null != kingdoms && kingdoms.size() > 0){
			for(String tid : kingdoms){
				tidList.add(Long.valueOf(tid));
			}
		}
		if(tidList.size() > 0){
			StringBuilder sb = new StringBuilder();
			sb.append("select * from topic t where t.id in (");
			for(int i=0;i<tidList.size();i++){
				if(i>0){
					sb.append(",");
				}
				sb.append(tidList.get(i));
			}
			sb.append(")");
			
			String sql = "select * from a_common_list where type=1 and activity_id=3 and target_id in (";
			for(int i=0;i<tidList.size();i++){
				if(i>0){
					sql = sql + ",";
				}
				sql = sql + tidList.get(i).toString();
			}
			sql = sql + ")";
			List<Map<String, Object>> kList = contentService.queryEvery(sql);
			Map<String, Map<String, Object>> alistMap = new HashMap<String, Map<String, Object>>();
			if(null != kList && kList.size() > 0){
				for(Map<String, Object> k : kList){
					alistMap.put(String.valueOf(k.get("target_id")), k);
				}
			}
			
			List<Map<String, Object>> list = contentService.queryEvery(sb.toString());
			if(null != list && list.size() > 0){
				ActivitySpecialKingdomDTO.Item item = null;
				Map<String, Object> alist = null;
				for(Map<String, Object> m : list){
					item = new ActivitySpecialKingdomDTO.Item();
					item.setTopicId((Long)m.get("id"));
					item.setH5url(localConfigService.getWebappUrl() + item.getTopicId());
					item.setTitle((String)m.get("title"));
					alist = alistMap.get(String.valueOf(item.getTopicId()));
					if(null != alist){
						item.setAlias((String)alist.get("alias"));
						item.setScore((Long)alist.get("score"));
					}
					dto.getResult().add(item);
				}
			}
		}
		
		ModelAndView view = new ModelAndView("zmjx/kingdomList");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value="/config/kingdomAdd")
	public ModelAndView addKingdom(KingdomDTO dto){
		if(dto.getTopicId() > 0){
			String value = String.valueOf(dto.getTopicId());
			Set<String> kingdoms = cacheService.smembers(CacheConstant.ACTIVITY_CORECIRCLE_SPECIAL_TOPIC_LIST_KEY);
			boolean needAdd = true;
			if(null != kingdoms && kingdoms.contains(value)){
				needAdd = false;
			}
			if(needAdd){
				cacheService.sadd(CacheConstant.ACTIVITY_CORECIRCLE_SPECIAL_TOPIC_LIST_KEY, value);
				//增加王国列表
				AcommonList alist = new AcommonList();
				alist.setActivityId(3l);
				alist.setAlias(dto.getAlias());
				alist.setScore(0l);
				alist.setTargetId(dto.getTopicId());
				alist.setType(1);
				alist.setUpdateTime(new Date());
				activityService.saveAcommonList(alist);
			}else{
				ModelAndView view = new ModelAndView("zmjx/kingdomNew");
				view.addObject("errMsg", "不能重复增加");
				return view;
			}
		}
		
		ModelAndView view = new ModelAndView("redirect:/zmjx/config/kingdomQuery");
		return view;
	}
	
	@RequestMapping(value="/config/kingdomDel")
	public ModelAndView deleteKingdom(KingdomDTO dto){
		if(dto.getTopicId() > 0){
			String value = String.valueOf(dto.getTopicId());
			Set<String> kingdoms = cacheService.smembers(CacheConstant.ACTIVITY_CORECIRCLE_SPECIAL_TOPIC_LIST_KEY);
			boolean needDel = false;
			if(null != kingdoms && kingdoms.contains(value)){
				needDel = true;
			}
			if(needDel){
				cacheService.srem(CacheConstant.ACTIVITY_CORECIRCLE_SPECIAL_TOPIC_LIST_KEY, value);
				AcommonList alist = activityService.getAcommonList(dto.getTopicId(), 3, 1);
				if(null != alist){
					activityService.deleteAcommonListById(alist.getId());
				}
			}
		}
		
		ModelAndView view = new ModelAndView("redirect:/zmjx/config/kingdomQuery");
		return view;
	}
	
	/**
	 * 将所有的配置这里呈现出来，然后进行各种修改
	 * @return
	 */
	@RequestMapping(value="/config/query")
	public ModelAndView configQuery(){
		ZmjxConfingDTO result = new ZmjxConfingDTO();
		
		//开关配置
		String specialSwitch = cacheService.get(CacheConstant.ACTIVITY_SPECIAL_TOPIC_HANDLE_KEY);
		if(StringUtils.isNotBlank(specialSwitch) && "on".equals(specialSwitch)){
			result.setActivitySwitch(1);
		}
		//总限额
		String totalLimitStr = cacheService.get(CacheConstant.SPECIAL_TOPIC_HOT_LIMIT_TOTAL);
		if(StringUtils.isNotBlank(totalLimitStr)){
			result.setTotalLimit(Long.valueOf(totalLimitStr));
		}
		//每日限额
		String dayLimitStr = cacheService.get(CacheConstant.SPECIAL_TOPIC_HOT_LIMIT_DAY);
		if(StringUtils.isNotBlank(dayLimitStr)){
			result.setDayLimit(Long.valueOf(dayLimitStr));
		}
		
		//分值配置
		//视频
		String key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + CacheConstant.SPECIAL_TOPIC_TYPE_VIDEO;
		ZmjxConfingDTO.LimitItem item = new ZmjxConfingDTO.LimitItem();
		item.setKey(key);
		item.setTitle("视频");
		this.genLimitItem(item, key);
		result.getLimitList().add(item);
		//音频
		key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + CacheConstant.SPECIAL_TOPIC_TYPE_AUDIO;
		item = new ZmjxConfingDTO.LimitItem();
		item.setKey(key);
		item.setTitle("音频");
		this.genLimitItem(item, key);
		result.getLimitList().add(item);
		//图
		key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + CacheConstant.SPECIAL_TOPIC_TYPE_IMAGE;
		item = new ZmjxConfingDTO.LimitItem();
		item.setKey(key);
		item.setTitle("图");
		this.genLimitItem(item, key);
		result.getLimitList().add(item);
		//文
		key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + CacheConstant.SPECIAL_TOPIC_TYPE_TEXT;
		item = new ZmjxConfingDTO.LimitItem();
		item.setKey(key);
		item.setTitle("文");
		this.genLimitItem(item, key);
		result.getLimitList().add(item);
		//@
		key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + CacheConstant.SPECIAL_TOPIC_TYPE_AT;
		item = new ZmjxConfingDTO.LimitItem();
		item.setKey(key);
		item.setTitle("@操作");
		this.genLimitItem(item, key);
		result.getLimitList().add(item);
		//分享操作
		key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + CacheConstant.SPECIAL_TOPIC_TYPE_SHARE;
		item = new ZmjxConfingDTO.LimitItem();
		item.setKey(key);
		item.setTitle("分享");
		this.genLimitItem(item, key);
		result.getLimitList().add(item);
		//王国对外分享操作
		key = CacheConstant.SPECIAL_TOPIC_KEY_PRE + CacheConstant.KEY_SEPARATOR_UNDERLINE + CacheConstant.SPECIAL_TOPIC_TYPE_OUT_SHARE;
		item = new ZmjxConfingDTO.LimitItem();
		item.setKey(key);
		item.setTitle("向其他王国分享");
		this.genLimitItem(item, key);
		result.getLimitList().add(item);
		
		ModelAndView view = new ModelAndView("zmjx/config");
		view.addObject("dataObj", result);
		return view;
	}
	
	private void genLimitItem(ZmjxConfingDTO.LimitItem item, String key){
		String limit = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_LIMIT);
		if(StringUtils.isNotBlank(limit)){
			item.setLimit(Integer.valueOf(limit));
		}
		String personAdd = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_PERSON+CacheConstant.SPECIAL_TOPIC_ACTION_ADD);
		if(StringUtils.isNotBlank(personAdd)){
			item.setPersonScoreAdd(Integer.valueOf(personAdd));
		}
		String personDel = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_PERSON+CacheConstant.SPECIAL_TOPIC_ACTION_DEL);
		if(StringUtils.isNotBlank(personDel)){
			item.setPersonScoreDel(Integer.valueOf(personDel));
		}
		String kingdomAdd = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_KINGDOM+CacheConstant.SPECIAL_TOPIC_ACTION_ADD);
		if(StringUtils.isNotBlank(kingdomAdd)){
			item.setAreaScoreAdd(Integer.valueOf(kingdomAdd));
		}
		String kingdomDel = cacheService.hGet(key, CacheConstant.SPECIAL_TOPIC_KINGDOM+CacheConstant.SPECIAL_TOPIC_ACTION_DEL);
		if(StringUtils.isNotBlank(kingdomDel)){
			item.setAreaScoreDel(Integer.valueOf(kingdomDel));
		}
	}
	
	@RequestMapping(value="/config/switch/{action}")
	public ModelAndView switchOpt(@PathVariable int action){
		if(action == 1){//开
			cacheService.set(CacheConstant.ACTIVITY_SPECIAL_TOPIC_HANDLE_KEY, "on");
		}else if(action == 2){//关
			cacheService.set(CacheConstant.ACTIVITY_SPECIAL_TOPIC_HANDLE_KEY, "off");
		}
		
		ModelAndView view = new ModelAndView("redirect:/zmjx/config/query");
		return view;
	}
	
	@RequestMapping(value = "/config/modify")
	@ResponseBody
	public String configModify(@RequestParam("key") String key, @RequestParam("personScoreAdd") int personScoreAdd, 
			@RequestParam("personScoreDel") int personScoreDel, @RequestParam("areaScoreAdd") int areaScoreAdd, 
			@RequestParam("areaScoreDel") int areaScoreDel, @RequestParam("limit") int limit){
		
		cacheService.hSet(key, CacheConstant.SPECIAL_TOPIC_PERSON+CacheConstant.SPECIAL_TOPIC_ACTION_ADD, String.valueOf(personScoreAdd));
		cacheService.hSet(key, CacheConstant.SPECIAL_TOPIC_PERSON+CacheConstant.SPECIAL_TOPIC_ACTION_DEL, String.valueOf(personScoreDel));
		cacheService.hSet(key, CacheConstant.SPECIAL_TOPIC_KINGDOM+CacheConstant.SPECIAL_TOPIC_ACTION_ADD, String.valueOf(areaScoreAdd));
		cacheService.hSet(key, CacheConstant.SPECIAL_TOPIC_KINGDOM+CacheConstant.SPECIAL_TOPIC_ACTION_DEL, String.valueOf(areaScoreDel));
		cacheService.hSet(key, CacheConstant.SPECIAL_TOPIC_LIMIT, String.valueOf(limit));
		
		return "0";
	}
	
	@RequestMapping(value = "/kingdom/hot/modify")
	@ResponseBody
	public String kingdomHotModify(@RequestParam("topicId") long topicId, @RequestParam("score") long score){
		AcommonList alist = activityService.getAcommonList(topicId, 3, 1);
		if(null != alist){
			alist.setScore(score);
			activityService.updateAcommonList(alist);
		}
		
		return "0";
	}
	
	@RequestMapping(value = "/config/modify2")
	@ResponseBody
	public String kingdomHotModify2(@RequestParam("key") String key, @RequestParam("value") String value){
		cacheService.set(key, value);
		
		return "0";
	}
}
