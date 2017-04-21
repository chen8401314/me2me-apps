package com.me2me.ml.user;

import com.plusnet.autoclassfiy.ClassifierResult;

/**
 * 文本机器学习服务。
 * @author zhangjiwei
 * @date Apr 13, 2017
 */
public interface IMLTextService {
	/**
	 * 向服务提供监督形式的训练样本
	 * @author zhangjiwei
	 * @date Apr 13, 2017
	 * @param text
	 * @param type
	 */
	public void feed(String text,String type);
	
	public void predict(String input,String version);
	/**
	 * 训练新的模型。
	 * @author zhangjiwei
	 * @date Apr 13, 2017
	 * @return 返回模型版本。
	 */
	public String train();
	
	public ClassifierResult predict(String input);
}
