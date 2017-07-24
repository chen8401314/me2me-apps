package com.me2me.live.dto;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class SearchQuotationListDto implements BaseEntity {

	private static final long serialVersionUID = -2717948116524216927L;

	private int totalPage;

    private int totalRecord;
	
	private List<QuotationElement> result = Lists.newArrayList();
	
	public QuotationElement createQuotationElement(){
		return new QuotationElement();
	}
	
	@Data
	public static class QuotationElement implements BaseEntity{

		private static final long serialVersionUID = 9104369954351141021L;
		
		private long id;
		private int type;
		private String quotation;
		private Date createTime;
	}
}
