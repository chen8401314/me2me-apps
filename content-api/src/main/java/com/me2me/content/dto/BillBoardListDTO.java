package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class BillBoardListDTO implements BaseEntity{
	private static final long serialVersionUID = -8781449680844822539L;
	
	private long targetId;
	private long sinceId;
	private int type;
}
