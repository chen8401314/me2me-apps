package com.me2me.search.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.search.dao.ContentForSearchJdbcDao;
import com.me2me.search.dto.ShowAssociatedWordDTO;
import com.me2me.search.dto.ShowSearchDTO;
import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UgcEsMapping;
import com.me2me.search.esmapping.UserEsMapping;
import com.me2me.user.model.UserFollow;
import com.me2me.user.model.UserProfile;
import com.me2me.search.mapper.SearchHotKeywordMapper;
import com.me2me.search.model.SearchHotKeyword;
import com.me2me.search.model.SearchHotKeywordExample;
import com.me2me.user.service.UserService;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private UserService userService;
	
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    @Autowired
    private ContentSearchService searchService;
    
    @Autowired
    private ContentForSearchJdbcDao contentForSearchJdbcDao;

    @Autowired
    private SearchHotKeywordMapper hotkeywordMapper;
    
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
    public Response allSearch(long uid, String keyword, int searchType, int page, int pageSize){
    	//首先记录搜索词
    	if(!StringUtils.isEmpty(keyword)){
    		searchService.addSearchHistory(keyword);
    	}
    	
    	ShowSearchDTO resultDTO = new ShowSearchDTO();
    	
    	FacetedPage<UgcEsMapping> ugcPage = null;
    	FacetedPage<TopicEsMapping> kingdomPage = null;
    	FacetedPage<UserEsMapping> userPage = null;
    	if(searchType == 0){//搜索全部，则返回UGC3个，王国3个，人3个
    		ugcPage = searchService.queryUGC(keyword, page, pageSize);
    		kingdomPage = searchService.queryKingdom(keyword, page, pageSize);
    		userPage = searchService.queryUsers(keyword, page, pageSize);
    	}else if(searchType == 1){//用户
    		userPage = searchService.queryUsers(keyword, page, pageSize);
    		resultDTO.setTotalPage(userPage.getTotalPages());
    		resultDTO.setTotalRecord(userPage.getTotalElements());
    	}else if(searchType == 2){//王国
    		kingdomPage = searchService.queryKingdom(keyword, page, pageSize);
    		resultDTO.setTotalPage(kingdomPage.getTotalPages());
    		resultDTO.setTotalRecord(kingdomPage.getTotalElements());
    	}else if(searchType == 3){//UGC
    		ugcPage = searchService.queryUGC(keyword, page, pageSize);
    		resultDTO.setTotalPage(ugcPage.getTotalPages());
    		resultDTO.setTotalRecord(ugcPage.getTotalElements());
    	}
    	
    	if(null != ugcPage){
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
    		}
    	}
    	
    	if(null != kingdomPage){
    		List<Long> tidList = new ArrayList<Long>();
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
    				kingdomElement.setTopicCount(topicCountMap.get(t.getId().toString()));
    			}else{
    				kingdomElement.setTopicCount(0);
    			}
    			if(null != reviewCountMap.get(t.getId().toString())){
    				kingdomElement.setReviewCount(reviewCountMap.get(t.getId().toString()));
    			}else{
    				kingdomElement.setReviewCount(0);
    			}
    			
    			
//    			kingdomElement.setAcCount();
//    			
//    			kingdomElement.setFavoriteCount();
//    			
//    			kingdomElement.setTags();
    			
    			
    			
    			
    		}
    		
    	}
    	
    	return null;
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
	
	public String searchForJSON(String key,String type,int page,int pageSize){
		FacetedPage pagedata =null;
		if("ugc".equals(type)){
			pagedata = searchService.queryUGC(key, page, pageSize);
		}else if("kingdom".equals(type)){
			pagedata = searchService.queryKingdom(key, page, pageSize);
		}else if("user".equals(type)){
			pagedata = searchService.queryUsers(key, page, pageSize);
		}
		String str = JSON.toJSONString(pagedata);
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
}
