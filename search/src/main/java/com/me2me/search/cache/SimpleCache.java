package com.me2me.search.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;
/**
 * 简单缓存实现 ，用于定长时间的对象缓存。
 * @author jiwei.zhang
 * @date 2016年5月12日
 */
@Component
public class SimpleCache {
	private Map<String, CachedObject> cacheObjectMap = new HashMap<String, CachedObject>();

	public SimpleCache() {
		Thread thread= new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						checkCache();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}

	/**
	 * 检查过期缓存并删除。
	 */
	private void checkCache() {
		Iterator<Map.Entry<String, CachedObject>> itr = cacheObjectMap.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, CachedObject> entry = itr.next();
			CachedObject obj = entry.getValue();
			if (isExpired(obj)) {
				itr.remove(); // 删除过期内容。
				//System.out.println("remove expired cache:"+entry.getKey());
			}
		}
	}

	/**
	 * 缓存对象
	 * 
	 * @param cacheExecutor
	 * @param key
	 * @param time 缓存毫秒数。如果设置0，永不过期。
	 */
	synchronized public void cache( String key,ICacheExecutor cacheExecutor, int seconds) {
		cache(key,cacheExecutor.makeCacheObject(),seconds);
	}
	/**
	 * 缓存一个对象，单位为秒
	 * @param cacheExecutor
	 * @param key
	 * @param time 缓存秒数。如果设置0，永不过期。
	 */
	synchronized public void cache(String key,Object value, int seconds) {
		CachedObject co = new CachedObject();
		co.setBeginDate(System.currentTimeMillis());
		co.setKey(key);
		co.setValue(value);
		co.setCacheTime(seconds);
		cacheObjectMap.put(key, co);
	}
	/**
	 * 取缓存
	 * @param key
	 * @return
	 */
	public Object getCache(String key){
		CachedObject co = this.cacheObjectMap.get(key);
		if(co!=null){
			return co.getValue();
		}
		return null;
	}
	/**
	 * 取缓存。
	 * @param cls
	 * @param key
	 * @return
	 */
	public <T> T getCache(Class<T> cls,String key){
		return (T) getCache(key);
	}
	/**
	 * 判断缓存是否过期
	 * 
	 * @param co
	 * @return
	 */
	public boolean isExpired(CachedObject co) {
		long passedTime = System.currentTimeMillis() - co.getBeginDate();
		long cacheTime=co.getCacheTime()*1000;
		if(cacheTime==0){
			return false;
		}else{
			return passedTime > cacheTime ? true : false;
		}
	}

	/**
	 * 判断缓存是否过期，如果key不存在返回true.
	 * 
	 * @param key
	 * @return
	 */
	public boolean isExpired(String key) {
		CachedObject obj = cacheObjectMap.get(key);
		if (obj != null) {
			return isExpired(obj);
		}
		return true;
	}
}
