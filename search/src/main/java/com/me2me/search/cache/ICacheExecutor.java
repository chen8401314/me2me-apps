package com.me2me.search.cache;

public interface ICacheExecutor {
	/**
	 * 制造要缓存的对象。
	 * @return
	 */
	public Object makeCacheObject();
}
