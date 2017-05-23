package com.plusnet.autoclassfiy.simplesbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final Integer DEFAULT_FEATURE_NUM=100;
	protected IDFKeywordService keywordService;
	private Logger log = LoggerFactory.getLogger(AbsSVMSimplesBuilder.class);
	/**
	 * 类初始化时会加载词典、分类字典、svm模型文件，比较耗时 
	 */
	public AbsSVMSimplesBuilder(){
		InputStream is = AbsSVMSimplesBuilder.class.getResourceAsStream(Constant.KEYWORD_FILE);
		 keywordService= new IDFKeywordServiceImpl(is);
	}
	/**
	 * 创建一行
	 * @param txt 文本内容
	 * @param type 分类ID，必须为数字
	 * @return
	 */
	protected String buildLine(String txt, Integer type) {
		return buildLine( txt, type,DEFAULT_FEATURE_NUM,new HashMap<>());
	}
	
	protected String buildLine(String txt,Integer type,int maxFeatureCount,Map<String,Float> weightKeywords){
		// idf 提取100个特征值
		//List<TFIDFKeyword> keywords = this.keywordService.getTFIDFKeywordByDoc(txt, maxFeatureCount, true,weightKeywords);
		List<TFIDFKeyword> keywords = this.keywordService.cutWord(txt);		// IDF提词效果不好，直接切词看看。
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
		//log.info("parse content:\n{}\n keywords:\n{}",txt,keywords);
		StringBuilder sb = new StringBuilder();
		sb.append(type + " ");
		for (TFIDFKeyword key : keywords) {
			Float score= weightKeywords.get(key.getName());
			if(score==null){
				score=1f;
			}
			double v =SVMUtils.to1(score);
			String sv = String.format("%.6f",v);
			sb.append(key.getIndex() + ":" + sv+" ");
		}
		return sb.toString();
	}
}
