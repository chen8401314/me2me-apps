package com.me2me.search.mappers;

import java.util.List;

import com.me2me.search.dto.THotKeyword;


/**
 * 热门关键字。
 * @author zhangjiwei
 * @date Apr 5, 2017
 */
public interface SearchHotKeywordMapper  {
	/**
	 * 获取指定数量的热门关键字。
	 * @author zhangjiwei
	 * @date Apr 5, 2017
	 * @param maxCount
	 * @return
	 */
	public List<THotKeyword> getHotKeywords(int maxCount);

}