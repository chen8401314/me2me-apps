package com.me2me.mgmt.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class LightBoxQueryDTO {

	@Setter
	@Getter
	private String searchTime;
	
	@Setter
	@Getter
	private List<LightBoxItem> result;
}
