package com.me2me.mgmt.request;

import com.me2me.user.dto.PromoterDto;

import lombok.Getter;
import lombok.Setter;

public class PromoterDTO {

	//请求参数
	@Getter
    @Setter
	private String txtStartDate;
	@Getter
    @Setter
	private String txtEndDate;
	
	//返回参数
	@Getter
    @Setter
	private PromoterDto promoterDto;
}
