package com.me2me.search.esmapping;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.me2me.search.constants.IndexConstants;

@Mapping(mappingPath=IndexConstants.TAG_SAMPLE_INDEX_NAME)
@Document(indexName=IndexConstants.TAG_SAMPLE_INDEX_NAME,type=IndexConstants.TAG_SAMPLE_INDEX_NAME)
public class TagTrainSampleEsMapping {
	
	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Long)
	private Integer id;		//    主键   自增长    必填    必须唯一 


	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.String)
	private String tag;		//     必填 
	
	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String summmary;		//     必填 
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String keywords;		//     必填 
	

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date creation_date;		//  


	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.String)
	private String alias_tag;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public String getSummmary() {
		return summmary;
	}


	public void setSummmary(String summmary) {
		this.summmary = summmary;
	}


	public String getKeywords() {
		return keywords;
	}


	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}


	public Date getCreation_date() {
		return creation_date;
	}


	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}


	public String getAlias_tag() {
		return alias_tag;
	}


	public void setAlias_tag(String alias_tag) {
		this.alias_tag = alias_tag;
	} 

	
}
