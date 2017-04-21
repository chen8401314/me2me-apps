package com.plusnet.deduplicate.utils;

public class SVMUtils {
	/**
	 * 数字归1化。
	 * @param v
	 * @return
	 */
	static public double to1(double v){
		return Math.atan(v)*2/Math.PI;
	}
	/**
	 * 数字归1化。
	 * @param v
	 * @return
	 */
	static public double to1(float v){
		return to1((double)v);
	}
}
