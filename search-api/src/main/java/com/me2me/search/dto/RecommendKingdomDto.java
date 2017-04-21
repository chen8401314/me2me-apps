package com.me2me.search.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;


/**
 * 推荐用户
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
public class RecommendKingdomDto implements BaseEntity {
	private static final long serialVersionUID = -3412681619710644087L;

	private List<RecommendKingdom> kingdomList = Lists.newArrayList();

	public List<RecommendKingdom> getKingdomList() {
		return kingdomList;
	}

	public void setKingdomList(List<RecommendKingdom> kingdomList) {
		this.kingdomList = kingdomList;
	}

	
}
