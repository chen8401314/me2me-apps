package com.me2me.mgmt.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.me2me.activity.model.Aactivity;

public class ActivityInfoDTO {

	@Getter
	@Setter
	private Aactivity activityInfo;
	@Getter
	@Setter
	private List<StageItem> stageList;
}
