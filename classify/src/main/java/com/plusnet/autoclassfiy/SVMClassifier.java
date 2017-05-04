package com.plusnet.autoclassfiy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.plusnet.autoclassfiy.idf.IDFKeywordService;
import com.plusnet.autoclassfiy.idf.IDFKeywordServiceImpl;
import com.plusnet.autoclassfiy.idf.TFIDFKeyword;
import com.plusnet.deduplicate.utils.SVMUtils;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;

/**
 * 文本分类器
 * @author zhangjiwei
 * @date 2016年9月7日
 *
 */
public class SVMClassifier {
	private IDFKeywordService keywordService;
	private Model svmModel;
	private JSONObject typeDic;
	private Logger log = LoggerFactory.getLogger(SVMClassifier.class);
	/**
	 * 类初始化时会加载词典、分类字典、svm模型文件，比较耗时 
	 */
	public SVMClassifier(){
		log.info("loading SVMClassifier model.");
		try {
			InputStream model =SVMClassifier.class.getResourceAsStream(Constant.SVM_MODEL_FILE);
			InputStream ml_types =SVMClassifier.class.getResourceAsStream(Constant.SVM_MODEL_FILE+".mapping");
			keywordService = new IDFKeywordServiceImpl();
			
			String dic = IOUtils.toString(ml_types,"utf-8");
			typeDic = JSON.parseObject(dic);
			svmModel = Model.load(new InputStreamReader(model));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 预测文本内容属于哪一类。
	 * @param content
	 * @return
	 */
	public ClassifierResult predict(String content) {
		List<TFIDFKeyword> keyList = keywordService.getTFIDFKeywordByDoc(content, Constant.MAX_FEATURE_KEYS, true);
		double[] dec_values = new double[svmModel.getNrClass()];
		double v = Linear.predictValues(svmModel, getNodes(keyList), dec_values);
		List<ScoreType> typeList = new ArrayList<>();
		
		for(int i=0;i<dec_values.length;i++){
			int index = svmModel.getLabels()[i];
			
			String typeStr = this.getTypeNameByIndex(index);
			ScoreType type = new ScoreType();
			type.setIndex(index);
			type.setScore(dec_values[i]);
			type.setType(typeStr);
			typeList.add(type);
		}
		Collections.sort(typeList);
		
		ClassifierResult result = new ClassifierResult();
		result.setFeatureKeyList(keyList);
		result.setResult((int) v);
		result.setResultType(getTypeNameByIndex((int) v));
		result.setSocreTypeList(typeList);
		return result;
	}
	private  Feature[] getNodes(List<TFIDFKeyword> keyList) {
		Feature[] nodes = new Feature[keyList.size()];
		int i = 0;
		for (TFIDFKeyword key : keyList) {
			double v =SVMUtils.to1(key.getTfidf());
			nodes[i] = new FeatureNode((int) key.getIndex(), v);
			i++;
		}
		return nodes;
	}
	
	private String getTypeNameByIndex(int index) {
		for(Entry<String, Object> entry:typeDic.entrySet()){
			String key = entry.getKey();
			Integer val =(Integer) entry.getValue();
			if(val==index){
				return key;
			}
		}
		return null;
	}
	
}
