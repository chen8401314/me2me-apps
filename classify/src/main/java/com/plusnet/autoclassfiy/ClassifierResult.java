package com.plusnet.autoclassfiy;

import java.util.List;

import com.plusnet.autoclassfiy.idf.TFIDFKeyword;

/**
 * 预测结果封装
 * @author zhangjiwei
 * @date 2016年9月7日
 *
 */
public class ClassifierResult {
	/**
	 * 分类结果
	 */
	private String resultType;
	private List<ScoreType> socreTypeList;
	private List<TFIDFKeyword> featureKeyList;
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}


	public List<ScoreType> getSocreTypeList() {
		return socreTypeList;
	}
	public void setSocreTypeList(List<ScoreType> socreTypeList) {
		this.socreTypeList = socreTypeList;
	}
	public List<TFIDFKeyword> getFeatureKeyList() {
		return featureKeyList;
	}
	public void setFeatureKeyList(List<TFIDFKeyword> featureKeyList) {
		this.featureKeyList = featureKeyList;
	}
	@Override
	public String toString() {
		return resultType;
	}
	
}

