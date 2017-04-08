package com.me2me.search.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
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
import com.me2me.common.utils.DateUtil;
import com.me2me.search.cache.SimpleCache;
import com.me2me.search.constants.IndexConstants;
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

import lombok.extern.slf4j.Slf4j;

/**
 * UGC搜索服务实现.
 *
 */
@Slf4j
@Service("contentSearchServiceImpl")
public class ContentSearchServiceImpl implements ContentSearchService {
	
	private static final String HOT_KEYWORD_CACHE_KEY = "SEARCH_HOT_KEYWORD";
	private static final String DEFAULT_START_TIME = "1900-01-01 00:00:00";
	private static final String DATE_FORMAT="yyyy-MM-dd hh:mm:ss";
	@Autowired
	private Client client;
	@Autowired
	private SearchVarMapper varMapper;
	
	@Autowired
	private SearchHistoryCountMapper shcMapper;

	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
	private SearchMapper searchMapper;

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
		bq.should(QueryBuilders.queryStringQuery(content).field("title").boost(3f));
		bq.should(QueryBuilders.queryStringQuery(content).field("content").boost(2f));
		
		SearchQuery sq = new NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(page, pageSize));
		FacetedPage<UgcEsMapping> result = esTemplate.queryForPage(sq, UgcEsMapping.class);
		return result;
	}

	@Override
	public FacetedPage<TopicEsMapping> queryKingdom(String content, int page, int pageSize) {

		BoolQueryBuilder bq = new BoolQueryBuilder();
		bq.should(QueryBuilders.queryStringQuery(content).field("title").boost(3f));
		bq.should(QueryBuilders.queryStringQuery(content).field("summary").boost(2f));
		bq.should(QueryBuilders.queryStringQuery(content).field("fragments").boost(1f));
		
		SearchQuery sq = new NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(page, pageSize));
		FacetedPage<TopicEsMapping> result = esTemplate.queryForPage(sq, TopicEsMapping.class);
		return result;
	}

	@Override
	public FacetedPage<UserEsMapping> queryUsers(String content, int page, int pageSize) {
		BoolQueryBuilder bq = new BoolQueryBuilder();
		bq.should(QueryBuilders.queryStringQuery(content).field("nick_name").boost(3f));
		bq.should(QueryBuilders.queryStringQuery(content).field("introduced").boost(2f));
		
		SearchQuery sq = new NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(page, pageSize));
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
				.should(new PrefixQueryBuilder("keyword", keyword))
				.should(new PrefixQueryBuilder("pin_yin_short", keyword))
				.should(new PrefixQueryBuilder("pin_yin", keyword));
		
		NativeSearchQuery sq =new  NativeSearchQuery(bq);
		sq.setPageable(new PageRequest(1, 20));
		
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
			sq.setPageable(new PageRequest(1, esCount));
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
		ClusterAdminClient  cc = client.admin().cluster();
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
		String indexName = IndexConstants.USER_INDEX_NAME;
		String beginDate = preIndex(fully, indexName);
		String endDate = DateUtil.date2string(new Date(), DATE_FORMAT);
		int count = 0;
		int skip=0;
		int batchSize= 1000;
		esTemplate.putMapping(UserEsMapping.class);
		while (true) {
			
			List<UserEsMapping> users =searchMapper.getUserPageByUpdateDate(beginDate, endDate, skip, batchSize);
			if (users == null || users.isEmpty()) {
				break;
			}
			List<IndexQuery> indexList = new ArrayList<>();
			for (UserEsMapping data : users) {
				IndexQuery query = new IndexQuery();
				String key = data.getUid()+"";
				
				query.setId(key);
				query.setObject(data);
				query.setIndexName(indexName);
				query.setType(indexName);
				indexList.add(query);
			}
			esTemplate.bulkIndex(indexList);
			
			skip += batchSize;
			count += users.size();
		}
		updateVarVal(indexName, endDate);
		return count;
	}
	@Override
	public int indexUgcData(boolean fully) throws Exception {
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
		}
		updateVarVal(indexName, endDate);
		return count;
	}

	@Override
	public int indexKingdomData(boolean fully) throws Exception {
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
			List<IndexQuery> indexList = new ArrayList<>();
			for (TopicEsMapping data : kingdomList) {
				IndexQuery query = new IndexQuery();
				String key = data.getId()+"";
				List<String> commentList = searchMapper.getKingdomFragmentsByTopicId(Integer.parseInt(key));
				String comments = StringUtils.join(commentList," \n");
				data.setFragments(comments);
				query.setId(key);
				query.setObject(data);
				query.setIndexName(indexName);
				query.setType(indexName);
				indexList.add(query);
			}
			
			esTemplate.bulkIndex(indexList);
			
			skip += batchSize;
			count += kingdomList.size();
		}
		updateVarVal(indexName, endDate);
		return count;
	}
	
	
	@Override
	public int indexSearchHistory(boolean fully) throws Exception {
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
		}
		updateVarVal(indexName, endDate);
		return count;
	}

}
