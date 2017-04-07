package com.me2me.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.me2me.search.dto.SearchQuery;
import com.me2me.search.dto.SearchResult;



public interface ElasticSearchHandler {

    /**
     * 创建索引
     * 
     * @param indexName
     */
    void createIndex(String indexName);
    
    void init();


    void addIndexData(String indexName,String type, List<String> jsonList) throws IOException;
    
    void addIndexData(String type, List<String> jsonList) throws IOException;
    
    /**
     * 指定index，type，并指定每个doc的id
     * @param indexName
     * @param type
     * @param idJsonMap
     * @throws IOException
     */
    void addIndexData(String indexName,String type, Map<String, String> idJsonMap) throws IOException;
    
    
    String getIndexName();
    
    /**
     * 通用检索方法，返回的对象需自行组装处理
     * @param query	检索参数，包含检索表达式和分页相关信息
     * @return
     */
    List<Map<String, Object>> commonSearch(SearchQuery query);
    /**
     * 通用检索方法，此方法包装了关键词高亮、结果总数等信息。
     * @param query	检索参数，包含检索表达式和分页相关信息
     * @return 如果无结果返回 null.
     */
    SearchResult<Map<String,Object>> commonSearchNew(SearchQuery query);
   /**
    * 删除索引
    * @param indexName
    */
    void deleteIndex(String indexName);
    /**
     * 查询 搜索历史索引库，用于搜索推荐，类似百度
     * @param keyword
     * @param count
     */
    List<String> searchRecommend(String keyword,int count);
    /**
     * 取搜索历史top n
     * @return
     */
    List<String> getSearchHistoryTopN(int count);
}