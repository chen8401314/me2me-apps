package com.me2me.mgmt.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
		try {
			List<Map<String, Object>> list = localJdbcDao
					.queryEvery("select p.uid,p.nick_name,p.avatar from user u,user_profile p where u.uid = p.uid ");
			for (Map<String, Object> map : list) {
				String uid = map.get("uid").toString();
				String nickName = map.get("nick_name") == null ? "" : map.get("nick_name").toString();
				String avatar = map.get("avatar") == null ? ""
						: Constant.QINIU_DOMAIN + "/" + map.get("avatar").toString();
				smsService.refreshUser(uid, nickName, avatar);
			}
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
