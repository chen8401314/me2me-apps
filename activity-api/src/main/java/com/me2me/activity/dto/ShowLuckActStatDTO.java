package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowLuckActStatDTO implements BaseEntity {

	private static final long serialVersionUID = 6092984688085989231L;
 
	private List<LuckActStatElement> result = Lists.newArrayList();
	
	@Data
	public static class LuckActStatElement implements BaseEntity{

		private static final long serialVersionUID = -8332411293590490840L;
		
		private String dateStr;
		private int enterUV;
		private int enterPV;
		private int prizeNum;
		private String prizeNames;
		
	}
}
