package com.plusnet.deduplicate.svm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.plusnet.autoclassfiy.ClassifierResult;
import com.plusnet.autoclassfiy.SVMClassifier;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;

public class SVMPredictTest {
	

	@Test
	public void predict() {
		SVMClassifier classifier = new SVMClassifier();
		String txt = "";
		try {
			txt = IOUtils.toString(SVMPredictTest.class.getResourceAsStream("/content.txt"), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//txt="美食打造震撼微型景观：苹果蛋糕闪光装置 美食打造震撼微型景观：路障设置现场";
		for (int i = 1; i <= 1; i++) {
			ClassifierResult result = classifier.predict(txt);
			System.out.println(txt);
			System.out.println(result.getFeatureKeyList());
			System.out.println(result.getSocreTypeList());
			System.out.println(result);
		}
	}

	/**
	 * 评测预测准确率
	 * 
	 * @throws Exception
	 */
//	@Test
	public void testModelAccuracy() throws Exception {
		
		File svmSimpleFile = new File("D:/svm-simples/svm.model.train"); // simples:62600,types:20,accuracy:79.3147%
		File modelFile = new File("D:/svm-simples/svm.model");

		Model model = Model.load(modelFile);
		Problem prob = Problem.readFromFile(svmSimpleFile, 1);
		float success = 0;
		for (int i = 0; i < prob.l; i++) {
			Feature[] fi = prob.x[i];
			double preset = prob.y[i];
			double pv = Linear.predict(model, fi);
			if (preset == pv) {
				success++;
			}
		}

		System.out.println(String.format("simples:%d,types:%d,accuracy:%s", prob.l, model.getNrClass(),
				success / prob.l * 100 + "%"));
	}

}
