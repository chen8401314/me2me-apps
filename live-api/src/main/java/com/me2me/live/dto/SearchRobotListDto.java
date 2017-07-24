package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class SearchRobotListDto implements BaseEntity {

	private static final long serialVersionUID = -2217948116524216927L;

	private int totalPage;

    private int totalRecord;
	
	private List<RobotElement> result = Lists.newArrayList();
	
	public RobotElement createTopicListedElement(){
		return new RobotElement();
	}
	
	@Data
	public static class RobotElement implements BaseEntity{

		private static final long serialVersionUID = 9204369954351141021L;
		
		private long id;
		private long uid;
		private String nickName;
		private String avatar;
	}
}
