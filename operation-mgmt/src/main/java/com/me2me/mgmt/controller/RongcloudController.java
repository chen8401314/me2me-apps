package com.me2me.mgmt.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.common.Constant;
import com.me2me.mgmt.dao.LocalJdbcDao;
import com.me2me.sms.service.SmsService;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

@Controller
@RequestMapping("/rongcloud")
public class RongcloudController {

	private static final Logger logger = LoggerFactory.getLogger(RongcloudController.class);

	@Autowired
	private LocalJdbcDao localJdbcDao;

	@Autowired
	private SmsService smsService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/rongcloudSet")
	public String rongcloudSet(HttpServletRequest request) throws Exception {
		return "rongcloud/rongcloud";
	}

	@RequestMapping(value = "/refreshAllUser")
	@ResponseBody
	public String refreshAllUser(HttpServletRequest mrequest) throws Exception {
		logger.info("开始全量刷新用户私信信息");
		long s = System.currentTimeMillis();
		try {
			String countSql = "select count(1) as cc from user_profile";
			List<Map<String, Object>> countList = localJdbcDao.queryEvery(countSql);
			int total = 0;
			if(null != countList && countList.size() > 0){
				Map<String, Object> count = countList.get(0);
				total = ((Long)count.get("cc")).intValue();
			}
			logger.info("共["+total+"]个用户需要刷新");
			
			String sql = "select p.uid,p.nick_name,p.avatar from user_profile p";
			int start = 0;
			int batch = 500;
			int left = total;
			String uid = null;
			String nickName = null;
			String avatar = null;
			while(start < total){
				List<Map<String, Object>> list = localJdbcDao.queryEvery(sql + " order by p.uid limit " + start + "," + batch);
				for(Map<String, Object> u : list){
					uid = u.get("uid").toString();
					nickName = u.get("nick_name") == null ? "" : u.get("nick_name").toString();
					avatar = u.get("avatar") == null ? "" : Constant.QINIU_DOMAIN + "/" + u.get("avatar").toString();
					smsService.refreshUser(uid, nickName, avatar);
				}
				left = left - list.size();
				start = start + batch;
				logger.info("本次共处理了["+list.size()+"]个用户，还剩["+left+"]个用户");
			}
			long e = System.currentTimeMillis();
			logger.info("全量刷新用户私信信息结束，共耗时["+(e-s)/1000+"]秒");
			return "1";
		} catch (Exception e) {
			logger.error("refreshAllUser执行失败", e);
			return "0";
		}
	}

	@RequestMapping(value = "/refreshUser")
	@ResponseBody
	public String refreshUser(long uid, HttpServletRequest mrequest) throws Exception {
		try {
			UserProfile userProfile = userService.getUserProfileByUid(uid);
			if (userProfile == null) {
				logger.info("用户["+uid+"]不存在");
				return "-1";
			} else {
				String uidStr = uid + "";
				String nickName = userProfile.getNickName() == null ? "" : userProfile.getNickName();
				String avatar = userProfile.getAvatar() == null ? "": Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar();
				smsService.refreshUser(uidStr, nickName, avatar);
				return "1";
			}
		} catch (Exception e) {
			logger.error("refreshUser执行失败", e);
			return "0";
		}
	}
}
