package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;
/**
 * 王国转让历史
 * @author chenxiang
 * @date 2017-06-08
 */
@Data
public class TopicTransferRecordDto implements BaseEntity{
	private static final long serialVersionUID = 1L;
	private List<TopicTransferRecordElement> topicTransferRecordList=Lists.newArrayList();
	
	@Data
	public static class TopicTransferRecordElement implements BaseEntity{
		private static final long serialVersionUID = 1L;
		
		private long sinceId;
		private String oldNickName;
		private String oldAvatar;
		private String newNickName;
		private String newAvatar;
		private double transferPrice;
		private long createTime;
	}
}
