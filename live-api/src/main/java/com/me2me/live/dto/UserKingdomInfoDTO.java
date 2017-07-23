package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class UserKingdomInfoDTO implements BaseEntity {
	private static final long serialVersionUID = 5968876520837730663L;

	private int totalKingdomCount = 0;
	private int validKingdomCount = 0;
}
