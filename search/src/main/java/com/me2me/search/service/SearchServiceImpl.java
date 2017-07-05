package com.me2me.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.me2me.cache.CacheConstant;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.io.dto.ShowRecContentDTO;
import com.me2me.io.service.FileTransferService;
import com.me2me.search.dao.ContentForSearchJdbcDao;
import com.me2me.search.dao.SearchMybatisDao;
import com.me2me.search.dto.RecommendKingdom;
import com.me2me.search.dto.RecommendKingdomDto;
import com.me2me.search.dto.RecommendUser;
import com.me2me.search.dto.RecommendUserDto;
import com.me2me.search.dto.RecommendListDto;
import com.me2me.search.dto.RecommendTagDto;
import com.me2me.search.dto.ShowAssociatedWordDTO;
import com.me2me.search.dto.ShowRecWordDTO;
import com.me2me.search.dto.ShowSearchDTO;
import com.me2me.search.enums.RecommendReason;
import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UgcEsMapping;
import com.me2me.search.esmapping.UserEsMapping;
import com.me2me.user.model.AppConfig;
import com.me2me.user.model.EmotionInfo;
import com.me2me.user.model.EmotionRecord;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserProfile;
import com.me2me.search.mapper.SearchHotKeywordMapper;
import com.me2me.search.mapper.SearchMapper;
import com.me2me.search.model.SearchHotKeyword;
import com.me2me.search.model.SearchHotKeywordExample;
import com.me2me.search.model.SearchUserDislike;
import com.me2me.user.service.UserService;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
	
    @Autowired
    private UserService userService;
    
    @Autowired
    private ContentSearchService searchService;
    
    @Autowired
    private FileTransferService fileTransferService;
    
    @Autowired
    private ContentForSearchJdbcDao contentForSearchJdbcDao;

    @Autowired
    private SearchHotKeywordMapper hotkeywordMapper;
    
    @Autowired
    private SearchMybatisDao searchMybatisDao;
    @Autowired
	private SearchMapper searchMapper;
    @Autowired
    private CacheService cacheService;
    
    @Override
    public Response search(String keyword,int page,int pageSize,long uid,int isSearchFans) {
        if(isSearchFans == Specification.SearchType.FANS.index){
            return userService.searchFans(keyword, page, pageSize, uid);
        }else {
            return userService.search(keyword, page, pageSize, uid);
        }
    }

    @Override
    public Response assistant(String keyword) {
        return userService.assistant(keyword);
    }

    @Override
    public Response associatedWord(String keyword){
    	List<String> list = searchService.associateKeywordList(keyword, 10);
    	
    	ShowAssociatedWordDTO resultDTO = new ShowAssociatedWordDTO();
    	for(String k : list){
    		ShowAssociatedWordDTO.WordElement e = null;
    		if(!StringUtils.isEmpty(k)){
    			e = new ShowAssociatedWordDTO.WordElement();
    			e.setSearchWords(k);
    			resultDTO.getResult().add(e);
    		}
    	}
    	
    	return Response.success(resultDTO);
    }
    
    @Override
    public Response allSearch(long uid, String keyword, int searchType, int contentType, int page, int pageSize){
    	//首先记录搜索词
    	if(!StringUtils.isEmpty(keyword)){
    		searchService.addSearchHistory(keyword);
    		keyword = QueryParser.escape(keyword);//将一些不可预见的特殊字符都转义一下
    	}
    	
    	ShowSearchDTO resultDTO = new ShowSearchDTO();
    	
    	FacetedPage<UgcEsMapping> ugcPage = null;
    	FacetedPage<TopicEsMapping> kingdomPage = null;
    	FacetedPage<UserEsMapping> userPage = null;
    	try{
	    	if(searchType == 0){//搜索全部，则返回UGC3个，王国3个，人3个
	    		if(page <= 1){
		    		ugcPage = searchService.queryUGC(keyword, 1, 20);
		    		kingdomPage = searchService.queryKingdom(keyword, -1, 1, 20);
		    		userPage = searchService.queryUsers(keyword, 1, 20);
	    		}
	    	}else if(searchType == 1){//用户
	    		userPage = searchService.queryUsers(keyword, page, pageSize);
	    		if(null != userPage){
		    		resultDTO.setTotalPage(userPage.getTotalPages());
		    		resultDTO.setTotalRecord(userPage.getTotalElements());
	    		}
	    	}else if(searchType == 2){//王国
	    		int searchKingdomType = -1;
	    		if(contentType == 1){
	    			searchKingdomType = 0;
	    		}else if(contentType == 2){
	    			searchKingdomType = 1000;
	    		}
	    		kingdomPage = searchService.queryKingdom(keyword, searchKingdomType, page, pageSize);
	    		if(null != kingdomPage){
	    			resultDTO.setTotalPage(kingdomPage.getTotalPages());
		    		resultDTO.setTotalRecord(kingdomPage.getTotalElements());
	    		}
	    	}else if(searchType == 3){//UGC
	    		ugcPage = searchService.queryUGC(keyword, page, pageSize);
	    		if(null != ugcPage){
	    			resultDTO.setTotalPage(ugcPage.getTotalPages());
		    		resultDTO.setTotalRecord(ugcPage.getTotalElements());
	    		}
	    	}
    	}catch(Exception e){
    		log.error("检索异常", e);
    	}
    	
    	if(null != ugcPage){
    		this.buildUgcSearchResult(uid, resultDTO, ugcPage, searchType);
    	}
    	
    	if(null != kingdomPage){
    		this.buildKingdomSearchResult(uid, resultDTO, kingdomPage, searchType);
    	}
    	
    	if(null != userPage){
    		this.buildUserSearchResult(uid, resultDTO, userPage, searchType);
    	}
    	//System.out.println("search all, uid:"+uid+",keyword:"+keyword+",searchType:"+searchType+",contentType:"+contentType+",page:"+page+",pageSize:"+pageSize+",ret:\n"+JSON.toJSONString(resultDTO, true));
    	return Response.success(resultDTO);
    }
    
    private void buildUgcSearchResult(long uid, ShowSearchDTO resultDTO, FacetedPage<UgcEsMapping> ugcPage, int searchType){
    	if(null == ugcPage){
    		return;
    	}
    	List<Long> cidList = new ArrayList<Long>();
		List<Long> uidList = new ArrayList<Long>();
    	if(null != ugcPage){
    		for(UgcEsMapping ugc : ugcPage){
    			cidList.add(ugc.getId());
    		}
    	}
    	Map<String, Map<String, Object>> contentMap = new HashMap<String, Map<String, Object>>();
    	if(cidList.size() > 0){
    		List<Map<String, Object>> contentList = contentForSearchJdbcDao.getUGCContentByIds(cidList);
    		if(null != contentList && contentList.size() > 0){
    			for(Map<String, Object> m : contentList){
    				contentMap.put(String.valueOf(m.get("id")), m);
    				uidList.add((Long)m.get("uid"));
    			}
    		}
    	}
    	Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
    	//一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
    	if(uidList.size() > 0){
    		List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
    		if(null != userList && userList.size() > 0){
    			for(UserProfile u : userList){
    				userMap.put(u.getUid().toString(), u);
    			}
    		}
    		List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
            if(null != userFollowList && userFollowList.size() > 0){
            	for(UserFollow uf : userFollowList){
            		followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            	}
            }
    	}
		
		ShowSearchDTO.UgcElement ugcElement = null;
		Map<String, Object> content = null;
		UserProfile userProfile = null;
		int i = 0;
		for(UgcEsMapping ugc : ugcPage){
			ugcElement = new ShowSearchDTO.UgcElement();
			content = contentMap.get(ugc.getId().toString());
			if(null == content){
				continue;
			}
			userProfile = userMap.get(String.valueOf(content.get("uid")));
			if(null == userProfile){
				continue;
			}
			ugcElement.setUid(userProfile.getUid());
			ugcElement.setNickName(userProfile.getNickName());
			ugcElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
			ugcElement.setV_lv(userProfile.getvLv());
			ugcElement.setLevel(userProfile.getLevel());
			if(null != followMap.get(uid+"_"+userProfile.getUid())){
				ugcElement.setIsFollowed(1);
			}else{
				ugcElement.setIsFollowed(0);
			}
			if(null != followMap.get(userProfile.getUid()+"_"+uid)){
				ugcElement.setIsFollowMe(1);
			}else{
				ugcElement.setIsFollowMe(0);
			}
			ugcElement.setCid((Long)content.get("id"));
			ugcElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)content.get("conver_image"));
			ugcElement.setContent((String)content.get("content"));
			ugcElement.setTitle((String)content.get("title"));
			ugcElement.setType(0);
			ugcElement.setReadCount((Integer)content.get("read_count_dummy"));
			ugcElement.setReviewCount((Integer)content.get("review_count"));
			resultDTO.getUgcData().add(ugcElement);
			i++;
			if(searchType == 0 && i>= 3){
				break;
			}
		}
    }
    
    private void buildKingdomSearchResult(long uid, ShowSearchDTO resultDTO, FacetedPage<TopicEsMapping> kingdomPage, int searchType){
    	if(null == kingdomPage){
    		return;
    	}
    	
    	List<Long> tidList = new ArrayList<Long>();
		List<Long> ceTidList = new ArrayList<Long>();
		for(TopicEsMapping topic : kingdomPage){
			tidList.add(topic.getId());
		}
		List<Long> uidList = new ArrayList<Long>();
		Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> topicContentMap = new HashMap<String, Map<String, Object>>();
		//一次性查询王国订阅信息
        Map<String, String> liveFavouriteMap = new HashMap<String, String>();
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = contentForSearchJdbcDao.getTopicMembersCount(tidList);
        if(null == topicMemberCountMap){
        	topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询王国的标签信息
        Map<String, String> topicTagMap = new HashMap<String, String>();
		if(tidList.size() > 0){
			List<Map<String, Object>> topicList = contentForSearchJdbcDao.getTopicByIds(tidList);
			if(null != topicList && topicList.size() > 0){
				Long topicUid = null;
				for(Map<String, Object> m : topicList){
					topicMap.put(String.valueOf(m.get("id")), m);
					topicUid = (Long)m.get("uid");
					if(!uidList.contains(topicUid)){
						uidList.add(topicUid);
					}
					if(((Integer)m.get("type")).intValue() == 1000){//聚合王国
						ceTidList.add((Long)m.get("id"));
					}
				}
			}
			List<Map<String, Object>> topicContentList = contentForSearchJdbcDao.getTopicContentByTopicIds(tidList);
			if(null != topicContentList && topicContentList.size() > 0){
				for(Map<String, Object> m : topicContentList){
					topicContentMap.put(String.valueOf(m.get("forward_cid")), m);
				}
			}
			List<Map<String,Object>> liveFavouriteList = contentForSearchJdbcDao.getLiveFavoritesByUidAndTopicIds(uid, tidList);
            if(null != liveFavouriteList && liveFavouriteList.size() > 0){
            	for(Map<String,Object> lf : liveFavouriteList){
            		liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
            	}
            }
            List<Map<String, Object>> tcList = contentForSearchJdbcDao.getTopicUpdateCount(tidList);
            if(null != tcList && tcList.size() > 0){
            	for(Map<String, Object> m : tcList){
            		topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
            		reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            	}
            }
            List<Map<String, Object>> topicTagList = contentForSearchJdbcDao.getTopicTagDetailListByTopicIds(tidList);
            if(null != topicTagList && topicTagList.size() > 0){
            	long tid = 0;
            	String tags = null;
            	Long topicId = null;
            	for(Map<String, Object> ttd : topicTagList){
            		topicId = (Long)ttd.get("topic_id");
            		if(topicId.longValue() != tid){
            			//先插入上一次
            			if(tid > 0 && !StringUtils.isEmpty(tags)){
            				topicTagMap.put(String.valueOf(tid), tags);
            			}
            			//再初始化新的
            			tid = topicId.longValue();
            			tags = null;
            		}
            		if(tags != null){
            			tags = tags + ";" + (String)ttd.get("tag");
            		}else{
            			tags = (String)ttd.get("tag");
            		}
            	}
            	if(tid > 0 && !StringUtils.isEmpty(tags)){
            		topicTagMap.put(String.valueOf(tid), tags);
            	}
            }
		}
		Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTidList.size() > 0){
        	List<Map<String,Object>> acCountList = contentForSearchJdbcDao.getTopicAggregationAcCountByTopicIds(ceTidList);
        	if(null != acCountList && acCountList.size() > 0){
        		for(Map<String,Object> a : acCountList){
        			acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
        		}
        	}
        }
		Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
		//一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
		if(uidList.size() > 0){
			List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
			if(null != userList && userList.size() > 0){
				for(UserProfile u : userList){
					userMap.put(u.getUid().toString(), u);
				}
			}
			List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
            if(null != userFollowList && userFollowList.size() > 0){
            	for(UserFollow uf : userFollowList){
            		followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            	}
            }
		}
		
		Map<String, Object> topic = null;
		Map<String, Object> topicContent = null;
		UserProfile userProfile = null;
		ShowSearchDTO.KingdomElement kingdomElement = null;
		int i = 0;
		for(TopicEsMapping t : kingdomPage){
			kingdomElement = new ShowSearchDTO.KingdomElement();
			topic = topicMap.get(t.getId().toString());
			if(null == topic){
				continue;
			}
			topicContent = topicContentMap.get(t.getId().toString());
			if(null == topicContent){
				continue;
			}
			userProfile = userMap.get(String.valueOf(topic.get("uid")));
			if(null == userProfile){
				continue;
			}
			kingdomElement.setPrice((Integer)topic.get("price"));
			kingdomElement.setUid(userProfile.getUid());
			kingdomElement.setNickName(userProfile.getNickName());
			kingdomElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
			kingdomElement.setV_lv(userProfile.getvLv());
			kingdomElement.setLevel(userProfile.getLevel());
			if(null != followMap.get(uid+"_"+userProfile.getUid())){
				kingdomElement.setIsFollowed(1);
			}else{
				kingdomElement.setIsFollowed(0);
			}
			if(null != followMap.get(userProfile.getUid()+"_"+uid)){
				kingdomElement.setIsFollowMe(1);
			}else{
				kingdomElement.setIsFollowMe(0);
			}
			kingdomElement.setTopicId((Long)topic.get("id"));
			kingdomElement.setTitle((String)topic.get("title"));
			kingdomElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
			kingdomElement.setType(3);
			kingdomElement.setContentType((Integer)topic.get("type"));
			if(null != topic.get("create_time")){
				kingdomElement.setCreateTime(((Date)topic.get("create_time")).getTime());
			}
			kingdomElement.setUpdateTime(((Date)topic.get("update_time")).getTime());
			kingdomElement.setLastUpdateTime((Long)topic.get("long_time"));
			kingdomElement.setInternalStatus(this.getInternalStatus((String)topic.get("core_circle"), uid));
			kingdomElement.setCid((Long)topicContent.get("id"));
			kingdomElement.setLikeCount((Integer)topicContent.get("like_count"));
			kingdomElement.setReadCount((Integer)topicContent.get("read_count_dummy"));
			if(null != liveFavouriteMap.get(t.getId().toString())){
				kingdomElement.setFavorite(1);
			}else{
				kingdomElement.setFavorite(0);
			}
			if(null != topicCountMap.get(t.getId().toString())){
				kingdomElement.setTopicCount(topicCountMap.get(t.getId().toString()));
			}else{
				kingdomElement.setTopicCount(0);
			}
			if(null != reviewCountMap.get(t.getId().toString())){
				kingdomElement.setReviewCount(reviewCountMap.get(t.getId().toString()));
			}else{
				kingdomElement.setReviewCount(0);
			}
			if(null == topicMemberCountMap.get(t.getId().toString())){
				kingdomElement.setFavoriteCount(1);//默认只有国王一个成员
			}else{
				kingdomElement.setFavoriteCount(topicMemberCountMap.get(t.getId().toString()).intValue()+1);
			}
			if(kingdomElement.getContentType() == 1000){//聚合王国需要返回子王国数
				if(null != acCountMap.get(t.getId().toString())){
					kingdomElement.setAcCount(acCountMap.get(t.getId().toString()).intValue());
				}else{
					kingdomElement.setAcCount(0);
				}
			}
			if(null != topicTagMap.get(t.getId().toString())){
				kingdomElement.setTags(topicTagMap.get(t.getId().toString()));
            }else{
            	kingdomElement.setTags("");
            }
			resultDTO.getKingdomData().add(kingdomElement);
			i++;
			if(searchType == 0 && i >= 3){
				break;
			}
		}
    }
    
    private void buildUserSearchResult(long uid, ShowSearchDTO resultDTO, FacetedPage<UserEsMapping> userPage, int searchType){
    	if(null == userPage){
    		return;
    	}
    	List<Long> uidList = new ArrayList<Long>();
    	for(UserEsMapping user : userPage){
    		uidList.add(user.getUid());
    	}
    	Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
		//一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
		if(uidList.size() > 0){
			List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
			if(null != userList && userList.size() > 0){
				for(UserProfile u : userList){
					userMap.put(u.getUid().toString(), u);
				}
			}
			List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
            if(null != userFollowList && userFollowList.size() > 0){
            	for(UserFollow uf : userFollowList){
            		followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            	}
            }
		}
    	
		UserProfile userProfile = null;
    	ShowSearchDTO.UserElement userElement = null;
    	int i=0;
    	for(UserEsMapping user : userPage){
    		userElement = new ShowSearchDTO.UserElement();
    		userProfile = userMap.get(user.getUid().toString());
    		if(null == userProfile){
    			continue;
    		}
    		userElement.setUid(userProfile.getUid());
    		userElement.setNickName(userProfile.getNickName());
    		userElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
    		userElement.setV_lv(userProfile.getvLv());
    		userElement.setLevel(userProfile.getLevel());
    		userElement.setIntroduced(userProfile.getIntroduced());
    		if(null != followMap.get(uid+"_"+userProfile.getUid())){
    			userElement.setIsFollowed(1);
			}else{
				userElement.setIsFollowed(0);
			}
			if(null != followMap.get(userProfile.getUid()+"_"+uid)){
				userElement.setIsFollowMe(1);
			}else{
				userElement.setIsFollowMe(0);
			}
			resultDTO.getUserData().add(userElement);
			i++;
			if(searchType == 0 && i >= 3){
				break;
			}
    	}
    }
    
    //判断核心圈身份
    private int getInternalStatus(String coreCircle, long uid) {
    	int internalStatus = 0;
        if(!StringUtils.isEmpty(coreCircle)){
        	JSONArray array = JSON.parseArray(coreCircle);
        	for (int i = 0; i < array.size(); i++) {
                if (array.getLong(i) == uid) {
                    internalStatus = Specification.SnsCircle.CORE.index;
                    break;
                }
            }
        }
        return internalStatus;
    }
    
    @Override
    public Response recWord(){
    	ShowRecWordDTO result = new ShowRecWordDTO();
    	List<SearchHotKeyword> list = searchMybatisDao.getRecWords(8);
    	if(null != list && list.size() > 0){
    		ShowRecWordDTO.WordElement e = null;
    		for(SearchHotKeyword w : list){
    			if(!StringUtils.isEmpty(w.getKeyword())){
    				e = new ShowRecWordDTO.WordElement();
    				e.setWord(w.getKeyword());
    				result.getWordData().add(e);
    			}
    		}
    	}
    	
    	return Response.success(result);
    }
	
	@Override
	public int indexUserData(boolean fully) throws Exception {
		return searchService.indexUserData(fully);
	}

	@Override
	public int indexUgcData(boolean fully) throws Exception {
		return searchService.indexUgcData(fully);
	}

	@Override
	public int indexKingdomData(boolean fully) throws Exception {
		return searchService.indexKingdomData(fully);
	}

	@Override
	public int indexSearchHistory(boolean fully) throws Exception {
		return searchService.indexSearchHistory(fully);
	}
	
	public String searchForJSON(String key,String type, int contentType, int page,int pageSize){
		FacetedPage pagedata =null;
		
		if("ugc".equals(type)){
			pagedata = searchService.queryUGC(key, page, pageSize);
		}else if("kingdom".equals(type)){
			pagedata = searchService.queryKingdom(key, contentType, page, pageSize);
		}else if("user".equals(type)){
			pagedata = searchService.queryUsers(key, page, pageSize);
		}
		String str = JSON.toJSONString(pagedata);
		//System.out.println("search for json, key:"+key+",type:"+type+",contentType:"+contentType+",page:"+page+",pageSize:"+pageSize+",ret:\n"+str);
		return str;
	}

	@Override
	public List<SearchHotKeyword> getAllHotKeyword() {
		SearchHotKeywordExample example = new SearchHotKeywordExample();
		example.createCriteria().andIsValidEqualTo(1);
		example.setOrderByClause("order_num asc,id desc");
		return hotkeywordMapper.selectByExample(example);
	}

	@Override
	public SearchHotKeyword getHotKeywordById(int id) {
		return hotkeywordMapper.selectByPrimaryKey(id);
	}

	@Override
	public void addHotKeyword(SearchHotKeyword hk) {
		hotkeywordMapper.insertSelective(hk);
	}

	@Override
	public void updateHotKeyword(SearchHotKeyword hk) {
		hotkeywordMapper.updateByPrimaryKeySelective(hk);
	}

	@Override
	public void delHotKeyword(int id) {
		hotkeywordMapper.deleteByPrimaryKey(id);
	}

	public Response recommendUser(long uid,int page,int pageSize){
		UserProfile profile = userService.getUserProfileByUid(uid);
		String hobby = StringUtils.join(searchMapper.getUserHobbyIds(uid),",");
		int completion = this.getPersonaCompleted(profile, hobby);
		
		List<RecommendUser> resultpage = null;
		if(completion >= 10){
			//查出我已关注过的，这些是不能反回的
			List<Long> myFollowUidList = userService.getFollowList(uid);
			resultpage = this.searchService.getRecommendUserList(uid, page, pageSize, myFollowUidList);
		}
		
		if(null == resultpage){
			resultpage = new ArrayList<RecommendUser>();
		}
		if(page == 1 && resultpage.size() == 0){//第一页就没有数据的话，默认返回“米汤管家”这个用户
			UserProfile user = userService.getUserByNickName("米汤管家");
			if(null != user){
				RecommendUser ruser = new RecommendUser();
				ruser.setUid(user.getUid());
				ruser.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
				ruser.setNickName(user.getNickName());
				ruser.setV_lv(user.getvLv());
				ruser.setLevel(user.getLevel());
				ruser.setReason("");
				ruser.setTagMatchedLength(0);
				ruser.setUserTags(new ArrayList<String>());
				resultpage.add(ruser);
			}
		}
		
		RecommendUserDto dto = new RecommendUserDto();
		dto.setRecUserData(resultpage);
		return Response.success(dto);
	}

	/**
	 * 计算用户画像完成度
	 * @return
	 */
	private int getPersonaCompleted(UserProfile profile, String hobby){
		int result = 0;
		//头像
		if(!StringUtils.isEmpty(profile.getAvatar()) && !StringUtils.equals("default.png", profile.getAvatar())){
			result = result + 5;
		}
		//爱好
		if(!StringUtils.isEmpty(hobby)){
			result = result + 5;
		}
		//性取向
		if(profile.getLikeGender() != null && profile.getLikeGender().intValue() > 0){
			result = result + 5;
		}
		//年龄段
		if(profile.getAgeGroup()!=null && profile.getAgeGroup().intValue()>0){
			result = result + 5;
		}
		//职业
		if(profile.getOccupation()!=null && profile.getOccupation().intValue()>0){
			result = result + 5;
		}
		//性别
		if(profile.getGender()!=null){
			result = result + 5;
		}
		//MBTI人格
		if(!StringUtils.isEmpty(profile.getMbti())){
			result = result + 5;
		}
		//用户情绪
		int userEmotionCount = contentForSearchJdbcDao.countUserEmotions(profile.getUid(), 0);
		if(userEmotionCount > 0){
			int e = 10 + userEmotionCount - 1;
			if(e > 30){
				e = 30;
			}
			result = result + e;
		}
		
		return result;
	}
	
	private static Map<String, String> mbtiDic = new HashMap<String, String>() {
		{
			put("INTJ", "建筑师");
			put("INTP", "逻辑学家");
			put("ENTJ", "指挥官");
			put("ENTP", "辩论家");
			put("INFJ", "提倡者");
			put("INFP", "调停者");
			put("ENFJ", "主人公");
			put("ENFP", "竞选者");
			put("ISTJ", "物流师");
			put("ISFJ", "守卫者");
			put("ESTJ", "总经理");
			put("ESFJ", "执政官");
			put("ISTP", "鉴赏家");
			put("ISFP", "探险家");
			put("ESTP", "企业家");
			put("ESFP", "表演者");
		}
	};
	
	
	public Response recommendIndex(long uid,int page, String token, String version){
		RecommendListDto indexData = new RecommendListDto();
		UserProfile profile = userService.getUserProfileByUid(uid);
		List<Long> hobbyIds = searchMapper.getUserHobbyIds(uid);
		String hobby = StringUtils.join(hobbyIds,",");
		//画像完成度
		int completion = this.getPersonaCompleted(profile, hobby);
		UserProfile meAdmin = null;
		if(page == 1){//第一页需要额外返回用户画像，用户推荐
			//查用户画像信息
			RecommendListDto.RecPerson person = new RecommendListDto.RecPerson();
			indexData.setPersona(person);
			person.setUid(uid);
			person.setNickName(profile.getNickName());
			person.setAvatar(Constant.QINIU_DOMAIN + "/" + profile.getAvatar());
			person.setV_lv(profile.getvLv());
			person.setLevel(profile.getLevel());
			person.setSex(profile.getGender());
			person.setHobby(hobby);
			if(null != profile.getAgeGroup()){
				person.setAgeGroup(profile.getAgeGroup());
			}
			if(null != profile.getOccupation()){
				person.setCareer(profile.getOccupation());
			}
			if(null != profile.getLikeGender()){
				person.setSexOrientation(profile.getLikeGender());
			}
			person.setCompletion(completion);
			
			String cnMbti = mbtiDic.get(profile.getMbti());
			if(cnMbti!=null){
				cnMbti=profile.getMbti()+"\n"+cnMbti;
			}
			person.setMbti(CommonUtils.toUsefulString(cnMbti));
			
			EmotionInfo firstUserEmotionInfo = null;
			List<EmotionRecord> erList = userService.getUserEmotionRecords(uid, 20);
			if(null != erList && erList.size() > 0){
				List<Long> emotionIdList = new ArrayList<Long>();
				for(EmotionRecord er : erList){
					if(!emotionIdList.contains(er.getEmotionid())){
						emotionIdList.add(er.getEmotionid());
					}
				}
				Map<String, EmotionInfo> emotionInfoMap = new HashMap<String, EmotionInfo>();
				Map<String, Map<String, Object>> bigEmotionMap = new HashMap<String, Map<String, Object>>();
				if(emotionIdList.size() > 0){
					List<EmotionInfo> eInfoList = userService.getEmotionInfosByIds(emotionIdList);
					if(null != eInfoList && eInfoList.size() > 0){
						List<Long> bigEmotionids = new ArrayList<Long>();
						for(EmotionInfo eInfo : eInfoList){
							emotionInfoMap.put(eInfo.getId().toString(), eInfo);
							if(!bigEmotionids.contains(eInfo.getEmotionpackid())){
								bigEmotionids.add(eInfo.getEmotionpackid());
							}
						}
						if(bigEmotionids.size() > 0){
							List<Map<String, Object>> bigEmotionList = contentForSearchJdbcDao.getEmotionsByIds(bigEmotionids);
							if(null != bigEmotionList && bigEmotionList.size() > 0){
								for(Map<String, Object> m : bigEmotionList){
									bigEmotionMap.put(m.get("id").toString(), m);
								}
							}
						}
					}
				}
				
				//查询用户的情绪王国
				Map<String, Object> userEmotionKingdom = contentForSearchJdbcDao.getUserEmotionKingdom(uid);
				
				int totalUserEmotionCount = contentForSearchJdbcDao.countUserEmotions(uid, 0);
				
				RecommendListDto.UserEmotion userEmotion = null;
				EmotionInfo emotionInfo = null;
				Map<String, Object> bigEmotion = null;
				RecommendListDto.EmotionPackage emotionPackage = null;
				Date now = new Date();
				for(EmotionRecord er : erList){
					userEmotion = new RecommendListDto.UserEmotion();
//					userEmotion.setId(er.getId());
					userEmotion.setRid(er.getId());//表情设定记录ID
					userEmotion.setCreateTime(er.getCreateTime().getTime());
					long timeInterval = (now.getTime()-er.getCreateTime().getTime())/1000;
					userEmotion.setTimeInterval(timeInterval);
					userEmotion.setFreeValue(er.getFreevalue());
					userEmotion.setHappyValue(er.getHappyvalue());
					
					emotionInfo = emotionInfoMap.get(er.getEmotionid().toString());
					if(null == firstUserEmotionInfo){//只取第一个
						firstUserEmotionInfo = emotionInfo;
					}
					if(null != emotionInfo){
						userEmotion.setId(emotionInfo.getId());//坐标表情ID
						userEmotion.setEmotionName(emotionInfo.getEmotionname());
						bigEmotion = bigEmotionMap.get(emotionInfo.getEmotionpackid().toString());
						if(null != bigEmotion){
							emotionPackage = new RecommendListDto.EmotionPackage();
							emotionPackage.setContent((String)bigEmotion.get("extra"));
							emotionPackage.setEmojiType(1);//默认大表情
							emotionPackage.setExtra((String)bigEmotion.get("extra"));
							emotionPackage.setH((Integer)bigEmotion.get("h"));
							emotionPackage.setId((Integer)bigEmotion.get("id"));
							emotionPackage.setImage(Constant.QINIU_DOMAIN + "/" + (String)bigEmotion.get("image"));
							emotionPackage.setThumb(Constant.QINIU_DOMAIN + "/" + (String)bigEmotion.get("thumb"));
							emotionPackage.setThumb_h((Integer)bigEmotion.get("thumb_h"));
							emotionPackage.setThumb_w((Integer)bigEmotion.get("thumb_w"));
							emotionPackage.setTitle((String)bigEmotion.get("title"));
							emotionPackage.setW((Integer)bigEmotion.get("w"));
							userEmotion.setEmotionPack(emotionPackage);
						}
					}
					userEmotion.setRecordCount(totalUserEmotionCount);
					if(null != userEmotionKingdom){
						userEmotion.setTopicId((Long)userEmotionKingdom.get("id"));
						userEmotion.setInternalStatus(Specification.SnsCircle.CORE.index);//自己的肯定是核心圈
					}
					indexData.getPersona().getEmotionList().add(userEmotion);
				}
			}
			indexData.setPersona(person);
			
			//增加用户最近一次设置的情绪对应的公共王国属性
			if(null != firstUserEmotionInfo){
				Map<String, Object> topic = contentForSearchJdbcDao.getTopicById(firstUserEmotionInfo.getTopicid());
				if(null != topic){
					RecommendListDto.EmotionKingdom emotionKingdom = new RecommendListDto.EmotionKingdom();
					emotionKingdom.setContentType((Integer)topic.get("type"));
					emotionKingdom.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
					emotionKingdom.setInternalStatus(this.getInternalStatus((String)topic.get("core_circle"), uid));
					emotionKingdom.setTitle((String)topic.get("title"));
					emotionKingdom.setTopicId(firstUserEmotionInfo.getTopicid());
					indexData.setEmotionKingdom(emotionKingdom);
				}
			}
			
			List<RecommendUser> resultpage = null;
			if(completion >= 10){//完成度大于等于10的才有只能推荐（包括推人和推内容）
				// 查推荐用户
				//变更逻辑，按匹配度进行计算
				//1.最大值为99%
				//2.基本资料匹配44%，心理测试对应22%，最近3次情绪状态匹配33%
				List<Long> myFollowUidList = userService.getFollowList(uid);
//				resultpage = this.searchService.getRecommendUserList(uid, 1, 30, myFollowUidList);
				//第二套算法
				resultpage = this.userRecList(profile, hobbyIds, firstUserEmotionInfo, myFollowUidList, 30);
				if(null == resultpage){
					resultpage = new ArrayList<RecommendUser>();
				}
				
				if(resultpage.size() > 1){
					Collections.sort(resultpage, new Comparator<RecommendUser>() {
			            public int compare(RecommendUser a, RecommendUser b) {
			                if(a.getMatching() > b.getMatching()){
			                	return -1;
			                }else if(a.getMatching() == b.getMatching()){
			                	return 0;
			                }else{
			                	return 1;
			                }
			            }
			        });
				}
			}else{
				resultpage = new ArrayList<RecommendUser>();
				meAdmin = userService.getUserByNickName("米汤管家");
				if(null != meAdmin){
					RecommendUser ruser = new RecommendUser();
					ruser.setUid(meAdmin.getUid());
					ruser.setAvatar(Constant.QINIU_DOMAIN + "/" + meAdmin.getAvatar());
					ruser.setNickName(meAdmin.getNickName());
					ruser.setV_lv(meAdmin.getvLv());
					ruser.setLevel(meAdmin.getLevel());
					ruser.setReason("米汤官方推荐");
					ruser.setTagMatchedLength(0);
					ruser.setUserTags(new ArrayList<String>());
					resultpage.add(ruser);
				}
			}
			if(null != resultpage && resultpage.size() > 0){
				indexData.getRecUserData().addAll(resultpage);
			}
		}
		
		if(completion >= 10){//完成度大于等于10的才有只能推荐（包括推人和推内容）
			// 查内容推荐（取10条王国数据，再取10条文章数据）
			List<Long> dislistTopicIds = new ArrayList<Long>();
			List<SearchUserDislike> dislikeList = searchMybatisDao.getSearchUserDislikesByUidsAndType(uid, 3);
			if(null != dislikeList && dislikeList.size() > 0){
				for(SearchUserDislike sud : dislikeList){
					dislistTopicIds.add(sud.getCid());
				}
			}
			List<TopicEsMapping> kingdoms = this.searchService.getTopicEsMappingList(uid, dislistTopicIds, page, 10);
			this.builderRecKingdomInfo(indexData, kingdoms, uid);
			//再取10条文章
			ShowRecContentDTO recContent = fileTransferService.getRecContents(String.valueOf(uid), token, version, "");
			if(null != recContent && "0".equals(recContent.getResultCode()) 
					&& null != recContent.getContents() && recContent.getContents().size() > 0){
				RecommendListDto.ContentData contentData = null;
				for(ShowRecContentDTO.RecContentElement rc : recContent.getContents()){
					if(rc.getContentType() > 1){//只要文章和音乐
						continue;
					}
					contentData = new RecommendListDto.ContentData();
					contentData.setType(5);//文章
					contentData.setContentType(rc.getContentType());
					contentData.setContentId(rc.getContentId());
					contentData.setTitle(rc.getTitle());
					contentData.setLinkUrl(rc.getLinkUrl());
					contentData.setCoverImage(rc.getCoverImage());
					contentData.setUpdateTime(rc.getUpdateTime());
					contentData.setReason("智能推荐");
					contentData.setReadCount(rc.getReadCount());
					contentData.setLikeCount(rc.getLikeCount());
					contentData.setReviewCount(rc.getReviewCount());
					indexData.getRecContentData().add(contentData);
				}
			}
			
			if(indexData.getRecContentData().size() > 1){
				Collections.shuffle(indexData.getRecContentData());
			}
		}else{
			if(page == 1){//不满10%的只有第一页才有一个置顶的王国
				Map<String, Object> meTopic = null;
				if(null != meAdmin){
					meTopic = contentForSearchJdbcDao.getTopicByUidAndTitle(meAdmin.getUid(), "米汤官方发言处");
				}
				Map<String, Object> topicContent = null;
				if(null != meTopic){
					topicContent = contentForSearchJdbcDao.getTopicContentByTopicId((Long)meTopic.get("id"));
				}
				if(null != topicContent){
					RecommendListDto.ContentData kingdomElement = new RecommendListDto.ContentData();
					kingdomElement.setUid(meAdmin.getUid());
					kingdomElement.setNickName(meAdmin.getNickName());
					kingdomElement.setAvatar(Constant.QINIU_DOMAIN + "/" + meAdmin.getAvatar());
					kingdomElement.setV_lv(meAdmin.getvLv());
					kingdomElement.setLevel(meAdmin.getLevel());
					kingdomElement.setIsFollowed(userService.isFollow(meAdmin.getUid(), uid));
					kingdomElement.setIsFollowMe(userService.isFollow(uid, meAdmin.getUid()));
					kingdomElement.setTopicId((Long)meTopic.get("id"));
					kingdomElement.setTitle((String)meTopic.get("title"));
					kingdomElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)meTopic.get("live_image"));
					kingdomElement.setType(3);
					kingdomElement.setContentType((Integer)meTopic.get("type"));
					if(null != meTopic.get("create_time")){
						kingdomElement.setCreateTime(((Date)meTopic.get("create_time")).getTime());
					}
					kingdomElement.setUpdateTime(((Date)meTopic.get("update_time")).getTime());
					kingdomElement.setLastUpdateTime((Long)meTopic.get("long_time"));
					kingdomElement.setInternalStatus(this.getInternalStatus((String)meTopic.get("core_circle"), uid));
					kingdomElement.setCid((Long)topicContent.get("id"));
					kingdomElement.setLikeCount((Integer)topicContent.get("like_count"));
					kingdomElement.setReadCount((Integer)topicContent.get("read_count_dummy"));
					if(contentForSearchJdbcDao.isFavoriteTopic(uid, kingdomElement.getTopicId())){
						kingdomElement.setFavorite(1);
					}else{
						kingdomElement.setFavorite(0);
					}
					List<Long> tidList = new ArrayList<Long>();
					tidList.add(kingdomElement.getTopicId());
					List<Map<String,Object>> countList = contentForSearchJdbcDao.getTopicUpdateCount(tidList);
					if(null != countList && countList.size() > 0){
						Map<String,Object> count = countList.get(0);
						kingdomElement.setTopicCount(((Long)count.get("topicCount")).intValue());
						kingdomElement.setReviewCount(((Long)count.get("reviewCount")).intValue());
					}else{
						kingdomElement.setTopicCount(0);
						kingdomElement.setReviewCount(0);
					}
					kingdomElement.setFavoriteCount(contentForSearchJdbcDao.getSingleTopicMemberCount(kingdomElement.getTopicId()) + 1);
					if(kingdomElement.getContentType() == 1000){//聚合王国需要返回子王国数
						kingdomElement.setAcCount(contentForSearchJdbcDao.getSingleTopicAggregationAcCount(kingdomElement.getTopicId()));
					}
					List<String> topicTags = contentForSearchJdbcDao.getSingleTopicTag(kingdomElement.getTopicId());
					if(null != topicTags && topicTags.size() > 0){
						String tags = "";
						for(String s : topicTags){
							tags = tags + ";" + s;
						}
						kingdomElement.setTags(tags.substring(1));
					}else{
						kingdomElement.setTags("");
					}
					kingdomElement.setReason("米汤官方推荐");
					indexData.getRecContentData().add(kingdomElement);
				}
			}
		}
		
		if(null != indexData.getPersona() && null != indexData.getPersona().getEmotionList()
				&& indexData.getPersona().getEmotionList().size() > 0){
			RecommendListDto.UserEmotion ue = indexData.getPersona().getEmotionList().get(0);
			long timeInterval = (new Date().getTime()-ue.getCreateTime())/1000;
			ue.setTimeInterval(timeInterval);
		}
		
		return Response.success(indexData);
	}
	
	private List<RecommendUser> userRecList(UserProfile userProfile, List<Long> hobbyIds, EmotionInfo firstUserEmotionInfo, List<Long> noUidList, int pageSize){
		if(null == noUidList){
			noUidList = new ArrayList<Long>();
		}
		noUidList.add(userProfile.getUid());//将自己也放进去
		//先从缓存列表中查看是否有记录
		List<RecommendUser> result = new ArrayList<RecommendUser>();
		
		String recDate = cacheService.get(CacheConstant.USER_REC_DATE_KEY);
		if(StringUtils.isEmpty(recDate)){
			recDate = DateUtil.date2string(new Date(), "yyyyMMdd");
		}
		String cacheKey = CacheConstant.USER_REC_DAY_LIST_KEY_PRE + userProfile.getUid().toString()+":"+recDate;
		String recUsers = cacheService.get(cacheKey);
		if(!StringUtils.isEmpty(recUsers)){//缓存中有
			String[] tmp = recUsers.split(";");
			String[] tmp2 = null;
			RecommendUser rUser = null;
			for(String t : tmp){
				tmp2 = t.split(",");
				if(tmp2.length == 2){
					if(noUidList.contains(Long.valueOf(tmp2[0]))){
						continue;
					}
					rUser = new RecommendUser();
					rUser.setUid(Long.valueOf(tmp2[0]));
					rUser.setMatching(Integer.valueOf(tmp2[1]));
					result.add(rUser);
				}
			}
			
			if(result.size() > pageSize){
				Collections.shuffle(result);
				result = result.subList(0, pageSize);
			}
			
			//补充其他用户信息
			if(result.size() > 0){
				List<Long> uidList = new ArrayList<Long>();
				for(RecommendUser ru : result){
					uidList.add(Long.valueOf(ru.getUid()));
				}
				List<Map<String, Object>> userProfileList = contentForSearchJdbcDao.getUserProfileInfoByUids(uidList);
				Map<String, Map<String, Object>> userMap = new HashMap<String, Map<String, Object>>();
				if(null != userProfileList && userProfileList.size() > 0){
					for(Map<String, Object> u : userProfileList){
						userMap.put(((Long)u.get("uid")).toString(), u);
					}
				}
				Map<String, Object> user = null;
				RecommendUser ru = null;
				for(int i=0;i<result.size();i++){
					ru = result.get(i);
					user = userMap.get(String.valueOf(ru.getUid()));
					if(null == user){
						result.remove(i);
						i--;
					}else{
						ru.setAvatar(Constant.QINIU_DOMAIN + "/" + user.get("avatar"));
						ru.setLevel(((Integer)user.get("level")).intValue());
						ru.setNickName((String)user.get("nick_name"));
						ru.setV_lv(((Integer)user.get("v_lv")).intValue());
					}
				}
			}
		}else{//缓存中没有，则需要从数据库中获取，并将结果存入缓存
			List<AppConfig> configList = userService.getAppConfigsByType("用户智能推荐配置");
			Map<String, String> configMap = new HashMap<String, String>();
			if(null != configList && configList.size() > 0){
				for(AppConfig a : configList){
					configMap.put(a.getConfigKey(), a.getConfigValue());
				}
			}
			int sexScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_SEX"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_SEX"));
			int ageSameScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_AGESAME"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_AGESAME"));
			int ageAdjoinScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_AGEADJOIN"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_AGEADJOIN"));
			int hobbyScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_HOBBY"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_HOBBY"));
			int tagScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_TAG"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_TAG"));
			int emotionScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_EMOTION"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_EMOTION"));
			int careerScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_CAREER"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_CAREER"));
			int followScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_FOLLOW"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_FOLLOW"));
			int joinKingdomScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_JOINKINGDOM"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_JOINKINGDOM"));
			int mbitPeibanScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_MBTI_PEIBAN"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_MBTI_PEIBAN"));
			int mbitHuobanScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_MBTI_HUOBAN"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_MBTI_HUOBAN"));
			int mbitTongzuScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_MBTI_TONGZU"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_MBTI_TONGZU"));
			int mbitZhichiScore = StringUtils.isEmpty(configMap.get("REC_USER_SCORE_MBTI_ZHICHI"))?0:Integer.valueOf(configMap.get("REC_USER_SCORE_MBTI_ZHICHI"));
			String userLevelScores = configMap.get("REC_USER_SCORE_USER_LEVEL");
			Map<String, Integer> userLevelScoreMap = new HashMap<String, Integer>();
			if(!StringUtils.isEmpty(userLevelScores)){
				String[] scores = userLevelScores.split(",");
				for(int i=1;i<scores.length+1;i++){
					userLevelScoreMap.put(String.valueOf(i), Integer.valueOf(scores[i-1]));
				}
			}
			
			Map<String, Map<String, Integer>> mbtiScoreMap = new HashMap<String, Map<String, Integer>>();
			//初始化，由于分值配置不一样，故每次独立初始化
			//INFP
			Map<String, Integer> scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INTP", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ENTP", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ENFP", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ESFJ", Integer.valueOf(mbitZhichiScore));
			mbtiScoreMap.put("INFP", scoreMap);
			//INFJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("ENFJ", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("NETP", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ISFJ", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ESFJ", Integer.valueOf(mbitTongzuScore));
			mbtiScoreMap.put("INFJ", scoreMap);
			//INTP
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFP", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ENFP", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ENTP", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ESTJ", Integer.valueOf(mbitZhichiScore));
			mbtiScoreMap.put("INTP", scoreMap);
			//INTJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("ENFP", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ENTJ", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ISTJ", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ESTJ", Integer.valueOf(mbitTongzuScore));
			mbtiScoreMap.put("INTJ", scoreMap);
			//ENFP
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFP", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("INTP", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ENTP", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ISFJ", Integer.valueOf(mbitZhichiScore));
			mbtiScoreMap.put("ENFP", scoreMap);
			//ENFJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFJ", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("INTP", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ISFJ", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ESFJ", Integer.valueOf(mbitPeibanScore));
			mbtiScoreMap.put("ENFJ", scoreMap);
			//ENTP
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFP", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("INTP", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ENFP", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ISTJ", Integer.valueOf(mbitZhichiScore));
			mbtiScoreMap.put("ENTP", scoreMap);
			//ENTJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFP", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("INTJ", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ISTJ", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ESTJ", Integer.valueOf(mbitPeibanScore));
			mbtiScoreMap.put("ENTJ", scoreMap);
			//ISFP
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("ENFJ", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ISTP", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ESFP", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ESTP", Integer.valueOf(mbitTongzuScore));
			mbtiScoreMap.put("ISFP", scoreMap);
			//ISFJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFJ", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ENFJ", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ESFJ", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ESTP", Integer.valueOf(mbitZhichiScore));
			mbtiScoreMap.put("ISFJ", scoreMap);
			//ISTP
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("ENTJ", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ISFP", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ESFP", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ESTP", Integer.valueOf(mbitHuobanScore));
			mbtiScoreMap.put("ISTP", scoreMap);
			//ISTJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INTJ", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ENTJ", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ESFP", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ESTJ", Integer.valueOf(mbitHuobanScore));
			mbtiScoreMap.put("ISTJ", scoreMap);
			//ESFP
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFJ", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ISFP", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ISTP", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ESTP", Integer.valueOf(mbitPeibanScore));
			mbtiScoreMap.put("ESFP", scoreMap);
			//ESFJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INFJ", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ENFJ", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ISFJ", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ISTP", Integer.valueOf(mbitZhichiScore));
			mbtiScoreMap.put("ESFJ", scoreMap);
			//ESTP
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INTJ", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ISFP", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ISTP", Integer.valueOf(mbitHuobanScore));
			scoreMap.put("ESFP", Integer.valueOf(mbitPeibanScore));
			mbtiScoreMap.put("ESTP", scoreMap);
			//ESTJ
			scoreMap = new HashMap<String, Integer>();
			scoreMap.put("INTJ", Integer.valueOf(mbitTongzuScore));
			scoreMap.put("ENTJ", Integer.valueOf(mbitPeibanScore));
			scoreMap.put("ISFP", Integer.valueOf(mbitZhichiScore));
			scoreMap.put("ISTJ", Integer.valueOf(mbitHuobanScore));
			mbtiScoreMap.put("ESTJ", scoreMap);
			
			List<Long> uidList = new ArrayList<Long>();
			//性取向
			if(userProfile.getLikeGender().intValue() == 1){//爱男
				this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.SEX_MALE.type), noUidList);
			}else if(userProfile.getLikeGender().intValue() == 2){//爱女
				this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.SEX_FEMALE.type), noUidList);
			}else if(userProfile.getLikeGender().intValue() == 3){//男女通吃
				this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.COMMON.type), noUidList);
			}else{//默认喜欢女
				this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.SEX_FEMALE.type), noUidList);
			}
			//年龄范围
			if(userProfile.getAgeGroup().intValue() > 0){
				if(userProfile.getAgeGroup().intValue() == 1){//00后
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_00.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_95.type), noUidList);
				}else if(userProfile.getAgeGroup().intValue() == 2){//95后
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_00.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_95.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_90.type), noUidList);
				}else if(userProfile.getAgeGroup().intValue() == 3){//90后
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_95.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_90.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_85.type), noUidList);
				}else if(userProfile.getAgeGroup().intValue() == 4){//85后
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_90.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_85.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_80.type), noUidList);
				}else if(userProfile.getAgeGroup().intValue() == 5){//80后
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_85.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_80.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_OLD.type), noUidList);
				}else if(userProfile.getAgeGroup().intValue() == 6){//活化石
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_80.type), noUidList);
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.AGE_OLD.type), noUidList);
				}
			}
			//爱好
			if(null != hobbyIds && hobbyIds.size() > 0){
				for(Long hobbyId : hobbyIds){
					this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.getUserRecInitTypeByValue("HOBBY_"+hobbyId.toString()).type), noUidList);
				}
			}

			//MBTI测试
			Map<String, Integer> rMap = null;
			if(!StringUtils.isEmpty(userProfile.getMbti())){
				rMap = mbtiScoreMap.get(userProfile.getMbti());
				if(null != rMap && rMap.size() > 0){
					for(Map.Entry<String, Integer> entry : rMap.entrySet()){
						this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.getUserRecInitTypeByValue("MBTI_"+entry.getKey()).type), noUidList);
					}
				}
			}
			
			//情绪记录
			this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.getUserRecInitTypeByValue("情绪_"+firstUserEmotionInfo.getEmotionname()).type), noUidList);
			//职业
			if(userProfile.getOccupation().intValue() > 0){
				this.builderUsreList(uidList, this.getInitUserByType(Specification.UserRecInitType.getUserRecInitTypeByValue("职业_"+userProfile.getOccupation().intValue()).type), noUidList);
			}
			
			//好了，人已经拉的差不多了，下面进行匹配度计算
			if(uidList.size() > 300){
				//打乱顺序，然后取300个
				Collections.shuffle(uidList);
				uidList = uidList.subList(0, 300);
			}
			
			List<Map<String, Object>> userProfileList = contentForSearchJdbcDao.getUserProfileInfoByUids(uidList);
			if(null != userProfileList && userProfileList.size() > 0){
				Map<String, List<Long>> userHobbyIdsMap = contentForSearchJdbcDao.getUserHobbyIdsByUids(uidList);
				if(null == userHobbyIdsMap){
					userHobbyIdsMap = new HashMap<String, List<Long>>();
				}
				List<Long> hasSameTagTopicKingList = contentForSearchJdbcDao.getSameTagTopicKing(userProfile.getUid().longValue(), uidList);
				if(null == hasSameTagTopicKingList){
					hasSameTagTopicKingList = new ArrayList<Long>();
				}
				Map<String, Long> userLastEmotionIdMap = contentForSearchJdbcDao.getUserLastEmotionId(uidList);
				if(null == userLastEmotionIdMap){
					userLastEmotionIdMap = new HashMap<String, Long>();
				}
				Map<String, Long> sameFollowCountMap = contentForSearchJdbcDao.getSameFollowCountByUids(userProfile.getUid().longValue(), uidList);
				if(null == sameFollowCountMap){
					sameFollowCountMap = new HashMap<String, Long>();
				}
				Map<String, Long> sameJoinTopicCountMap = contentForSearchJdbcDao.getSameJoinTopicCountByUids(userProfile.getUid().longValue(), uidList);
				if(null == sameJoinTopicCountMap){
					sameJoinTopicCountMap = new HashMap<String, Long>();
				}
				
				RecommendUser rUser = null;
				List<Long> rUserHobbyIds = null;
				String mbti = null;
				for(Map<String, Object> u : userProfileList){
					rUser = new RecommendUser();
					rUser.setUid((Long)u.get("uid"));
					rUser.setAvatar(Constant.QINIU_DOMAIN + "/" + (String)u.get("avatar"));
					rUser.setLevel((Integer)u.get("level"));
					rUser.setNickName((String)u.get("nick_name"));
					rUser.setV_lv((Integer)u.get("v_lv"));
					//匹配得分
					int score = 0;
					//性取向
					if((userProfile.getLikeGender().intValue() == 1 && ((Integer)u.get("gender")).intValue() == 1)
							|| ((userProfile.getLikeGender().intValue() == 2 || userProfile.getLikeGender().intValue() == 0) && ((Integer)u.get("gender")).intValue() != 1)
							|| userProfile.getLikeGender().intValue() == 3){
						score = score + sexScore;
					}
					//年龄范围
					int ageGroup = ((Integer)u.get("age_group")).intValue();
					if(userProfile.getAgeGroup().intValue() > 0 && ageGroup > 0){
						int abs = Math.abs(userProfile.getAgeGroup().intValue()-ageGroup);
						if(abs == 0){//一样的
							score = score + ageSameScore;
						}else if(abs == 1){//前后格
							score = score + ageAdjoinScore;
						}
					}
					//爱好
					rUserHobbyIds = userHobbyIdsMap.get(String.valueOf(u.get("uid")));
					if(null != rUserHobbyIds && rUserHobbyIds.size() > 0
							&& null != hobbyIds && hobbyIds.size() > 0){
						for(Long id : rUserHobbyIds){
							if(hobbyIds.contains(id)){
								score = score + hobbyScore;
							}
						}
					}
					//是否有相同tag的王国
					if(hasSameTagTopicKingList.contains((Long)u.get("uid"))){
						score = score + tagScore;
					}
					//MBTI
					mbti = (String)u.get("mbti");
					if(null != rMap && rMap.size() > 0 && !StringUtils.isEmpty(mbti)
							&& null != rMap.get(mbti)){
						int mbitScore = rMap.get(mbti).intValue();
						score = score + mbitScore;
					}
					
					//情绪
					if(null != firstUserEmotionInfo && null != userLastEmotionIdMap.get(String.valueOf(u.get("uid")))
							&& firstUserEmotionInfo.getId().longValue() == userLastEmotionIdMap.get(String.valueOf(u.get("uid"))).longValue()){
						score = score + emotionScore;
					}
					//职业
					if(userProfile.getOccupation().intValue() > 0 && ((Integer)u.get("occupation")).intValue() > 0
							&& userProfile.getOccupation().intValue() == ((Integer)u.get("occupation")).intValue()){
						score = score + careerScore;
					}
					//关注同一个人
					if(null != sameFollowCountMap.get(String.valueOf(u.get("uid")))){
						score = score + sameFollowCountMap.get(String.valueOf(u.get("uid"))).intValue()*followScore;
					}
					//加入同一个王国
					if(null != sameJoinTopicCountMap.get(String.valueOf(u.get("uid")))){
						score = score + sameJoinTopicCountMap.get(String.valueOf(u.get("uid"))).intValue()*joinKingdomScore;
					}
					//等级加成
					if(null != userLevelScoreMap.get(userProfile.getLevel().toString())){
						score = score + userLevelScoreMap.get(userProfile.getLevel().toString()).intValue();
					}
					rUser.setTagMatchedLength(score);//暂时放这个字段，等正式算好匹配度后再清除
					
					result.add(rUser);
				}
				
				//计算匹配度
				if(result.size() > 0){
					Collections.sort(result, new Comparator<RecommendUser>() {
			            public int compare(RecommendUser a, RecommendUser b) {
			                if(a.getTagMatchedLength() > b.getTagMatchedLength()){
			                	return -1;
			                }else if(a.getTagMatchedLength() == b.getTagMatchedLength()){
			                	return 0;
			                }else{
			                	return 1;
			                }
			            }
			        });
					int batch = result.size()%pageSize==0?result.size()/pageSize:(result.size()/pageSize)+1;
					int n = 0;
					int score = 99;
					for(RecommendUser ru : result){
						ru.setMatching(score);
						ru.setTagMatchedLength(0);//清除临时存储的分值
						n++;
						if(n>=batch){
							n = 0;
							score--;
						}
					}
					//将300个记录存入缓存
					StringBuilder sb = new StringBuilder();
					RecommendUser ru = null;
					for(int i=0;i<result.size();i++){
						ru = result.get(i);
						if(i>0){
							sb.append(";");
						}
						sb.append(ru.getUid()).append(",").append(ru.getMatching());
					}
					cacheService.set(cacheKey, sb.toString());
				}
			}
			
			if(result.size() > pageSize){
				Collections.shuffle(result);
				result = result.subList(0, pageSize);
			}
		}
		
		return result;
	}
	
	private void builderUsreList(List<Long> result, List<Long> srcList, List<Long> noUidList){
		if(null != srcList && srcList.size() > 0){
			for(Long uid : srcList){
				if(noUidList.contains(uid)){
					continue;
				}
				if(!result.contains(uid)){
					result.add(uid);
				}
			}
		}
	}
	
	private List<Long> getInitUserByType(String type){
		List<Long> uidList = null;
		String cache = cacheService.get(CacheConstant.USER_REC_INIT_LIST_KEY_PRE + type);
		if(StringUtils.isEmpty(cache)){
			uidList = contentForSearchJdbcDao.getInitUserByType(type);
		}else{//非空
			uidList = new ArrayList<Long>();
			String[] tmp = cache.split(",");
			for(String t : tmp){
				uidList.add(Long.valueOf(t));
			}
		}
		return uidList;
	}
	
	private void builderRecKingdomInfo(RecommendListDto indexData, List<TopicEsMapping> kingdoms, long uid){
		if(null == kingdoms || kingdoms.size() == 0){
    		return;
    	}
    	
    	List<Long> tidList = new ArrayList<Long>();
		List<Long> ceTidList = new ArrayList<Long>();
		for(TopicEsMapping topic : kingdoms){
			tidList.add(topic.getId());
		}
		List<Long> uidList = new ArrayList<Long>();
		Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> topicContentMap = new HashMap<String, Map<String, Object>>();
		//一次性查询王国订阅信息
        Map<String, String> liveFavouriteMap = new HashMap<String, String>();
        //一次性查询所有王国的国王更新数，以及评论数
        Map<String, Long> topicCountMap = new HashMap<String, Long>();
        Map<String, Long> reviewCountMap = new HashMap<String, Long>();
        //一次性查询所有王国的成员数
        Map<String, Long> topicMemberCountMap = contentForSearchJdbcDao.getTopicMembersCount(tidList);
        if(null == topicMemberCountMap){
        	topicMemberCountMap = new HashMap<String, Long>();
        }
        //一次性查询王国的标签信息
        Map<String, String> topicTagMap = new HashMap<String, String>();
		if(tidList.size() > 0){
			List<Map<String, Object>> topicList = contentForSearchJdbcDao.getTopicByIds(tidList);
			if(null != topicList && topicList.size() > 0){
				Long topicUid = null;
				for(Map<String, Object> m : topicList){
					topicMap.put(String.valueOf(m.get("id")), m);
					topicUid = (Long)m.get("uid");
					if(!uidList.contains(topicUid)){
						uidList.add(topicUid);
					}
					if(((Integer)m.get("type")).intValue() == 1000){//聚合王国
						ceTidList.add((Long)m.get("id"));
					}
				}
			}
			List<Map<String, Object>> topicContentList = contentForSearchJdbcDao.getTopicContentByTopicIds(tidList);
			if(null != topicContentList && topicContentList.size() > 0){
				for(Map<String, Object> m : topicContentList){
					topicContentMap.put(String.valueOf(m.get("forward_cid")), m);
				}
			}
			List<Map<String,Object>> liveFavouriteList = contentForSearchJdbcDao.getLiveFavoritesByUidAndTopicIds(uid, tidList);
            if(null != liveFavouriteList && liveFavouriteList.size() > 0){
            	for(Map<String,Object> lf : liveFavouriteList){
            		liveFavouriteMap.put(((Long)lf.get("topic_id")).toString(), "1");
            	}
            }
            List<Map<String, Object>> tcList = contentForSearchJdbcDao.getTopicUpdateCount(tidList);
            if(null != tcList && tcList.size() > 0){
            	for(Map<String, Object> m : tcList){
            		topicCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("topicCount"));
            		reviewCountMap.put(String.valueOf(m.get("topic_id")), (Long)m.get("reviewCount"));
            	}
            }
            List<Map<String, Object>> topicTagList = contentForSearchJdbcDao.getTopicTagDetailListByTopicIds(tidList);
            if(null != topicTagList && topicTagList.size() > 0){
            	long tid = 0;
            	String tags = null;
            	Long topicId = null;
            	for(Map<String, Object> ttd : topicTagList){
            		topicId = (Long)ttd.get("topic_id");
            		if(topicId.longValue() != tid){
            			//先插入上一次
            			if(tid > 0 && !StringUtils.isEmpty(tags)){
            				topicTagMap.put(String.valueOf(tid), tags);
            			}
            			//再初始化新的
            			tid = topicId.longValue();
            			tags = null;
            		}
            		if(tags != null){
            			tags = tags + ";" + (String)ttd.get("tag");
            		}else{
            			tags = (String)ttd.get("tag");
            		}
            	}
            	if(tid > 0 && !StringUtils.isEmpty(tags)){
            		topicTagMap.put(String.valueOf(tid), tags);
            	}
            }
		}
		Map<String, Long> acCountMap = new HashMap<String, Long>();
        if(ceTidList.size() > 0){
        	List<Map<String,Object>> acCountList = contentForSearchJdbcDao.getTopicAggregationAcCountByTopicIds(ceTidList);
        	if(null != acCountList && acCountList.size() > 0){
        		for(Map<String,Object> a : acCountList){
        			acCountMap.put(String.valueOf(a.get("topic_id")), (Long)a.get("cc"));
        		}
        	}
        }
		Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
		//一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
		if(uidList.size() > 0){
			List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
			if(null != userList && userList.size() > 0){
				for(UserProfile u : userList){
					userMap.put(u.getUid().toString(), u);
				}
			}
			List<UserFollow> userFollowList = userService.getAllFollows(uid, uidList);
            if(null != userFollowList && userFollowList.size() > 0){
            	for(UserFollow uf : userFollowList){
            		followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
            	}
            }
		}
		
		Map<String, Object> topic = null;
		Map<String, Object> topicContent = null;
		UserProfile userProfile = null;
		RecommendListDto.ContentData kingdomElement = null;
		for(TopicEsMapping t : kingdoms){
			kingdomElement = new RecommendListDto.ContentData();
			topic = topicMap.get(t.getId().toString());
			if(null == topic){
				continue;
			}
			topicContent = topicContentMap.get(t.getId().toString());
			if(null == topicContent){
				continue;
			}
			userProfile = userMap.get(String.valueOf(topic.get("uid")));
			if(null == userProfile){
				continue;
			}
			kingdomElement.setUid(userProfile.getUid());
			kingdomElement.setNickName(userProfile.getNickName());
			kingdomElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
			kingdomElement.setV_lv(userProfile.getvLv());
			if(null != followMap.get(uid+"_"+userProfile.getUid())){
				kingdomElement.setIsFollowed(1);
			}else{
				kingdomElement.setIsFollowed(0);
			}
			if(null != followMap.get(userProfile.getUid()+"_"+uid)){
				kingdomElement.setIsFollowMe(1);
			}else{
				kingdomElement.setIsFollowMe(0);
			}
			kingdomElement.setTopicId((Long)topic.get("id"));
			kingdomElement.setTitle((String)topic.get("title"));
			kingdomElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + (String)topic.get("live_image"));
			kingdomElement.setType(3);
			kingdomElement.setContentType((Integer)topic.get("type"));
			if(null != topic.get("create_time")){
				kingdomElement.setCreateTime(((Date)topic.get("create_time")).getTime());
			}
			kingdomElement.setUpdateTime(((Date)topic.get("update_time")).getTime());
			kingdomElement.setLastUpdateTime((Long)topic.get("long_time"));
			kingdomElement.setInternalStatus(this.getInternalStatus((String)topic.get("core_circle"), uid));
			kingdomElement.setCid((Long)topicContent.get("id"));
			kingdomElement.setLikeCount((Integer)topicContent.get("like_count"));
			kingdomElement.setReadCount((Integer)topicContent.get("read_count_dummy"));
			if(null != liveFavouriteMap.get(t.getId().toString())){
				kingdomElement.setFavorite(1);
			}else{
				kingdomElement.setFavorite(0);
			}
			if(null != topicCountMap.get(t.getId().toString())){
				kingdomElement.setTopicCount(topicCountMap.get(t.getId().toString()).intValue());
			}else{
				kingdomElement.setTopicCount(0);
			}
			if(null != reviewCountMap.get(t.getId().toString())){
				kingdomElement.setReviewCount(reviewCountMap.get(t.getId().toString()).intValue());
			}else{
				kingdomElement.setReviewCount(0);
			}
			if(null == topicMemberCountMap.get(t.getId().toString())){
				kingdomElement.setFavoriteCount(1);//默认只有国王一个成员
			}else{
				kingdomElement.setFavoriteCount(topicMemberCountMap.get(t.getId().toString()).intValue()+1);
			}
			if(kingdomElement.getContentType() == 1000){//聚合王国需要返回子王国数
				if(null != acCountMap.get(t.getId().toString())){
					kingdomElement.setAcCount(acCountMap.get(t.getId().toString()).intValue());
				}else{
					kingdomElement.setAcCount(0);
				}
			}
			if(null != topicTagMap.get(t.getId().toString())){
				kingdomElement.setTags(topicTagMap.get(t.getId().toString()));
            }else{
            	kingdomElement.setTags("");
            }
			kingdomElement.setReason(RecommendReason.SAME_TAG);
			indexData.getRecContentData().add(kingdomElement);
		}
	}

	@Override
	public Response recommendKingdom(long uid, int page, int pageSize) {
		List<RecommendKingdom> kingdoms = this.searchService.getRecommendKingdomList(uid, page, pageSize);
		RecommendKingdomDto dt = new RecommendKingdomDto();
		dt.setKingdomList(kingdoms);
		return Response.success(dt);
	}
	
	@Override
	public Response recContentDislike(long uid, long cid, int type){
		SearchUserDislike sud = searchMybatisDao.getSearchUserDislikeByUidAndCidAndType(uid, cid, type);
		if(null == sud){
			sud = new SearchUserDislike();
			sud.setUid(uid);
			sud.setCid(cid);
			sud.setType(type);
			sud.setCreateTime(new Date());
			searchMybatisDao.saveSearchUserDislike(sud);
		}
		
		return Response.success(ResponseStatus.OPERATION_SUCCESS.status, ResponseStatus.OPERATION_SUCCESS.message);
	}
	
	@Override
	public List<Map<String, Object>> topicAtUserList(String keyword, long searchUid){
		FacetedPage<UserEsMapping> esResult = this.searchService.queryUsers4AtUserList(keyword, 1, 20, searchUid);
		List<Long> uidList = new ArrayList<Long>();
		for(UserEsMapping u : esResult){
			uidList.add(u.getUid());
		}
		Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
		if(null != uidList && uidList.size() > 0){
			List<UserProfile> userList = userService.getUserProfilesByUids(uidList);
			if(null != userList && userList.size() > 0){
				for(UserProfile u : userList){
					userMap.put(u.getUid().toString(), u);
				}
			}
		}
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		UserProfile user = null;
		Map<String, Object> m = null;
		for(UserEsMapping u : esResult){
			user = userMap.get(u.getUid().toString());
			if(null != user){
				m = new HashMap<String, Object>();
				m.put("uid", user.getUid());
				m.put("nick_name", user.getNickName());
				m.put("avatar", user.getAvatar());
				m.put("v_lv", user.getvLv());
				result.add(m);
			}
		}
		return result;
	}

	@Override
	public int indexTagSample() throws Exception {
		return searchService.indexTagSample();
	}

	@Override
	public Response<RecommendTagDto> recommendTags(String content, int count) {
		List<String> ret = searchService.recommendTags(content, count);
		RecommendTagDto dto = new RecommendTagDto();
		dto.setTags(ret);
		return Response.success(dto);
	}
}
