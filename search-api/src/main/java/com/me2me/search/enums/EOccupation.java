package com.me2me.search.enums;
/**
 * 职业
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
public enum EOccupation {
	ZYD(1,"作业党"),
	MN(2,"码农"),
	JLD(3,"家里蹲"),
	SYR(4,"手艺人"),
	BDZC(5,"霸道总裁"),
	BJG(6,"编辑狗"),
	KZCF(7,"靠嘴吃饭"),
	BYTS(8,"白衣天使"),
	YD(9,"园丁"),
	WRMFW(10,"为人民服务");
	
	private int value;
	private String name;
	EOccupation(int v,String name){
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
	public static EOccupation fromCode(int code){
		for(EOccupation g:EOccupation.values()){
			if(g.value==code){
				return g;
			}
		}
		return null;
	}
	
}
