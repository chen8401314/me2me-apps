package com.me2me.mgmt.request;

import com.me2me.mgmt.vo.DatatablePage;

public class KingdomUserRequest extends DatatablePage {
	private Integer topicId;
	
	private String fragment;
	private String startTime;
	private String endTime;
	private Integer firstSpeakFlag = 0;//本王国第一次发言状态，0全部，1第一次，2不是第一次

	public Integer getTopicId() {
		return topicId;
	}

	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getFirstSpeakFlag() {
		return firstSpeakFlag;
	}

	public void setFirstSpeakFlag(Integer firstSpeakFlag) {
		this.firstSpeakFlag = firstSpeakFlag;
	}
	
	
}
