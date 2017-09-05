package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowAnchorListDTO implements BaseEntity {
	private static final long serialVersionUID = -748268165893758794L;

	private List<AnchorInfoElement> resultList = Lists.newArrayList();
	
	@Data
	public static class AnchorInfoElement implements BaseEntity {
		private static final long serialVersionUID = 143750252687188302L;
		
		private long aid;
		private String avatar;
		private String nickName;
		private String summary;
		private int signUpCount;
		private String qq;
		private int status;
	}
}
