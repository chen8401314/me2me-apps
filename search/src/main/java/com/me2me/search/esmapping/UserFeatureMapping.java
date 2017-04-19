package com.me2me.search.esmapping;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.me2me.search.constants.IndexConstants;
/**
 * 用户画像索引
 * @author zhangjiwei
 * @date Apr 18, 2017
 */
@Mapping(mappingPath = IndexConstants.USER_FEATURE_INDEX_NAME)
@Document(indexName = IndexConstants.USER_FEATURE_INDEX_NAME, type = IndexConstants.USER_FEATURE_INDEX_NAME)
public class UserFeatureMapping {
	@Field(index = FieldIndex.no, store = true, type = FieldType.Long)
	private Long id; // 主键，自增 主键 自增长 必填 必须唯一

	@Field(index = FieldIndex.no, store = true, type = FieldType.Long)
	private Long uid; // 用户id 必填

	@Field(index = FieldIndex.no, store = true, type = FieldType.String)
	private String avatar; // 用户头像 必填

	@Field(index = FieldIndex.analyzed, store = true, type = FieldType.String, indexAnalyzer = "ik", searchAnalyzer = "ik")
	private String nick_name; // 必填

	@Field(index = FieldIndex.not_analyzed, store = true, type = FieldType.Integer)
	private Integer v_lv; // 是否是大V(0 否 1 是) 必填

	@Field(index = FieldIndex.not_analyzed, store = true, type = FieldType.Integer)
	private Integer gender; // 性别，0女1男 必填

	@Field(index = FieldIndex.not_analyzed, store = true, type = FieldType.String)
	private String like_gender; // 用户头像 必填

	@Field(index = FieldIndex.analyzed, store = true, type = FieldType.String, indexAnalyzer = "ik", searchAnalyzer = "ik")
	private String like_tags; // 用户头像 必填

	@Field(index = FieldIndex.not_analyzed, store = true, type = FieldType.String)
	private String age_group; // 生日 必填

	@Field(index = FieldIndex.not_analyzed, store = true, type = FieldType.String)
	private String occupation; // 用户头像 必填
	
	
	@Field(index = FieldIndex.not_analyzed, store = true, type = FieldType.String)
	private String like_content_type; // 用户头像 必填
	
	
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Integer getV_lv() {
		return v_lv;
	}

	public void setV_lv(Integer v_lv) {
		this.v_lv = v_lv;
	}

	public String getLike_gender() {
		return like_gender;
	}

	public void setLike_gender(String like_gender) {
		this.like_gender = like_gender;
	}

	public String getLike_tags() {
		return like_tags;
	}

	public void setLike_tags(String like_tags) {
		this.like_tags = like_tags;
	}

	public String getAge_group() {
		return age_group;
	}

	public void setAge_group(String age_group) {
		this.age_group = age_group;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getLike_content_type() {
		return like_content_type;
	}

	public void setLike_content_type(String like_content_type) {
		this.like_content_type = like_content_type;
	}

}
