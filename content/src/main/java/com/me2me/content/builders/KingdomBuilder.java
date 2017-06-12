package com.me2me.content.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.me2me.common.Constant;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dao.LiveForContentJdbcDao;
import com.me2me.content.dto.BasicKingdomInfo;
import com.me2me.content.model.Content;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

import lombok.extern.java.Log;

/**
 * 王国构造器，简化程序里的构建王国实体操作。
 * 
 * @author zhangjiwei
 * @date Jun 9, 2017
 */
@Log
@Component
public class KingdomBuilder {
	@Autowired
	private ContentMybatisDao contentMybatisDao;

	@Autowired
	private LiveForContentJdbcDao liveForContentJdbcDao;

	@Autowired
	private UserService userService;

	public List<BasicKingdomInfo> buildKingdomByIds(List<Long> kingdomIds, long currentUid) {
		List<Map<String, Object>> topicList = this.liveForContentJdbcDao.getTopicListByIds(kingdomIds);
		return this.buildKingdoms(topicList, currentUid);
	}
	/**
	 * 构造王国实体类。
	 * @author zhangjiwei
	 * @date Jun 9, 2017
	 * @param topicList
	 * @param currentUid
	 * @return
	 */
	public List<BasicKingdomInfo> buildKingdoms(List<Map<String, Object>> topicList, long currentUid) {
		List<Long> uidList = new ArrayList<>();
		List<Long> topicIdList = new ArrayList<>();
		Map<String, Object> liveFavouriteMap = new HashMap<>();
		Map<String, Content> topicContentMap = new HashMap<>();
		Map<String, Long> reviewCountMap = new HashMap<>();
		Map<String, Long> topicMemberCountMap = new HashMap<>();
		Map<String, String> topicTagMap = new HashMap<>();

		if (null != topicList && topicList.size() > 0) {
			Long uid = null;
			for (Map<String, Object> m : topicList) {
				uid = (Long) m.get("uid");
				if (!uidList.contains(uid)) {
					uidList.add(uid);
				}
				Long topicId = (Long) m.get("id");
				if (!topicIdList.contains(topicId)) {
					topicIdList.add(topicId);
				}
			}
		}
		List<Map<String, Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid,
				topicIdList);
		if (null != liveFavouriteList && liveFavouriteList.size() > 0) {
			for (Map<String, Object> lf : liveFavouriteList) {
				liveFavouriteMap.put(((Long) lf.get("topic_id")).toString(), "1");
			}
		}
		List<Content> topicContentList = contentMybatisDao.getContentByTopicIds(topicIdList);
		if (null != topicContentList && topicContentList.size() > 0) {
			for (Content c : topicContentList) {
				topicContentMap.put(c.getForwardCid().toString(), c);
			}
		}
		List<Map<String, Object>> tcList = liveForContentJdbcDao.getTopicUpdateCount(topicIdList);
		if (null != tcList && tcList.size() > 0) {
			for (Map<String, Object> m : tcList) {
				reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long) m.get("reviewCount"));
			}
		}
		topicMemberCountMap = liveForContentJdbcDao.getTopicMembersCount(topicIdList);
		List<Map<String, Object>> topicTagList = liveForContentJdbcDao.getTopicTagDetailListByTopicIds(topicIdList);
		if (null != topicTagList && topicTagList.size() > 0) {
			long tid = 0;
			String tags = null;
			Long topicId = null;
			for (Map<String, Object> ttd : topicTagList) {
				topicId = (Long) ttd.get("topic_id");
				if (topicId.longValue() != tid) {
					// 先插入上一次
					if (tid > 0 && !StringUtils.isEmpty(tags)) {
						topicTagMap.put(String.valueOf(tid), tags);
					}
					// 再初始化新的
					tid = topicId.longValue();
					tags = null;
				}
				if (tags != null) {
					tags = tags + ";" + (String) ttd.get("tag");
				} else {
					tags = (String) ttd.get("tag");
				}
			}
			if (tid > 0 && !StringUtils.isEmpty(tags)) {
				topicTagMap.put(String.valueOf(tid), tags);
			}
		}
		if (null == topicMemberCountMap) {
			topicMemberCountMap = new HashMap<String, Long>();
		}
		Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
		// 一次性查询关注信息
		Map<String, String> followMap = new HashMap<String, String>();
		if (uidList.size() > 0) {
			List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
			if (null != userList && userList.size() > 0) {
				for (UserProfile u : userList) {
					userMap.put(u.getUid().toString(), u);
				}
			}
			List<UserFollow> userFollowList = userService.getAllFollows(currentUid, uidList);
			if (null != userFollowList && userFollowList.size() > 0) {
				for (UserFollow uf : userFollowList) {
					followMap.put(uf.getSourceUid() + "_" + uf.getTargetUid(), "1");
				}
			}
		}
		List<BasicKingdomInfo> result = new ArrayList<>();
		Content topicContent = null;
		UserProfile userProfile = null;
		for (Map<String, Object> topic : topicList) {
			BasicKingdomInfo data = new BasicKingdomInfo();
			long topicId = (Long) topic.get("id");
			long uid = Long.valueOf(topic.get("uid").toString());
			data.setUid(uid);
			userProfile = userMap.get(String.valueOf(uid));
			if (null == userProfile) {
				log.info("用户[uid=" + uid + "]不存在");
				continue;
			}
			data.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
			data.setNickName(userProfile.getNickName());
			data.setV_lv(userProfile.getvLv());
			if (null != followMap.get(currentUid + "_" + uid)) {
				data.setIsFollowed(1);
			} else {
				data.setIsFollowed(0);
			}
			if (null != followMap.get(uid + "_" + currentUid)) {
				data.setIsFollowMe(1);
			} else {
				data.setIsFollowMe(0);
			}
			data.setContentType((Integer) topic.get("type"));
			if (null != liveFavouriteMap.get(String.valueOf(topicId))) {
				data.setFavorite(1);
			} else {
				data.setFavorite(0);
			}
			topicContent = topicContentMap.get(String.valueOf(topicId));
			if (null == topicContent) {
				continue;
			}
			data.setPrice((Integer) topic.get("price"));
			data.setId(topicContent.getId());
			data.setCid(topicContent.getId());
			data.setTopicId(topicId);
			data.setForwardCid(topicId);
			data.setTitle((String) topic.get("title"));
			data.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String) topic.get("live_image"));
			data.setInternalStatus(getInternalStatus(topic, currentUid));
			if (null != topicMemberCountMap.get(String.valueOf(topicId))) {
				data.setFavoriteCount(topicMemberCountMap.get(String.valueOf(topicId)).intValue() + 1);
			} else {
				data.setFavoriteCount(1);
			}
			data.setReadCount(topicContent.getReadCountDummy());
			data.setLikeCount(topicContent.getLikeCount());
			if (null != reviewCountMap.get(String.valueOf(topicId))) {
				data.setReviewCount(reviewCountMap.get(String.valueOf(topicId)).intValue());
			} else {
				data.setReviewCount(0);
			}
			if (null != topicTagMap.get(String.valueOf(topicId))) {
				data.setTags(topicTagMap.get(String.valueOf(topicId)));
			} else {
				data.setTags("");
			}
			result.add(data);
		}
		return result;
	}

	// 判断核心圈身份
	private int getInternalStatus(Map<String, Object> topic, long uid) {
		int internalStatus = 0;
		String coreCircle = (String) topic.get("core_circle");
		if (null != coreCircle) {
			JSONArray array = JSON.parseArray(coreCircle);
			for (int i = 0; i < array.size(); i++) {
				if (array.getLong(i) == uid) {
					internalStatus = Specification.SnsCircle.CORE.index;
					break;
				}
			}
		}

		// if (internalStatus == 0) {
		// internalStatus = userService.getUserInternalStatus(uid,
		// (Long)topic.get("uid"));
		// }

		return internalStatus;
	}
}
