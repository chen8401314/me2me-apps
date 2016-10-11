package com.me2me.mgmt.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.web.Response;
import com.me2me.content.dto.KingTopicDto;
import com.me2me.content.dto.ShowKingTopicDto;
import com.me2me.content.service.ContentService;
import com.me2me.mgmt.Utils.DateUtil;
import com.me2me.mgmt.request.DailyActiveDTO;
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
}
