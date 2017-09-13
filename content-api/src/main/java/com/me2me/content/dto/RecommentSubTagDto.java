package com.me2me.content.dto;

import java.util.ArrayList;
import java.util.List;

import com.me2me.common.web.BaseEntity;

import lombok.Data;
@Data
public class RecommentSubTagDto implements BaseEntity{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> subTagList=new ArrayList<>();
}
