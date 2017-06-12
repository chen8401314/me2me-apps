package com.me2me.activity.dto;

import lombok.Getter;
import lombok.Setter;

public class LuckActStat2DTO extends LuckActStatDTO {

	private static final long serialVersionUID = 4810775330223655904L;

	@Getter
	@Setter
	private String dateStr;
	@Getter
	@Setter
	private int prizeNum;
}
