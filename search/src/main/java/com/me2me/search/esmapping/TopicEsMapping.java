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

	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String live_image;		//     必填 

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String title;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer status;		//0未结束1结束2移除（移除之后只在我的日志显示不在直播列表显示）     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date create_time;		//  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date update_time;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long long_time;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String qrcode;		//直播二维码     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String core_circle;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer type;		//类型，0普通王国，1000聚合王国  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer ce_audit_type;		//聚合王国属性，是否需要国王审核才能加入此聚合王国，0是，1否  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer ac_audit_type;		//个人王国属性，是否需要国王审核才能收录此个人王国，0是，1否  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer ac_publish_type;		//个人王国属性，是否允许聚合王国下发消息，0是，1否  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer rights;		//可见类型，1公开，2私密  

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String summary;		//王国简介  
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String nick_name;		//     必填 
	
	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String avatar;		//用户头像     必填 
	
	@Field(index=FieldIndex.analyzed,store=false,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String fragments;
	
	
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
	public String getLive_image() {
		return live_image;
	}
	public void setLive_image(String live_image) {
		this.live_image = live_image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
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
	public Integer getCe_audit_type() {
		return ce_audit_type;
	}
	public void setCe_audit_type(Integer ce_audit_type) {
		this.ce_audit_type = ce_audit_type;
	}
	public Integer getAc_audit_type() {
		return ac_audit_type;
	}
	public void setAc_audit_type(Integer ac_audit_type) {
		this.ac_audit_type = ac_audit_type;
	}
	public Integer getAc_publish_type() {
		return ac_publish_type;
	}
	public void setAc_publish_type(Integer ac_publish_type) {
		this.ac_publish_type = ac_publish_type;
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
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getFragments() {
		return fragments;
	}
	public void setFragments(String fragments) {
		this.fragments = fragments;
	}
	
}
