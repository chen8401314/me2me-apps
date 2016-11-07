package com.me2me.mgmt.request;

import com.me2me.user.dto.ShowVersionControlDto;

import lombok.Getter;
import lombok.Setter;

public class AppVersionQueryDTO {

	@Getter
	@Setter
	private String version;
	@Getter
	@Setter
	private int platform;
	
	@Getter
	@Setter
	private ShowVersionControlDto data;
}
