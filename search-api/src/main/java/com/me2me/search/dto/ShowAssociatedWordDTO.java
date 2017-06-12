package com.me2me.search.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowAssociatedWordDTO implements BaseEntity {
	private static final long serialVersionUID = -3412681619710644087L;

	private List<WordElement> result = Lists.newArrayList();
	
	@Data
	public static class WordElement implements BaseEntity {
		private static final long serialVersionUID = 9028502331707312506L;
		
		private String searchWords;
	}
}
