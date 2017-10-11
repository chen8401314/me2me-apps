package com.me2me.mgmt.request;

import com.me2me.mgmt.vo.DatatablePage;

public class KingdomGiftRequest extends DatatablePage {

	private long topicId;

	public long getTopicId() {
		return topicId;
	}

	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}
}
