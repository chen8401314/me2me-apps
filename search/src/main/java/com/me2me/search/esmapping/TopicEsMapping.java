package com.me2me.search.esmapping;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.me2me.search.constants.IndexConstants;

@Mapping(mappingPath=IndexConstants.KINGDOM_INDEX_NAME)
@Document(indexName=IndexConstants.KINGDOM_INDEX_NAME,type=IndexConstants.KINGDOM_INDEX_NAME)
public class TopicEsMapping {
	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long id;		//    主键   自增长    必填    必须唯一 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long uid;		//     必填 


	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String title;		//     必填 


	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date create_time;		//  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date update_time;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long long_time;		//     必填 


	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String core_circle;		//     必填 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer type;		//类型，0普通王国，1000聚合王国  
 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer rights;		//可见类型，1公开，2私密  

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String summary;		//王国简介  
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String nick_name;		//     必填 
	
	
	@Field(index=FieldIndex.analyzed,store=false,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String fragments;
	
	
	@Field(index=FieldIndex.analyzed,store=false,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String tags;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Long getLong_time() {
		return long_time;
	}
	public void setLong_time(Long long_time) {
		this.long_time = long_time;
	}
	public String getCore_circle() {
		return core_circle;
	}
	public void setCore_circle(String core_circle) {
		this.core_circle = core_circle;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getRights() {
		return rights;
	}
	public void setRights(Integer rights) {
		this.rights = rights;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getFragments() {
		return fragments;
	}
	public void setFragments(String fragments) {
		this.fragments = fragments;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
}
