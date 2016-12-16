package com.cse.utils.query.virtuoso.bean;

/**
 * 对应于Sparql查询返回一列
 * @author chengwenyao
 *
 */
public class SingleColumn {
	// 对应于查询得到的结果
	private String firstValue;

	public String getFirstValue() {
		return firstValue;
	}

	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}
	
}
