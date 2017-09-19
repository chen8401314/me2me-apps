package com.me2me.live.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

@Data
public class TopicCategoryDto implements BaseEntity {
	private static final long serialVersionUID = 1L;
	private List<Category> categories = new ArrayList<>();

	@Data
	public static class Category implements Serializable{
		private static final long serialVersionUID = 1L;
		private String kcName;
		private int kcid;
		private String kcIcon;
		private String kcImage;
	}
}
