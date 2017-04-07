package com.me2me.search.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.me2me.search.ElasticSearchHandler;
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
import com.me2me.search.mappers.HotKeywordMapper;
import com.me2me.search.model.SearchHistoryCount;
import com.me2me.search.model.SearchHistoryCountExample;
import com.me2me.search.service.ContentSearchService;
import com.me2me.search.service.kafka.KafkaTemplate;

import lombok.extern.slf4j.Slf4j;


/**
 * UGC搜索服务实现.
 *
 */
@Slf4j
@Service("contentSearchServiceImpl")
public class ContentSearchServiceImpl implements ContentSearchService {
	private static final String  HOT_KEYWORD_CACHE_KEY="SEARCH_HOT_KEYWORD";
	@Resource(name = "searchHandler")
	private ElasticSearchHandler es;


	@Value("${es.ugc.index}")
	private String ugcIndexName;
	
	@Value("${es.kingdom.index}")
    private String kingdomIndexName;
	
	@Value("${es.persona.index}")
    private String personaIndexName;
	
	@Value("${es.article.index}")
    private String articleIndexName;
	
	@Resource(name="searchHistoryKafkaTemplate")
	private KafkaTemplate<String, String> kafka;
	
	@Resource
    protected RedisTemplate redisTemplate;
	
	@Autowired
	private HotKeywordMapper hotKeywordDao;
	
	
	@Autowired
	private SearchHistoryCountMapper shcMapper;
	/**
	 * 消息缓存。用来记录搜索消息
	 */
	private Queue<SearchHistoryCount> searchHistoryQueue = new LinkedBlockingQueue<>();
	/**
	 * 向kafka发送消息时，用的key.
	 */
	private final static String SEARCH_MSG_KEY="SEARCH_CONTENT";
	
	@Value("${es.searchHistory.index}")
	private String searchHistoryIndexName;

	private static Logger logger = LoggerFactory.getLogger(ContentSearchServiceImpl.class);
	
	/**
	 * 系统启动时启动搜索消息同步线程。
	 * @author zhangjiwei
	 * @date Apr 5, 2017
	 */
	@PostConstruct
	private void init(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleWithFixedDelay(new  Runnable() {
			public void run() {
				try{
					while(searchHistoryQueue.size()>0){
						SearchHistoryCount count = searchHistoryQueue.poll();
						addHistoryCount(count);
					}
				}catch(Exception e){
					log.error("处理搜索消息出错。",e);
				}
			}
		},1,1,TimeUnit.SECONDS);		//1 秒一次。避免队列过大，造成系统崩溃。
	}
	
	@Override
	public SearchResult<SearchResultUGC> queryUGC(String content, int page, int pageSize) {
		
		String exp = "title:"+content+";";
		exp+="content:"+content;
		SearchQuery query = new SearchQuery();
		query.setPageSize(pageSize);
		int startRow = (page-1)*pageSize;
		query.setStartRow(startRow);
		query.setIndex(ugcIndexName);
		query.setTypeName("ugc-content");
		query.setEsExpression(exp);
		HighlightField hf = new HighlightField("title");
		hf.setPreTags(new String[]{"<b style='color:red'>"});
		hf.setPostTags(new String[]{"</b>"});
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
						//System.out.println(e.getMessage());
					}
					ugc.setContent((String) entry.get("content"));
					try {
	                    Integer id = Integer.parseInt((String)entry.get("id"));
	                    ugc.setId(id);
	                } catch(Exception e) {
	                    //
	                }
					ugc.setTitle((String) entry.get("title"));
					try {
	                    ugc.setUid((Integer) entry.get("uid"));
	                } catch(ClassCastException e) {
	                    try {
	                        ugc.setUid(Integer.valueOf((String) entry.get("uid")));
	                    } catch(ClassCastException e1) {
	                        //
	                    }
	                }
					ugc.setUserName((String) entry.get("userName"));
					ugc.setCoverImg((String) entry.get("coverImg"));
					ugc.setPredictType((String)entry.get("predictType"));
					ugc.setContentType(ContentType.UGC);
					ugc.setUnionId((String)entry.get("cid"));
					r2.add(ugc);
				    } catch(Exception e) {
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
		exp+="content:"+content+";";
		exp+="+kingdomId:-1";
		SearchQuery query = new SearchQuery();
		query.setPageSize(pageSize);
		
		int startRow = (page-1)*pageSize;
		
		query.setStartRow(startRow);
		query.setIndex(kingdomIndexName);
		query.setTypeName("kingdom");
		query.setEsExpression(exp);
		
		HighlightField hf = new HighlightField("title");
		hf.setPreTags(new String[]{"<b style='color:red'>"});
		hf.setPostTags(new String[]{"</b>"});
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
						//System.out.println(e.getMessage());
					}
					kingdom.setContent((String) entry.get("content"));
					/*
					if (c.getContent().equals("历史最贵吧？")) {
					    System.out.println(ToStringBuilder.reflectionToString(entry));
					}
					*/
					try {
					    Integer id = Integer.parseInt((String)entry.get("id"));
					    kingdom.setId(id);
					} catch(Exception e) {
					    //e.printStackTrace();
					    continue;
					}
					kingdom.setTitle((String) entry.get("title"));
					try {
					    kingdom.setUid((Integer) entry.get("uid"));
					} catch(ClassCastException e) {
					    try {
					        kingdom.setUid(Integer.valueOf((String) entry.get("uid")));
					    } catch(ClassCastException e1) {
					        //
					    }
					}
					kingdom.setUserName((String) entry.get("userName"));
					kingdom.setKingdomId(Integer.parseInt(entry.get("cid")+""));
					int kid = (Integer)entry.get("kingdomId");
					if(kid!=-1){
						kingdom.setKingdomId(kid);
					}
					kingdom.setContentType(ContentType.KINGDOM);
					kingdom.setKingdomImg((String) entry.get("kingdomImg"));
					kingdom.setCoverImg((String) entry.get("fragmentImg"));
					kingdom.setPredictType((String)entry.get("predictType"));
					kingdom.setUnionId((String)entry.get("cid"));
					r2.add(kingdom);
			    } catch(Exception e) {
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
		exp+="content:"+keyword;
		SearchQuery query = new SearchQuery();
		query.setPageSize(pageSize);
		
		int startRow = (page-1)*pageSize;
		
		query.setStartRow(startRow);
		query.setIndex(articleIndexName);
		query.setTypeName("article");
		query.setEsExpression(exp);
		
		HighlightField hf = new HighlightField("title");
		hf.setPreTags(new String[]{"<b style='color:red'>"});
		hf.setPostTags(new String[]{"</b>"});
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
				String content= (String) entry.get("content");
				content=content.replaceAll("<.*?>","");
				article.setContent(content);
				article.setId((Integer) entry.get("id"));
				article.setTitle((String) entry.get("title"));
				article.setAuthor((String) entry.get("author"));
				article.setCoverImg((String) entry.get("cover"));
				try {
				    article.setCid( entry.get("cid").toString());
				} catch(ClassCastException e) {
				    article.setCid(entry.get("cid").toString());
				}
				r2.add(article);
			    } catch(Exception e) {
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
		int startRow = (page-1)*pageSize;
		
		query.setStartRow(startRow);
		query.setIndex(personaIndexName);
		query.setTypeName("persona");
		query.setEsExpression(exp);
		
		HighlightField hf = new HighlightField("nickName");
		hf.setPreTags(new String[]{"<b style='color:red'>"});
		hf.setPostTags(new String[]{"</b>"});
		hf.setFragmentSize(200);
		query.addHightlightField(hf);

		
		SearchResult<Map<String, Object>> result = es.commonSearchNew(query);
		List<SearchResultUser> result2 = null;
		if (result != null) {
			result2 = new ArrayList<SearchResultUser>();
			for (Map<String, Object> entry : result.getDataList()) {
			    try {
			    	SearchResultUser profile = new SearchResultUser();
					profile.setMobile(entry.get("mobile")+"");
					profile.setPhoto(entry.get("avatar")+"");
					profile.setSex(entry.get("sex")+"");
					profile.setUid(entry.get("uid")+"");
					profile.setUserName(entry.get("nickName")+"");
					profile.setvLv(Integer.parseInt(entry.get("vLv").toString()));
					result2.add(profile);
			    } catch(Exception e) {
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
		
		SearchHistoryCount hc =new SearchHistoryCount();
		hc.setName(searchContent);
		hc.setCreationDate(new Date());
		hc.setLastQueryDate(new Date());
		//kafka.send(SEARCH_MSG_KEY,searchContent);		// 取消消息队列，直接入库
		searchHistoryQueue.add(hc);		// 简单队列。
		logger.debug("send query history:"+searchContent);
	}

	@Override
	public List<String> recommendKeywordList(String keyword,boolean isPinYing, int count) {
	    return es.searchRecommend(keyword, 10);
	}

	@Override
	public List<String> getHotKeywordList(int count) {
		// 从redis 中取热词列表，如果已经过期重新统计。10分钟刷新
		BoundValueOperations vb = redisTemplate.boundValueOps(HOT_KEYWORD_CACHE_KEY);
		try{
			List<String> keyList = (List<String>) vb.get();
			if(keyList==null || keyList.isEmpty()){
				keyList=new ArrayList<String>();
				List<THotKeyword> hotKeyList =hotKeywordDao.getHotKeywords(count);
				if(hotKeyList!=null){
					for(THotKeyword keyword:hotKeyList){
						keyList.add(keyword.getKeyword());
					}
				}
				List<String> strList2= es.getSearchHistoryTopN(20);
				for(String keyword:strList2){
					if(!keyList.contains(keyword)){
						keyList.add(keyword);
					}
				}
				vb.set(keyList,1, TimeUnit.MINUTES);
			}
			return keyList;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	private void addHistoryCount(SearchHistoryCount hc) {
		SearchHistoryCountExample  example = new SearchHistoryCountExample();
		example.createCriteria().andNameEqualTo(hc.getName());
		List<SearchHistoryCount> dbs = shcMapper.selectByExample(example);
		if(dbs.size()>0){
			SearchHistoryCount dbCount = dbs.get(0);
			dbCount.setLastQueryDate(hc.getLastQueryDate());
			dbCount.setSearchCount(dbCount.getSearchCount()+1);
			shcMapper.updateByPrimaryKeySelective(dbCount);
		}else{
			hc.setSearchCount(1);
			shcMapper.insertSelective(hc);
		}
	}

}
