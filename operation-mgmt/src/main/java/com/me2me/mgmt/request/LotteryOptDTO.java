package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.activity.dto.ShowLuckActsDTO;

public class LotteryOptDTO {

	@Getter
    @Setter
	private int active;
	@Getter
    @Setter
	private ShowLuckActsDTO data;
}
