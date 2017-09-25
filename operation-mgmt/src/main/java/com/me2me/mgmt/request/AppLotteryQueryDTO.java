package com.me2me.mgmt.request;

import com.me2me.mgmt.vo.DatatablePage;

public class AppLotteryQueryDTO extends DatatablePage {

	private String kingdomName;
	private String lotteryName;
	
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
}
