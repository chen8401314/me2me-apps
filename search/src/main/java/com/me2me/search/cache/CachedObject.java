package com.me2me.search.cache;

/**
 * 缓存对象
 * @author jiwei.zhang
 * @date 2016年5月12日
 */
public class CachedObject {
	private long beginDate;
	private String key;
	private Object value;
	private int cacheTime;
	
	
	public int getCacheTime() {
		return cacheTime;
	}
	public void setCacheTime(int cacheTime) {
		this.cacheTime = cacheTime;
	}

	public long getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(long beginDate) {
		this.beginDate = beginDate;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
