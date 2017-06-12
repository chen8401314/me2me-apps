package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pc329 on 2017/3/17.
 */
@Data
public class PricedKingdomDto implements BaseEntity {
	private static final long serialVersionUID = 1L;
	private List<TopicData> listData = Lists.newArrayList();

	@Data
	public static class TopicData extends BasicKingdomInfo implements BaseEntity {
		private static final long serialVersionUID = 1L;
		
	}

}
