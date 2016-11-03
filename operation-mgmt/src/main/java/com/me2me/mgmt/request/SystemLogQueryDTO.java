package com.me2me.mgmt.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.me2me.mgmt.dal.entity.MgmtSysLog;

public class SystemLogQueryDTO {

	@Getter
    @Setter
	private String optDesc;
	@Getter
    @Setter
	private String startDate;
	@Getter
    @Setter
	private String endDate;
	
	@Getter
    @Setter
	private List<MgmtSysLog> result;
}
