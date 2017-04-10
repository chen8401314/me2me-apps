package com.me2me.search.esmapping;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.me2me.search.constants.IndexConstants;
@Mapping(mappingPath=IndexConstants.SEARCH_HISTORY_INDEX_NAME)
@Document(indexName=IndexConstants.SEARCH_HISTORY_INDEX_NAME,type=IndexConstants.SEARCH_HISTORY_INDEX_NAME)
public class SearchHistoryEsMapping{

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer id;		//    主键    必填    必须唯一 

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String name;		//  

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer search_count;		//  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date last_query_date;		//  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date creation_date;		//  
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String name_pinyin;
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String name_pinyin_short;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSearch_count() {
		return search_count;
	}

	public void setSearch_count(Integer search_count) {
		this.search_count = search_count;
	}

	public Date getLast_query_date() {
		return last_query_date;
	}

	public void setLast_query_date(Date last_query_date) {
		this.last_query_date = last_query_date;
	}

	public Date getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}

	public String getName_pinyin() {
		return name_pinyin;
	}

	public void setName_pinyin(String name_pinyin) {
		this.name_pinyin = name_pinyin;
	}

	public String getName_pinyin_short() {
		return name_pinyin_short;
	}

	public void setName_pinyin_short(String name_pinyin_short) {
		this.name_pinyin_short = name_pinyin_short;
	}
	
	
}