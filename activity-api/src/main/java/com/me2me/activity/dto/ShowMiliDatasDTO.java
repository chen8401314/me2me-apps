package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowMiliDatasDTO implements BaseEntity {
	private static final long serialVersionUID = -8178436835116399603L;

	private long totalCount;
	private long totalPage;
	
	private List<MiliDataElement> result = Lists.newArrayList();
	
	@Data
	public static class MiliDataElement implements BaseEntity{
		private static final long serialVersionUID = -2583359109760089870L;
		
		private long id;
		private String mkey;
		private String content;
		private int orderby;
		private int status;
	}
}
