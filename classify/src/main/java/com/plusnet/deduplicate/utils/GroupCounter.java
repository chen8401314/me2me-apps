package com.plusnet.deduplicate.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupCounter {
	public Map<Object, Long> counterMap;

	public GroupCounter() {
		counterMap = new LinkedHashMap<>();
	}

	/**
	 * 使key对应的值+指定的数量
	 * 
	 * @param key
	 *            待修改的key.
	 * @param num
	 *            自增量，如1
	 */
	synchronized void countUp(Object key, int num) {
		Long val = counterMap.get(key);
		if (val == null) {
			val = 0L;
		}
		val += num;
		counterMap.put(key, val);
	}

	/**
	 * key对应的计数+1
	 * 
	 * @param key
	 */
	synchronized void countUp(Object key) {
		Long val = counterMap.get(key);
		if (val == null) {
			val = 0L;
		}
		val++;
		counterMap.put(key, val);
	}

	@Override
	public String toString() {
		return counterMap.toString();
	}

	/**
	 * 如果key不存在，返回 null
	 * 
	 * @param key
	 * @return
	 */
	public Long getKeyCount(String key) {
		return counterMap.get(key);
	}

	public Map<Object, Long> getAllKeyCounts() {
		return this.counterMap;
	}

	/**
	 * 取排序过的keycount,
	 * 
	 * @param sortDesc
	 *            1降序，其它升序。
	 * @return
	 */
	public Map<Object, Long> getSortedKeyCounts() {
		List<Map.Entry<String, Long>> mappingList = new ArrayList(counterMap.entrySet());
		// 通过比较器实现比较排序
		Collections.sort(mappingList, new Comparator<Map.Entry<String, Long>>() {
			public int compare(Map.Entry<String, Long> mapping1, Map.Entry<String, Long> mapping2) {
				if(mapping1.getValue()==mapping2.getValue()){
					return 0;
				}
				return mapping1.getValue()>mapping2.getValue()?-1:1;
			}
		});
		Map<Object, Long> newMap = new LinkedHashMap<Object, Long>();  
        for (int i = 0; i < mappingList.size(); i++) {  
            newMap.put(mappingList.get(i).getKey(), mappingList.get(i).getValue());  
        }  
        return newMap;  
	}
}
