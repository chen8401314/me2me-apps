package com.me2me.mgmt.request;

import com.me2me.activity.dto.ShowLuckPrizeDTO;

import lombok.Getter;
import lombok.Setter;

public class LotteryPrizeQueryDTO {

	@Getter
    @Setter
	private int active;
	
	@Getter
    @Setter
	private ShowLuckPrizeDTO data;
}
