package com.dd.whateat.bean;

import java.io.Serializable;

public class KeyWords implements Comparable,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "KeyWords [name=" + name + ", weight=" + weight + "]";
	}

	private String name;
	private int weight;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Object kw) {
		KeyWords keyWords = (KeyWords) kw;
		int w1 = keyWords.getWeight();
		int w2 = this.getWeight();

		return w2 < w1 ? 1 : -1; // 按照权重由大到小排列
	}
}
