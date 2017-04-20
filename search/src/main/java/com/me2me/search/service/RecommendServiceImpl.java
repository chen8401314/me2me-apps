package com.me2me.search.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import com.me2me.common.utils.DateUtil;
import com.me2me.search.ThreadPool;
import com.me2me.search.constants.IndexConstants;
import com.me2me.search.dao.ContentForSearchJdbcDao;
import com.me2me.search.esmapping.TopicEsMapping;
import com.me2me.search.esmapping.UserEsMapping;
import com.me2me.search.esmapping.UserFeatureMapping;
import com.me2me.search.mapper.SearchHistoryCountMapper;
import com.me2me.search.mapper.SearchMapper;
import com.me2me.search.mapper.SearchVarMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecommendServiceImpl implements RecommendService {
	static final String HOT_KEYWORD_CACHE_KEY = "SEARCH_HOT_KEYWORD";
	static final String DEFAULT_START_TIME = "1900-01-01 00:00:00";
	static final String DATE_FORMAT="yyyy-MM-dd hh:mm:ss";
	@Autowired
	protected SearchVarMapper varMapper;
	
	@Autowired
	protected SearchHistoryCountMapper shcMapper;

	@Autowired
	protected ElasticsearchTemplate esTemplate;
	
	@Autowired
	protected SearchMapper searchMapper;
    @Autowired
    protected ContentForSearchJdbcDao searchJdbcDao;
	
	private final static SimpleDateFormat BIRTHDAY_FORMATER=new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	protected ElasticSearchIndexHelper indexHelper;
	
	@Override
	public List<UserFeatureMapping> getRecommendUserList(int uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getRecommendTagList(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TopicEsMapping> getRecommendKingomList(int uid) {
		// TODO Auto-generated method stub
		return null;
	}
/*	private String getAgeGroup(String birthday){
		try {
			Date birDate = BIRTHDAY_FORMATER.parse(birthday);
			Calendar cd = Calendar.getInstance();
			cd.setTime(birDate);
			int year= cd.get(Calendar.YEAR);
			if(year<1980){
				return "活化石";
			}else if(year)
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	@Override
	public void indexUserFeature(boolean fully) {
		ThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				log.info("indexUserData started");
				String indexName = IndexConstants.USER_FEATURE_INDEX_NAME;
				String beginDate = indexHelper.preIndex(fully, indexName);
				String endDate = DateUtil.date2string(new Date(), DATE_FORMAT);
				int count = 0;
				int skip = 0;
				int batchSize = 1000;
				esTemplate.putMapping(UserEsMapping.class);
				while (true) {
					List<UserEsMapping> users = searchMapper.getUserPageByUpdateDate(beginDate, endDate, skip,batchSize);
					if (users == null || users.isEmpty()) {
						break;
					}
					List<IndexQuery> indexList = new ArrayList<>();
					for (UserEsMapping data : users) {
						IndexQuery query = new IndexQuery();
						UserFeatureMapping ufm = new UserFeatureMapping();
						ufm.setAge_group("");
						ufm.setAvatar(data.getAvatar());
						ufm.setGender(data.getGender());
						ufm.setId(data.getId());
						ufm.setLike_content_type("");
						ufm.setLike_gender("");
						ufm.setLike_tags("");
						ufm.setNick_name(data.getNick_name());
						ufm.setOccupation("");
						ufm.setV_lv(data.getV_lv());
						
						String key = data.getUid() + "";

						query.setId(key);
						query.setObject(ufm);
						query.setIndexName(indexName);
						query.setType(indexName);
						indexList.add(query);
					}
					esTemplate.bulkIndex(indexList);

					skip += batchSize;
					count += users.size();
					log.info("indexUserData processed:" + count);
				}
				indexHelper.updateVarVal(indexName, endDate);
				log.info("indexUserData finished.");
			}
		});

	}

}