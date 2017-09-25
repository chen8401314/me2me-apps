package com.me2me.mgmt.request;

import com.me2me.mgmt.vo.DatatablePage;

public class AppLotteryUserQueryDTO extends DatatablePage {
	
	private String kingdomName;
	private String lotteryName;

	private String nickName;
	private int appoint;
	private long lotteryId;
	
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getAppoint() {
		return appoint;
	}
	public void setAppoint(int appoint) {
		this.appoint = appoint;
	}
	public String getKingdomName() {
		return kingdomName;
	}
	public void setKingdomName(String kingdomName) {
		this.kingdomName = kingdomName;
	}
	public String getLotteryName() {
		return lotteryName;
	}
	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}
	public long getLotteryId() {
		return lotteryId;
	}
	public void setLotteryId(long lotteryId) {
		this.lotteryId = lotteryId;
	}
}
