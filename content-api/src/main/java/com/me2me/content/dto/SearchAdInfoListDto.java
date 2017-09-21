package com.me2me.content.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class SearchAdInfoListDto implements BaseEntity {

	private static final long serialVersionUID = -2213948116524216927L;

	private int totalPage;

    private int totalRecord;
	
	private List<AdInfoElement> result = Lists.newArrayList();
	
	public AdInfoElement createAdInfoElement(){
		return new AdInfoElement();
	}
	
	@Data
	public static class AdInfoElement implements BaseEntity{

		private static final long serialVersionUID = 9204369954150141021L;
		
		private long id;
		private String adTitle;
		private String adCover;
		private int adCoverWidth;
		private int adCoverHeight;
		private Date effectiveTime;
		private int displayProbability;
		private int type;
		private long topicId;
		private String adUrl;
		private long bannerId;
		private String bannerName;
		private int status;
		private Date createTime;
	}
}
