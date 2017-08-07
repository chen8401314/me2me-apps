package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class SpecialKingdomInfoDTO implements BaseEntity {
	private static final long serialVersionUID = 3276725823221194052L;

	private long topicId;
	private String title;
	private String coverImage;
	private int type;
	private String summary;
}
