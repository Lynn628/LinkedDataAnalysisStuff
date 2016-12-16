package com.cse.utils.query.virtuoso.bean;

/**
 * 对应于Sparql查询返回三列
 * 
 * @author chengwenyao
 * 
 */
public class TripleColumn {
	// 对应于查询得到的第一列
	private String firstValue;
	// 对应于查询得到的第二列
	private String secondValue;
	// 对应于查询得到的第三列
	private String thirdValue;

	public String getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}

	public String getSecondValue() {
		return secondValue;
	}

	public void setSecondValue(String secondValue) {
		this.secondValue = secondValue;
	}

	public String getThirdValue() {
		return thirdValue;
	}

	public void setThirdValue(String thirdValue) {
		this.thirdValue = thirdValue;
	}
}
