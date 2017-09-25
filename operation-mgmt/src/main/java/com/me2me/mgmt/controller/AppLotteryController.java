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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.common.page.PageBean;
import com.me2me.mgmt.dao.LocalJdbcDao;
import com.me2me.mgmt.request.AppLotteryQueryDTO;
import com.me2me.mgmt.request.AppLotteryUserQueryDTO;

@Controller
@RequestMapping("/appLottery")
public class AppLotteryController {

	private static final Logger logger = LoggerFactory.getLogger(AppLotteryController.class);
	
	@Autowired
	private LocalJdbcDao localJdbcDao;
	
	@RequestMapping(value = "/list")
	public ModelAndView list(){
		ModelAndView view = new ModelAndView("lottery/appLotteryList");
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value = "/listPage")
	public AppLotteryQueryDTO listPage(AppLotteryQueryDTO dto){
		PageBean page= dto.toPageBean();
		
		StringBuilder sb = new StringBuilder();
		sb.append("from lottery_info i,topic t where i.topic_id=t.id and i.status>=0");
		if(StringUtils.isNotBlank(dto.getKingdomName())){
			sb.append(" and t.title like '%").append(dto.getKingdomName()).append("%'");
		}
		if(StringUtils.isNotBlank(dto.getLotteryName())){
			sb.append(" and i.title like '%").append(dto.getLotteryName()).append("%'");
		}
		
		String countSql = "select count(1) " + sb.toString();
		String querySql = "select i.id,i.topic_id,t.title as kingdomName,i.title as lotteryName,i.create_time,i.end_time,i.status " + sb.toString() + " order by i.id desc limit ?,?";
		
		int count = localJdbcDao.queryForObject(countSql, Integer.class);
		List<Map<String, Object>> dataList = localJdbcDao.queryForList(querySql,(page.getCurrentPage()-1)*page.getPageSize(),page.getPageSize());
		if(null != dataList && dataList.size() > 0){
			for(Map<String, Object> m : dataList){
				int status = (Integer)m.get("status");
				if(status == 2){
					m.put("status_str", "抽奖结束");
				}else{
					Date endTime = (Date)m.get("end_time");
					long currentTime = System.currentTimeMillis();
					if(endTime.getTime() > currentTime){
						m.put("status_str", "进行中");
					}else{
						m.put("status_str", "等待开奖");
					}
				}
			}
		}
		
		dto.setRecordsTotal(count);
		dto.setData(dataList);
		return dto;
	}
	
	@RequestMapping(value = "/userList")
	public ModelAndView userList(AppLotteryUserQueryDTO dto){
		ModelAndView view = new ModelAndView("lottery/appLotteryUserList");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value = "/userListPage")
	public AppLotteryUserQueryDTO userListPage(AppLotteryUserQueryDTO dto){
		PageBean page= dto.toPageBean();
		
		StringBuilder sb = new StringBuilder();
		sb.append("from user_profile u,(select l.uid,min(l.create_time) as mintime");
		sb.append(" from lottery_content l where l.lottery_id=").append(dto.getLotteryId()).append(" group by l.uid ) m");
		sb.append(" LEFT JOIN lottery_prohibit p on p.lottery_id=").append(dto.getLotteryId()).append(" and p.uid=m.uid");
		sb.append(" LEFT JOIN lottery_appoint a on a.lottery_id=").append(dto.getLotteryId()).append(" and a.uid=m.uid");
		sb.append(" LEFT JOIN lottery_win w on w.lottery_id=").append(dto.getLotteryId()).append(" and w.uid=m.uid");
		sb.append(" where u.uid=m.uid");
		if(StringUtils.isNotBlank(dto.getNickName())){
			sb.append(" and u.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		if(dto.getAppoint() == 1){
			sb.append(" and a.uid is not null");
		}else if(dto.getAppoint() == 2){
			sb.append(" and a.uid is null");
		}
		
		String countSql = "select count(1) " + sb.toString();
		String querySql = "select u.nick_name,u.uid,m.mintime,IFNULL(a.uid,0) as appoint,IFNULL(p.uid,0) as prohibit,IFNULL(w.uid,0) as win "
				+ sb.toString() + " order by m.mintime asc limit ?,?";
		
		int count = localJdbcDao.queryForObject(countSql, Integer.class);
		List<Map<String, Object>> dataList = localJdbcDao.queryForList(querySql,(page.getCurrentPage()-1)*page.getPageSize(),page.getPageSize());
		
		if(null != dataList && dataList.size() > 0){
			List<Long> uidList = new ArrayList<Long>();
			Long uid = null;
			for(Map<String, Object> d : dataList){
				uid = (Long)d.get("uid");
				if(!uidList.contains(uid)){
					uidList.add(uid);
				}
			}
			StringBuilder iSql = new StringBuilder();
			iSql.append("select u.referee_uid,count(1) as cc from user_profile u");
			iSql.append(" where u.is_activate=1 and u.referee_uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					iSql.append(",");
				}
				iSql.append(uidList.get(i).toString());
			}
			iSql.append(") group by u.referee_uid");
			
			Map<String, Long> iMap = new HashMap<String, Long>();
			List<Map<String, Object>> cList = localJdbcDao.queryForList(iSql.toString());
			if(null != cList && cList.size() > 0){
				for(Map<String, Object> c : cList){
					iMap.put(String.valueOf(c.get("referee_uid")), (Long)c.get("cc"));
				}
			}
			
			for(Map<String, Object> d : dataList){
				long prohibit = (Long)d.get("prohibit");
				long win = (Long)d.get("win");
				if(prohibit > 0){
					d.put("status_str", "<font color='red'>被屏蔽</font>");
				}else if(win > 0){
					d.put("status_str", "<font color='greed'>已中奖</font>");
				}else{
					d.put("status_str", "未中奖");
				}
				uid = (Long)d.get("uid");
				if(null != iMap.get(uid.toString())){
					d.put("icount", iMap.get(uid.toString()));
				}else{
					d.put("icount", 0);
				}
			}
		}
		
		dto.setRecordsTotal(count);
		dto.setData(dataList);
		return dto;
	}
	
	@ResponseBody
	@RequestMapping(value = "/setAppoint")
	public String setAppoint(@RequestParam("lid")long lid,@RequestParam("uid")long uid,@RequestParam("a")int action){
		if(lid <= 0 || uid <= 0){
			return "传递的参数异常";
		}
		
		String querySql = "select * from lottery_appoint a where a.lottery_id=? and a.uid=?";
		List<Map<String, Object>> list = localJdbcDao.queryForList(querySql, lid, uid);
		Map<String, Object> appoint = null;
		if(null != list && list.size() > 0){
			appoint = list.get(0);
		}
		if(action == 0){//取消指定
			if(null != appoint){
				Long id = (Long)appoint.get("id");
				String delSql = "delete from lottery_appoint where id=?";
				localJdbcDao.executeSqlWithParams(delSql, id);
			}
		}else{//设置指定
			if(null == appoint){
				String insertSql = "insert into lottery_appoint(lottery_id,uid,create_time) values(?,?,now())";
				localJdbcDao.executeSqlWithParams(insertSql, lid, uid);
			}
		}
		
		return "0";
	}
}
