package com.me2me.common.utils;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;

public class ProbabilityUtils {
	/**
	 * 判断是否出现在某一概率
	 * 
	 * @author zhangjiwei
	 * @date Sep 12, 2017
	 * @param prob
	 *            概率大小，如70,20
	 * @return
	 */
	public static boolean isInProb(int prob) {
		Set<Integer> rightDigs = new HashSet<>();
		while (prob > rightDigs.size()) { // 随机序列。
			rightDigs.add(RandomUtils.nextInt(0, 101));
		}
		return rightDigs.contains(RandomUtils.nextInt(0, 101));
	}
}
