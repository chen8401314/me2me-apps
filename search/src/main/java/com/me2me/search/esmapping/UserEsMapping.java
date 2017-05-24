package com.me2me.search.esmapping;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.me2me.search.constants.IndexConstants;

@Mapping(mappingPath=IndexConstants.USER_INDEX_NAME)
@Document(indexName=IndexConstants.USER_INDEX_NAME,type=IndexConstants.USER_INDEX_NAME)
public class UserEsMapping {
	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long id;		//主键，自增    主键   自增长    必填    必须唯一 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Long)
	private Long uid;		//用户id     必填 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.String)
	private String mobile;		//用户手机号码     必填 

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String nick_name;		//     必填 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer gender;		//性别，0女1男     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String birthday;		//生日     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String avatar;		//用户头像     必填 

	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String)
	private String introduced;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date create_time;		//  

	@Field(index=FieldIndex.no,store=true,type=FieldType.Date)
	private Date update_time;		//     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Integer)
	private Integer is_promoter;		//是否是推广员 0 否 1是     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.Long)
	private Long referee_uid;		//推广者uid     必填 

	@Field(index=FieldIndex.no,store=true,type=FieldType.String)
	private String third_part_bind;		//     必填 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer v_lv;		//是否是大V(0 否 1 是)     必填 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.String)
	private String channel;		//渠道  

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer platform;		//平台，1安卓，2 IOS，3 H5  

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.String)
	private String register_version;		//注册版本号，v2.2.2版本开始有的     必填 

	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer like_gender;		//是否是大V(0 否 1 是)     必填 
	
	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer age_group;		//是否是大V(0 否 1 是)     必填 
	
	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.Integer)
	private Integer occupation;		//是否是大V(0 否 1 是)     必填 
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String tags;		//是否是大V(0 否 1 是)     必填 
	
	@Field(index=FieldIndex.not_analyzed,store=true,type=FieldType.String)
	private String mbit;
	
	@Field(index=FieldIndex.analyzed,store=true,type=FieldType.String,indexAnalyzer="ik",searchAnalyzer="ik")
	private String last_emotions;
	
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getIntroduced() {
		return introduced;
	}

	public void setIntroduced(String introduced) {
		this.introduced = introduced;
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

	public Integer getIs_promoter() {
		return is_promoter;
	}

	public void setIs_promoter(Integer is_promoter) {
		this.is_promoter = is_promoter;
	}

	public Long getReferee_uid() {
		return referee_uid;
	}

	public void setReferee_uid(Long referee_uid) {
		this.referee_uid = referee_uid;
	}

	public String getThird_part_bind() {
		return third_part_bind;
	}

	public void setThird_part_bind(String third_part_bind) {
		this.third_part_bind = third_part_bind;
	}

	public Integer getV_lv() {
		return v_lv;
	}

	public void setV_lv(Integer v_lv) {
		this.v_lv = v_lv;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getRegister_version() {
		return register_version;
	}

	public void setRegister_version(String register_version) {
		this.register_version = register_version;
	}

	public Integer getLike_gender() {
		return like_gender;
	}

	public void setLike_gender(Integer like_gender) {
		this.like_gender = like_gender;
	}

	public Integer getAge_group() {
		return age_group;
	}

	public void setAge_group(Integer age_group) {
		this.age_group = age_group;
	}

	public Integer getOccupation() {
		return occupation;
	}

	public void setOccupation(Integer occupation) {
		this.occupation = occupation;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getMbit() {
		return mbit;
	}

	public void setMbit(String mbit) {
		this.mbit = mbit;
	}

	public String getLast_emotions() {
		return last_emotions;
	}

	public void setLast_emotions(String last_emotions) {
		this.last_emotions = last_emotions;
	}
	
	
}
