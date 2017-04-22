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
	long packageId;
	int emojiType;
	String packageName;
	String packageCover;
	int  packageVersion;
	int  packagePversion;
	List<PackageDetailData> emojiData=Lists.newArrayList();
	public void addEmojiData(PackageDetailData data){
		emojiData.add(data);
	}
	@Data
	public static class PackageDetailData implements BaseEntity{
		private static final long serialVersionUID = 1L;
		long id;
		String title;
		String image;
		String thumb;
		long w;
		long h;
		long thumb_w;
		long thumb_h;
		String extra;
	}
}
