package com.plusnet.autoclassfiy;

public class ScoreType implements Comparable<ScoreType>{
	private String type;
	private int index;
	private double score;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	@Override
	public int compareTo(ScoreType o) {
		if(o.getScore()-this.getScore()==0){
			return 0;
		}
		return (o.getScore()-this.getScore())>0?1:-1;
	}
	@Override
	public String toString() {
		return type +"("+score + ")";
	}
}

