package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class LuckActStatDTO implements BaseEntity {

	private static final long serialVersionUID = -2671526232262489651L;

	private int enterUV;
	private int enterPV;
	
}
