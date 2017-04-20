package com.me2me.search.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;


/**
 * 推荐用户
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
public class RecommendListDto<T> implements BaseEntity {
	private static final long serialVersionUID = -3412681619710644087L;

	private List<T> dataList = Lists.newArrayList();

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	
	
}
