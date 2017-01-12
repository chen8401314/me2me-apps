package com.me2me.mgmt.request;

import java.util.List;

import com.me2me.activity.model.AppUiControl;

import lombok.Getter;
import lombok.Setter;

public class AppUIQueryDTO {

	@Setter
	@Getter
	private String searchTime;
	
	@Setter
	@Getter
	private List<AppUiControl> result;
}
