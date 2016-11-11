package com.me2me.mgmt.request;

import com.me2me.activity.dto.ShowLuckActStatDTO;

import lombok.Getter;
import lombok.Setter;

public class LotteryStatusQueryDTO {

	@Getter
    @Setter
	private int active;
	
	@Getter
    @Setter
	private ShowLuckActStatDTO data;
}
