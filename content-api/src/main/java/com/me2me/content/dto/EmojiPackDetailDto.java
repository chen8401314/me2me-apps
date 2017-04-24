package com.me2me.content.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class EmojiPackDetailDto implements BaseEntity{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private long packageId;
	private int emojiType;
	private String packageName;
	private String packageCover;
	private int  packageVersion;
	private int  packagePversion;
	private List<PackageDetailData> emojiData=Lists.newArrayList();

	@Data
	public static class PackageDetailData implements BaseEntity{
		private static final long serialVersionUID = 1L;
		private long id;
		private String title;
		private String image;
		private String thumb;
		private long w;
		private long h;
		private long thumb_w;
		private long thumb_h;
		private String extra;
		private int emojiType;
	}
}
