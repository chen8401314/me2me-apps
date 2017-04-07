package com.me2me.search.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightBuilder.Field;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.me2me.search.ElasticSearchHandler;
import com.me2me.search.constants.IndexConstants;
import com.me2me.search.dto.HighlightField;
import com.me2me.search.dto.QueryOrder;
import com.me2me.search.dto.SearchQuery;
import com.me2me.search.dto.SearchResult;

/**
 * 老徐专用 。
 * @author zhangjiwei
 * @date Apr 7, 2017
 */
@Deprecated
//@Service("searchHandler")
public class ElasticSearchHandlerImpl implements ElasticSearchHandler {

    private static Logger logger = LoggerFactory.getLogger(ElasticSearchHandlerImpl.class);
    
    @Autowired
    private ElasticsearchTemplate template;
    
    private Client client;

    /**
     * 创建索引客户端
     * 
     * @param ipAddress
     */
    public ElasticSearchHandlerImpl() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.plusnet.search.client.ElasticSearchHandler#createIndex(java.lang.
     * String)
     */
    @Override
    public void createIndex(String indexName) {
		if(! template.indexExists(indexName)){
		     template.createIndex(indexName);
		}
    }

    /**
     * 创建索引结构（type和mapping)
     */
    public void createMapping(String indexName, String type, XContentBuilder builder) {
        PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(type).source(builder);
        client.admin().indices().putMapping(mapping).actionGet();
    }

    public void bulkIndex(String indexName,String type, List<String> jsonData) {
        // createIndex(indexName);
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder().startObject().startObject(type).endObject();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        createMapping(indexName, type, builder);
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName, type).setRefresh(true);
        logger.trace("创建索引=========");
        for (int i = 0; i < jsonData.size(); i++) {
            bulkRequest.add(requestBuilder.setSource(jsonData.get(i)));
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
            for (BulkItemResponse item : bulkResponse.getItems()) {
                if (item.isFailed()) {
                    logger.debug(item.getFailureMessage());
                }
            }
        }
    }


    /**
     * 建立索引到指定的index
     * 
     * @param indexName
     * @param type
     * @param jsonData
     * @throws IOException
     */
    public void addIndexData(String indexName, String type, List<String> jsonData) throws IOException {
        // createIndex(indexName);
        XContentBuilder builder = null;

        builder = XContentFactory.jsonBuilder().startObject().startObject(type).endObject();
        

        createMapping(indexName, type, builder);
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName, type).setRefresh(true);
        if (logger.isTraceEnabled())
        logger.trace("创建索引[{}-{}]: {}", indexName, type, jsonData);
        
        for (int i = 0; i < jsonData.size(); i++) {
            requestBuilder.setSource(jsonData.get(i)).execute().actionGet();
        }
        // client.close();
    }
    
    private SearchHit[] _searcher(QueryBuilder queryBuilder, String indexName, String type, int offset, int pageSize) {
        return this._searcher(queryBuilder, indexName, type, offset, pageSize, false);
    }
   
    private SearchHit[] _searcher(QueryBuilder queryBuilder, String indexName, String type, int offset, int pageSize, boolean sorted) {
        SearchRequestBuilder srb = client.prepareSearch(indexName).setTypes(type);
        if (sorted) {
            FieldSortBuilder fsb = new FieldSortBuilder("level");
            fsb.order(SortOrder.DESC);
            fsb.unmappedType("integer");
            srb.addSort(fsb);
        }
        SearchResponse searchResponse = srb.setFrom(offset)
                .setSize(pageSize).setQuery(queryBuilder).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        logger.debug("查询记录条数：{}", hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
        return searchHists;
    }
    
    private SearchHits _searcher2(QueryBuilder queryBuilder, String indexName, String type, int offset, int pageSize) {
        return this._searcher2(queryBuilder, indexName, type, offset, pageSize, false);
    }
    
    private SearchHits _searcher2(QueryBuilder queryBuilder, String indexName, String type, int offset, int pageSize, boolean sorted) {
        SearchRequestBuilder srb = client.prepareSearch(indexName).setTypes(type);
        if (sorted) {
            FieldSortBuilder fsb = new FieldSortBuilder("level");
            fsb.order(SortOrder.DESC);
            fsb.unmappedType("integer");
            srb.addSort(fsb);
        }
        SearchResponse searchResponse = srb.setFrom(offset)
                .setSize(pageSize).setQuery(queryBuilder).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        
        return hits;
    }
    
    /**
     * 2016-10-09 zhangjiwei:此查询会将 总命中数添加到query参数中去,弥补之前的查询没有总数返回的问题。
     * @param queryBuilder
     * @param indexName
     * @param type
     * @param offset
     * @param pageSize
     * @param sorted
     * @param query 
     * @return
     */
    private SearchHit[] _searcher(QueryBuilder queryBuilder, SearchQuery query) {
        SearchHits hits = _searchPage(queryBuilder, query);
        logger.debug("查询记录条数：{}", hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
        query.setTotalItem(hits.getTotalHits());
        return searchHists;
    }

    private SearchHits _searchPage(QueryBuilder queryBuilder, SearchQuery query) {
        String indexName = query.getIndex();
    	 int offset = query.getStartRow();
         int pageSize = query.getPageSize();
         boolean sorted =false;
         String type=query.getTypeName();
        SearchRequestBuilder srb = client.prepareSearch(indexName).setTypes(type);
        // highlight
        List<HighlightField> highlights=query.getHightlightFields();
    	for(HighlightField hf:highlights){
    		Field fd =new Field(hf.getName());
    		fd.preTags(hf.getPreTags())
    		.postTags(hf.getPostTags())
    		.fragmentSize(hf.getFragmentSize())
    		.numOfFragments(hf.getNumOfFragments())
    		.fragmentOffset(hf.getFragmentOffset());
    		srb.addHighlightedField(fd);
    	}
    	// order
    	List<QueryOrder> orderList = query.getOrders();
    	for(QueryOrder order:orderList){
    		SortOrder so = order.isAsc()?SortOrder.ASC:SortOrder.DESC;
    		srb.addSort(order.getOrderBy(),so);
    	}
    	// field
    	List<String> fieldList = query.getLoadFieldList();
    	for(String field:fieldList){
    		srb.addField(field);
    	}
    	
        if (sorted) {
            FieldSortBuilder fsb = new FieldSortBuilder("level");
            fsb.order(SortOrder.DESC);
            fsb.unmappedType("integer");
            srb.addSort(fsb);
        }
        SearchResponse searchResponse = srb.setFrom(offset)
                .setSize(pageSize).setQuery(queryBuilder).execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        return hits;
    }
    

    //FIXME 性能过差
    /*
    public List<ContentTO> searchArticle(SearchQuery query) {

        int offset = query.calStartRow();
        int pageSize = query.getPageSize();
   
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        String classify = query.getClassifyId();
        if (StringUtils.isNotBlank(classify)) {
            QueryBuilder queryBuilder = QueryBuilders.matchPhrasePrefixQuery("classifyId", classify);
            //QueryBuilder queryBuilder = QueryBuilders.termQuery("classifyId", classify);
            boolBuilder.must(queryBuilder);
        } else if (query.getContent() != null && query.getContent().getSource() != null) {
            int sourceId = query.getContent().getSource().getId();
            QueryBuilder queryBuilder = QueryBuilders.termQuery("sourceId", sourceId);
            logger.debug("执行查询：id=[{}] page:[{} {}]", sourceId, offset, pageSize);
            boolBuilder.must(queryBuilder);
        }

        
        String tags = null;
        String keywords = null;
        
        if (!StringUtil.isBlank(query.getTags())) {

            tags = query.getTags();

            QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("tag", tags);
            boolBuilder.should(queryBuilder); //如果按分类将完全匹配

        }
        
        if (!StringUtil.isEmpty(query.getKeywords())) {

            keywords = query.getKeywords();

            QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("keywords", keywords);
            boolBuilder.should(queryBuilder);

        }
        
        logger.debug("执行查询：classify=[{}] tags=[{}] keywords=[{}] page:[{} {}]", classify, query.getTags(),
                query.getKeywords(), offset, pageSize);
       

        SearchHit[] result = _searcher(boolBuilder, indexName, "article", offset, pageSize);
        List<ContentTO> lists = doResult(query, result);
        return lists;
    }
    */
    
    private String getStringJsonValue(JSONObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            return jsonObject.getString(key);
        }
        return null;
    }

    private int getIntJsonValue(JSONObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            try {
                return Integer.parseInt(jsonObject.getString(key));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }



        @Override
	public void addIndexData(String indexName, String type,
			Map<String, String> idJsonMap) throws IOException {
		XContentBuilder builder = null;

        builder = XContentFactory.jsonBuilder().startObject().startObject(type).endObject();
        

        createMapping(indexName, type, builder);
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName, type).setRefresh(true);
        
        for(Map.Entry<String, String> entry : idJsonMap.entrySet()){
        	requestBuilder.setId(entry.getKey());
        	requestBuilder.setSource(entry.getValue()).execute().actionGet();
        }
	}


    @Override
	public SearchResult<Map<String, Object>> commonSearchNew(SearchQuery query) {
    	SearchHits hits = basePageSearch(query);
    	if(hits!=null){
	    	SearchResult<Map<String, Object>> sr = new SearchResult();
	    	sr.setCurPage(query.getCurrentPage());
	    	sr.setPageSize(query.getPageSize());
	    	sr.setAllItemCount(hits.getTotalHits());
    	
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        SearchHit[] result = hits.getHits();
	        if(null != result){
	        	for(SearchHit hit : result){
	        		Map<String, Object> map = hit.getSource();
	        		if(null != map){
	        			for(Entry<String, org.elasticsearch.search.highlight.HighlightField> entry :hit.getHighlightFields().entrySet()){
		        			String key = entry.getKey();
		        			org.elasticsearch.search.highlight.HighlightField field = entry.getValue();
		        			
		        			/*
		        			String content = map.get(key).toString();
		        			// 去除杂质
		        			content=content.replaceAll("<.*?>","").replaceAll("\\s+", "");
		        			BytesStreamInput is = null;
		        			try {
		        				is = new BytesStreamInput(content.getBytes()); 
		        				field.readFrom(is);
							} catch (IOException e) {
								e.printStackTrace();
							}finally{
								try {
									if(is!=null) is.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							*/
	        				String str = StringUtils.join(field.getFragments(),"...");
	        				map.put(key,str);
		        		}
	        			list.add(map);
	        		}
	        	}
	        
	        }
	        sr.setDataList(list);
	        return sr;
    	}
		return null;
	}

	private SearchHit[] baseSearch(SearchQuery query) {
        SearchHits hits = basePageSearch(query);
        
        SearchHit[] result = hits == null ? null : hits.getHits();
        return result;
    }

    private SearchHits basePageSearch(SearchQuery query) {
        if(null == query || StringUtils.isBlank(query.getIndex())
				|| StringUtils.isBlank(query.getTypeName())
				|| StringUtils.isBlank(query.getEsExpression())){
			logger.debug("查询index或type或es表达式为空");
			return null;
		}
		
        if (logger.isDebugEnabled())
		logger.debug("执行查询：index=[{}], type=[{}], tags=[{}]", query.getIndex(), query.getTypeName(), query.getEsExpression());
		
		
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        
        boolean flag = false;
        if(StringUtils.isNotBlank(query.getEsExpression())){
        	String[] tmp = query.getEsExpression().split(";");
        	String[] tmp2 = null;
        	String key = null;
        	String value = null;
        	QueryBuilder queryBuilder = null;
        	for(String t : tmp){
        		if(StringUtils.isNotBlank(t)){
        			tmp2 = t.split(":");
        			if(tmp2.length == 2){
        				if(tmp2[0].startsWith(">=") || tmp2[0].startsWith("<=")){
        					key = tmp2[0].substring(2);
        				}else if(tmp2[0].startsWith("+") || tmp2[0].startsWith("-")
        						|| tmp2[0].startsWith(">") || tmp2[0].startsWith("<")){
            				key = tmp2[0].substring(1);
            			}else{
            				key = tmp2[0];
            			}
        				key=key.trim();
            			value = tmp2[1];
            			if(tmp2[1].equals("*")){
            				flag = true;
            			}else if(tmp2[0].startsWith(">=")){
            				queryBuilder = QueryBuilders.rangeQuery(key).gte(value);
            				boolBuilder.must(queryBuilder);
            				flag = true;
            			}else if(tmp2[0].startsWith("<=")){
            				queryBuilder = QueryBuilders.rangeQuery(key).lte(value);
            				boolBuilder.must(queryBuilder);
            				flag = true;
            				
            			}else if(tmp2[0].startsWith(">")){
            				queryBuilder = QueryBuilders.rangeQuery(key).gt(value);
            				boolBuilder.must(queryBuilder);
            				flag = true;
            			}else if(tmp2[0].startsWith("<")){
            				queryBuilder = QueryBuilders.rangeQuery(key).lt(value);
            				boolBuilder.must(queryBuilder);
            				flag = true;
            			}else{
            				queryBuilder = QueryBuilders.queryStringQuery(value).field(key);
                			if(tmp2[0].startsWith("+")){
                				boolBuilder.must(queryBuilder);
                				flag = true;
                			}else if(tmp2[0].startsWith("-")){
                				boolBuilder.mustNot(queryBuilder);
                				flag = true;
                			}else{
                				boolBuilder.should(queryBuilder);
                				flag = true;
                			}
            			}
        			}
        		}
        	}
        }
        
        if(!flag){
        	logger.debug("表达式异常，未解析到有效查询条件，返回空");
        	return null;
        }
       
        SearchHits result = _searchPage(boolBuilder,query);
        return result;
    }

	@Override
	public void deleteIndex(String indexName) {
		IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);

		IndicesExistsResponse inExistsResponse = client.admin().indices()
		                    .exists(inExistsRequest).actionGet();
		if(inExistsResponse.isExists()){
			DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(indexName)
                    .execute().actionGet();
		}
	}

	@Override
	public List<String> searchRecommend(String keyword, int count) {
//		searchHistoryIndexName
		BoolQueryBuilder bq = new BoolQueryBuilder()
		.should(new PrefixQueryBuilder("keyword", keyword).boost(0.5f))
		.should(new PrefixQueryBuilder("pin_yin_short", keyword).boost(0.4f))
		.should(new PrefixQueryBuilder("pin_yin", keyword).boost(0.3f));
		
		SearchResponse resp=client.prepareSearch(IndexConstants.SEARCH_HISTORY_INDEX_NAME)
				.addField("_id")
				.addSort("count", SortOrder.DESC)
				.setQuery(bq)
				.setSize(count)
				.execute().actionGet();
	    SearchHits hits = resp.getHits();
	    List<String> ret = new ArrayList<String>();
	    for(SearchHit hit: hits){
	    	String item =  hit.getId();
	    	ret.add(item);
	    }
		return ret;
	}

	@Override
	public List<String> getSearchHistoryTopN(int count) {
		 BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
		
		SearchResponse resp=client.prepareSearch(IndexConstants.SEARCH_HISTORY_INDEX_NAME)
				.addField("keyword")
				.addSort("count", SortOrder.DESC)
				.setQuery(boolBuilder)
				.setSize(count)
				.execute().actionGet();
		SearchHits hits = resp.getHits();
		 List<String> ret = new ArrayList<String>();
		    for(SearchHit hit: hits){
		    	String item =  hit.getId();
		    	ret.add(item);
		    }
		return ret;
	}
}
