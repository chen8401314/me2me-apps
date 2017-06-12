package com.me2me.search.cache;
/**
 * 简单缓存实现 ，用于定长时间的对象缓存。
 * @author jiwei.zhang
 * @date 2016年5月12日
 */
public interface ICacheExecutor {
	/**
	 * 制造要缓存的对象。
	 * @return
	 */
	public Object makeCacheObject();
}
