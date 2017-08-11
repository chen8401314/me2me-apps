package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.me2me.common.page.PageBean;
import com.me2me.content.service.ContentService;
import com.me2me.live.model.TopicPriceSubsidyConfig;
import com.me2me.live.service.LiveService;
import com.me2me.mgmt.request.ConfigItem;
import com.me2me.mgmt.request.KingdomBusinessDTO;
import com.me2me.mgmt.request.KingdomDTO;
import com.me2me.mgmt.request.KingdomQueryDTO;
import com.me2me.mgmt.request.KingdomUserRequest;
import com.me2me.mgmt.request.SearchUserDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.mgmt.task.app.KingdomPriceTask;
import com.me2me.mgmt.vo.DatatablePage;
import com.me2me.user.service.UserService;

@Controller
@RequestMapping("/price")
public class PriceController {

	private static final Logger logger = LoggerFactory.getLogger(PriceController.class);
	
	@Autowired
    private ContentService contentService;
	@Autowired
	private LiveService liveService;
	@Autowired
	private UserService userService;
	@Autowired
	private KingdomPriceTask kingdomPriceTask;
	
	//////新实现
	

	
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
	
	@RequestMapping(value = "/kingdomUser")
	public ModelAndView kingdomUser(KingdomQueryDTO dto){
		ModelAndView view = new ModelAndView("price/kingdomUser");
		view.addObject("dataObj",dto);
		return view;
	}
	@ResponseBody
	@RequestMapping(value = "/kingdomUserPage")
	public KingdomUserRequest kingdomUserPage(KingdomUserRequest dto){
		PageBean page= dto.toPageBean();
		String sql ="select r1.*,up.nick_name,un.me_number,up.create_time from ("+
					"SELECT uid,count(1) messages from topic_fragment where topic_id=? group by uid "+
					") r1,user_profile up,user_no un "+
					"where r1.uid=up.uid and r1.uid=un.uid "+
					"order by up.create_time desc";
		
		String countSql = "select count(1) from ("+sql+") c";
		String pageSql = sql+" limit ?,?";
		
		int count = contentService.queryForObject(countSql, Integer.class,dto.getTopicId());
		List dataList = contentService.queryForList(pageSql, dto.getTopicId(),(page.getCurrentPage()-1)*page.getPageSize(),page.getPageSize());
		dto.setRecordsTotal(count);
		dto.setData(dataList);
		return dto;
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
			pageSize = 100;
		}
		
		int start = (page-1)*pageSize;
		StringBuilder sb = new StringBuilder();
		sb.append("select t.*,d.last_price_incr,d.steal_price,d.diligently,d.approve from topic t LEFT JOIN topic_data d on t.id=d.topic_id");
		if(StringUtils.isNotBlank(dto.getTitle())){
			sb.append(" where t.title like '%").append(dto.getTitle()).append("%'");
		}
		sb.append(" order by ");
		if("update_time".equals(dto.getOrderbyParam())){
			sb.append("t.");
		}
		sb.append(dto.getOrderbyParam()).append(" ").append(dto.getOrderby());;
		sb.append(" limit ").append(start).append(",").append(pageSize);

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
		
		List<Map<String, Object>> queryList = contentService.queryEvery(sb.toString());
		if(null != queryList && queryList.size() > 0){
			List<Long> topicIdList = new ArrayList<Long>();
			List<Long> uidList = new ArrayList<Long>();
			Long uid = null;
			for(Map<String, Object> m : queryList){
				uid = (Long)m.get("uid");
				if(!uidList.contains(uid)){
					uidList.add(uid);
				}
				topicIdList.add((Long)m.get("id"));
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
				if(null != m.get("last_price_incr")){
					item.setLastPriceIncr((Integer)m.get("last_price_incr"));
				}
				if(null != m.get("steal_price")){
					item.setStealPrice((Integer)m.get("steal_price"));
				}
				if(null != m.get("diligently")){
					item.setDiligently((Double)m.get("diligently"));
				}
				if(null != m.get("approve")){
					item.setApprove((Double)m.get("approve"));
				}
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
		logger.info("王国国王变更，王国："+dto.getTopicId()+"，新国王UID：" + dto.getNewUid());
		return liveService.changeTopicKing(dto.getTopicId(), dto.getNewUid());
	}
	
	private static List<String> fragmentScoreKeyList = new ArrayList<String>() {
		private static final long serialVersionUID = 7846617633906369703L;

		{
			this.add("TOPICFRAGMENTSCORE_0_0");
			this.add("TOPICFRAGMENTSCORE_1_0");
			this.add("TOPICFRAGMENTSCORE_0_1");
			this.add("TOPICFRAGMENTSCORE_3_0");
			this.add("TOPICFRAGMENTSCORE_4_0");
			this.add("TOPICFRAGMENTSCORE_11_11");
			this.add("TOPICFRAGMENTSCORE_10_10");
			this.add("TOPICFRAGMENTSCORE_15_15");
			this.add("TOPICFRAGMENTSCORE_13_13");
			this.add("TOPICFRAGMENTSCORE_12_12");
			this.add("TOPICFRAGMENTSCORE_1000_0");
			this.add("TOPICFRAGMENTSCORE_1000_17");
			this.add("TOPICFRAGMENTSCORE_1000_72");
			this.add("TOPICFRAGMENTSCORE_52_70");
			this.add("TOPICFRAGMENTSCORE_52_71");
			this.add("TOPICFRAGMENTSCORE_52_72");
			this.add("TOPICFRAGMENTSCORE_52_73");
			this.add("TOPICFRAGMENTSCORE_52_74");
			this.add("TOPICFRAGMENTSCORE_51_70");
			this.add("TOPICFRAGMENTSCORE_51_71");
			this.add("TOPICFRAGMENTSCORE_51_72");
			this.add("TOPICFRAGMENTSCORE_51_73");
			this.add("TOPICFRAGMENTSCORE_51_74");
			this.add("TOPICFRAGMENTSCORE_54_0");
			this.add("TOPICFRAGMENTSCORE_54_51");
			this.add("TOPICFRAGMENTSCORE_54_2");
			this.add("TOPICFRAGMENTSCORE_54_63");
			this.add("TOPICFRAGMENTSCORE_54_62");
			this.add("TOPICFRAGMENTSCORE_54_70");
			this.add("TOPICFRAGMENTSCORE_54_71");
			this.add("TOPICFRAGMENTSCORE_54_72");
			this.add("TOPICFRAGMENTSCORE_54_73");
			this.add("TOPICFRAGMENTSCORE_54_74");
			this.add("TOPICFRAGMENTSCORE_55_0");
			this.add("TOPICFRAGMENTSCORE_55_51");
			this.add("TOPICFRAGMENTSCORE_55_2");
			this.add("TOPICFRAGMENTSCORE_55_63");
			this.add("TOPICFRAGMENTSCORE_55_62");
			this.add("TOPICFRAGMENTSCORE_55_70");
			this.add("TOPICFRAGMENTSCORE_55_71");
			this.add("TOPICFRAGMENTSCORE_55_72");
			this.add("TOPICFRAGMENTSCORE_55_73");
			this.add("TOPICFRAGMENTSCORE_55_74");
			this.add("TOPICFRAGMENTSCORE_56_0");
			this.add("TOPICFRAGMENTSCORE_56_51");
			this.add("TOPICFRAGMENTSCORE_56_2");
			this.add("TOPICFRAGMENTSCORE_56_63");
			this.add("TOPICFRAGMENTSCORE_56_62");
			this.add("TOPICFRAGMENTSCORE_56_70");
			this.add("TOPICFRAGMENTSCORE_56_71");
			this.add("TOPICFRAGMENTSCORE_56_72");
			this.add("TOPICFRAGMENTSCORE_56_73");
			this.add("TOPICFRAGMENTSCORE_56_74");
			this.add("TOPICFRAGMENTSCORE_51_16");
			this.add("TOPICFRAGMENTSCORE_52_17");
			this.add("TOPICFRAGMENTSCORE_52_18");
			this.add("TOPICFRAGMENTSCORE_51_17");
			this.add("TOPICFRAGMENTSCORE_51_18");
			this.add("TOPICFRAGMENTSCORE_52_19");
			this.add("TOPICFRAGMENTSCORE_52_20");
			this.add("TOPICFRAGMENTSCORE_51_20");
			this.add("TOPICFRAGMENTSCORE_52_21");
		}
	};
	
	private static Map<String, String> fragmentScoreMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 7846617633906369703L;
		{
			this.put("TOPICFRAGMENTSCORE_0_0", "主播发言");
			this.put("TOPICFRAGMENTSCORE_1_0", "粉丝回复");
			this.put("TOPICFRAGMENTSCORE_0_1", "主播发图");
			this.put("TOPICFRAGMENTSCORE_3_0", "主播贴标");
			this.put("TOPICFRAGMENTSCORE_4_0", "粉丝贴标");
			this.put("TOPICFRAGMENTSCORE_11_11", "主播@");
			this.put("TOPICFRAGMENTSCORE_10_10", "粉丝@");
			this.put("TOPICFRAGMENTSCORE_15_15", "核心圈@");
			this.put("TOPICFRAGMENTSCORE_13_13", "主播发语音");
			this.put("TOPICFRAGMENTSCORE_12_12", "主播发视频");
			this.put("TOPICFRAGMENTSCORE_1000_0", "系统文本");
			this.put("TOPICFRAGMENTSCORE_1000_17", "系统链接");
			this.put("TOPICFRAGMENTSCORE_1000_72", "系统王国链接");
			this.put("TOPICFRAGMENTSCORE_52_70", "主播UGC链接");
			this.put("TOPICFRAGMENTSCORE_52_71", "主播PGC链接");
			this.put("TOPICFRAGMENTSCORE_52_72", "主播王国连接");
			this.put("TOPICFRAGMENTSCORE_52_73", "主播活动连接");
			this.put("TOPICFRAGMENTSCORE_52_74", "主播PPGC连接");
			this.put("TOPICFRAGMENTSCORE_51_70", "粉丝UGC链接");
			this.put("TOPICFRAGMENTSCORE_51_71", "粉丝PGC链接");
			this.put("TOPICFRAGMENTSCORE_51_72", "粉丝王国连接");
			this.put("TOPICFRAGMENTSCORE_51_73", "粉丝活动连接");
			this.put("TOPICFRAGMENTSCORE_51_74", "粉丝智能推荐连接");
			this.put("TOPICFRAGMENTSCORE_54_0", "下发文本");
			this.put("TOPICFRAGMENTSCORE_54_51", "下发图片");
			this.put("TOPICFRAGMENTSCORE_54_2", "下发标签");
			this.put("TOPICFRAGMENTSCORE_54_63", "下发音频");
			this.put("TOPICFRAGMENTSCORE_54_62", "下发视频");
			this.put("TOPICFRAGMENTSCORE_54_70", "下发UGC链接");
			this.put("TOPICFRAGMENTSCORE_54_71", "下发PGC链接");
			this.put("TOPICFRAGMENTSCORE_54_72", "下发王国链接");
			this.put("TOPICFRAGMENTSCORE_54_73", "下发活动链接");
			this.put("TOPICFRAGMENTSCORE_54_74", "下发智能推荐链接");
			this.put("TOPICFRAGMENTSCORE_55_0", "主播转发文本");
			this.put("TOPICFRAGMENTSCORE_55_51", "主播转发图片");
			this.put("TOPICFRAGMENTSCORE_55_2", "主播转发标签");
			this.put("TOPICFRAGMENTSCORE_55_63", "主播转发音频");
			this.put("TOPICFRAGMENTSCORE_55_62", "主播转发视频");
			this.put("TOPICFRAGMENTSCORE_55_70", "主播转发UGC链接");
			this.put("TOPICFRAGMENTSCORE_55_71", "主播转发PGC链接");
			this.put("TOPICFRAGMENTSCORE_55_72", "主播转发王国链接");
			this.put("TOPICFRAGMENTSCORE_55_73", "主播转发活动链接");
			this.put("TOPICFRAGMENTSCORE_55_74", "主播转发智能推荐链接");
			this.put("TOPICFRAGMENTSCORE_56_0", "粉丝转发文本");
			this.put("TOPICFRAGMENTSCORE_56_51", "粉丝主播转发图片");
			this.put("TOPICFRAGMENTSCORE_56_2", "粉丝主播转发标签");
			this.put("TOPICFRAGMENTSCORE_56_63", "粉丝主播转发音频");
			this.put("TOPICFRAGMENTSCORE_56_62", "粉丝主播转发视频");
			this.put("TOPICFRAGMENTSCORE_56_70", "粉丝主播转发UGC链接");
			this.put("TOPICFRAGMENTSCORE_56_71", "粉丝主播转发PGC链接");
			this.put("TOPICFRAGMENTSCORE_56_72", "粉丝主播转发王国链接");
			this.put("TOPICFRAGMENTSCORE_56_73", "粉丝主播转发活动链接");
			this.put("TOPICFRAGMENTSCORE_56_74", "粉丝主播转发智能推荐链接");
			this.put("TOPICFRAGMENTSCORE_51_16", "足迹");
			this.put("TOPICFRAGMENTSCORE_52_17", "主播中表情");
			this.put("TOPICFRAGMENTSCORE_52_18", "主播大表情");
			this.put("TOPICFRAGMENTSCORE_51_17", "粉丝中表情");
			this.put("TOPICFRAGMENTSCORE_51_18", "粉丝大表情");
			this.put("TOPICFRAGMENTSCORE_52_19", "投票");
			this.put("TOPICFRAGMENTSCORE_52_20", "主播逗一逗");
			this.put("TOPICFRAGMENTSCORE_51_20", "粉丝逗一逗");
			this.put("TOPICFRAGMENTSCORE_52_21", "王国所有权转让书");
		}
	};
	
	private static List<String> commonConfigKeyList = new ArrayList<String>(){
		private static final long serialVersionUID = -5775528521716170797L;
		
		{
			this.add("EXCHANGE_RATE");
			this.add("EXCHANGE_RATE222");
		}
	};
	
	private static Map<String, String> commonConfigMap = new HashMap<String, String>(){
		private static final long serialVersionUID = -5775528521716170797L;
		
		{
			this.put("EXCHANGE_RATE", "汇率(1RMB=?MB)");
			this.put("EXCHANGE_RATE222", "汇率222(1RMB=?MB)");
		}
	};
	
	@RequestMapping(value = "/configQuery/{type}")
	public ModelAndView fragmentScoreQuery(@PathVariable String type){
		List<ConfigItem> result = new ArrayList<ConfigItem>();
		
		List<String> keyList = null;
		Map<String, String> paramMap = null;
		if("fragmentScore".equals(type)){
			keyList = fragmentScoreKeyList;
			paramMap = fragmentScoreMap;
		}else if("commonConfig".equals(type)){
			keyList = commonConfigKeyList;
			paramMap = commonConfigMap;
		}
		
		if(null != keyList && keyList.size() > 0 && null != paramMap && paramMap.size() > 0){
			Map<String, String> configMap = userService.getAppConfigsByKeys(keyList);
			
			ConfigItem item = null;
			for(Map.Entry<String, String> entry : paramMap.entrySet()){
				item = new ConfigItem(entry.getKey(),entry.getValue(),ConfigItem.ConfigType.DB,null==configMap.get(entry.getKey())?"":configMap.get(entry.getKey()));
				result.add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("price/"+type);
		view.addObject("dataObj",result);
		
		return view;
	}
	
	@RequestMapping(value = "/configModify/{type}")
	@ResponseBody
	public String fragmentScoreModify(@RequestParam("k")String key, 
			@RequestParam("v")String value, @PathVariable String type){
		if(StringUtils.isBlank(key)){
			logger.warn("key不能为空");
			return "key不能为空";
		}
		
		Map<String, String> paramMap = null;
		if("fragmentScore".equals(type)){
			paramMap = fragmentScoreMap;
		}else if("commonConfig".equals(type)){
			paramMap = commonConfigMap;
		}
		String desc = "";
		if(null != paramMap && paramMap.size() > 0){
			desc = paramMap.get(key);
			if(desc == null){
				desc = "";
			}
		}
		
		userService.saveAppConfig(key, value);
		
		return "0";
	}
	
	@RequestMapping(value = "/runTask")
	@ResponseBody
	public String runTask(@RequestParam("m")int mode, 
			@RequestParam("t")String runTime) throws Exception{
		
		try{
			if(mode == 1){//增量
				logger.info("增量执行，执行时间："+runTime);
				kingdomPriceTask.executeIncr(runTime);
			}else if(mode == 2){//全量
				logger.info("全量执行，执行时间："+runTime);
				kingdomPriceTask.executeFull(runTime);
			}else{
				return "未知的执行模式";
			}
		}catch(Exception e){
			logger.error("任务执行失败", e);
			return "执行失败";
		}
		
		return "执行完成";
	}
	
	@RequestMapping(value = "/initPrice")
	@ResponseBody
	public String initPrice(){
		try{
			String sql1 = "update topic set price=0,listing_time=null,update_time=update_time";
			String sql2 = "delete from topic_data";
			String sql3 = "delete from topic_price_his";
			String sql4 = "delete from topic_price_push";
			
			contentService.executeSql(sql1);
			contentService.executeSql(sql2);
			contentService.executeSql(sql3);
			contentService.executeSql(sql4);
		}catch(Exception e){
			logger.error("初始化失败", e);
			return "初始化失败";
		}
		return "初始化完成";
	}
	

	@RequestMapping(value = "/taskConsole")
	public ModelAndView taskConsole(){
		ModelAndView view = new ModelAndView("price/taskConsole");
		return view;
	}
	
	@RequestMapping(value = "/listTopicPriceSubsidyConfig")
	@SystemControllerLog(description = "补贴配置列表")
	public String listTopicPriceSubsidyConfig(HttpServletRequest request) throws Exception {
		List<TopicPriceSubsidyConfig> datas = liveService.getTopicPriceSubsidyConfigList();
		request.setAttribute("data",datas);
		return "price/listTopicPriceSubsidyConfig";
	}
	@RequestMapping(value = "/addTopicPriceSubsidyConfig")
	@SystemControllerLog(description = "添加补贴配置")
	public String addTopicPriceSubsidyConfig(HttpServletRequest request,DatatablePage dpage) throws Exception {
		return "price/addTopicPriceSubsidyConfig";
	}
	
	@RequestMapping(value = "/modifyTopicPriceSubsidyConfig")
	@SystemControllerLog(description = "修改补贴配置")
	public String modifyTopicPriceSubsidyConfig(HttpServletRequest request,DatatablePage dpage) throws Exception {
		String id = request.getParameter("id");
		TopicPriceSubsidyConfig item= liveService.getTopicPriceSubsidyConfigById(Long.valueOf(id));
		request.setAttribute("item",item);
		return "price/addTopicPriceSubsidyConfig";
	}
	@RequestMapping(value = "/doSaveTopicPriceSubsidyConfig")
	@SystemControllerLog(description = "保存补贴配置")
	public String doSaveTopicPriceSubsidyConfig(TopicPriceSubsidyConfig tpsc,HttpServletRequest mrequest) throws Exception {
		try{
			if(tpsc.getId()!=null){
				liveService.editTopicPriceSubsidyConfig(tpsc);; 
			}else{
				liveService.saveTopicPriceSubsidyConfig(tpsc);
			}
			return "redirect:./listTopicPriceSubsidyConfig";
		}catch(Exception e){
			e.printStackTrace();
			mrequest.setAttribute("item",tpsc);
			return "redirect:./doSaveTopicPriceSubsidyConfig";
		}
	}
	@RequestMapping(value = "/delTopicPriceSubsidyConfig")
	@SystemControllerLog(description = "删除补贴配置")
	public String delTopicPriceSubsidyConfig(HttpServletRequest request) throws Exception {
		String id = request.getParameter("id");
	     liveService.delTopicPriceSubsidyConfig(Long.valueOf(id));
		return "redirect:./listTopicPriceSubsidyConfig";
	}
}
