package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

public class ActiveStatDTO {

	@Getter
    @Setter
	private long todayCount = 0;
	@Getter
    @Setter
	private long yesterdayCount = 0;
	@Getter
    @Setter
	private long threedayCount = 0;
	@Getter
    @Setter
	private long sevendayCount = 0;
}
