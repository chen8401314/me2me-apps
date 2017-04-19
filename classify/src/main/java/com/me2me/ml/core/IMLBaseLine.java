package com.me2me.ml.core;

import java.util.List;
/**
 * 机器学习基本步骤 特征提取-》训练-》预测
 * @author zhangjiwei
 * @date Apr 13, 2017
 * @param <T>
 */
public interface IMLBaseLine<T> {
	/**
	 * 提取特征
	 * @author zhangjiwei
	 * @date Apr 13, 2017
	 * @param input
	 * @return
	 */
	public List<T> extractFeatures(T input);
	
	public void train();
	
	public void predict();
}
