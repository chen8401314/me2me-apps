package com.me2me.live.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class SearchTopicListedListDto implements BaseEntity {

	private static final long serialVersionUID = -2207948116524216927L;

	private int totalPage;

    private int totalRecord;
	
	private List<TopicListedElement> result = Lists.newArrayList();
	
	public TopicListedElement createTopicListedElement(){
		return new TopicListedElement();
	}
	
	@Data
	public static class TopicListedElement implements BaseEntity{

		private static final long serialVersionUID = 9204369954350141021L;
		
		private long id;
		private String title;
		private String nickName;
		private double price;
		private Date createTime;
		private String meNumber;
		private int status;
	}
}
