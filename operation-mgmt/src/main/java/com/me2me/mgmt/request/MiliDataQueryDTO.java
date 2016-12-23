package com.me2me.mgmt.request;

import com.me2me.activity.dto.ShowMiliDatasDTO;

import lombok.Getter;
import lombok.Setter;

public class MiliDataQueryDTO {

	@Getter
    @Setter
	private String mkey;
	@Getter
    @Setter
	private int page;
	@Getter
    @Setter
	private int pageSize;
	
	
	@Getter
    @Setter
	private ShowMiliDatasDTO data;
}
