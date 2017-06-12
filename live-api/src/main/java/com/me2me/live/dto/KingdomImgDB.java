package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;
/**
 * 王国图库
 * @author zhangjiwei
 * @date May 5, 2017
 */
@Data
public class KingdomImgDB implements BaseEntity{
	private static final long serialVersionUID = 1L;
	private String topMonth;
	private long topMonthDataSize;
	private List<ImgData> imgData=Lists.newArrayList();
	
	@Data
	public static class ImgData implements BaseEntity{
		private static final long serialVersionUID = 1L;
		
		private long fragmentId;
		private String fragmentImage;
		private String fragment;
		private int type;
		private int contentType;
		private String extra;
		private String url;
		private long createTime;
	}
}
