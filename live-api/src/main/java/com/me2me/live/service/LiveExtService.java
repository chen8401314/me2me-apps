package com.me2me.live.service;

import com.me2me.common.web.Response;

/**
 * liveService 太大了，这个是拆分扩展方法。
 * @author zhangjiwei
 * @date Sep 19, 2017
 */
public interface LiveExtService {
	/**
	 * 查询所有分类。
	 * @author zhangjiwei
	 * @date Sep 19, 2017
	 * @return
	 */
	Response category();
	/**
	 * 查询王国分类分页
	 * @author zhangjiwei
	 * @date Sep 19, 2017
	 * @param uid
	 * @param kcid 分类ID
	 * @param page 分页
	 * @return
	 */
	Response kingdomByCategory(int uid,int kcid,int page);
}
