package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class KingdomImageListDTO implements BaseEntity {
	private static final long serialVersionUID = -2366501844208907430L;

	private List<ImageElement> imageDatas = Lists.newArrayList();
	
	@Data
	public static class ImageElement implements BaseEntity {
		private static final long serialVersionUID = 5812739484680868140L;
		
		private long fid;
		private String imageName;
		private String fragmentImage;
		private String extra;
	}
}
