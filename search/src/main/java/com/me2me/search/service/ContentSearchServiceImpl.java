package com.me2me.search.service;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.me2me.common.Constant;
import com.me2me.common.utils.DateUtil;
import com.me2me.search.ThreadPool;
import com.me2me.search.cache.SimpleCache;
import com.me2me.search.constants.IndexConstants;
import com.me2me.search.dao.ContentForSearchJdbcDao;
import com.me2me.search.dto.RecommendKingdom;
import com.me2me.search.dto.RecommendUser;
import com.me2me.search.enums.EAgeGroup;
import com.me2me.search.enums.ELikeGender;
import com.me2me.search.enums.EOccupation;
import com.me2me.search.enums.RecommendReason;
import com.me2me.search.esmapping.SearchHistoryEsMapping;
import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UgcEsMapping;
import com.me2me.search.esmapping.UserEsMapping;
import com.me2me.search.mapper.SearchHistoryCountMapper;
import com.me2me.search.mapper.SearchMapper;
import com.me2me.search.mapper.SearchVarMapper;
import com.me2me.search.model.SearchHistoryCount;
import com.me2me.search.model.SearchHistoryCountExample;
import com.me2me.search.model.SearchHotKeyword;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * UGC搜索服务实现.
 *
 */
@Slf4j
@Service("contentSearchServiceImpl")
public class ContentSearchServiceImpl implements ContentSearchService {
	
	static final String HOT_KEYWORD_CACHE_KEY = "SEARCH_HOT_KEYWORD";
	static final String DEFAULT_START_TIME = "1900-01-01 00:00:00";
	static final String DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
	
	@Autowired
	private SearchVarMapper varMapper;
	
	@Autowired
	private SearchHistoryCountMapper shcMapper;

	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
	private SearchMapper searchMapper;
    @Autowired
    private ContentForSearchJdbcDao searchJdbcDao;
    
    @Autowired
    private UserService userService;
	/**
	 * 消息缓存。用来记录搜索消息
	 */
	private Queue<SearchHistoryCount> searchHistoryQueue = new LinkedBlockingQueue<>();

	private static Logger logger = LoggerFactory.getLogger(ContentSearchServiceImpl.class);

	@Autowired
	private SimpleCache cache;
	

	/**
	 * 系统启动时启动搜索消息同步线程。
	 * 
	 * @author zhangjiwei
	 * @date Apr 5, 2017
	 */
	@PostConstruct
	private void init() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				try {
					while (searchHistoryQueue.size() > 0) {
						SearchHistoryCount count = searchHistoryQueue.poll();
						addHistoryCount(count);
					}
				} catch (Exception e) {
					log.error("处理搜索消息出错。", e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS); // 1 秒一次。避免队列过大，造成系统崩溃。
	}

	@Override
	public FacetedPage<UgcEsMapping> queryUGC(String content, int page, int pageSize) {
		BoolQueryBuilder bq = new BoolQueryBuilder();
		if(StringUtils.isEmpty(content)){
			bq.must(QueryBuilders.matchAllQuery());
		}else{
			bq.must(QueryBuilders.termQuery("rights", 1));	// 只查公开
			bq.should(QueryBuilders.queryStringQuery(content).field("title").boost(2f));
			bq.should(QueryBuilders.queryStringQuery(content).field("content"));
		}
		SearchQuery sq = new NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(--page, pageSize));
		FacetedPage<UgcEsMapping> result = esTemplate.queryForPage(sq, UgcEsMapping.class);
		return result;
	}

	@Override
	public FacetedPage<TopicEsMapping> queryKingdom(String content, int contentType, int page, int pageSize) {
		BoolQueryBuilder bq = new BoolQueryBuilder();
		if(StringUtils.isEmpty(content)){
			bq.must(QueryBuilders.matchAllQuery());
		}else{
			bq.should(QueryBuilders.queryStringQuery(content).field("title").boost(5f));
			bq.should(QueryBuilders.queryStringQuery(content).field("nick_name").boost(4f));
			bq.should(QueryBuilders.queryStringQuery(content).field("tags").boost(3f));
			bq.should(QueryBuilders.queryStringQuery(content).field("summary").boost(2f));
			bq.should(QueryBuilders.queryStringQuery(content).field("fragments"));
		}
		if(contentType >= 0){
			bq.must(QueryBuilders.termQuery("type", contentType));
		}
		SearchQuery sq = new NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(--page, pageSize));
		FacetedPage<TopicEsMapping> result = esTemplate.queryForPage(sq, TopicEsMapping.class);
		return result;
	}

	@Override
	public FacetedPage<UserEsMapping> queryUsers(String content, int page, int pageSize) {
		BoolQueryBuilder bq = new BoolQueryBuilder();
		if(StringUtils.isEmpty(content)){
			bq.must(QueryBuilders.matchAllQuery());
		}else{
			bq.should(QueryBuilders.queryStringQuery(content).field("nick_name").boost(3f));
			bq.should(QueryBuilders.queryStringQuery(content).field("introduced"));
		}
		
		SearchQuery sq = new NativeSearchQuery(bq);
		
		sq.setPageable(new PageRequest(--page, pageSize));
		
		FacetedPage<UserEsMapping> result = esTemplate.queryForPage(sq, UserEsMapping.class);
		return result;
	}

	@Override
	public void addSearchHistory(String searchContent) {

		SearchHistoryCount hc = new SearchHistoryCount();
		hc.setName(searchContent);
		hc.setCreationDate(new Date());
		hc.setLastQueryDate(new Date());
		// kafka.send(SEARCH_MSG_KEY,searchContent); // 取消消息队列，直接入库
		searchHistoryQueue.add(hc); // 简单队列。
		logger.debug("send query history:" + searchContent);
	}

	@Override
	public List<String> associateKeywordList(String keyword,  int count) {
		
		BoolQueryBuilder bq = new BoolQueryBuilder()
				.should(new PrefixQueryBuilder("name", keyword))
				.should(new PrefixQueryBuilder("name_pinyin", keyword))
				.should(new PrefixQueryBuilder("name_pinyin_short", keyword));
		
		NativeSearchQuery sq =new  NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(0, count));
		sq.addSort(new Sort(Direction.DESC,"search_count"));
		FacetedPage<SearchHistoryEsMapping>  dataList = esTemplate.queryForPage(sq, SearchHistoryEsMapping.class);
		
		List<String> ret = new ArrayList<String>();
		for (SearchHistoryEsMapping ks : dataList) {
			if (!ret.contains(ks.getName())) {
				ret.add(ks.getName());
			}
		}
		return ret;
	}

	@Override
	public List<String> getHotKeywordList(int dbCount,int esCount) {
		// 从redis 中取热词列表，如果已经过期重新统计。10分钟刷新
		List<String> keyList = (List<String>) cache.getCache(HOT_KEYWORD_CACHE_KEY);
		if (keyList == null || keyList.isEmpty()) {
			keyList = new ArrayList<String>();
			List<SearchHotKeyword> hotKeyList = searchMapper.getHotKeywords(dbCount);
			if (hotKeyList != null) {
				for (SearchHotKeyword keyword : hotKeyList) {
					keyList.add(keyword.getKeyword());
				}
			}
			
			QueryBuilder qb = QueryBuilders.matchAllQuery();
			SearchQuery sq = new NativeSearchQuery(qb);
			sq.addSort(new Sort(Direction.DESC, "search_count"));
			sq.setPageable(new PageRequest(0, esCount));
			FacetedPage<SearchHistoryEsMapping> result = esTemplate.queryForPage(sq, SearchHistoryEsMapping.class);
			for (SearchHistoryEsMapping keyword : result) {
				if (!keyList.contains(keyword.getName())) {
					keyList.add(keyword.getName());
				}
			}
			cache.cache(HOT_KEYWORD_CACHE_KEY, keyList, 60);
		}
		return keyList;
	}

	private void addHistoryCount(SearchHistoryCount hc) {
		SearchHistoryCountExample example = new SearchHistoryCountExample();
		example.createCriteria().andNameEqualTo(hc.getName());
		List<SearchHistoryCount> dbs = shcMapper.selectByExample(example);
		if (dbs.size() > 0) {
			SearchHistoryCount dbCount = dbs.get(0);
			dbCount.setLastQueryDate(hc.getLastQueryDate());
			dbCount.setSearchCount(dbCount.getSearchCount() + 1);
			shcMapper.updateByPrimaryKeySelective(dbCount);
		} else {
			hc.setSearchCount(1);
			shcMapper.insertSelective(hc);
		}
	}
	/**
	 * 修改系统配置中的项目，如果项不存在，则新增此项
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param key
	 * @param val
	 */
	private void updateVarVal(String key, String val) {
		if (varMapper.existsVar(key)) {
			varMapper.updateVar(key, val);
		} else {
			varMapper.addVar(key, val);
		}
	}

	/**
	 * 预处理索引，返回当前索引的上次更新时间。
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param fully
	 * @param indexName
	 */
	private String preIndex(boolean fully,String indexName){
		String beginDate = DEFAULT_START_TIME;
		if (fully) {
			if (esTemplate.indexExists(indexName)) {
				esTemplate.deleteIndex(indexName);
			}
			esTemplate.createIndex(indexName);
		} else {
			String lastDate = varMapper.getVar(indexName);
			if (lastDate != null) {
				beginDate=lastDate;
			}
		}
		
		return beginDate;
	}
	/**
	 * 复制map中指定的key.
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param sourceMap
	 * @param fields
	 * @return
	 */
	private Map<String,Object> copyMap(Map<String,Object> sourceMap,String fields){
		LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
		for(String field:fields.split(",")){
			dataMap.put(field, sourceMap.get(field));
		}
		return dataMap;
	}

	@Override
	public int indexUserData(boolean fully) throws Exception {
		ThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				log.info("indexUserData started");
				String indexName = IndexConstants.USER_INDEX_NAME;
				String beginDate = preIndex(fully, indexName);
				String endDate = DateUtil.date2string(new Date(), DATE_FORMAT);
				int count = 0;
				int skip=0;
				int batchSize= 1000;
				esTemplate.putMapping(UserEsMapping.class);
				while (true) {
					List<UserEsMapping> users =searchMapper.getUserPageByUpdateDate(beginDate, endDate, skip, batchSize);
					
					log.info("get users,begin:{} end:{} skip:{} batchSize:{}",beginDate, endDate, skip, batchSize);
					if (users == null || users.isEmpty()) {
						break;
					}
					List<Long> uids = new ArrayList<>();
					for(UserEsMapping user:users){
						uids.add(user.getUid());
					}
					
					Map<Long,String> hobbyMap = new HashMap<>();
					List<Map<String,Object>> hobbyList = searchMapper.getUserHobbyByUids(StringUtils.join(uids,","));
					for(Map<String,Object> row : hobbyList){
						Long k = (Long)row.get("uid");
						String v= (String) row.get("value");
						if(hobbyMap.containsKey(k)){
							v = hobbyMap.get(k) +" "+v;
						}
						hobbyMap.put(k, v);
					}
					
					List<IndexQuery> indexList = new ArrayList<>();
					for (UserEsMapping data : users) {
						IndexQuery query = new IndexQuery();
						String key = data.getUid()+"";
						data.setTags(hobbyMap.get(data.getUid()));
						query.setId(key);
						query.setObject(data);
						query.setIndexName(indexName);
						query.setType(indexName);
						indexList.add(query);
					}
					esTemplate.bulkIndex(indexList);
					
					skip += batchSize;
					count += users.size();
					log.info("indexUserData processed:"+count);
				}
				updateVarVal(indexName, endDate);
				log.info("indexUserData finished. end date:"+endDate);
			}
		});
		
		return 0;
	}
	@Override
	public int indexUgcData(boolean fully) throws Exception {
		ThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				log.info("indexUgcData started");
				String indexName = IndexConstants.UGC_INDEX_NAME;
				String beginDate = preIndex(fully, indexName);
				String endDate = DateUtil.date2string(new Date(), DATE_FORMAT);
				int count = 0;
				int skip=0;
				int batchSize= 1000;
				esTemplate.putMapping(UgcEsMapping.class);
				while (true) {
					List<UgcEsMapping> ugcList =searchMapper.getUgcPageByUpdateDate(beginDate, endDate, skip, batchSize);
					if (ugcList == null || ugcList.isEmpty()) {
						break;
					}
					List<IndexQuery> indexList = new ArrayList<>();
					for (UgcEsMapping data : ugcList) {
						IndexQuery query = new IndexQuery();
						String key =data.getId()+"";
						
						query.setId(key);
						query.setObject(data);
						query.setIndexName(indexName);
						query.setType(indexName);
						indexList.add(query);
					}
					esTemplate.bulkIndex(indexList);
					
					skip += batchSize;
					count += ugcList.size();
					log.info("indexUgcData processed:"+count);
				}
				updateVarVal(indexName, endDate);
				log.info("indexUgcData finished.");
			}
		});
		
		return 0;
	}

	@Override
	public int indexKingdomData(boolean fully) throws Exception {
	
		ThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				log.info("indexKingdomData started");
				String indexName = IndexConstants.KINGDOM_INDEX_NAME;
				String beginDate = preIndex(fully, indexName);
				String endDate = DateUtil.date2string(new Date(), DATE_FORMAT);
				int count = 0;
				int skip=0;
				int batchSize= 1000;
				esTemplate.putMapping(TopicEsMapping.class);
				while (true) {
					List<TopicEsMapping> kingdomList =searchMapper.getKingdomPageByUpdateDate(beginDate, endDate, skip, batchSize);
					if (kingdomList == null || kingdomList.isEmpty()) {
						break;
					}
					List<Long> kids = getBeanProperty(kingdomList, "id");
					Map<Long,List<String>> textMap = new HashMap<>();
					List<Map<String,Object>> hobbyList = searchMapper.getKingdomFragmentsByTopicIds(StringUtils.join(kids,","));
					for(Map<String,Object> row : hobbyList){
						Long k = (Long)row.get("topic_id");
						String v= (String) row.get("fragment");
						List<String> valueList = textMap.get(k);
						if(valueList!=null){
							valueList.add(v);
						}else{
							valueList = new ArrayList<>();
							valueList.add(v);
							textMap.put(k, valueList);
						}
					}
					
					List<IndexQuery> indexList = new ArrayList<>();
					for (TopicEsMapping data : kingdomList) {
						IndexQuery query = new IndexQuery();
						String key = data.getId()+"";
						List<String> valueList = textMap.get(data.getId());
						if(valueList!=null){
							List<String> subvalueList= valueList.size()>100?valueList.subList(0, 100):valueList;
							String comments = StringUtils.join(subvalueList,",");
							data.setFragments(comments);
						}
						String tags =searchJdbcDao.getTopicTagsByTopicId(data.getId());
						data.setTags(tags);
						
						query.setId(key);
						query.setObject(data);
						query.setIndexName(indexName);
						query.setType(indexName);
						indexList.add(query);
					}
					
					esTemplate.bulkIndex(indexList);
					
					skip += batchSize;
					count += kingdomList.size();
					log.info("indexKingdomData processed:"+count);
				}
				updateVarVal(indexName, endDate);
				log.info("indexKingdomData finished.");
				
			}
		});
		
		return 0;
	}
	
	
	@Override
	public int indexSearchHistory(boolean fully) throws Exception {
		ThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				log.info("indexSearchHistory started");
				String indexName = IndexConstants.SEARCH_HISTORY_INDEX_NAME;
				String beginDate=preIndex(fully,indexName);
				String endDate = DateUtil.date2string(new Date(), DATE_FORMAT);
				int skip = 0;
				int batchSize = 1000;
				int count = 0;
				while (true) {
					List<SearchHistoryEsMapping> pageList = searchMapper.getSearchHistoryPageByDate(beginDate, endDate, skip, batchSize);
					if (null == pageList || pageList.isEmpty()) {
						break;
					}
					List<IndexQuery> indexList = new ArrayList<>();
					for (SearchHistoryEsMapping data : pageList) {
						IndexQuery query = new IndexQuery();
						String key =data.getName();
						try {
							String pinyin = PinyinHelper.convertToPinyinString(key, "", PinyinFormat.WITHOUT_TONE);
							String py = PinyinHelper.getShortPinyin(key); // nhsj
							data.setName_pinyin(pinyin);
							data.setName_pinyin_short(py);
						} catch (PinyinException e) {
							e.printStackTrace();
						}
						query.setId(key);
						query.setObject(data);
						query.setIndexName(indexName);
						query.setType(indexName);
						indexList.add(query);
					}
					esTemplate.bulkIndex(indexList);
					skip += batchSize;
					count += pageList.size();
					log.info("indexSearchHistory processed:"+count);
				}
				updateVarVal(indexName, endDate);
				log.info("indexSearchHistory finished.");
				
			}
		});
		
		return 0;
	}

	@Override
	public List<RecommendUser> getRecommendUserList(long uid,int page,int pageSize, List<Long> noUids) {
		UserProfile user = userService.getUserProfileByUid(uid);
		BoolQueryBuilder bq = new BoolQueryBuilder();
		List<String> userHobbyList = searchMapper.getUserHobby(user.getUid());
		if(user!=null){
			String tags= StringUtils.join(userHobbyList," ").trim();
			if(null == noUids){
				noUids = new ArrayList<Long>();
			}
			noUids.add(uid);
			bq.mustNot(QueryBuilders.termsQuery("uid", noUids));//过滤掉不需要的uid
			if(user.getLikeGender()!=null && user.getLikeGender()!=ELikeGender.ALL.getValue()){
				int likeGender=user.getLikeGender().equals(ELikeGender.BOY.getValue())?1:0;	// 性取向转换为性别。	BOY(1,"爱男神"),	GIRL(2,"爱女神"),ALL(3,"男女通吃");
				bq.must(QueryBuilders.termQuery("gender", likeGender).boost(0.1f));
			}
			if(tags!=null){
				bq.should(QueryBuilders.queryStringQuery(tags).field("tags").boost(3f));
			}
			if(user.getOccupation()!=null){
				bq.should(QueryBuilders.termQuery("occupation",user.getOccupation()).boost(2f));
			}
			if(user.getAgeGroup()!=null){
				bq.should(QueryBuilders.termQuery("age_group",user.getAgeGroup()));
			}
		}
		
		SearchQuery sq = new NativeSearchQuery(bq);
		
		sq.setPageable(new PageRequest(--page, pageSize));
		
		FacetedPage<UserEsMapping> result = esTemplate.queryForPage(sq, UserEsMapping.class);
		List<Long> uidList = new ArrayList<Long>();
		for(UserEsMapping userMap:result){
			uidList.add(userMap.getUid());
		}
		Map<String, UserProfile> userProfileMap = new HashMap<String, UserProfile>();
		if(uidList.size() > 0){
			List<UserProfile> ulist = userService.getUserProfilesByUids(uidList);
			if(null != ulist && ulist.size() > 0){
				for(UserProfile u : ulist){
					userProfileMap.put(u.getUid().toString(), u);
				}
			}
		}
		
		List<RecommendUser> userList = new ArrayList<>();
		RecommendUser userInfo = null;
		UserProfile userProfile = null;
    	for(UserEsMapping userMap:result){
    		userInfo = new RecommendUser();
    		userProfile = userProfileMap.get(String.valueOf(userMap.getUid()));
    		if(null == userProfile){
    			continue;
    		}
    		
    		userInfo.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
    		userInfo.setNickName(userProfile.getNickName());
    		userInfo.setUid(userProfile.getUid());
    		userInfo.setV_lv(userProfile.getvLv());
    		
    		boolean sameTags =false;
    		if(userMap.getTags()!=null && !StringUtils.isEmpty(userMap.getTags())){
	    		String[] userTags = userMap.getTags().split(" ");
	    		if(userTags!=null && userTags.length>0){
	    			List<String> matchedTagList = new ArrayList<>();
	    			List<String> notMatchedTagList = new ArrayList<>();
	    			//兴趣
	    			for( String tag:userTags){
	    				for(String mytag:userHobbyList){
	            			if(mytag.equals(tag)){
	            				matchedTagList.add(mytag);
	            			}else if(!matchedTagList.contains(tag)){
	            				notMatchedTagList.add(tag);
	            			}
	            		}
	    			}
	    			//职业
	    			if(user.getOccupation()==userMap.getOccupation()){
	    				matchedTagList.add(EOccupation.fromCode(userMap.getOccupation()).getName());
	    			}else{
	    				notMatchedTagList.add(EOccupation.fromCode(userMap.getOccupation()).getName());
	    			}
	    			//年龄段
	    			if(user.getAgeGroup()==userMap.getAge_group()){
	    				matchedTagList.add(EAgeGroup.fromCode(userMap.getAge_group()).getName());
	    			}else{
	    				notMatchedTagList.add(EAgeGroup.fromCode(userMap.getAge_group()).getName());
	    			}
	    			//男女
	    			boolean isSex = false;
	    			if(user.getLikeGender()==ELikeGender.BOY.getValue() && userMap.getGender()==1){
	    				matchedTagList.add("男");
	    				isSex = true;
	    			}else if(user.getLikeGender()==ELikeGender.GIRL.getValue() && userMap.getGender()==0){
	    				matchedTagList.add("女");
	    				isSex = true;
	    			}else if(user.getLikeGender()==ELikeGender.ALL.getValue()){
	    				if(userMap.getGender()==1){
	    					matchedTagList.add("男");
	    				}else{
	    					matchedTagList.add("女");
	    				}
	    				isSex = true;
	    			}
	    			if(!isSex){
	    				if(userMap.getGender()==1){
	    					notMatchedTagList.add("男");
	    				}else{
	    					notMatchedTagList.add("女");
	    				}
	    				
	    			}
	    			
	    			if(matchedTagList.size()<10){
	    				userInfo.setTagMatchedLength(matchedTagList.size());		// 匹配长度
	    				matchedTagList.addAll(notMatchedTagList);
	    				if(matchedTagList.size()>10){
	    					matchedTagList = matchedTagList.subList(0, 10);
	    				}
	    			}else{
	    				userInfo.setTagMatchedLength(10);		// 匹配长度
	    				matchedTagList = matchedTagList.subList(0, 10);
	    			}
	    			
	    			userInfo.setUserTags(matchedTagList);
	   				sameTags=matchedTagList.size()>0;
	    		}
    		}
    		if(user.getLikeGender()!=null && userMap.getLike_gender()==user.getLikeGender()){
    			userInfo.setReason(RecommendReason.LIKE_GENDER);
    		}else if(sameTags){
    			userInfo.setReason(RecommendReason.SAME_TAG);
    		}else if(user.getOccupation()!=null && userMap.getOccupation()==user.getOccupation()){
    			userInfo.setReason(RecommendReason.SAME_OCCUPATION);
    		}else if(user.getAgeGroup()!=null && userMap.getAge_group()==user.getAgeGroup()){
    			userInfo.setReason(RecommendReason.SAME_AGE_GROUP);
    		}
    		userList.add(userInfo);
    	}
		return userList;
		
	}
	/**
	 * 取用户ID和用户配置的映射。
	 * @author zhangjiwei
	 * @date Apr 20, 2017
	 * @param uids
	 * @return
	 */
	private Map<Long,UserProfile> getUserMapByUids(List<Long> uids){
		List<UserProfile>  profileList = userService.getUserProfilesByUids(uids);
		Map<Long,UserProfile> userMap = new HashMap<>();
		for(UserProfile user:profileList){
			userMap.put(user.getUid(), user);
		}
		return userMap;
	}
	@Override
	public List<String> getRecommendTagList(String content) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 获取Bean中指定的字段，生成一个数组。
	 * @author zhangjiwei
	 * @date Apr 20, 2017
	 * @param dataList
	 * @param fieldName
	 * @return
	 */
	public <T> List<T> getBeanProperty(Iterable dataList,String fieldName){
		List<T> resultList = new ArrayList<>();
		for(Object t:dataList){
			try {
				Object value = PropertyUtils.getProperty(t, fieldName);
				resultList.add((T) value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultList;
	}
	
	@Override
	public List<RecommendKingdom> getRecommendKingdomList(long uid,int page,int pageSize) {
		UserProfile user = userService.getUserProfileByUid(uid);
		BoolQueryBuilder bq = new BoolQueryBuilder();
		List<String> userHobbyList = searchMapper.getUserHobby(user.getUid());
		if(user!=null){
			String tags= StringUtils.join(userHobbyList," ").trim();
			bq.mustNot(QueryBuilders.termQuery("uid", user.getUid()));
			if(tags!=null){
				bq.should(QueryBuilders.queryStringQuery(tags).field("title"));
				bq.should(QueryBuilders.queryStringQuery(tags).field("tags").boost(3f));
			}
		}
		
		SearchQuery sq = new NativeSearchQuery(bq);
		
		sq.setPageable(new PageRequest(--page, pageSize));
		
		FacetedPage<TopicEsMapping> result = esTemplate.queryForPage(sq, TopicEsMapping.class);
		List<Long> uids = getBeanProperty(result, "uid");
		Map<Long,UserProfile> profileMap = this.getUserMapByUids(uids);
		List<RecommendKingdom> kingdomList= new ArrayList<>();
		for(TopicEsMapping topic:result){
			RecommendKingdom kingdom = new RecommendKingdom();
			org.springframework.beans.BeanUtils.copyProperties(profileMap.get(topic.getUid()), kingdom);
			kingdom.setReason(RecommendReason.SAME_TAG);
			kingdom.setCover(topic.getLive_image());
			kingdom.setTopicId(topic.getId());
			kingdom.setTags(topic.getTags());
			kingdom.setTitle(topic.getTitle());
			
			kingdomList.add(kingdom);
		}
		
		return kingdomList;
	}
	
	@Override
	public List<TopicEsMapping> getTopicEsMappingList(long uid,List<Long> noIds,int page,int pageSize){
		UserProfile user = userService.getUserProfileByUid(uid);
		BoolQueryBuilder bq = new BoolQueryBuilder();
		List<String> userHobbyList = searchMapper.getUserHobby(user.getUid());
		if(user!=null){
			String tags= StringUtils.join(userHobbyList," ").trim();
			bq.mustNot(QueryBuilders.termQuery("uid", user.getUid()));
			if(null != noIds && noIds.size() > 0){//过滤掉不需要的王国id
				bq.mustNot(QueryBuilders.termsQuery("id", noIds));
			}
			if(tags!=null){
				bq.should(QueryBuilders.queryStringQuery(tags).field("title"));
				bq.should(QueryBuilders.queryStringQuery(tags).field("tags").boost(3f));
			}
		}
		
		SearchQuery sq = new NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(--page, pageSize));
		FacetedPage<TopicEsMapping> result = esTemplate.queryForPage(sq, TopicEsMapping.class);
		
		return result.getContent();
	}
}
