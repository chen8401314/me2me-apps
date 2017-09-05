package com.me2me.mgmt.request;

import com.me2me.mgmt.vo.DatatablePage;

public class UserInvitationDetailQueryDTO extends DatatablePage {

	private int searchType = 0;//0全部，1安卓，2IOS
	private long refereeUid;
	private String startTime;
	private String endTime;
	
	
	public long getRefereeUid() {
		return refereeUid;
	}
	public void setRefereeUid(long refereeUid) {
		this.refereeUid = refereeUid;
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
	public int getSearchType() {
		return searchType;
	}
	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}
}
