package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class HarvestKingdomCoinDTO implements BaseEntity {
	private static final long serialVersionUID = 870367402955596537L;

	private int gainCoin;
	private int upgrade;
	private int currentLevel;
}
