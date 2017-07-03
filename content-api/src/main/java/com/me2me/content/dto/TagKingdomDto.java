package com.me2me.content.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class TagKingdomDto implements BaseEntity{
	private static final long serialVersionUID = -1636362714045596434L;
	private String tagName;
	private List<String> hotTagList;
	private int personCount;
	private int kingdomCount;
	private List<BasicKingdomInfo> kingdomList=Lists.newArrayList();
}
