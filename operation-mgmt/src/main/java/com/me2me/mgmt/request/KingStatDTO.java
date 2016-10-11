package com.me2me.mgmt.request;

import com.me2me.content.dto.ShowKingTopicDto;

import lombok.Getter;
import lombok.Setter;

public class KingStatDTO {

	@Getter
    @Setter
	private String txtStartDate;
	@Getter
    @Setter
	private String txtEndDate;
	
	@Getter
    @Setter
	private ShowKingTopicDto kingDto;
}
