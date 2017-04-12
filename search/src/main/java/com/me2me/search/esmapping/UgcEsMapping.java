package com.me2me.search.esmapping;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.me2me.search.constants.IndexConstants;

@Mapping(mappingPath=IndexConstants.UGC_INDEX_NAME)
@Document(indexName=IndexConstants.UGC_INDEX_NAME,type=IndexConstants.UGC_INDEX_NAME)
public class UgcEsMapping {
	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long id;		//主键    主键   自增长    必填    必须唯一 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long uid;		//用户     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long forward_cid;		//     必填 

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String title;		//     必填 

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String feeling;		//写文章的感受标签     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer type;		//0 原生内容，1 转发原生UGC内容,2小编内容 3，直播，4 活动，5系统,6转发直播，7转发活动,8转发小编文章,9转发系统文章     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String forward_title;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer content_type;		//     必填 

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String content;		//内容     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer person_count;		//参与人数     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer review_count;		//评论数     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer like_count;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer favorite_count;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer read_count;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer read_count_dummy;		//虚拟阅读数     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer is_top;		//是否置顶     必填 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer rights;		//0仅自己，1公开     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer author;		//0用户1表示小编     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer status;		//0正常，1删除,2可恢复状态     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date create_time;		//创建时间     必填 
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String nick_name;		//     必填 
	
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

	public Long getForward_cid() {
		return forward_cid;
	}

	public void setForward_cid(Long forward_cid) {
		this.forward_cid = forward_cid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFeeling() {
		return feeling;
	}

	public void setFeeling(String feeling) {
		this.feeling = feeling;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getForward_title() {
		return forward_title;
	}

	public void setForward_title(String forward_title) {
		this.forward_title = forward_title;
	}

	public Integer getContent_type() {
		return content_type;
	}

	public void setContent_type(Integer content_type) {
		this.content_type = content_type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getPerson_count() {
		return person_count;
	}

	public void setPerson_count(Integer person_count) {
		this.person_count = person_count;
	}

	public Integer getReview_count() {
		return review_count;
	}

	public void setReview_count(Integer review_count) {
		this.review_count = review_count;
	}

	public Integer getLike_count() {
		return like_count;
	}

	public void setLike_count(Integer like_count) {
		this.like_count = like_count;
	}

	public Integer getFavorite_count() {
		return favorite_count;
	}

	public void setFavorite_count(Integer favorite_count) {
		this.favorite_count = favorite_count;
	}

	public Integer getRead_count() {
		return read_count;
	}

	public void setRead_count(Integer read_count) {
		this.read_count = read_count;
	}

	public Integer getRead_count_dummy() {
		return read_count_dummy;
	}

	public void setRead_count_dummy(Integer read_count_dummy) {
		this.read_count_dummy = read_count_dummy;
	}

	public Integer getIs_top() {
		return is_top;
	}

	public void setIs_top(Integer is_top) {
		this.is_top = is_top;
	}

	public Integer getRights() {
		return rights;
	}

	public void setRights(Integer rights) {
		this.rights = rights;
	}

	public Integer getAuthor() {
		return author;
	}

	public void setAuthor(Integer author) {
		this.author = author;
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

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	
}
