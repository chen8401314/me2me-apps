package com.me2me.mgmt.request;

import com.me2me.user.dto.ShowUsergagDto;

import lombok.Getter;
import lombok.Setter;

public class AppGagUserQueryDTO {

	@Getter
	@Setter
	private String uid;
	
	@Getter
	@Setter
	private ShowUsergagDto data;
}
