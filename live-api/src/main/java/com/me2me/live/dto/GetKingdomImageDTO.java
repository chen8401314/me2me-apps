package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class GetKingdomImageDTO implements BaseEntity {
	private static final long serialVersionUID = 6058945377315084490L;

	private int totalCount;
	private List<ImageElement> imageDatas = Lists.newArrayList();
	
	@Data
	public static class ImageElement implements BaseEntity {
		private static final long serialVersionUID = 548563725757753876L;
		
		private int index;
		private long fid;
		private String imageName;
		private String fragmentImage;
		private String extra;
	}
}
