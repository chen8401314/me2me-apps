package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.user.dto.SearchUserProfileDto;

public class AppUserQueryDTO {

	@Getter
    @Setter
	private String nickName;
	@Getter
    @Setter
	private int isV;
	@Getter
    @Setter
	private String mobile;
	@Getter
    @Setter
	private int status;//0：全部 1：正常 2：失效
	@Getter
    @Setter
	private String startTime;
	@Getter
    @Setter
	private String endTime;
	
	@Getter
    @Setter
	private SearchUserProfileDto data;
}
