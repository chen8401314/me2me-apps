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
	private SearchUserProfileDto data;
}
