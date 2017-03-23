package com.me2me.mgmt.controller;

import java.util.ArrayList;
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
import com.me2me.mgmt.request.IosWapxQueryDTO;
import com.me2me.mgmt.request.KingDayQueryDTO;
import com.me2me.mgmt.request.KingStatDTO;
import com.me2me.mgmt.request.KingdomCreateDetailQueryDTO;
import com.me2me.mgmt.request.PromoterDTO;
import com.me2me.mgmt.request.UserRegisterDetailQueryDTO;
import com.me2me.mgmt.request.UserRegisterQueryDTO;
import com.me2me.mgmt.services.LocalConfigService;
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
	@Autowired
	private LocalConfigService localConfigService;

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
	
	@RequestMapping(value = "/king/day/query")
	public ModelAndView kingDayQuery(KingDayQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(DateUtil.addDay(now, -9), "yyyy-MM-dd"));
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		
		String startTime = dto.getStartTime() + " 00:00:00";
		String endTime = dto.getEndTime() + " 23:59:59";
		
		StringBuilder sb = new StringBuilder();
		sb.append("select m1.dd as dayStr,m1.c as totalKingdomCount,");
		sb.append("m1.cc as newUserKingdomCount,m2.c2 as updateKingdomCount,");
		sb.append("m2.cc2 as totalKingFragmentCount,m3.c3 as totalUserFragmentCount");
		sb.append(" from (");
		sb.append("select DATE_FORMAT(t.create_time,'%Y-%m-%d') as dd,count(1) as c,");
		sb.append("count(if(u.create_time>='").append(startTime);
		sb.append("' and u.create_time<='").append(endTime);
		sb.append("',TRUE,NULL)) as cc");
		sb.append(" from topic t LEFT JOIN user_profile u on t.uid=u.uid");
		sb.append(" where t.create_time>='").append(startTime);
		sb.append("' and t.create_time<='").append(endTime);
		sb.append("' group by DATE_FORMAT(t.create_time,'%Y-%m-%d')");
		sb.append(") m1 LEFT JOIN (");
		sb.append("select DATE_FORMAT(f.create_time,'%Y-%m-%d') as dd2,count(DISTINCT f.topic_id) as c2, count(1) as cc2");
		sb.append(" from topic_fragment f");
		sb.append(" where f.create_time>='").append(startTime);
		sb.append("' and f.create_time<='").append(endTime);
		sb.append("' and f.type in (0,11,12,13,15,52,55)");
		sb.append(" group by DATE_FORMAT(f.create_time,'%Y-%m-%d')");
		sb.append(") m2 on m1.dd=m2.dd2 LEFT JOIN (");
		sb.append("select DATE_FORMAT(f2.create_time,'%Y-%m-%d') as dd3,count(1) as c3");
		sb.append(" from topic_fragment f2");
		sb.append(" where f2.create_time>='").append(startTime);
		sb.append("' and f2.create_time<='").append(endTime);
		sb.append("' and f2.type not in (0,11,12,13,15,52,55)");
		sb.append(" group by DATE_FORMAT(f2.create_time,'%Y-%m-%d')");
		sb.append(") m3 on m1.dd=m3.dd3");
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		try{
			list = contentService.queryEvery(sb.toString());
		}catch(Exception e){
			logger.error("查询出错", e);
		}
		if(null != list && list.size() > 0){
			KingDayQueryDTO.Item item = null;
			for(Map<String, Object> m : list){
				item = new KingDayQueryDTO.Item();
				item.setDayStr((String)m.get("dayStr"));
				if(null != m.get("newUserKingdomCount")){
					item.setNewUserKingdomCount((Long)m.get("newUserKingdomCount"));
				}
				if(null != m.get("totalKingdomCount")){
					item.setTotalKingdomCount((Long)m.get("totalKingdomCount"));
				}
				if(null != m.get("totalKingFragmentCount")){
					item.setTotalKingFragmentCount((Long)m.get("totalKingFragmentCount"));
				}
				if(null != m.get("totalUserFragmentCount")){
					item.setTotalUserFragmentCount((Long)m.get("totalUserFragmentCount"));
				}
				if(null != m.get("updateKingdomCount")){
					item.setUpdateKingdomCount((Long)m.get("updateKingdomCount"));
				}
				dto.getResult().add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("stat/kingDay");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value = "/userRegister/detail/query")
	public ModelAndView userRegisterDetailQuery(UserRegisterDetailQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		
		String startTime = dto.getStartTime() + " 00:00:00";
		String endTime = dto.getEndTime() + " 23:59:59";
		
		int pageSize = dto.getPageSize();
		int start = (dto.getPage()-1) * pageSize;
		
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select p.* from user_profile p");
		pageSql.append(" where p.create_time>='").append(startTime);
		pageSql.append("' and p.create_time<='").append(endTime);
		pageSql.append("'");
		if(null != dto.getChannelCode() && !"".equals(dto.getChannelCode())){
			pageSql.append(" and p.channel='").append(dto.getChannelCode()).append("'");
		}
		if(null != dto.getNickName() && !"".equals(dto.getNickName())){
			pageSql.append(" and p.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		pageSql.append(" order by p.id limit ").append(start).append(",").append(pageSize);

		StringBuilder pageCountSql = new StringBuilder();
		pageCountSql.append("select count(1) as cc from user_profile p");
		pageCountSql.append(" where p.create_time>='").append(startTime);
		pageCountSql.append("' and p.create_time<='").append(endTime);
		pageCountSql.append("'");
		if(null != dto.getChannelCode() && !"".equals(dto.getChannelCode())){
			pageCountSql.append(" and p.channel='").append(dto.getChannelCode()).append("'");
		}
		if(null != dto.getNickName() && !"".equals(dto.getNickName())){
			pageCountSql.append(" and p.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> countList = null;
		try{
			list = contentService.queryEvery(pageSql.toString());
			countList = contentService.queryEvery(pageCountSql.toString());
		}catch(Exception e){
			logger.error("查询出错", e);
			ModelAndView view = new ModelAndView("stat/userRegisterDetail");
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
			List<Long> uidList = new ArrayList<Long>();
			for(Map<String, Object> u : list){
				uidList.add((Long)u.get("uid"));
			}
			
			StringBuilder fansSql = new StringBuilder();
			fansSql.append("select f.target_uid,count(1) as cc");
			fansSql.append(" from user_follow f where f.target_uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					fansSql.append(",");
				}
				fansSql.append(uidList.get(i));
			}
			fansSql.append(") group by f.target_uid");
			
			StringBuilder kingdomsSql = new StringBuilder();
			kingdomsSql.append("select t.uid,count(1) as cc");
			kingdomsSql.append(" from topic t where t.uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					kingdomsSql.append(",");
				}
				kingdomsSql.append(uidList.get(i));
			}
			kingdomsSql.append(") group by t.uid");
			
			List<Map<String, Object>> fansList = null;
			List<Map<String, Object>> kingdomsList = null;
			try{
				fansList = contentService.queryEvery(fansSql.toString());
				kingdomsList = contentService.queryEvery(kingdomsSql.toString());
			}catch(Exception e){
				logger.error("查询出错", e);
				ModelAndView view = new ModelAndView("stat/userRegisterDetail");
				view.addObject("errMsg", "查询出错");
				view.addObject("dataObj", dto);
				return view;
			}
			Map<String, Long> fansMap = new HashMap<String, Long>();
			if(null != fansList && fansList.size() > 0){
				for(Map<String, Object> fans : fansList){
					fansMap.put(String.valueOf(fans.get("target_uid")), (Long)fans.get("cc"));
				}
			}
			Map<String, Long> kingdomsMap = new HashMap<String, Long>();
			if(null != kingdomsList && kingdomsList.size() > 0){
				for(Map<String, Object> kingdoms : kingdomsList){
					kingdomsMap.put(String.valueOf(kingdoms.get("uid")), (Long)kingdoms.get("cc"));
				}
			}
			
			UserRegisterDetailQueryDTO.Item item = null;
			Long fansCount = null;
			Long kingdomsCount = null;
			for(Map<String, Object> u : list){
				item = new UserRegisterDetailQueryDTO.Item();
				item.setChannelCode((String)u.get("channel"));
				item.setRegisterVersion((String)u.get("register_version"));
				fansCount = fansMap.get(String.valueOf(u.get("uid")));
				if(null != fansCount){
					item.setFansCount(fansCount.longValue());
				}else{
					item.setFansCount(0);
				}
				kingdomsCount = kingdomsMap.get(String.valueOf(u.get("uid")));
				if(null != kingdomsCount){
					item.setKingdomCount(kingdomsCount.longValue());
				}else{
					item.setKingdomCount(0);
				}
				item.setNickName((String)u.get("nick_name"));
				if(null != u.get("platform")){
					item.setPlatform((Integer)u.get("platform"));
				}else{
					item.setPlatform(0);
				}
				item.setRegisterDate((Date)u.get("create_time"));
				item.setUid((Long)u.get("uid"));
				item.setRegisterMode(this.getRegisterMode((String)u.get("third_part_bind")));
				
				dto.getResult().add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("stat/userRegisterDetail");
		view.addObject("dataObj", dto);
		return view;
	}
	
	private String getRegisterMode(String thirdPartBind){
		StringBuilder sb = new StringBuilder();
		if(null != thirdPartBind && !"".equals(thirdPartBind)){
			if(thirdPartBind.indexOf("mobile") != -1){
				sb.append(",手机");
			}
			if(thirdPartBind.indexOf("qq") != -1){
				sb.append(",QQ");
			}
			if(thirdPartBind.indexOf("weixin") != -1){
				sb.append(",微信");
			}
		}
		String result = sb.toString();
		if(result.length() > 0){
			result = result.substring(1);
		}
		return result;
	}
	
	@RequestMapping(value="/userRegister/detail/page")
	@ResponseBody
	public String userRegisterDetailPage(UserRegisterDetailQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		
		String startTime = dto.getStartTime() + " 00:00:00";
		String endTime = dto.getEndTime() + " 23:59:59";
		
		int pageSize = dto.getPageSize();
		int start = (dto.getPage()-1) * pageSize;
		
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select p.* from user_profile p");
		pageSql.append(" where p.create_time>='").append(startTime);
		pageSql.append("' and p.create_time<='").append(endTime);
		pageSql.append("'");
		if(null != dto.getChannelCode() && !"".equals(dto.getChannelCode())){
			pageSql.append(" and p.channel='").append(dto.getChannelCode()).append("'");
		}
		if(null != dto.getNickName() && !"".equals(dto.getNickName())){
			pageSql.append(" and p.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		pageSql.append(" order by p.id limit ").append(start).append(",").append(pageSize);

		StringBuilder pageCountSql = new StringBuilder();
		pageCountSql.append("select count(1) as cc from user_profile p");
		pageCountSql.append(" where p.create_time>='").append(startTime);
		pageCountSql.append("' and p.create_time<='").append(endTime);
		pageCountSql.append("'");
		if(null != dto.getChannelCode() && !"".equals(dto.getChannelCode())){
			pageCountSql.append(" and p.channel='").append(dto.getChannelCode()).append("'");
		}
		if(null != dto.getNickName() && !"".equals(dto.getNickName())){
			pageCountSql.append(" and p.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> countList = null;
		try{
			list = contentService.queryEvery(pageSql.toString());
			countList = contentService.queryEvery(pageCountSql.toString());
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
			List<Long> uidList = new ArrayList<Long>();
			for(Map<String, Object> u : list){
				uidList.add((Long)u.get("uid"));
			}
			
			StringBuilder fansSql = new StringBuilder();
			fansSql.append("select f.target_uid,count(1) as cc");
			fansSql.append(" from user_follow f where f.target_uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					fansSql.append(",");
				}
				fansSql.append(uidList.get(i));
			}
			fansSql.append(") group by f.target_uid");
			
			StringBuilder kingdomsSql = new StringBuilder();
			kingdomsSql.append("select t.uid,count(1) as cc");
			kingdomsSql.append(" from topic t where t.uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					kingdomsSql.append(",");
				}
				kingdomsSql.append(uidList.get(i));
			}
			kingdomsSql.append(") group by t.uid");
			
			List<Map<String, Object>> fansList = null;
			List<Map<String, Object>> kingdomsList = null;
			try{
				fansList = contentService.queryEvery(fansSql.toString());
				kingdomsList = contentService.queryEvery(kingdomsSql.toString());
			}catch(Exception e){
				logger.error("查询出错", e);
			}
			Map<String, Long> fansMap = new HashMap<String, Long>();
			if(null != fansList && fansList.size() > 0){
				for(Map<String, Object> fans : fansList){
					fansMap.put(String.valueOf(fans.get("target_uid")), (Long)fans.get("cc"));
				}
			}
			Map<String, Long> kingdomsMap = new HashMap<String, Long>();
			if(null != kingdomsList && kingdomsList.size() > 0){
				for(Map<String, Object> kingdoms : kingdomsList){
					kingdomsMap.put(String.valueOf(kingdoms.get("uid")), (Long)kingdoms.get("cc"));
				}
			}
			
			UserRegisterDetailQueryDTO.Item item = null;
			Long fansCount = null;
			Long kingdomsCount = null;
			for(Map<String, Object> u : list){
				item = new UserRegisterDetailQueryDTO.Item();
				item.setChannelCode((String)u.get("channel"));
				item.setRegisterVersion((String)u.get("register_version"));
				fansCount = fansMap.get(String.valueOf(u.get("uid")));
				if(null != fansCount){
					item.setFansCount(fansCount.longValue());
				}else{
					item.setFansCount(0);
				}
				kingdomsCount = kingdomsMap.get(String.valueOf(u.get("uid")));
				if(null != kingdomsCount){
					item.setKingdomCount(kingdomsCount.longValue());
				}else{
					item.setKingdomCount(0);
				}
				item.setNickName((String)u.get("nick_name"));
				if(null != u.get("platform")){
					item.setPlatform((Integer)u.get("platform"));
				}else{
					item.setPlatform(0);
				}
				item.setRegisterDate((Date)u.get("create_time"));
				item.setUid((Long)u.get("uid"));
				item.setRegisterMode(this.getRegisterMode((String)u.get("third_part_bind")));
				
				dto.getResult().add(item);
			}
		}
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	@RequestMapping(value = "/kingdomCreate/detail/query")
	public ModelAndView kingdomCreateDetailQuery(KingdomCreateDetailQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		
		this.genKingdomCreateDetail(dto);
		
		ModelAndView view = new ModelAndView("stat/kingdomCreateDetail");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value="/kingdomCreate/detail/page")
	@ResponseBody
	public String kingdomCreateDetailPage(KingdomCreateDetailQueryDTO dto){
		
		this.genKingdomCreateDetail(dto);
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	private void genKingdomCreateDetail(KingdomCreateDetailQueryDTO dto){
		int pageSize = dto.getPageSize();
		int start = (dto.getPage()-1)*pageSize;
		String startTime = dto.getStartTime() + " 00:00:00";
		String endTime = dto.getEndTime() + " 23:59:59";
		
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("select t.id,t.title,p.uid,p.nick_name,p.third_part_bind,p.create_time,p.channel,p.platform");
		pageSql.append(" from topic t,user_profile p where t.uid=p.uid");
		pageSql.append(" and t.create_time>='").append(startTime);
		pageSql.append("' and t.create_time<='").append(endTime).append("'");
		if(null != dto.getChannelCode() && !"".equals(dto.getChannelCode())){
			pageSql.append(" and p.channel='").append(dto.getChannelCode()).append("'");
		}
		if(null != dto.getNickName() && !"".equals(dto.getNickName())){
			pageSql.append(" and p.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		pageSql.append("order by t.id limit ").append(start).append(",").append(pageSize);
		
		StringBuilder countSql = new StringBuilder();
		countSql.append("select count(1) as cc");
		countSql.append(" from topic t,user_profile p where t.uid=p.uid");
		countSql.append(" and t.create_time>='").append(startTime);
		countSql.append("' and t.create_time<='").append(endTime).append("'");
		if(null != dto.getChannelCode() && !"".equals(dto.getChannelCode())){
			countSql.append(" and p.channel='").append(dto.getChannelCode()).append("'");
		}
		if(null != dto.getNickName() && !"".equals(dto.getNickName())){
			countSql.append(" and p.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		List<Map<String, Object>> countList = null;
		try{
			list = contentService.queryEvery(pageSql.toString());
			countList = contentService.queryEvery(countSql.toString());
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
			List<Long> topicIdList = new ArrayList<Long>();
			for(Map<String, Object> k : list){
				topicIdList.add((Long)k.get("id"));
			}
			
			StringBuilder updateCountSql = new StringBuilder();
			updateCountSql.append("select f.topic_id,count(1) as cc");
			updateCountSql.append(" from topic_fragment f");
			updateCountSql.append(" where f.status=1 and f.topic_id in (");
			for(int i=0;i<topicIdList.size();i++){
				if(i>0){
					updateCountSql.append(",");
				}
				updateCountSql.append(topicIdList.get(i));
			}
			updateCountSql.append(") and f.type in (0,11,12,13,15,52,55)");
			updateCountSql.append(" group by f.topic_id");
			
			StringBuilder memberCountSql = new StringBuilder();
			memberCountSql.append("select f.topic_id,count(1) as cc");
			memberCountSql.append(" from live_favorite f where f.topic_id in (");
			for(int i=0;i<topicIdList.size();i++){
				if(i>0){
					memberCountSql.append(",");
				}
				memberCountSql.append(topicIdList.get(i));
			}
			memberCountSql.append(") group by f.topic_id");
			
			List<Map<String, Object>> updateCountList = null;
			List<Map<String, Object>> memberCountList = null;
			try{
				updateCountList = contentService.queryEvery(updateCountSql.toString());
				memberCountList = contentService.queryEvery(memberCountSql.toString());
			}catch(Exception e){
				logger.error("查询出错", e);
			}
			Map<String, Long> updateCountMap = new HashMap<String, Long>();
			if(null != updateCountList && updateCountList.size() > 0){
				for(Map<String, Object> m : updateCountList){
					updateCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("cc"));
				}
			}
			Map<String, Long> memberCountMap = new HashMap<String, Long>();
			if(null != memberCountList && memberCountList.size() > 0){
				for(Map<String, Object> m : memberCountList){
					memberCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("cc"));
				}
			}
			
			KingdomCreateDetailQueryDTO.Item item = null;
			Long updateCount = null;
			Long memberCount = null;
			for(Map<String, Object> k : list){
				item = new KingdomCreateDetailQueryDTO.Item();
				item.setChannel((String)k.get("channel"));
				item.setNickName((String)k.get("nick_name"));
				if(null != k.get("platform")){
					item.setPlatform((Integer)k.get("platform"));
				}else{
					item.setPlatform(0);
				}
				item.setRegisterDate((Date)k.get("create_time"));
				item.setRegisterMode(this.getRegisterMode((String)k.get("third_part_bind")));
				item.setTitle((String)k.get("title"));
				item.setTopicId((Long)k.get("id"));
				item.setH5Addr(localConfigService.getWebappUrl() + item.getTopicId());
				item.setUid((Long)k.get("uid"));
				updateCount = updateCountMap.get(String.valueOf(item.getTopicId()));
				if(null != updateCount){
					item.setUpdateCount(updateCount.longValue());
				}else{
					item.setUpdateCount(0);
				}
				memberCount = memberCountMap.get(String.valueOf(item.getTopicId()));
				if(null != memberCount){
					item.setMemberCount(memberCount.longValue());
				}else{
					item.setMemberCount(0);
				}
				dto.getResult().add(item);
			}
		}
	}
	
	@RequestMapping(value = "/userRegister/query")
	public ModelAndView userRegister(UserRegisterQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		
		String startTime = dto.getStartTime() + " 00:00:00";
		String endTime = dto.getEndTime() + " 23:59:59";
		
		StringBuilder sb = new StringBuilder();
		sb.append("select DATE_FORMAT(p.create_time,'%Y-%m-%d') as dateStr,");
		sb.append("count(1) as totalCount,");
		sb.append("count(if(p.third_part_bind like '%mobile%', TRUE, NULL)) as phoneCount,");
		sb.append("count(if(p.third_part_bind like '%qq%', TRUE, NULL)) as qqCount,");
		sb.append("count(if(p.third_part_bind like '%weixin%', TRUE, NULL)) as weixinCount");
		sb.append(" from user_profile p");
		sb.append(" where p.create_time>='").append(startTime);
		sb.append("' and p.create_time<='").append(endTime);
		sb.append("' group by DATE_FORMAT(p.create_time,'%Y-%m-%d')");
		
		dto.getResult().clear();
		List<Map<String, Object>> list = null;
		try{
			list = contentService.queryEvery(sb.toString());
		}catch(Exception e){
			logger.error("查询出错", e);
		}
		if(null != list && list.size() > 0){
			UserRegisterQueryDTO.Item item = null;
			for(Map<String, Object> m : list){
				item = new UserRegisterQueryDTO.Item();
				item.setDateStr((String)m.get("dateStr"));
				item.setTotalCount((Long)m.get("totalCount"));
				item.setPhoneCount((Long)m.get("phoneCount"));
				item.setQqCount((Long)m.get("qqCount"));
				item.setWeixinCount((Long)m.get("weixinCount"));
				
				dto.getResult().add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("stat/userRegister");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value = "/iosWapx/query")
	public ModelAndView iosWapxQuery(IosWapxQueryDTO dto){
		Date now = new Date();
		if(null == dto.getStartTime() || "".equals(dto.getStartTime())){
			dto.setStartTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		if(null == dto.getEndTime() || "".equals(dto.getEndTime())){
			dto.setEndTime(DateUtil.date2string(now, "yyyy-MM-dd"));
		}
		
		this.getIosWapxInfo(dto);
		
		ModelAndView view = new ModelAndView("stat/iosWapx");
		view.addObject("dataObj", dto);
		return view;
	}
	
	@RequestMapping(value="/iosWapx/page")
	@ResponseBody
	public String iosWapxPage(IosWapxQueryDTO dto){
		this.getIosWapxInfo(dto);
		
		JSONObject obj = (JSONObject)JSON.toJSON(dto);
		return obj.toJSONString();
	}
	
	private void getIosWapxInfo(IosWapxQueryDTO dto){
		int pageSize = dto.getPageSize();
		int start = (dto.getPage()-1)*dto.getPageSize();
		
		String startTime = dto.getStartTime() + " 00:00:00";
		String endTime = dto.getEndTime() + " 23:59:59";
		
		StringBuilder countSql = new StringBuilder();
		countSql.append("select count(1) as total,");
		countSql.append("count(if(t.status=0,TRUE,NULL)) as noticeCount,");
		countSql.append("count(if(t.status=0,NULL,TRUE)) as activeCount");
		countSql.append(" from ios_wapx t where t.update_time>='").append(startTime);
		countSql.append("' and t.update_time<='").append(endTime);
		countSql.append("'");
		
		StringBuilder querySql = new StringBuilder();
		querySql.append("select * from ios_wapx t where t.update_time>='");
		querySql.append(startTime).append("' and t.update_time<='");
		querySql.append(endTime).append("' order by t.update_time asc");
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
			long totalCount = (Long)count.get("total");
			int totalPage = totalCount%pageSize==0?(int)totalCount/pageSize:((int)totalCount/pageSize)+1;
			dto.setTotalCount((int)totalCount);
			dto.setTotalPage(totalPage);
			dto.getTotalItem().setTotalNoticeCount((Long)count.get("noticeCount"));
			dto.getTotalItem().setTotalActiveCount((Long)count.get("activeCount"));
		}
		
		if(null != list && list.size() > 0){
			IosWapxQueryDTO.Item item = null;
			for(Map<String, Object> map : list){
				item = new IosWapxQueryDTO.Item();
				item.setApp((String)map.get("app"));
				item.setCallbackurl((String)map.get("callbackurl"));
				item.setIdfa((String)map.get("idfa"));
				item.setIp((String)map.get("ip"));
				item.setOpenudid((String)map.get("openudid"));
				item.setOptTime((Date)map.get("update_time"));
				item.setOs((String)map.get("os"));
				item.setStatus((Integer)map.get("status"));
				item.setUdid((String)map.get("udid"));
				item.setUid((Long)map.get("uid"));
				dto.getResult().add(item);
			}
		}
	}
}
