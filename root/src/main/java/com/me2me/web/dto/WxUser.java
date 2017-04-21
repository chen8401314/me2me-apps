package com.me2me.web.dto;
/**
 * 微信认证用户。
 * @author zhangjiwei
 * @date Nov 23, 2016
 *
 */
public class WxUser {
	private String mobile;
	private String openid;
	private String access_token;
	private String headimgurl;
	private String nickname;
	private String sex;
	private String unionid;
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	@Override
	public String toString() {
		return "WxUser [openid=" + openid + ", access_token=" + access_token + ", headimgurl=" + headimgurl + ", nickname=" + nickname + ", sex=" + sex + ", unionid=" + unionid + "]";
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
