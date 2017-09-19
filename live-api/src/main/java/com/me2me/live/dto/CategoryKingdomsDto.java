package com.me2me.live.dto;

import java.util.ArrayList;
import java.util.List;

import com.me2me.common.web.BaseEntity;
import com.me2me.content.dto.NewKingdom;
import com.me2me.live.dto.TopicCategoryDto.Category;

import lombok.Data;
@Data
public class CategoryKingdomsDto implements BaseEntity{
	private static final long serialVersionUID = 1L;
	private Category category;
	private NewKingdom coverKingdom;
	private List<NewKingdom> kingdoms= new ArrayList<>();
}
