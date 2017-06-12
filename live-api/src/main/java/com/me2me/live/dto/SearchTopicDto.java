package com.me2me.live.dto;

import java.io.Serializable;
import java.util.Date;

import com.me2me.live.model.Topic;
/**
 * 后台搜索王国使用。
 * @author zhangjiwei
 * @date Mar 24, 2017
 */
public class SearchTopicDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private String nickName;
	private String avatar;
	private Integer vLv;
	private Integer uid;
	
	private Integer topicId;
	private String title;
	private Date createTime;
	private Date updateTime;
	private Integer type;
    private Integer contentType;
    
    private Integer personCount;
    private Integer reviewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer readCount;
    private Integer readCountDummy;
	

	private Integer textCount;
	private Integer imgCount;
	private Integer videoCount;
	private Integer audioCount;
	private Integer updateCount;
	
	
	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Integer getvLv() {
		return vLv;
	}
	public void setvLv(Integer vLv) {
		this.vLv = vLv;
	}
	public Integer getTopicId() {
		return topicId;
	}
	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getContentType() {
		return contentType;
	}
	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}
	public Integer getPersonCount() {
		return personCount;
	}
	public void setPersonCount(Integer personCount) {
		this.personCount = personCount;
	}
	public Integer getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}
	public Integer getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}
	public Integer getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(Integer favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public Integer getReadCount() {
		return readCount;
	}
	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}
	public Integer getReadCountDummy() {
		return readCountDummy;
	}
	public void setReadCountDummy(Integer readCountDummy) {
		this.readCountDummy = readCountDummy;
	}
	public Integer getTextCount() {
		return textCount;
	}
	public void setTextCount(Integer textCount) {
		this.textCount = textCount;
	}
	public Integer getImgCount() {
		return imgCount;
	}
	public void setImgCount(Integer imgCount) {
		this.imgCount = imgCount;
	}
	public Integer getVideoCount() {
		return videoCount;
	}
	public void setVideoCount(Integer videoCount) {
		this.videoCount = videoCount;
	}
	public Integer getAudioCount() {
		return audioCount;
	}
	public void setAudioCount(Integer audioCount) {
		this.audioCount = audioCount;
	}
	public Integer getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(Integer updateCount) {
		this.updateCount = updateCount;
	}
	
	
}
