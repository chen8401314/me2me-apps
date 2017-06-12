package com.me2me.content.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class EmojiPackDto implements BaseEntity{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private List<PackageData> packageData = Lists.newArrayList();
	
	@Data
	public static class PackageData implements BaseEntity{
		private static final long serialVersionUID = 1L;
		int id;
		String name;
		String cover;
		int emojiType;
		int version;
		int pVersion;
		String extra;
	}
}
