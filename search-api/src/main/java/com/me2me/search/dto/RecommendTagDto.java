package com.me2me.search.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;
@Data
public class RecommendTagDto implements BaseEntity{
	private static final long serialVersionUID = 1L;
	private List<String> tags = Lists.newArrayList();
}
