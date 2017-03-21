package com.me2me.live.dto;

import java.io.Serializable;
/**
 * 仅供后台搜索王国使用。
 * @author zhangjiwei
 * @date Mar 20, 2017
 */
public class SearchDropAroundTopicDto implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String nickName;
	private int topicId;
	private int uid;
	private int vLv;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getTopicId() {
		return topicId;
	}
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getvLv() {
		return vLv;
	}
	public void setvLv(int vLv) {
		this.vLv = vLv;
	}
	
	
	
}
