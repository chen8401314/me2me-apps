package com.me2me.mgmt.request;

import com.me2me.activity.dto.ShowLuckStatusDTO;

import lombok.Getter;
import lombok.Setter;

public class LotteryQueryDTO {

	@Getter
    @Setter
	private int active;
	
	@Getter
    @Setter
	private ShowLuckStatusDTO data;
}
