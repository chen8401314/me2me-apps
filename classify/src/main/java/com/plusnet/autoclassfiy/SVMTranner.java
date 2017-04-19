package com.plusnet.autoclassfiy;

import java.io.File;

import de.bwaldvogel.liblinear.Train;
/**
 *  svm 训练器。执行本类，生成训练样本
 * @author zhangjiwei
 * @date 2016年9月8日
 *
 */
public class SVMTranner {
	public static void main(String[] args) {
		File simpleFile = new File("D:/opt/svm/svm-simples-all");
		File modelFile = new File("D:/opt/svm/svm-model-all");
		try {
			Train.main(new String[] { simpleFile.getAbsolutePath(), modelFile.getAbsolutePath() });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
