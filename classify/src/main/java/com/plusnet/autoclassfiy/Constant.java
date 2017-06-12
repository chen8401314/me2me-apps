package com.plusnet.autoclassfiy;
/**
 * 常量
 * @author zhangjiwei
 * @date 2016年9月8日
 *
 */
public class Constant {
	/**
	 * idf keyword 模型文件路径
	 */
	public static final String KEYWORD_FILE="/svm/tfidf_keywords.json";
	/**
	 * svm 模型路径
	 */
	public static final String SVM_MODEL_FILE="/svm/svm.model";
	/**
	 * 最大特征向量数量
	 */
	public static final int MAX_FEATURE_KEYS=200;
	/**
	 * 文档总数keyword
	 */
	public static final  String ALL_DOCS_KEY="__DOC_COUNT__";
	public static final int ALL_DOCS_KEY_ID=-1;
	/**
	 * 当前索引编号keyword
	 */
	public static final  String CUR_POS_KEY="__CUR_POS__";
	public static final int CUR_KEY_POS_ID=-2;
}
