package com.me2me.content.dto;

import java.io.Serializable;

import java.util.Date;

import com.me2me.content.model.BillBoardRelation;
/**
 * 榜单排名数据,三合一多功能bean，后台管理使用。
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
public class BillBoardRelationDto extends BillBoardRelation implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nickName;
	private long uid;
	private Date userRegDate;
	private int vLv;
	private String avatar;
	//------王国属性
	private long topicId;
	private String cover;
	private String title;
	private int aggregation;
	//------榜单属性
	private long rankingId;
	private String rankingCover;
	private String rankingName;
	private Integer rankingType;
	
	public int getAggregation() {
		return aggregation;
	}
	public void setAggregation(int aggregation) {
		this.aggregation = aggregation;
	}
	public long getTopicId() {
		return topicId;
	}
	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}
	public long getRankingId() {
		return rankingId;
	}
	public void setRankingId(long rankingId) {
		this.rankingId = rankingId;
	}
	public String getRankingCover() {
		return rankingCover;
	}
	public void setRankingCover(String rankingCover) {
		this.rankingCover = rankingCover;
	}
	public String getRankingName() {
		return rankingName;
	}
	public void setRankingName(String rankingName) {
		this.rankingName = rankingName;
	}
	public Integer getRankingType() {
		return rankingType;
	}
	public void setRankingType(Integer rankingType) {
		this.rankingType = rankingType;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public Date getUserRegDate() {
		return userRegDate;
	}
	public void setUserRegDate(Date userRegDate) {
		this.userRegDate = userRegDate;
	}
	public int getvLv() {
		return vLv;
	}
	public void setvLv(int vLv) {
		this.vLv = vLv;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
