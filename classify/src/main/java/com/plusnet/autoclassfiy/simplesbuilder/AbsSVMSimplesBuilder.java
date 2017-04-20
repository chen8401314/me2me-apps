package com.plusnet.autoclassfiy.simplesbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.plusnet.autoclassfiy.Constant;
import com.plusnet.autoclassfiy.idf.IDFKeywordService;
import com.plusnet.autoclassfiy.idf.IDFKeywordServiceImpl;
import com.plusnet.autoclassfiy.idf.TFIDFKeyword;
import com.plusnet.deduplicate.utils.SVMUtils;

/**
 * svm训练样本制作器。
 * @author zhangjiwei
 * @date 2016年9月7日
 *
 */
public abstract class AbsSVMSimplesBuilder {
	public static final Integer MAX_FEATURE_NUM=100;
	protected IDFKeywordService keywordService;
	protected Map<String, Object> typeDic;
	/**
	 * 类初始化时会加载词典、分类字典、svm模型文件，比较耗时 
	 */
	public AbsSVMSimplesBuilder(){
		InputStream ml_types =AbsSVMSimplesBuilder.class.getResourceAsStream(Constant.TYPE_DIC_FILE);
		keywordService = new IDFKeywordServiceImpl();
		try {
			String dic = IOUtils.toString(ml_types,"utf-8");
			typeDic = JSON.parseObject(dic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建一行
	 * @param txt 文本内容
	 * @param type 分类ID，必须为数字
	 * @return
	 */
	protected String buildLine(String txt, Integer type) {
		// idf 提取100个特征值
		List<TFIDFKeyword> keywords = this.keywordService.getTFIDFKeywordByDoc(txt, MAX_FEATURE_NUM, true);
		if(keywords.isEmpty()){
			return null;
		}
		// 按id 倒序
		Collections.sort(keywords, new Comparator<TFIDFKeyword>() {
			@Override
			public int compare(TFIDFKeyword o1, TFIDFKeyword o2) {
				int ret = (int) (o1.getIndex() - o2.getIndex());
				return ret;
			}
		});

		StringBuilder sb = new StringBuilder();
		sb.append(type + " ");
		for (TFIDFKeyword key : keywords) {
			double v =SVMUtils.to1(key.getTfidf());
			String sv = String.format("%.6f",v);
			sb.append(key.getIndex() + ":" + sv+" ");
		}
		return sb.toString();
	}
	
	protected String getIndexByTypeName(String typeName) {
		for(Map.Entry<String, Object> obj:this.typeDic.entrySet()){
			if(obj.getValue().equals(typeName)){
				return obj.getKey();
			}
		}
		return null;
	}
	
	
}