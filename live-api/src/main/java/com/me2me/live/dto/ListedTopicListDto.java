package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;
/**
 * 王国上市列表
 * @author chenxiang
 * @date 2017-07-03
 */
@Data
public class ListedTopicListDto implements BaseEntity{
	private static final long serialVersionUID = 1L;
	private int isClosed;
	private long customerServiceUid;
	private List<TopicListedElement> topicList=Lists.newArrayList();
	
	@Data
	public static class TopicListedElement implements BaseEntity{
		private static final long serialVersionUID = 1L;
		
		private long sinceId;
		private long uid;
		private String coverImage;
		private String title;
		private String nickName;
		private long topicId;
		private int contentType;
		private int reviewCount;
		private int personCount;
		private int readCount;
		private int price;
		private int status;
		private double priceRMB;
		private long buyUid;
	}
}
