package com.me2me.content.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.me2me.common.Constant;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dao.LiveForContentJdbcDao;
import com.me2me.content.dto.BasicKingdomInfo;
import com.me2me.content.dto.NewKingdom;
import com.me2me.content.dto.ShowAttentionDto;
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
	private JdbcTemplate jdbc;
	
	@Autowired
	private UserService userService;

	public List<BasicKingdomInfo> buildKingdomByIds(List<Long> kingdomIds, long currentUid) {
		List<Map<String, Object>> topicList = this.liveForContentJdbcDao.getTopicListByIds(kingdomIds);
		return this.buildKingdoms(topicList, currentUid);
	}
	/**
	 * 上市王国信息
	 * @author zhangjiwei
	 * @date Sep 13, 2017
	 * @param topicList
	 * @return
	 */
	public List<BasicKingdomInfo> buildListingKingdoms(List<Map<String,Object>> topicList, long currentUid){
		if(topicList==null || topicList.isEmpty()){
			return new ArrayList<>();
		}
		
		List<Long> topicIdList = new ArrayList<Long>();
		Long tid = null;
		for(Map<String,Object> m : topicList){
			tid = (Long)m.get("id");
			if(!topicIdList.contains(tid)){
				topicIdList.add(tid);
			}
		}
		
		Map<String, String> liveFavouriteMap = new HashMap<String, String>();
		List<Map<String, Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
		if (null != liveFavouriteList && liveFavouriteList.size() > 0) {
			for (Map<String, Object> lf : liveFavouriteList) {
				liveFavouriteMap.put(((Long) lf.get("topic_id")).toString(), "1");
			}
		}
		
		
		double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));
		double minRmb =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_RMB_BRAND_MIN"));
		List<BasicKingdomInfo> result = new ArrayList<>();
		for (Map<String, Object> topic : topicList) {
			BasicKingdomInfo data = new BasicKingdomInfo();
			long topicId = (Long) topic.get("id");
			long uid = Long.valueOf(topic.get("uid").toString());
			data.setUid(uid);
			data.setPrice((Integer) topic.get("price"));
			data.setPriceRMB(exchangeKingdomPrice(data.getPrice()));
			data.setTopicId(topicId);
			data.setForwardCid(topicId);
			data.setTitle((String) topic.get("title"));
			data.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String) topic.get("live_image"));
			boolean isJoin = false;
			if (null != liveFavouriteMap.get(String.valueOf(topicId))) {
				isJoin = true;
			}
			data.setInternalStatus(getInternalStatus(topic, currentUid, isJoin));
			data.setShowPriceBrand(data.getPrice()!=null && data.getPrice()>=minPrice?1:0);
			data.setShowRMBBrand(data.getPriceRMB()>=minRmb?1:0);// 显示吊牌
			result.add(data);
		}
		return result;
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
		if(topicList==null || topicList.isEmpty()){
			return new ArrayList<>();
		}
		List<Long> uidList = new ArrayList<>();
		List<Long> topicIdList = new ArrayList<>();
		Map<String, Object> liveFavouriteMap = new HashMap<>();
		Map<String, Content> topicContentMap = new HashMap<>();
		Map<String, Long> reviewCountMap = new HashMap<>();
		Map<String, Long> topicMemberCountMap = new HashMap<>();
		Map<String, String> topicTagMap = new HashMap<>();
		double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));
		double minRmb =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_RMB_BRAND_MIN"));

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
			data.setLevel(userProfile.getLevel());
			if(!StringUtils.isEmpty(userProfile.getAvatarFrame())){
				data.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatarFrame());
			}
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
			boolean isJoin = false;
			if (null != liveFavouriteMap.get(String.valueOf(topicId))) {
				data.setFavorite(1);
				isJoin = true;
			} else {
				data.setFavorite(0);
			}
			topicContent = topicContentMap.get(String.valueOf(topicId));
			if (null == topicContent) {
				continue;
			}
			data.setType(topicContent.getType());
			data.setPrice((Integer) topic.get("price"));
			data.setPriceRMB(exchangeKingdomPrice(data.getPrice()));
			data.setId(topicContent.getId());
			data.setCid(topicContent.getId());
			data.setTopicId(topicId);
			data.setForwardCid(topicId);
			data.setTitle((String) topic.get("title"));
			data.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String) topic.get("live_image"));
			
			data.setInternalStatus(getInternalStatus(topic, currentUid, isJoin));
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
			data.setShowPriceBrand(data.getPrice()!=null && data.getPrice()>=minPrice?1:0);
			data.setShowRMBBrand(data.getPriceRMB()>=minRmb?1:0);// 显示吊牌
			result.add(data);
		}
		return result;
	}
	
	/**
	 * 米汤币兑换人名币
	 * @param price
	 * @return
	 */
	public double exchangeKingdomPrice(int price) {
		String  result =  userService.getAppConfigByKey("EXCHANGE_RATE");
		if(StringUtils.isEmpty(result)){
			return 0;
		}
		BigDecimal exchangeRate = new BigDecimal(result);
		return new BigDecimal(price).divide(exchangeRate, 2, RoundingMode.HALF_UP).doubleValue();
	}
	// 判断核心圈身份
	private int getInternalStatus(Map<String, Object> topic, long uid, boolean isJoin) {
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
		
		if(internalStatus == 0 && isJoin){
			internalStatus = 1;
		}

		return internalStatus;
	}
	//showType 1RMB,2米汤币
	public List<NewKingdom> buildFullNewKingdom(long currentUid, List<Map<String, Object>> topicList, int showType) {
		if(topicList==null || topicList.isEmpty()){
			return new ArrayList<>();
		}
		List<Long> uidList = new ArrayList<>();
		List<Long> topicIdList = new ArrayList<>();
		Map<String, Object> liveFavouriteMap = new HashMap<>();
		Map<String, Content> topicContentMap = new HashMap<>();
		Map<String, Long> reviewCountMap = new HashMap<>();
		Map<String, Long> topicMemberCountMap = new HashMap<>();
		Map<String, String> topicTagMap = new HashMap<>();
		double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));
		double minRmb =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_RMB_BRAND_MIN"));

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
		
		//一次性获取王国的外露内容
		Map<String, List<Map<String, Object>>> topicOutDataMap = new HashMap<String, List<Map<String, Object>>>();
	    String v = userService.getAppConfigByKey("KINGDOM_OUT_MINUTE");
	    int limitMinute = 3;
	    if(!StringUtils.isEmpty(v)){
	    	limitMinute = Integer.valueOf(v).intValue();
	    }
	    List<Map<String,Object>> topicOutList = liveForContentJdbcDao.getOutFragments(topicIdList, limitMinute);
	    if(null != topicOutList && topicOutList.size() > 0){
	    	Long topicId = null;
	    	List<Map<String, Object>> toList = null;
	    	Long atUid = null;
	    	Long fragmentUid = null;
	    	for(Map<String,Object> m : topicOutList){
	    		topicId = (Long)m.get("topic_id");
	    		toList = topicOutDataMap.get(topicId.toString());
	    		if(null == toList){
	    			toList = new ArrayList<Map<String, Object>>();
	    			topicOutDataMap.put(topicId.toString(), toList);
	    		}
	    		toList.add(m);
	    		atUid = (Long)m.get("at_uid");
	    		if(null != atUid && atUid.longValue() > 0){
	    			if(!uidList.contains(atUid)){
	                    uidList.add(atUid);
	                }
	    		}
	    		fragmentUid = (Long)m.get("uid");
	    		if(null != fragmentUid && fragmentUid.longValue() > 0){
	    			if(!uidList.contains(fragmentUid)){
	                    uidList.add(fragmentUid);
	                }
	    		}
	    	}
	    }
		
		List<Map<String, Object>> liveFavouriteList = liveForContentJdbcDao.getLiveFavoritesByUidAndTopicIds(currentUid, topicIdList);
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
		List<NewKingdom> result = new ArrayList<>();
		Content topicContent = null;
		UserProfile userProfile = null;
		// 查询所有的分类信息
		List<Map<String,Object>> typeList = jdbc.queryForList("select * from topic_category");
		Map<Integer,Map<String,Object>> typeMap = new HashMap<>();
		for(Map<String,Object> type:typeList){
			int id = Integer.parseInt(type.get("id").toString());
			typeMap.put(id, type);
		}
		
		List<Map<String, Object>> topicOutDataList = null;
		Map<String, Object> topicOutData = null;
		UserProfile lastUserProfile = null;
		ShowAttentionDto.OutDataElement outElement = null;
		for (Map<String, Object> topic : topicList) {
			NewKingdom data = new NewKingdom();
			long topicId = (Long) topic.get("id");
			long uid = Long.valueOf(topic.get("uid").toString());
			data.setUid(uid);
			userProfile = userMap.get(String.valueOf(uid));
			if (null == userProfile) {
				log.info("用户[uid=" + uid + "]不存在");
				continue;
			}
			// 分类信息
			int categoryId = topic.get("category_id")==null?0:Integer.parseInt(topic.get("category_id")+"");
			Map<String,Object> typeinfo= typeMap.get(categoryId);
			if(typeinfo!=null){
				data.setKcName(typeinfo.get("name").toString());
				data.setKcIcon(Constant.QINIU_DOMAIN+"/"+typeinfo.get("icon"));
				data.setKcid(Integer.parseInt(typeinfo.get("id")+""));
			}
			data.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
			data.setNickName(userProfile.getNickName());
			data.setVip(userProfile.getvLv());
			data.setLevel(userProfile.getLevel());
			if(!StringUtils.isEmpty(userProfile.getAvatarFrame())){
				data.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatarFrame());
			}
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
			boolean isJoin = false;
			data.setContentType((Integer) topic.get("type"));
			if (null != liveFavouriteMap.get(String.valueOf(topicId))) {
				data.setFavorite(1);
				isJoin  = true;
			} else {
				data.setFavorite(0);
			}
			//王国可见类型
			if((int)topic.get("rights")==Specification.KingdomRights.PRIVATE_KINGDOM.index){
				data.setRights(Specification.KingdomRights.PRIVATE_KINGDOM.index);
				//当前用户是否可见
				int internalStatus = getInternalStatus(topic, currentUid, isJoin);
				if(internalStatus==Specification.SnsCircle.CORE.index){
					data.setCanView(Specification.CanViewStatus.CAN_VIEW.index);
				}else{
					data.setCanView(Specification.CanViewStatus.NOT_CAN_VIEW.index);
				}
			}else{
				data.setRights(Specification.KingdomRights.PUBLIC_KINGDOM.index);
				data.setCanView(Specification.CanViewStatus.NOT_CAN_VIEW.index);
			}
			
			topicContent = topicContentMap.get(String.valueOf(topicId));
			if (null == topicContent) {
				continue;
			}
			
			data.setType(topicContent.getType());
			data.setPrice((Integer) topic.get("price"));
			data.setPriceRMB(exchangeKingdomPrice(data.getPrice()));
			data.setCid(topicContent.getId());
			data.setTopicId(topicId);
			data.setTitle((String) topic.get("title"));
			data.setUpdateTime(((Date)topic.get("update_time")).getTime());
			data.setCreateTime(((Date)topic.get("create_time")).getTime());
			
			data.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String) topic.get("live_image"));
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
			if(showType == 2){//米汤币
				data.setShowPriceBrand(data.getPrice()>=minPrice?1:0);
			}else if(showType == 1){//RMB
				data.setShowRMBBrand(data.getPriceRMB()>=minRmb?1:0);// 显示吊牌
			}
			//增加王国外露内容
        	topicOutDataList = topicOutDataMap.get(String.valueOf(topicId));
        	if(null != topicOutDataList && topicOutDataList.size() > 0){
        		//先判断是否UGC
        		//第一个如果是UGC则其他的不要了，如果不是，则后面如果有UGC则不要了
        		topicOutData = topicOutDataList.get(0);
        		lastUserProfile = userMap.get(String.valueOf(topicOutData.get("uid")));
        		if(null != lastUserProfile){//这里放上最近发言的那个人的头像
        			data.setUid(lastUserProfile.getUid());
        			data.setNickName(lastUserProfile.getNickName());
        			data.setVip(lastUserProfile.getvLv());
        			data.setLevel(lastUserProfile.getLevel());
        			data.setAvatar(Constant.QINIU_DOMAIN + "/" + lastUserProfile.getAvatar());
        			if(!StringUtils.isEmpty(lastUserProfile.getAvatarFrame())){
        				data.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + lastUserProfile.getAvatarFrame());
        			}else{
        				data.setAvatarFrame(null);
        			}
        			if(null != followMap.get(currentUid+"_"+lastUserProfile.getUid())){
        				data.setIsFollowed(1);
                    }else{
                    	data.setIsFollowed(0);
                    }
                    if(null != followMap.get(lastUserProfile.getUid()+"_"+currentUid)){
                    	data.setIsFollowMe(1);
                    }else{
                    	data.setIsFollowMe(0);
                    }
        		}
        		int t = ((Integer)topicOutData.get("type")).intValue();
        		int contentType = ((Integer)topicOutData.get("content_type")).intValue();
        		if((t == 0 || t == 52) && contentType == 23){//第一个是UGC
        			outElement = new ShowAttentionDto.OutDataElement();
        			outElement.setId((Long)topicOutData.get("id"));
        			outElement.setType((Integer)topicOutData.get("type"));
        			outElement.setContentType((Integer)topicOutData.get("content_type"));
        			outElement.setFragment((String)topicOutData.get("fragment"));
        			String fragmentImage = (String)topicOutData.get("fragment_image");
                    if (!StringUtils.isEmpty(fragmentImage)) {
                    	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                    }
        			outElement.setAtUid((Long)topicOutData.get("at_uid"));
        			outElement.setExtra((String)topicOutData.get("extra"));
        			data.getUgcData().add(outElement);
        		}else{//第一个不是UGC
        			for(int j=0;j<topicOutDataList.size();j++){
        				topicOutData = topicOutDataList.get(j);
        				t = ((Integer)topicOutData.get("type")).intValue();
        				contentType = ((Integer)topicOutData.get("content_type")).intValue();
        				if((t == 0 || t == 52) && contentType == 23){//UGC不要了
        					continue;
        				}else if((t == 0 || t == 55 || t==52) && contentType == 0){//文本
        					if(data.getTextData().size() == 0){
        						outElement = new ShowAttentionDto.OutDataElement();
                    			outElement.setId((Long)topicOutData.get("id"));
                    			outElement.setType((Integer)topicOutData.get("type"));
                    			outElement.setContentType((Integer)topicOutData.get("content_type"));
                    			outElement.setFragment((String)topicOutData.get("fragment"));
                    			String fragmentImage = (String)topicOutData.get("fragment_image");
                                if (!StringUtils.isEmpty(fragmentImage)) {
                                	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                }
                    			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                    			outElement.setExtra((String)topicOutData.get("extra"));
                    			data.getTextData().add(outElement);
        					}
        				}else if(t==13 || (t == 55 && contentType == 63)){//音频
        					if(data.getAudioData().size() == 0){
        						outElement = new ShowAttentionDto.OutDataElement();
                    			outElement.setId((Long)topicOutData.get("id"));
                    			outElement.setType((Integer)topicOutData.get("type"));
                    			outElement.setContentType((Integer)topicOutData.get("content_type"));
                    			outElement.setFragment((String)topicOutData.get("fragment"));
                    			String fragmentImage = (String)topicOutData.get("fragment_image");
                                if (!StringUtils.isEmpty(fragmentImage)) {
                                	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                }
                    			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                    			outElement.setExtra((String)topicOutData.get("extra"));
                    			data.getAudioData().add(outElement);
        					}
        				}else{//图片区展示部分
        					if(data.getImageData().size() < 3){
        						outElement = new ShowAttentionDto.OutDataElement();
                    			outElement.setId((Long)topicOutData.get("id"));
                    			outElement.setType((Integer)topicOutData.get("type"));
                    			outElement.setContentType((Integer)topicOutData.get("content_type"));
                    			outElement.setFragment((String)topicOutData.get("fragment"));
                    			String fragmentImage = (String)topicOutData.get("fragment_image");
                                if (!StringUtils.isEmpty(fragmentImage)) {
                                	outElement.setFragmentImage(Constant.QINIU_DOMAIN + "/" + fragmentImage);
                                }
                    			outElement.setAtUid((Long)topicOutData.get("at_uid"));
                    			outElement.setExtra((String)topicOutData.get("extra"));
                    			data.getImageData().add(outElement);
        					}
        				}
        			}
        		}
        	}
			result.add(data);
		}
		return result;
	}
	public List<NewKingdom> buildSimpleNewKingdom(long currentUid, List<Map<String, Object>> topicList) {
		if(topicList==null || topicList.isEmpty()){
			return new ArrayList<>();
		}
		
		double minPrice =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_PRICE_BRAND_MIN"));
		double minRmb =Double.parseDouble((String) userService.getAppConfigByKey("KINGDOM_SHOW_RMB_BRAND_MIN"));
		List<NewKingdom> result = new ArrayList<>();
		for (Map<String, Object> topic : topicList) {
			NewKingdom data = new NewKingdom();
			long topicId = (Long) topic.get("id");
			long uid = Long.valueOf(topic.get("uid").toString());
			data.setUid(uid);
			data.setPrice((Integer) topic.get("price"));
			data.setPriceRMB(exchangeKingdomPrice(data.getPrice()));
			data.setTopicId(topicId);
			data.setTitle((String) topic.get("title"));
			data.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String) topic.get("live_image"));
			data.setShowPriceBrand(data.getPrice()>=minPrice?1:0);
			data.setShowRMBBrand(data.getPriceRMB()>=minRmb?1:0);// 显示吊牌
			data.setUpdateTime(((Date)topic.get("update_time")).getTime());
			data.setCreateTime(((Date)topic.get("create_time")).getTime());
			result.add(data);
		}
		return result;
	}
}
