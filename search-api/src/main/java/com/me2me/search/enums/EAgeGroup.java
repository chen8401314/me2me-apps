package com.me2me.search.enums;
/**
 * 用户年龄段
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
public enum EAgeGroup {

	AGE00(1,"00后"),
	AGE95(2,"95后"),
	AGE90(3,"90后"),
	AGE80(4,"80后"),
	AGE85(5,"85后"),
	AGE70(6,"70后");
	
	private int value;
	private String name;
	EAgeGroup(int v,String name){
		this.value=v;
		this.name=name;
	}
	
	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	/**
	 * 从code构造。
	 * @author zhangjiwei
	 * @date Apr 20, 2017
	 * @param code
	 * @return
	 */
	public static EAgeGroup fromCode(int code){
		for(EAgeGroup g:EAgeGroup.values()){
			if(g.value==code){
				return g;
			}
		}
		return null;
	}
	
}
