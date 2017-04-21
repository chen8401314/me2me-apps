package com.me2me.search.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import com.me2me.search.mapper.SearchVarMapper;
/**
 * 索引帮助类。
 * @author zhangjiwei
 * @date Apr 19, 2017
 */
@Component
public class ElasticSearchIndexHelper {
	static final String DEFAULT_START_TIME = "1900-01-01 00:00:00";
	@Autowired
	protected ElasticsearchTemplate esTemplate;
	@Autowired
	protected SearchVarMapper varMapper;
	/**
	 * 修改系统配置中的项目，如果项不存在，则新增此项
	 * @author zhangjiwei
	 * @date Apr 7, 2017
	 * @param key
	 * @param val
	 */
	public void updateVarVal(String key, String val) {
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
	public String preIndex(boolean fully,String indexName){
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
	public Map<String,Object> copyMap(Map<String,Object> sourceMap,String fields){
		LinkedHashMap<String, Object> dataMap = new LinkedHashMap<>();
		for(String field:fields.split(",")){
			dataMap.put(field, sourceMap.get(field));
		}
		return dataMap;
	}
}
