package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.Constant;
import com.me2me.common.page.PageBean;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Response;
import com.me2me.io.service.FileTransferService;
import com.me2me.mgmt.dao.LocalJdbcDao;
import com.me2me.mgmt.request.AppGagUserQueryDTO;
import com.me2me.mgmt.request.AppUserDTO;
import com.me2me.mgmt.request.AppUserQueryDTO;
import com.me2me.mgmt.request.AvatarFrameQueryDTO;
import com.me2me.mgmt.request.AvatarFrameUserQueryDTO;
import com.me2me.mgmt.request.UserInvitationDetailQueryDTO;
import com.me2me.mgmt.request.UserInvitationQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.sms.service.SmsService;
import com.me2me.user.dto.ShowUsergagDto;
import com.me2me.user.dto.UserSignUpDto;
import com.me2me.user.model.UserGag;
import com.me2me.user.service.UserService;
import com.plusnet.sso.api.vo.JsonResult;


@Controller
@RequestMapping("/appuser")
public class AppUserController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);
	
	@Autowired
    private UserService userService;
	@Autowired
	private ActivityService activityService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private LocalJdbcDao localJdbcDao;
	@Autowired
	private FileTransferService fileTransferService;
	
	@RequestMapping(value = "/query")
	public ModelAndView query(AppUserQueryDTO dto) {
		ModelAndView view = new ModelAndView("appuser/userList");
		int vLv = -1;
		if(dto.getIsV() == 1){
			vLv = 1;
		}else if(dto.getIsV() == 2){
			vLv = 0;
		}
		
		int status = -1;
		if(dto.getStatus() == 1){
			status = 0;
		}else if(dto.getStatus() == 2){
			status = 1;
		}
		
		long meCode = 0;
		if(null != dto.getMeCode() && !"".equals(dto.getMeCode())){
			meCode = Long.valueOf(dto.getMeCode());
		}
		
		int excellent = -1;
		if(dto.getYunying() == 1){
			excellent = 1;
		}else if(dto.getYunying() == 2){
			excellent = 0;
		}
		
		StringBuilder sb = new StringBuilder();
    	sb.append("select p.avatar,u.create_time,p.gender,p.mobile,p.nick_name,p.third_part_bind,p.uid,p.v_lv,p.birthday,u.disable_user,n.me_number,p.level,p.excellent ");
    	sb.append("from user u,user_profile p,user_no n ");
    	sb.append("where u.uid=p.uid and u.uid=n.uid ");
    	if(StringUtils.isNotBlank(dto.getNickName())){
    		sb.append("and p.nick_name like '%").append(dto.getNickName()).append("%' ");
    	}
    	if(StringUtils.isNotBlank(dto.getMobile())){
    		sb.append("and p.mobile like '%").append(dto.getMobile()).append("%' ");
    	}
    	if(vLv >= 0){
    		sb.append("and p.v_lv=").append(vLv).append(" ");
    	}
    	if(status >= 0){
    		sb.append("and u.disable_user=").append(status).append(" ");
    	}
    	if(excellent >= 0){
    		sb.append("and p.excellent=").append(excellent).append(" ");
    	}
    	if(StringUtils.isNotBlank(dto.getStartTime())){
    		sb.append("and u.create_time>='").append(dto.getStartTime()).append("' ");
    	}
    	if(StringUtils.isNotBlank(dto.getEndTime())){
    		sb.append("and u.create_time<='").append(dto.getEndTime()).append("' ");
    	}
    	if(meCode > 0){
    		sb.append("and n.me_number=").append(meCode).append(" ");
    	}
    	sb.append("order by u.create_time desc limit 0,200");
		
		List<Map<String, Object>> list = localJdbcDao.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			AppUserQueryDTO.UserProfileElement e = null;
			for(Map<String, Object> u : list){
				e = new AppUserQueryDTO.UserProfileElement();
				e.setAvatar(Constant.QINIU_DOMAIN + "/" + (String)u.get("avatar"));
				e.setBirthday((String)u.get("birthday"));
				e.setCreateTime((Date)u.get("create_time"));
				e.setExcellent((Integer)u.get("excellent"));
				e.setGender((Integer)u.get("gender"));
				e.setLevel((Integer)u.get("level"));
				e.setMeCode((Long)u.get("me_number"));
				e.setMobile((String)u.get("mobile"));
				e.setNickName((String)u.get("nick_name"));
				e.setStatus((Integer)u.get("disable_user"));
				e.setThirdPartBind((String)u.get("third_part_bind"));
				e.setUid((Long)u.get("uid"));
				e.setVlv((Integer)u.get("v_lv"));
				dto.getResult().add(e);
			}
		}

		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value = "/userLevel/modify")
	@ResponseBody
	public String sendMsg(@RequestParam("u")long uid, @RequestParam("l")int level){
		String updateSql = "update user_profile set level=? where uid=?";
		localJdbcDao.executeSqlWithParams(updateSql, level, uid);
		return "0";
	}
	
	@RequestMapping(value="/option/vlv")
	@SystemControllerLog(description = "上大V或取消大V操作")
    public ModelAndView optionVlv(HttpServletRequest req){
    	int action = Integer.valueOf(req.getParameter("a"));
    	long uid = Long.valueOf(req.getParameter("i"));
    	String nickName = req.getParameter("m");
    	if(null == nickName){
    		nickName = "";
    	}
    	if(action == 1){
    		userService.optionV(1, uid);
    	}else{
    		userService.optionV(2, uid);
    	}
    	ModelAndView view = new ModelAndView("redirect:/appuser/query");
    	view.addObject("nickName",nickName);
    	return view;
    }
	
	@RequestMapping(value="/option/status")
	@SystemControllerLog(description = "禁止或恢复用户")
	public ModelAndView optionStatus(HttpServletRequest req){
		int action = Integer.valueOf(req.getParameter("a"));
    	long uid = Long.valueOf(req.getParameter("i"));
    	String nickName = req.getParameter("m");
    	if(null == nickName){
    		nickName = "";
    	}
    	if(action == 1){
    		userService.optionDisableUser(1, uid);
    	}else{
    		userService.optionDisableUser(2, uid);
    	}
    	ModelAndView view = new ModelAndView("redirect:/appuser/query");
    	view.addObject("nickName",nickName);
		return view;
	}
	
	@RequestMapping(value="/option/excellent")
	public ModelAndView optionExcellent(HttpServletRequest req){
		int action = Integer.valueOf(req.getParameter("a"));
    	long uid = Long.valueOf(req.getParameter("i"));
    	String nickName = req.getParameter("m");
    	if(null == nickName){
    		nickName = "";
    	}
    	String updateSql = "update user_profile set excellent=? where uid=?";
    	if(action == 1){//取消
    		localJdbcDao.executeSqlWithParams(updateSql, 0, uid);
    	}else{//设置
    		localJdbcDao.executeSqlWithParams(updateSql, 1, uid);
    	}
    	ModelAndView view = new ModelAndView("redirect:/appuser/query");
    	view.addObject("nickName",nickName);
		return view;
	}
	
	@RequestMapping(value="/createUser")
	@SystemControllerLog(description = "创建马甲号")
	public ModelAndView createUser(AppUserDTO dto){
		if(dto.getCount() > 0 && dto.getMobile() > 0 && StringUtils.isNotBlank(dto.getPwd())){
			UserSignUpDto userSignUpDto = new UserSignUpDto();
			userSignUpDto.setGender(0);//默认女的
			long m = dto.getMobile();
			for(int i=0;i<dto.getCount();i++){
				long mobile = m + i;
				userSignUpDto.setMobile(String.valueOf(mobile));
				userSignUpDto.setNickName(userSignUpDto.getMobile());
				userSignUpDto.setEncrypt(dto.getPwd());
				userService.signUp(userSignUpDto);
			}
		}
		ModelAndView view = new ModelAndView("redirect:/appuser/query");
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/gaguser/query")
	@SystemControllerLog(description = "APP禁言用户查询")
	public ModelAndView gagUserQuery(AppGagUserQueryDTO dto){
		long uid = 0;
		if(StringUtils.isNotBlank(dto.getUid())){
			uid = Long.valueOf(dto.getUid());
		}
		
		Response resp = userService.getGagUserPageByTargetUid(uid, 1, 200);
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowUsergagDto data = (ShowUsergagDto)resp.getData();
			if(null != data.getResult() && data.getResult().size() > 0){
				for(ShowUsergagDto.UsergagElement e : data.getResult()){
					if(e.getUid() == 0){
						e.setUserName("运营管理员");
					}
				}
			}
			
			dto.setData(data);
		}
		ModelAndView view = new ModelAndView("appuser/gagList");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value="/gaguser/add/{uid}")
	@SystemControllerLog(description = "添加APP禁言用户")
	public ModelAndView addGagUser(@PathVariable long uid){
		if(uid > 0){
			UserGag gag = new UserGag();
			gag.setCid(Long.valueOf(0));
			gag.setGagLevel(0);//后台设置的默认为全部
			gag.setTargetUid(uid);
			gag.setType(0);//后台设置的默认为全部
			gag.setUid(Long.valueOf(0));//默认运营管理员
			userService.addGagUser(gag);
		}
		
		ModelAndView view = new ModelAndView("redirect:/appuser/gaguser/query");
		return view;
	}
	
	@RequestMapping(value="/gaguser/remove/{gid}")
	@SystemControllerLog(description = "取消APP禁言用户")
	public ModelAndView delGagUser(@PathVariable long gid){
		if(gid > 0){
			userService.deleteGagUserById(gid);
		}
		
		ModelAndView view = new ModelAndView("redirect:/appuser/gaguser/query");
		return view;
	}
	
	@RequestMapping(value="/msgsender")
	public ModelAndView msgsender(){
		ModelAndView view = new ModelAndView("/appuser/smsConsole");
		return view;
	}
	
	@RequestMapping(value = "/sendMsg")
	@ResponseBody
	public String sendMsg(@RequestParam("id")String msgId, 
			@RequestParam("m")int mode, @RequestParam("ms")String mobiles){
		if(StringUtils.isBlank(msgId)){
			return "请输入正确的短信模板ID";
		}
		if(mode == 1){
			if(StringUtils.isBlank(mobiles)){
				return "请指定手机号";
			}
			logger.info("指定手机号发送，指定的手机号为：【"+mobiles+"】");
			String[] tmp = mobiles.split(",");
			if(null != tmp && tmp.length > 0){
				long total = 0;
				List<String> sendList = new ArrayList<String>();
				for(String t : tmp){
					if(StringUtils.isNotBlank(t)){
						total++;
						sendList.add(t);
						if(sendList.size() >= 180){
		    				smsService.send7dayCommon(msgId, sendList, null);
		                    logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
		                    sendList.clear();
		    			}
					}
				}
				if(sendList.size() > 0){
		    		smsService.send7dayCommon(msgId, sendList, null);
		            logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
		            sendList.clear();
		    	}
				logger.info("共["+total+"]个手机号发送了消息");
			}else{
				logger.info("没有手机号需要发送");
			}
		}else if(mode == 2){
			logger.info("全部注册手机号发送");
			//先获取所有手机用户手机号
	    	List<String> mobileList = activityService.getAllUserMobilesInApp();
	    	if(null == mobileList){
	    		mobileList = new ArrayList<String>();
	    	}
	    	logger.info("共["+mobileList.size()+"]个手机号待发送（这里包含马甲号，下面会去除）");
	    	
	    	long total = 0;
	    	List<String> sendList = new ArrayList<String>();
	    	for(String mobile : mobileList){
	    		if(checkMobile(mobile)){
	    			total++;
	    			sendList.add(mobile);
	    			if(sendList.size() >= 180){
	    				smsService.send7dayCommon(msgId, sendList, null);
	                    logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
	                    sendList.clear();
	    			}
	    		}
	    	}
	    	if(sendList.size() > 0){
	    		smsService.send7dayCommon(msgId, sendList, null);
	            logger.info("send [" + sendList.size() + "] user! total ["+total+"]");
	            sendList.clear();
	    	}
	    	logger.info("共["+total+"]个手机号发送了消息");
		}else{
			return "不支持的发送模式";
		}
		
		return "执行完成";
	}
	
	private boolean checkMobile(String mobile){
    	if(!StringUtils.isEmpty(mobile)){
    		if(!mobile.startsWith("100") && !mobile.startsWith("111")
    				&& !mobile.startsWith("123") && !mobile.startsWith("1666")
    				&& !mobile.startsWith("180000") && !mobile.startsWith("18888888")
    				&& !mobile.startsWith("18900") && !mobile.startsWith("19000")
    				&& !mobile.startsWith("2") && !mobile.startsWith("3")
    				&& !mobile.startsWith("4") && !mobile.startsWith("5")
    				&& !mobile.startsWith("6") && !mobile.startsWith("7")
    				&& !mobile.startsWith("8") && !mobile.startsWith("9")){
    			return true;
    		}
    	}
    	return false;
    }
	
	@RequestMapping(value = "/avatarFrame/list")
	public ModelAndView avatarFrameList(AvatarFrameQueryDTO dto) {
		ModelAndView view = new ModelAndView("appuser/avatarFrameList");
		
		StringBuilder sb = new StringBuilder();
		sb.append("select * from user_avatar_frame");
		if(StringUtils.isNotBlank(dto.getName())){
			sb.append(" where name like '%").append(dto.getName()).append("%'");
		}
		sb.append(" order by id desc");
		List<Map<String, Object>> list = this.localJdbcDao.queryEvery(sb.toString());
		if(null != list && list.size() > 0){
			List<String> afList = new ArrayList<String>();
			for(Map<String, Object> m : list){
				afList.add((String)m.get("avatar_frame"));
			}
			
			StringBuilder countSql = new StringBuilder();
			countSql.append("select u.avatar_frame,count(1) as cc ");
			countSql.append("from user_profile u where u.avatar_frame in (");
			for(int i=0;i<afList.size();i++){
				if(i>0){
					countSql.append(",");
				}
				countSql.append("'").append(afList.get(i)).append("'");
			}
			countSql.append(") group by u.avatar_frame");
			List<Map<String, Object>> countList = localJdbcDao.queryEvery(countSql.toString());
			Map<String, Long> countMap = new HashMap<String, Long>();
			if(null != countList && countList.size() > 0){
				for(Map<String, Object> n : countList){
					countMap.put((String)n.get("avatar_frame"), (Long)n.get("cc"));
				}
			}
			
			AvatarFrameQueryDTO.AvatarFrameItem item = null;
			for(Map<String, Object> m : list){
				item = new AvatarFrameQueryDTO.AvatarFrameItem();
				item.setId((Long)m.get("id"));
				item.setName((String)m.get("name"));
				item.setAvatarFrame((String)m.get("avatar_frame"));
				if(null != countMap.get((String)m.get("avatar_frame"))){
					item.setCount(countMap.get((String)m.get("avatar_frame")).intValue());
				}else{
					item.setCount(0);
				}
				dto.getResults().add(item);
			}
		}
		
		view.addObject("dataObj",dto);
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value = "/avatarFrame/uploadPic")
	public JsonResult uploadPic(@RequestParam("file")MultipartFile file,HttpServletRequest mrequest) throws Exception {
		try{
			if(file!=null && StringUtils.isNotEmpty(file.getOriginalFilename()) && file.getSize()>0){
				String imgName = SecurityUtils.md5(mrequest.getSession().getId()+System.currentTimeMillis()+RandomUtils.nextInt(1111111, 9999999), "1");
	    		fileTransferService.upload(file.getBytes(), imgName);
	    		String insertSql = "insert into user_avatar_frame(name,avatar_frame) values('',?)";
	    		localJdbcDao.executeSqlWithParams(insertSql, imgName);
	    		return JsonResult.success();
			}
		}catch(Exception e){
			return JsonResult.error("上传失败");
		}
		return JsonResult.error("上传失败");
	}
	
	@RequestMapping(value = "/avatarFrame/del/{id}")
	public ModelAndView delAvatarFrame(@PathVariable long id){
		String delSql = "delete from user_avatar_frame where id="+id;
		localJdbcDao.executeSql(delSql);
		
		ModelAndView view = new ModelAndView("redirect:/appuser/avatarFrame/list");
		return view;
	}
	
	@RequestMapping(value = "/avatarFrame/modifyName")
	@ResponseBody
	public String modifyAvatarFrameName(@RequestParam("i")long id, @RequestParam("n")String newName){
		String updateSql = "update user_avatar_frame set name=? where id=?";
		localJdbcDao.executeSqlWithParams(updateSql, newName, id);
		return "0";
	}
	
	@RequestMapping(value = "/avatarFrame/userList")
	public ModelAndView avatarFrameUserList(AvatarFrameUserQueryDTO dto){
		ModelAndView view = new ModelAndView("appuser/avatarFrameUserList");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = "/avatarFrame/userListPage")
	public AvatarFrameUserQueryDTO avatarFrameUserListPage(AvatarFrameUserQueryDTO dto){
		PageBean page= dto.toPageBean();
		String userSql = " from user_profile u where u.avatar_frame=?";
		
		String querySql = "select u.uid,u.nick_name,u.third_part_bind,u.mobile,u.create_time" + userSql + " order by u.id desc limit ?,?";
		String countSql = "select count(1)" + userSql;
		
		int count = localJdbcDao.queryForObject(countSql, Integer.class, dto.getAvatarFrame());
		List<Map<String, Object>> dataList = localJdbcDao.queryForList(querySql,dto.getAvatarFrame(),(page.getCurrentPage()-1)*page.getPageSize(),page.getPageSize());
		
		dto.setRecordsTotal(count);
		dto.setData(dataList);
		return dto;
	}
	
	@RequestMapping(value = "/invitation/list")
	public ModelAndView invitationList(UserInvitationQueryDTO dto) {
		ModelAndView view = new ModelAndView("appuser/userInvitation");
		view.addObject("dataObj",dto);
		
		if(StringUtils.isBlank(dto.getNickName()) && (null == dto.getUid() || dto.getUid().longValue() == 0)
				&& (null == dto.getMeNo() || dto.getMeNo().longValue() == 0)
				&& StringUtils.isBlank(dto.getMobile())){
			logger.info("用户条件必须填一个");
			return view;
		}
		
		StringBuilder userSql = new StringBuilder();
		userSql.append("select u.uid,u.nick_name,u.mobile,n.me_number from user_profile u,user_no n where u.uid=n.uid");
		if(StringUtils.isNotBlank(dto.getNickName())){
			userSql.append(" and u.nick_name like '%").append(dto.getNickName()).append("%'");
		}
		if(null != dto.getUid() && dto.getUid() > 0){
			userSql.append(" and u.uid=").append(dto.getUid());
		}
		if(null != dto.getMeNo() && dto.getMeNo() > 0){
			userSql.append(" and n.me_number='").append(dto.getMeNo()).append("'");
		}
		if(StringUtils.isNotBlank(dto.getMobile())){
			userSql.append(" and u.mobile='").append(dto.getMobile()).append("'");
		}
		List<Map<String, Object>> userList = localJdbcDao.queryEvery(userSql.toString());
		if(null != userList && userList.size() > 0){
			List<Long> uidList = new ArrayList<Long>();
			Long uid = null;
			for(Map<String, Object> u : userList){
				uid = (Long)u.get("uid");
				if(!uidList.contains(uid)){
					uidList.add(uid);
				}
			}
			
			StringBuilder countSql = new StringBuilder();
			countSql.append("select u.referee_uid,count(1) as totalCount,");
			countSql.append("count(if(u.channel='0' or u.platform=2,TRUE,NULL)) as iosCount,");
			countSql.append("count(if(u.channel!='0' or u.platform=1,TRUE,NULL)) as andriodCount");
			countSql.append(" from user_profile u where u.referee_uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					countSql.append(",");
				}
				countSql.append(uidList.get(i).toString());
			}
			countSql.append(") and u.is_activate=1");
			if(StringUtils.isNotBlank(dto.getStartTime())){
				countSql.append(" and u.create_time>='").append(dto.getStartTime()).append(" 00:00:00'");
			}
			if(StringUtils.isNotBlank(dto.getEndTime())){
				countSql.append(" and u.create_time<='").append(dto.getEndTime()).append(" 23:59:59'");
			}
			countSql.append(" group by u.referee_uid");
			List<Map<String, Object>> countList = localJdbcDao.queryEvery(countSql.toString());
			Map<String, Map<String, Object>> countMap = new HashMap<String, Map<String, Object>>();
			if(null != countList && countList.size() > 0){
				for(Map<String, Object> m : countList){
					countMap.put(String.valueOf(m.get("referee_uid")), m);
				}
			}
			
			Map<String, Object> count = null;
			UserInvitationQueryDTO.Item item = null;
			for(Map<String, Object> u : userList){
				item = new UserInvitationQueryDTO.Item();
				item.setUid((Long)u.get("uid"));
				item.setNichName((String)u.get("nick_name"));
				item.setMeNo((Long)u.get("me_number"));
				item.setMobile((String)u.get("mobile"));
				
				count = countMap.get(String.valueOf(item.getUid()));
				if(null != count){
					item.setTotalCount((Long)count.get("totalCount"));
					item.setIosCount((Long)count.get("iosCount"));
					item.setAndroidCount((Long)count.get("andriodCount"));
				}
				dto.getResults().add(item);
			}
		}
		
		return view;
	}
	
	@RequestMapping(value = "/invitation/detail")
	public ModelAndView invitationDetail(UserInvitationDetailQueryDTO dto){
		ModelAndView view = new ModelAndView("appuser/userInvitarionDetail");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = "/invitation/detailPage")
	public UserInvitationDetailQueryDTO invitationDetailPage(UserInvitationDetailQueryDTO dto){
		PageBean page= dto.toPageBean();
		StringBuilder userSql = new StringBuilder();
		userSql.append(" from user_profile u where u.is_activate=1");
		userSql.append(" and u.referee_uid=").append(dto.getRefereeUid());
		if(dto.getSearchType() == 1){//安卓
			userSql.append(" and (u.channel!='0' or u.platform=1)");
		}else if(dto.getSearchType() == 2){//IOS
			userSql.append(" and (u.channel='0' or u.platform=2)");
		}
		if(StringUtils.isNotBlank(dto.getStartTime())){
			userSql.append(" and u.create_time>='").append(dto.getStartTime()).append(" 00:00:00'");
		}
		if(StringUtils.isNotBlank(dto.getEndTime())){
			userSql.append(" and u.create_time<='").append(dto.getEndTime()).append(" 23:59:59'");
		}
		
		String querySql = "select u.uid,u.nick_name,u.third_part_bind,u.mobile,u.create_time" + userSql.toString() + " order by u.id desc limit ?,?";
		String countSql = "select count(1)" + userSql.toString();
		
		int count = localJdbcDao.queryForObject(countSql, Integer.class);
		List<Map<String, Object>> dataList = localJdbcDao.queryForList(querySql,(page.getCurrentPage()-1)*page.getPageSize(),page.getPageSize());
		if(null != dataList && dataList.size() > 0){
			List<Long> uidList = new ArrayList<Long>();
			Long uid = null;
			for(Map<String, Object> u : dataList){
				uid = (Long)u.get("uid");
				if(!uidList.contains(uid)){
					uidList.add(uid);
				}
			}
			//王国数
			StringBuilder kingSql = new StringBuilder();
			kingSql.append("select t.uid,count(1) as cc from topic t");
			kingSql.append(" where t.uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					kingSql.append(",");
				}
				kingSql.append(uidList.get(i).toString());
			}
			kingSql.append(") group by t.uid");
			Map<String, Long> kingCountMap = new HashMap<String, Long>();
			List<Map<String, Object>> kingList = localJdbcDao.queryForList(kingSql.toString());
			if(null != kingList && kingList.size() > 0){
				for(Map<String, Object> c : kingList){
					kingCountMap.put(String.valueOf(c.get("uid")), (Long)c.get("cc"));
				}
			}
			//发言数
			StringBuilder fragmentSql = new StringBuilder();
			fragmentSql.append("select f.uid,count(1) as cc from topic_fragment f");
			fragmentSql.append(" where f.status=1 and f.uid in (");
			for(int i=0;i<uidList.size();i++){
				if(i>0){
					fragmentSql.append(",");
				}
				fragmentSql.append(uidList.get(i).toString());
			}
			fragmentSql.append(") group by f.uid");
			Map<String, Long> fragmentCountMap = new HashMap<String, Long>();
			List<Map<String, Object>> fragmentList = localJdbcDao.queryForList(fragmentSql.toString());
			if(null != fragmentList && fragmentList.size() > 0){
				for(Map<String, Object> c : fragmentList){
					fragmentCountMap.put(String.valueOf(c.get("uid")), (Long)c.get("cc"));
				}
			}
			
			//加上两个数据
			for(Map<String, Object> d : dataList){
				uid = (Long)d.get("uid");
				d.put("ip", "");
				if(null != kingCountMap.get(uid.toString())){
					d.put("kingdomCount", kingCountMap.get(uid.toString()));
				}else{
					d.put("kingdomCount", 0);
				}
				if(null != fragmentCountMap.get(uid.toString())){
					d.put("speakCount", fragmentCountMap.get(uid.toString()));
				}else{
					d.put("speakCount", 0);
				}
			}
		}
		
		dto.setRecordsTotal(count);
		dto.setData(dataList);
		return dto;
	}
}
