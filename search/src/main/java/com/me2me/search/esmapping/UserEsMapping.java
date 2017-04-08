package com.me2me.search.esmapping;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.Mapping;

import com.me2me.search.constants.IndexConstants;

@Mapping
@Document(indexName=IndexConstants.USER_INDEX_NAME,type=IndexConstants.USER_INDEX_NAME)
public class UserEsMapping {
	@Field(index=FieldIndex.no,store=true)
	private Long id;		//主键，自增    主键   自增长    必填    必须唯一 

	@Field(index=FieldIndex.no,store=true)
	private Long uid;		//用户id     必填 

	@Field(index=FieldIndex.no,store=true)
	private String mobile;		//用户手机号码     必填 

	@Field(index=FieldIndex.analyzed,store=true)
	private String nick_name;		//     必填 

	@Field(index=FieldIndex.no,store=true)
	private Integer gender;		//性别，0女1男     必填 

	@Field(index=FieldIndex.no,store=true)
	private String birthday;		//生日     必填 

	@Field(index=FieldIndex.no,store=true)
	private String avatar;		//用户头像     必填 

	@Field(index=FieldIndex.no,store=true)
	private Long years_id;		//年代情怀     必填 

	@Field(index=FieldIndex.no,store=true)
	private Long star_id;		//星座     必填 

	@Field(index=FieldIndex.no,store=true)
	private Long social_class;		//社会阶层     必填 

	@Field(index=FieldIndex.no,store=true)
	private Long industry;		//所属行业     必填 

	@Field(index=FieldIndex.no,store=true)
	private Long marriage_status;		//婚恋状态     必填 

	@Field(index=FieldIndex.no,store=true)
	private Long bear_status;		//生育状态     必填 

	@Field(index=FieldIndex.no,store=true)
	private Integer excellent;		//0 普通 1 大V     必填 

	@Field(index=FieldIndex.no,store=true)
	private String introduced;		//     必填 

	@Field(index=FieldIndex.no,store=true)
	private Date create_time;		//  

	@Field(index=FieldIndex.no,store=true)
	private Date update_time;		//     必填 

	@Field(index=FieldIndex.no,store=true)
	private Integer is_promoter;		//是否是推广员 0 否 1是     必填 

	@Field(index=FieldIndex.no,store=true)
	private Integer is_activate;		//是否激活 0未激活 1激活     必填 

	@Field(index=FieldIndex.no,store=true)
	private Long referee_uid;		//推广者uid     必填 

	@Field(index=FieldIndex.no,store=true)
	private String qrcode;		//     必填 

	@Field(index=FieldIndex.no,store=true)
	private String third_part_bind;		//     必填 

	@Field(index=FieldIndex.no,store=true)
	private Integer v_lv;		//是否是大V(0 否 1 是)     必填 

	@Field(index=FieldIndex.no,store=true)
	private Integer is_client_login;		//是否需要检查昵称重复 0 否 1 是     必填 

	@Field(index=FieldIndex.no,store=true)
	private String channel;		//渠道  

	@Field(index=FieldIndex.no,store=true)
	private Integer platform;		//平台，1安卓，2 IOS，3 H5  

	@Field(index=FieldIndex.no,store=true)
	private String register_version;		//注册版本号，v2.2.2版本开始有的     必填 

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

	public Long getYears_id() {
		return years_id;
	}

	public void setYears_id(Long years_id) {
		this.years_id = years_id;
	}

	public Long getStar_id() {
		return star_id;
	}

	public void setStar_id(Long star_id) {
		this.star_id = star_id;
	}

	public Long getSocial_class() {
		return social_class;
	}

	public void setSocial_class(Long social_class) {
		this.social_class = social_class;
	}

	public Long getIndustry() {
		return industry;
	}

	public void setIndustry(Long industry) {
		this.industry = industry;
	}

	public Long getMarriage_status() {
		return marriage_status;
	}

	public void setMarriage_status(Long marriage_status) {
		this.marriage_status = marriage_status;
	}

	public Long getBear_status() {
		return bear_status;
	}

	public void setBear_status(Long bear_status) {
		this.bear_status = bear_status;
	}

	public Integer getExcellent() {
		return excellent;
	}

	public void setExcellent(Integer excellent) {
		this.excellent = excellent;
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

	public Integer getIs_activate() {
		return is_activate;
	}

	public void setIs_activate(Integer is_activate) {
		this.is_activate = is_activate;
	}

	public Long getReferee_uid() {
		return referee_uid;
	}

	public void setReferee_uid(Long referee_uid) {
		this.referee_uid = referee_uid;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
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

	public Integer getIs_client_login() {
		return is_client_login;
	}

	public void setIs_client_login(Integer is_client_login) {
		this.is_client_login = is_client_login;
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
	
	
}
