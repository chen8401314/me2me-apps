package com.me2me.content.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class SearchAdBannerListDto implements BaseEntity {

	private static final long serialVersionUID = -2203948116524216927L;

	private int totalPage;

    private int totalRecord;
	
	private List<AdBannerElement> result = Lists.newArrayList();
	
	public AdBannerElement createAdBannerElement(){
		return new AdBannerElement();
	}
	
	@Data
	public static class AdBannerElement implements BaseEntity{

		private static final long serialVersionUID = 9204369954350141021L;
		
		private long id;
		private String adBannerName;
		private String bannerPosition;
		private int status;
		private Date createTime;
		private int adBannerHeight;
		private int adBannerWidth;
	}
}
