package com.me2me.mgmt.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ChannelQueryDTO {

	@Getter
    @Setter
	private String channelCode;
	
	@Getter
    @Setter
	private List<AppChannelItem> result = new ArrayList<AppChannelItem>();
	
	public void resultClear(){
		this.result.clear();
	}
}
