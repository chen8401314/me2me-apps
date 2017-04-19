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
	SVMClassifier classifier = new SVMClassifier();

	@Test
	public void predict() {
		String txt = "";
		try {
			txt = IOUtils.toString(SVMPredictTest.class.getResourceAsStream("/content.txt"), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// String txt=
		// "美食是一个完整的世界，你相信通过它能扑捉一个曼妙的世界吗？如果你想看各路风景，换个角度看世界，通过美食景观你能从鲜亮的猕猴桃世界、缤纷的蔬菜世界里，找到自己想要的生活美感。
		// 西雅图艺术家Christopher
		// Boffoli的“Disparity”系列作品旨在引发美国民众对过度消费的关注，他将自制的各种雕像粘贴在真实的食物上，一个个微型景观栩栩如生，震撼世界了双眼！
		// 美食打造震撼微型景观：甜橙割草机 美食打造震撼微型景观：豌豆伐木场 美食打造震撼微型景观：黑莓犯罪现场调查 意大利腊香肠救火现场
		// 美食打造震撼微型景观：甜椒摩托车训练场 美食打造震撼微型景观：花生酱蛋糕修理员 巧克力面包屑清洁员 美食打造震撼微型景观：贝壳情侣
		// 美食打造震撼微型景观：薄饼制作团队 美食打造震撼微型景观：猕猴桃粉刷匠 美食打造震撼微型景观：杯型蛋糕滑雪场
		// 美食打造震撼微型景观：杏仁小甜饼制作团队 美食打造震撼微型景观：黄瓜日光浴 美食打造震撼微型景观：暖手蛋糕
		// 美食打造震撼微型景观：巧克力采石场 美食打造震撼微型景观：高级瑜伽 美食打造震撼微型景观：果馅糕点垂钓池
		// 美食打造震撼微型景观：苹果蛋糕闪光装置 美食打造震撼微型景观：路障设置现场";
		
		txt="美食打造震撼微型景观：苹果蛋糕闪光装置 美食打造震撼微型景观：路障设置现场";
		for (int i = 1; i <= 10; i++) {
			ClassifierResult result = classifier.predict(txt);
			System.out.println(result);
		}
	}

	/**
	 * 评测预测准确率
	 * 
	 * @throws Exception
	 */
	//@Test
	public void testModelAccuracy() throws Exception {
		
		File svmSimpleFile = new File(SVMPredictTest.class.getResource("/svm-simples-all").getFile()); // simples:62600,types:20,accuracy:79.3147%
		File modelFile = new File(SVMPredictTest.class.getResource("/conf/svm-model-all").getFile());

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
