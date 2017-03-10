package com.me2me.mgmt.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.content.dto.KingTopicDto;
import com.me2me.content.dto.ShowKingTopicDto;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.request.ChannelRegisterDetailDTO;
import com.me2me.mgmt.request.ChannelRegisterQueryDTO;
import com.me2me.mgmt.request.DailyActiveDTO;
import com.me2me.mgmt.request.KingDayQueryDTO;
import com.me2me.mgmt.request.KingStatDTO;
import com.me2me.mgmt.request.PromoterDTO;
import com.me2me.monitor.dto.LoadReportDto;
import com.me2me.monitor.dto.MonitorReportDto;
import com.me2me.monitor.service.MonitorService;
import com.me2me.user.dto.PromoterDto;
import com.me2me.user.service.UserService;

@Controller
@RequestMapping("/stat")
public class StatController {

	private static final Logger logger = LoggerFactory.getLogger(StatController.class);
	
	@Autowired
	private MonitorService monitorService;
	@Autowired
    private UserService userService;
	@Autowired
    private ContentService contentService;

	private static Map<String, String> channelSelectMap = new HashMap<String, String>();

	@PostConstruct
	public void init() {
		channelSelectMap.put("0", "ALL");
		channelSelectMap.put("118", "baidu");
		channelSelectMap.put("119", "91zhushaou");
		channelSelectMap.put("120", "360");
		channelSelectMap.put("121", "jifeng");
		channelSelectMap.put("122", "anzhi");
		channelSelectMap.put("123", "xiaomi");
		channelSelectMap.put("124", "uc");
		channelSelectMap.put("125", "yyb");
		channelSelectMap.put("126", "meizu");
		channelSelectMap.put("127", "huawei");
		channelSelectMap.put("128", "lianxiang");
		channelSelectMap.put("129", "sogo");
		channelSelectMap.put("130", "mumayi");
		channelSelectMap.put("131", "liqu");
		channelSelectMap.put("132", "jinli");
		channelSelectMap.put("133", "yybei");
		channelSelectMap.put("134", "kuchuan");
		channelSelectMap.put("135", "smartisan");
		channelSelectMap.put("136", "youyi");
		channelSelectMap.put("137", "maopao");
		channelSelectMap.put("138", "wandoujia");
		channelSelectMap.put("139", "yyh");
		channelSelectMap.put("140", "tianyi");
		channelSelectMap.put("141", "nduo");
		channelSelectMap.put("142", "shoujizg");
		channelSelectMap.put("143", "nearme");
		channelSelectMap.put("144", "apple");
	}

	@RequestMapping(value = "/dailyActive/query")
	public ModelAndView dailyActiveQuery(DailyActiveDTO daDTO) {
		if (null == daDTO.getTxtStartDate()) {
			daDTO.setTxtStartDate(DateUtil.date2string(new Date(), "yyyy-MM-dd"));
		}
		if (null == daDTO.getTxtEndDate()) {
			daDTO.setTxtEndDate(DateUtil.date2string(DateUtil.addDay(new Date(), 1), "yyyy-MM-dd"));
		}
		
		daDTO.setDdlClassName(channelSelectMap.get(String.valueOf(daDTO.getDdlClass())));
		// 启动
		daDTO.setBoot(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 0, 0));
		//登录
		daDTO.setLogin(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 1));
		//注册
		daDTO.setReg(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 2));
		//浏览
		daDTO.setView(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 3));
		//发布内容
		daDTO.setPubCon(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 4));
		//发布直播
		daDTO.setPubLive(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 5));
		//点赞
		daDTO.setZan(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 6));
		//取消点赞
		daDTO.setCzan(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 7));
		//评论
		daDTO.setCommon(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 8));
		//感受标签
		daDTO.setTags(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 9));
		//关注
		daDTO.setAttention(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 10));
		//取消关注
		daDTO.setCattention(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 11));
		//转发内容
		daDTO.setForwarding(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 12));
		//阅读热门
		daDTO.setHot(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 13));
		//阅读最新
		daDTO.setAnew(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 14));
		//关注文章
		daDTO.setAarticle(this.report(daDTO.getDdlClass(), daDTO.getTxtStartDate(), daDTO.getTxtEndDate(), 1, 15));
		
		ModelAndView view = new ModelAndView("stat/dailyActive");
		view.addObject("dataObj", daDTO);
		return view;
	}

	@SuppressWarnings("rawtypes")
	private long report(int channel, String startDate, String endDate, int type, int actionType) {
		MonitorReportDto monitorReportDto = new MonitorReportDto();
		monitorReportDto.setChannel(channel);
		monitorReportDto.setStartDate(startDate);
		monitorReportDto.setEndDate(endDate);
		monitorReportDto.setType(type);
		monitorReportDto.setActionType(actionType);
		
		Response resp = null;
		try{
			if (monitorReportDto.getType() == 0) {
				resp = monitorService.loadBootReport(monitorReportDto);
			} else if (monitorReportDto.getType() == 1) {
				resp = monitorService.loadActionReport(monitorReportDto);
			} else if (monitorReportDto.getType() == 2) {
				resp = monitorService.loadActivityReport(monitorReportDto);
			} else {
				resp = Response.failure("参数非法...");
			}
		}catch(Exception e){
			logger.error("查询失败", e);
		}
		if (null != resp && resp.getCode() == 200) {
			LoadReportDto dto = (LoadReportDto) resp.getData();
			if (null != dto) {
				return dto.getCounter();
			}
		}
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/promoter/query")
	public ModelAndView promoterQuery(PromoterDTO pDTO) {
		Response resp = userService.getPromoter(null,pDTO.getTxtStartDate(),pDTO.getTxtEndDate());
		if(null != resp && resp.getCode() == 200){
			pDTO.setPromoterDto((PromoterDto)resp.getData());
		}
		ModelAndView view = new ModelAndView("stat/promoter");
		view.addObject("dataObj", pDTO);
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/king/query")
	public ModelAndView kingQuery(KingStatDTO kDTO) {
		if (null == kDTO.getTxtStartDate()) {
			kDTO.setTxtStartDate(DateUtil.date2string(new Date(), "yyyy-MM-dd"));
		}
		if (null == kDTO.getTxtEndDate()) {
			kDTO.setTxtEndDate(DateUtil.date2string(DateUtil.addDay(new Date(), 1), "yyyy-MM-dd"));
		}
		
		KingTopicDto kingTopic = new KingTopicDto();
		if(StringUtils.isNotBlank(kDTO.getTxtStartDate())){
			kingTopic.setStartDate(kDTO.getTxtStartDate());
		}
		if(StringUtils.isNotBlank(kDTO.getTxtEndDate())){
			kingTopic.setEndDate(kDTO.getTxtEndDate());
		}
		Response resp = contentService.kingTopic(kingTopic);
		if(null != resp && resp.getCode() == 200){
			kDTO.setKingDto((ShowKingTopicDto)resp.getData());
		}
		
		ModelAndView view = new ModelAndView("stat/king");
		view.addObject("dataObj", kDTO);
		return view;
	}
	
	@RequestMapping(value = "/channelRegister/query")
	public ModelAndView channelRegisterQuery(ChannelRegisterQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(now, "yyyy-MM-dd")+" 00:00:00");
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd")+" 23:59:59");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("select c.code,m.cu,m.ck from t_channel c LEFT JOIN (");
		sb.append("select u.channel,count(t.id) as ck,count(DISTINCT u.uid) as cu");
		sb.append(" from user_profile u  LEFT JOIN topic t on u.uid=t.uid");
		sb.append(" and t.create_time>='").append(dto.getStartTime());
		sb.append("' and t.create_time<='").append(dto.getEndTime());
		sb.append("' where u.create_time>='").append(dto.getStartTime());
		sb.append("' and u.create_time<='").append(dto.getEndTime());
		sb.append("' and u.channel is not NULL and u.channel<>''");
		sb.append(" group by u.channel ) m on c.code=m.channel");
		if(null != dto.getChannelCode() && !"".equals(dto.getChannelCode())){
			sb.append(" where c.code like '%").append(dto.getChannelCode()).append("%'");
		}
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		try{
			list = contentService.queryEvery(sb.toString());
		}catch(Exception e){
			logger.error("查询出错", e);
		}
		if(null != list && list.size() > 0){
			ChannelRegisterQueryDTO.Item item = null;
			for(Map<String, Object> m : list){
				item = new ChannelRegisterQueryDTO.Item();
				item.setChannelCode((String)m.get("code"));
				if(null != m.get("cu")){
					item.setRegisterCount((Long)m.get("cu"));
				}else{
					item.setRegisterCount(0);
				}
				if(null != m.get("ck")){
					item.setKingdomCount((Long)m.get("ck"));
				}else{
					item.setKingdomCount(0);
				}
				
				dto.getResult().add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("stat/channelRegister");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value = "/channelRegister/detail")
	public ModelAndView channelRegisterDetail(ChannelRegisterDetailDTO dto){
		dto.setPage(1);
		dto.setPageSize(10);
		int page = dto.getPage();
		if(page <=0){
			page = 1;
		}
		int pageSize = dto.getPageSize();
		if(pageSize <= 0){
			pageSize = 10;
		}
		int start = (page-1)*pageSize;
		
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select u2.uid,u2.nick_name,u2.mobile,u2.gender,u2.create_time,m.ck");
		pageSql.append(" from user_profile u2,(select u.uid,count(t.id) as ck");
		pageSql.append(" from user_profile u LEFT JOIN topic t on u.uid=t.uid");
		pageSql.append(" and t.create_time>='").append(dto.getStartTime());
		pageSql.append("' and t.create_time<='").append(dto.getEndTime());
		pageSql.append("' where u.channel='").append(dto.getChannelCode());
		pageSql.append("' and u.create_time>='").append(dto.getStartTime());
		pageSql.append("' and u.create_time<='").append(dto.getEndTime());
		pageSql.append("' group by u.uid) m where u2.uid=m.uid");
		pageSql.append(" order by u2.create_time limit ").append(start);
		pageSql.append(",").append(pageSize);
		
		StringBuilder pageCountSql = new StringBuilder();
		pageCountSql.append("select count(1) as cc from user_profile u");
		pageCountSql.append(" where u.channel='").append(dto.getChannelCode());
		pageCountSql.append("' and u.create_time>='").append(dto.getStartTime());
		pageCountSql.append("' and u.create_time<='").append(dto.getEndTime());
		pageCountSql.append("'");
		
		//汇总信息
		StringBuilder countUserSql = new StringBuilder();
		countUserSql.append("select count(1) as ct,COUNT(if(u.gender<>1,TRUE,NULL)) as cw,");
		countUserSql.append("COUNT(if(u.gender=1,TRUE,NULL)) as cm from user_profile u");
		countUserSql.append(" where u.channel='").append(dto.getChannelCode());
		countUserSql.append("' and u.create_time>='").append(dto.getStartTime());
		countUserSql.append("' and u.create_time<='").append(dto.getEndTime());
		countUserSql.append("'");
		
		StringBuilder countKingdomSql = new StringBuilder();
		countKingdomSql.append("select count(1) as ck from topic t,user_profile u");
		countKingdomSql.append(" where t.uid=u.uid and u.channel='").append(dto.getChannelCode());
		countKingdomSql.append("' and u.create_time>='").append(dto.getStartTime());
		countKingdomSql.append("' and u.create_time<='").append(dto.getEndTime());
		countKingdomSql.append("' and t.create_time>='").append(dto.getStartTime());
		countKingdomSql.append("' and t.create_time<='").append(dto.getEndTime());
		countKingdomSql.append("'");
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> countList = null;
		List<Map<String, Object>> countUserList = null;
		List<Map<String, Object>> countKingdomList = null;
		try{
			list = contentService.queryEvery(pageSql.toString());
			countList = contentService.queryEvery(pageCountSql.toString());
			countUserList = contentService.queryEvery(countUserSql.toString());
			countKingdomList = contentService.queryEvery(countKingdomSql.toString());
		}catch(Exception e){
			logger.error("查询出错", e);
			ModelAndView view = new ModelAndView("stat/channelRegisterDetail");
			view.addObject("errMsg", "查询出错");
			view.addObject("dataObj", dto);
			return view;
		}
		if(null != countList && countList.size() > 0){
			Map<String, Object> count = countList.get(0);
			long totalCount = (Long)count.get("cc");
			int totalPage = totalCount%pageSize==0?(int)totalCount/pageSize:((int)totalCount/pageSize)+1;
			dto.setTotalCount((int)totalCount);
			dto.setTotalPage(totalPage);
		}
		if(null != list && list.size() > 0){
			ChannelRegisterDetailDTO.Item item = null;
			for(Map<String, Object> m : list){
				item = new ChannelRegisterDetailDTO.Item();
				item.setKingdomCount((Long)m.get("ck"));
				item.setMobile((String)m.get("mobile"));
				item.setNickName((String)m.get("nick_name"));
				item.setRegisterTime((Date)m.get("create_time"));
				item.setSex((Integer)m.get("gender"));
				item.setUid((Long)m.get("uid"));
				dto.getResult().add(item);
			}
		}
		if(null != countUserList && countUserList.size() > 0){
			Map<String, Object> countUser = countUserList.get(0);
			dto.setTotalUserCount((Long)countUser.get("ct"));
			dto.setManCount((Long)countUser.get("cm"));
			dto.setWomanCount((Long)countUser.get("cw"));
		}
		if(null != countKingdomList && countKingdomList.size() > 0){
			Map<String, Object> countKingdom = countKingdomList.get(0);
			dto.setTotalKingdomCount((Long)countKingdom.get("ck"));
		}
		
		ModelAndView view = new ModelAndView("stat/channelRegisterDetail");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value="/channelRegister/detail/Page")
	@ResponseBody
	public String channelRegisterDetailPage(ChannelRegisterDetailDTO dto){
		int page = dto.getPage();
		if(page <=0){
			page = 1;
		}
		int pageSize = dto.getPageSize();
		if(pageSize <= 0){
			pageSize = 10;
		}
		int start = (page-1)*pageSize;
		
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select u2.uid,u2.nick_name,u2.mobile,u2.gender,u2.create_time,m.ck");
		pageSql.append(" from user_profile u2,(select u.uid,count(t.id) as ck");
		pageSql.append(" from user_profile u LEFT JOIN topic t on u.uid=t.uid");
		pageSql.append(" and t.create_time>='").append(dto.getStartTime());
		pageSql.append("' and t.create_time<='").append(dto.getEndTime());
		pageSql.append("' where u.channel='").append(dto.getChannelCode());
		pageSql.append("' and u.create_time>='").append(dto.getStartTime());
		pageSql.append("' and u.create_time<='").append(dto.getEndTime());
		pageSql.append("' group by u.uid) m where u2.uid=m.uid");
		pageSql.append(" order by u2.create_time limit ").append(start);
		pageSql.append(",").append(pageSize);

		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		try{
			list = contentService.queryEvery(pageSql.toString());
		}catch(Exception e){
			logger.error("查询出错", e);
		}
		if(null != list && list.size() > 0){
			ChannelRegisterDetailDTO.Item item = null;
			for(Map<String, Object> m : list){
				item = new ChannelRegisterDetailDTO.Item();
				item.setKingdomCount((Long)m.get("ck"));
				item.setMobile((String)m.get("mobile"));
				item.setNickName((String)m.get("nick_name"));
				item.setRegisterTime((Date)m.get("create_time"));
				item.setSex((Integer)m.get("gender"));
				item.setUid((Long)m.get("uid"));
				dto.getResult().add(item);
			}
		}
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	@RequestMapping(value = "/channelRegister/detail")
	public ModelAndView kingDayQuery(KingDayQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(DateUtil.addDay(now, -9), "yyyy-MM-dd"));
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		
		StringBuilder sb = new StringBuilder();
		
		
		
		ModelAndView view = new ModelAndView("stat/kingDay");
		view.addObject("dataObj", dto);
		return view;
	}
}
