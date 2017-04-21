package com.plusnet.autoclassfiy.idf;
/**
 * 机器学习包中基本的关键词，用于TFIDF，svm等。
 * @author jiwei.zhang
 * @date 2016年8月11日
 */


public class TFIDFKeyword {
	private String name;
	private int appearCount;	// 该关键词出现次数
	private String keyType;		//词性
	private double idf;
	private int index;
	private double tf;
	private double tfidf;
	

	public TFIDFKeyword() {
		super();
	}

	public TFIDFKeyword(String name) {
		super();
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getAppearCount() {
		return appearCount;
	}

	public void setAppearCount(int appearCount) {
		this.appearCount = appearCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public double getIdf() {
		return idf;
	}

	public void setIdf(double idf) {
		this.idf = idf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TFIDFKeyword other = (TFIDFKeyword) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public double getTf() {
		return tf;
	}

	public void setTf(double tf) {
		this.tf = tf;
	}

	public double getTfidf() {
		return tfidf;
	}

	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}

	@Override
	public String toString() {
		return name + "(" + index+"/"+tfidf+ ")";
	}

}
