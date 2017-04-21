package com.plusnet.autoclassfiy.idf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
/**
 * 词库服务。此服务是NLP学习中的基础服务，提供TF-IDF持续训练，关键词索引等服务。
 * @author jiwei.zhang
 * @date 2016年8月12日
 */
public interface IDFKeywordService {

	/**
	 * 向词库服务中添加一个待训练(IDF模型)的文档。
	 * @param key
	 */
	public void addTrainDoc(String doc);
	/**
	 * 保存训练结果
	 */
	public void saveTrainResult(File file);
	
	/**
	 * 获取关键字,如果不存在，则新建。
	 * @param key
	 * @return
	 */
	public TFIDFKeyword getKeyword(String key);

	/**
	 * 切词
	 * @param content
	 * @return
	 */
	public List<TFIDFKeyword> cutWord(String content);
	
	/**
	 * 从输入的文档中提取关键词，并计算TF-IDF值。 
	 * @param doc 输入的文档 
	 * @return 关键词列表
	 */
	public List<TFIDFKeyword> getTFIDFKeywordByDoc(String doc);
	
	/**
	 * 从输入的文档中提取关键词，并计算TF-IDF值。 
	 * @param doc 待分析的文档
	 * @param maxKeywordNum 最大关键词数量
	 * @param sort true:按tf-idf值降序，false 不排序。
	 * @return 关键词列表
	 */
	public List<TFIDFKeyword> getTFIDFKeywordByDoc(String doc,int maxKeywordNum,boolean sort);
	
	public void reloadModel(InputStream modelFile) throws IOException;
	
}
