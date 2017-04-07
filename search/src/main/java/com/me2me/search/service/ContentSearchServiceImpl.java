package com.me2me.search.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.me2me.common.page.PageBean;
import com.me2me.common.utils.DateUtil;
import com.me2me.search.ElasticSearchHandler;
import com.me2me.search.cache.SimpleCache;
import com.me2me.search.constants.IndexConstants;
import com.me2me.search.dto.HighlightField;
import com.me2me.search.dto.SearchQuery;
import com.me2me.search.dto.SearchResult;
import com.me2me.search.dto.SearchResultArticle;
import com.me2me.search.dto.SearchResultKingdom;
import com.me2me.search.dto.SearchResultUGC;
import com.me2me.search.dto.SearchResultUser;
import com.me2me.search.dto.THotKeyword;
import com.me2me.search.enums.ContentType;
import com.me2me.search.mapper.SearchHistoryCountMapper;
import com.me2me.search.mapper.SearchVarMapper;
import com.me2me.search.mappers.SearchHotKeywordMapper;
import com.me2me.search.model.SearchHistoryCount;
import com.me2me.search.model.SearchHistoryCountExample;
import com.me2me.user.dto.SearchUserDto;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * UGC搜索服务实现.
 *
 */
@Slf4j
@Service("contentSearchServiceImpl")
public class ContentSearchServiceImpl implements ContentSearchService {
	private static final String USER_INDEX_DATE_KEY = "USER_LAST_INDEX_DATE";
	private static final String KINGDOM_LAST_INDEX_DATE_KEY = "KINGDOM_LAST_INDEX_DATE";
	private static final String UGC_LAST_INDEX_DATE_KEY = "UGC_LAST_INDEX_DATE";
	private static final String SEARCH_HISTORY_LAST_INDEX_DATE_KEY = "SEARCH_HISTORY_LAST_INDEX_DATE";

	private static final String HOT_KEYWORD_CACHE_KEY = "SEARCH_HOT_KEYWORD";
	private static final String DEFAULT_START_TIME = "1900-01-01";
	@Resource(name = "searchHandler")
	private ElasticSearchHandler es;
	@Autowired
	private SearchVarMapper varMapper;

	@Autowired
	private SearchHotKeywordMapper hotKeywordDao;

	@Autowired
	private SearchHistoryCountMapper shcMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private ElasticsearchTemplate esTemplate;

	/**
	 * 消息缓存。用来记录搜索消息
	 */
	private Queue<SearchHistoryCount> searchHistoryQueue = new LinkedBlockingQueue<>();
	/**
	 * 向kafka发送消息时，用的key.
	 */
	private final static String SEARCH_MSG_KEY = "SEARCH_CONTENT";

	@Value("${es.searchHistory.index}")
	private String searchHistoryIndexName;

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
	public SearchResult<SearchResultUGC> queryUGC(String content, int page, int pageSize) {

		String exp = "title:" + content + ";";
		exp += "content:" + content;
		SearchQuery query = new SearchQuery();
		query.setPageSize(pageSize);
		int startRow = (page - 1) * pageSize;
		query.setStartRow(startRow);
		query.setIndex(IndexConstants.UGC_INDEX_NAME);
		query.setTypeName("ugc-content");
		query.setEsExpression(exp);
		HighlightField hf = new HighlightField("title");
		hf.setPreTags(new String[] { "<b style='color:red'>" });
		hf.setPostTags(new String[] { "</b>" });
		hf.setFragmentSize(200);
		query.addHightlightField(hf);
		SearchResult<Map<String, Object>> result = es.commonSearchNew(query);

		List<SearchResultUGC> r2 = null;
		if (result != null) {
			r2 = new ArrayList<SearchResultUGC>();
			for (Map<String, Object> entry : result.getDataList()) {
				try {
					SearchResultUGC ugc = new SearchResultUGC();
					String date = (String) entry.get("createTime");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
					try {
						date = date.replace("Z", " UTC");// 注意是空格+UTC
						Date creationDate = sdf.parse(date);
						ugc.setCreationDate(creationDate);
					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
					ugc.setContent((String) entry.get("content"));
					try {
						Integer id = Integer.parseInt((String) entry.get("id"));
						ugc.setId(id);
					} catch (Exception e) {
						//
					}
					ugc.setTitle((String) entry.get("title"));
					try {
						ugc.setUid((Integer) entry.get("uid"));
					} catch (ClassCastException e) {
						try {
							ugc.setUid(Integer.valueOf((String) entry.get("uid")));
						} catch (ClassCastException e1) {
							//
						}
					}
					ugc.setUserName((String) entry.get("userName"));
					ugc.setCoverImg((String) entry.get("coverImg"));
					ugc.setPredictType((String) entry.get("predictType"));
					ugc.setContentType(ContentType.UGC);
					ugc.setUnionId((String) entry.get("cid"));
					r2.add(ugc);
				} catch (Exception e) {
					logger.error("UGC查询转换错误", e);
				}
			}

		}
		SearchResult sr = new SearchResult(query.getTotalItem(), r2);
		sr.setAllItemCount(result.getAllItemCount());
		return sr;
	}

	@Override
	public SearchResult<SearchResultKingdom> queryKingdom(String content, int page, int pageSize) {

		String exp = "title:" + content + ";";
		exp += "content:" + content + ";";
		exp += "+kingdomId:-1";
		SearchQuery query = new SearchQuery();
		query.setPageSize(pageSize);

		int startRow = (page - 1) * pageSize;

		query.setStartRow(startRow);
		query.setIndex(IndexConstants.KINGDOM_INDEX_NAME);
		query.setTypeName("kingdom");
		query.setEsExpression(exp);

		HighlightField hf = new HighlightField("title");
		hf.setPreTags(new String[] { "<b style='color:red'>" });
		hf.setPostTags(new String[] { "</b>" });
		hf.setFragmentSize(200);
		query.addHightlightField(hf);

		SearchResult<Map<String, Object>> result = es.commonSearchNew(query);
		List<SearchResultKingdom> r2 = null;
		if (result != null) {
			r2 = new ArrayList<SearchResultKingdom>();
			for (Map<String, Object> entry : result.getDataList()) {
				try {
					SearchResultKingdom kingdom = new SearchResultKingdom();
					String date = (String) entry.get("createTime");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
					try {
						date = date.replace("Z", " UTC");// 注意是空格+UTC
						Date creationDate = sdf.parse(date);
						kingdom.setCreationDate(creationDate);
					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
					kingdom.setContent((String) entry.get("content"));
					/*
					 * if (c.getContent().equals("历史最贵吧？")) {
					 * System.out.println(ToStringBuilder.reflectionToString(
					 * entry)); }
					 */
					try {
						Integer id = Integer.parseInt((String) entry.get("id"));
						kingdom.setId(id);
					} catch (Exception e) {
						// e.printStackTrace();
						continue;
					}
					kingdom.setTitle((String) entry.get("title"));
					try {
						kingdom.setUid((Integer) entry.get("uid"));
					} catch (ClassCastException e) {
						try {
							kingdom.setUid(Integer.valueOf((String) entry.get("uid")));
						} catch (ClassCastException e1) {
							//
						}
					}
					kingdom.setUserName((String) entry.get("userName"));
					kingdom.setKingdomId(Integer.parseInt(entry.get("cid") + ""));
					int kid = (Integer) entry.get("kingdomId");
					if (kid != -1) {
						kingdom.setKingdomId(kid);
					}
					kingdom.setContentType(ContentType.KINGDOM);
					kingdom.setKingdomImg((String) entry.get("kingdomImg"));
					kingdom.setCoverImg((String) entry.get("fragmentImg"));
					kingdom.setPredictType((String) entry.get("predictType"));
					kingdom.setUnionId((String) entry.get("cid"));
					r2.add(kingdom);
				} catch (Exception e) {
					logger.error("王国查询转换错误", e);
				}
			}

		}
		SearchResult sr = new SearchResult(query.getTotalItem(), r2);
		sr.setAllItemCount(result.getAllItemCount());
		return sr;
	}

	@Override
	public SearchResult<SearchResultArticle> queryArticle(String keyword, int page, int pageSize) {

		String exp = "title:" + keyword + ";";
		exp += "content:" + keyword;
		SearchQuery query = new SearchQuery();
		query.setPageSize(pageSize);

		int startRow = (page - 1) * pageSize;

		query.setStartRow(startRow);
		query.setIndex(IndexConstants.ARTICLE_INDEX_NAME);
		query.setTypeName("article");
		query.setEsExpression(exp);

		HighlightField hf = new HighlightField("title");
		hf.setPreTags(new String[] { "<b style='color:red'>" });
		hf.setPostTags(new String[] { "</b>" });
		hf.setFragmentSize(200);

		query.addHightlightField(hf);

		SearchResult<Map<String, Object>> result = es.commonSearchNew(query);
		List<SearchResultArticle> r2 = null;
		if (result != null) {
			r2 = new ArrayList<SearchResultArticle>();
			for (Map<String, Object> entry : result.getDataList()) {
				try {
					SearchResultArticle article = new SearchResultArticle();

					String date = (String) entry.get("releaseDate");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {

						date = date.replace("Z", " UTC");// 注意是空格+UTC
						Date creationDate = sdf.parse(date);
						article.setCreationDate(creationDate);
					} catch (Exception e) {
						//
					}
					article.setUrl((String) entry.get("url"));
					article.setCategory((String) entry.get("tag"));
					String content = (String) entry.get("content");
					content = content.replaceAll("<.*?>", "");
					article.setContent(content);
					article.setId((Integer) entry.get("id"));
					article.setTitle((String) entry.get("title"));
					article.setAuthor((String) entry.get("author"));
					article.setCoverImg((String) entry.get("cover"));
					try {
						article.setCid(entry.get("cid").toString());
					} catch (ClassCastException e) {
						article.setCid(entry.get("cid").toString());
					}
					r2.add(article);
				} catch (Exception e) {
					logger.error("文章查询转换错误", e);
				}
			}
		}

		SearchResult sr = new SearchResult(query.getTotalItem(), r2);
		sr.setAllItemCount(result.getAllItemCount());
		return sr;
	}

	@Override
	public SearchResult<SearchResultUser> queryUsers(String keyword, int page, int pageSize) {

		String exp = "nickName:" + keyword + ";";
		SearchQuery query = new SearchQuery();
		query.setPageSize(pageSize);
		int startRow = (page - 1) * pageSize;

		query.setStartRow(startRow);
		query.setIndex(IndexConstants.USER_INDEX_NAME);
		query.setTypeName("persona");
		query.setEsExpression(exp);

		HighlightField hf = new HighlightField("nickName");
		hf.setPreTags(new String[] { "<b style='color:red'>" });
		hf.setPostTags(new String[] { "</b>" });
		hf.setFragmentSize(200);
		query.addHightlightField(hf);

		SearchResult<Map<String, Object>> result = es.commonSearchNew(query);
		List<SearchResultUser> result2 = null;
		if (result != null) {
			result2 = new ArrayList<SearchResultUser>();
			for (Map<String, Object> entry : result.getDataList()) {
				try {
					SearchResultUser profile = new SearchResultUser();
					profile.setMobile(entry.get("mobile") + "");
					profile.setPhoto(entry.get("avatar") + "");
					profile.setSex(entry.get("sex") + "");
					profile.setUid(entry.get("uid") + "");
					profile.setUserName(entry.get("nickName") + "");
					profile.setvLv(Integer.parseInt(entry.get("vLv").toString()));
					result2.add(profile);
				} catch (Exception e) {
					logger.error("文章查询转换错误", e);
				}
			}
		}

		SearchResult sr = new SearchResult(query.getTotalItem(), result2);
		sr.setAllItemCount(result.getAllItemCount());
		return sr;
	}

	@Override
	public void logSearchHistory(String searchContent) {

		SearchHistoryCount hc = new SearchHistoryCount();
		hc.setName(searchContent);
		hc.setCreationDate(new Date());
		hc.setLastQueryDate(new Date());
		// kafka.send(SEARCH_MSG_KEY,searchContent); // 取消消息队列，直接入库
		searchHistoryQueue.add(hc); // 简单队列。
		logger.debug("send query history:" + searchContent);
	}

	@Override
	public List<String> recommendKeywordList(String keyword, boolean isPinYing, int count) {
		return es.searchRecommend(keyword, 10);
	}

	@Override
	public List<String> getHotKeywordList(int count) {
		// 从redis 中取热词列表，如果已经过期重新统计。10分钟刷新
		List<String> keyList = (List<String>) cache.getCache(HOT_KEYWORD_CACHE_KEY);
		if (keyList == null || keyList.isEmpty()) {
			keyList = new ArrayList<String>();
			List<THotKeyword> hotKeyList = hotKeywordDao.getHotKeywords(count);
			if (hotKeyList != null) {
				for (THotKeyword keyword : hotKeyList) {
					keyList.add(keyword.getKeyword());
				}
			}
			List<String> strList2 = es.getSearchHistoryTopN(20);
			for (String keyword : strList2) {
				if (!keyList.contains(keyword)) {
					keyList.add(keyword);
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

	public void updateVarVal(String key, String val) {
		if (varMapper.existsVar(key)) {
			varMapper.updateVar(key, val);
		} else {
			varMapper.addVar(key, val);
		}
	}

	public String getVarVal(String key) {
		return varMapper.getVar(key);
	}

	@Override
	public int indexUserData(boolean fully) throws Exception {
		String beginDate = varMapper.getVar(USER_INDEX_DATE_KEY);
		if (beginDate == null) {
			beginDate = "1900-01-01";
		} else {
			Date begin = DateUtil.string2date(beginDate, "yyyy-MM-dd");
			beginDate = DateUtil.date2string(DateUtils.addDays(begin, 1), "yyyy-MM-dd");
		}
		String endDate = DateUtil.date2string(new Date(), "yyyy-MM-dd");
		Map<String, Object> params = new HashMap<>();
		params.put("update_time_min", beginDate);
		params.put("update_time_max", endDate);
		int count = 0;
		int i = 1;
		while (true) {
			PageBean page = new PageBean<>();
			page.setCurrentPage(i);
			PageBean<SearchUserDto> users = userService.searchUserPage(page, params);

			if (users == null || users.getDataList().size() == 0) {
				break;
			}
			count += users.getDataList().size();
			i++;
		}
		varMapper.updateVar(USER_INDEX_DATE_KEY, endDate);
		return count;
	}

	@Override
	public int indexUgcData(boolean fully) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexKingdomData(boolean fully) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexSearchHistory(boolean fully) throws Exception {
		String beginDate = DEFAULT_START_TIME;
		if (fully) {
			esTemplate.deleteIndex(IndexConstants.SEARCH_HISTORY_INDEX_NAME);
		} else {
			beginDate = varMapper.getVar(USER_INDEX_DATE_KEY);
			if (beginDate != null) {
				Date begin = DateUtil.string2date(beginDate, "yyyy-MM-dd");
				beginDate = DateUtil.date2string(DateUtils.addDays(begin, 1), "yyyy-MM-dd");
			} else {
				beginDate = DEFAULT_START_TIME;
			}
		}
		if (!esTemplate.indexExists(IndexConstants.SEARCH_HISTORY_INDEX_NAME)) {
			es.createIndex(IndexConstants.SEARCH_HISTORY_INDEX_NAME);
		}

		String endDate = DateUtil.date2string(new Date(), "yyyy-MM-dd");
		int skip = 0;
		int batchSize = 1000;
		int count = 0;
		while (true) {
			List<Map<String, Object>> pageList = shcMapper.selectPageByDate(beginDate, endDate, skip, batchSize);
			skip += batchSize;
			if (null == pageList || pageList.size() == 0) {
				break;
			}
			List<IndexQuery> indexList = new ArrayList<>();
			for (Map<String, Object> data : pageList) {
				IndexQuery query = new IndexQuery();
				String key = (String) data.get("name");
				try {
					String pinyin = PinyinHelper.convertToPinyinString(key, "", PinyinFormat.WITHOUT_TONE);
					String py = PinyinHelper.getShortPinyin(key); // nhsj
					data.put("pin_yin", pinyin);
					data.put("pin_yin_short", py);
				} catch (PinyinException e) {
					e.printStackTrace();
				}
				query.setId(key);
				query.setObject(data);
				query.setIndexName(IndexConstants.SEARCH_HISTORY_INDEX_NAME);
				query.setType(IndexConstants.SEARCH_HISTORY_INDEX_NAME);
				indexList.add(query);
			}
			esTemplate.bulkIndex(indexList);
			count += pageList.size();
		}
		varMapper.updateVar(SEARCH_HISTORY_LAST_INDEX_DATE_KEY, endDate);
		return count;
	}

}
