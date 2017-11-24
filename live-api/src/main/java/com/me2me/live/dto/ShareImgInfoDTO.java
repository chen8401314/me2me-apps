package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShareImgInfoDTO implements BaseEntity {
	private static final long serialVersionUID = -136051781095303227L;

	private List<ImageInfoElement> imageInfos = Lists.newArrayList();
	private String textImageDatas;
	private int textImageHeight;
	private int textImageWidth;
	
	@Data
	public static class ImageInfoElement implements BaseEntity {
		private static final long serialVersionUID = -3058029731685590433L;
		
		private int type;
		private String imageUrl;
		private int textHeightStart;
		private int textHeightEnd;
	}
}
