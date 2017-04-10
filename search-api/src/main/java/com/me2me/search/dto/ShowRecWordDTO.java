package com.me2me.search.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowRecWordDTO implements BaseEntity {
	private static final long serialVersionUID = -8578000604958132771L;

	private List<WordElement> wordData = Lists.newArrayList();
	
	@Data
	public static class WordElement implements BaseEntity {
		private static final long serialVersionUID = 650312522928845599L;
		
		private String word;
	}
}
