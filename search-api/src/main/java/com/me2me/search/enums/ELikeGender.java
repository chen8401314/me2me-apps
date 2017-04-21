package com.me2me.search.enums;
/**
 * 性别喜好
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
public enum ELikeGender {

	BOY(1,"爱男神"),
	GIRL(2,"爱女神"),
	ALL(3,"男女通吃");
	
	private int value;
	private String name;
	ELikeGender(int v,String name){
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
	public static ELikeGender fromCode(int code){
		for(ELikeGender g:ELikeGender.values()){
			if(g.value==code){
				return g;
			}
		}
		return null;
	}
	
}
