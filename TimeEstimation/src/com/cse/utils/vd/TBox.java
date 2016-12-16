package com.cse.utils.vd;

import com.cse.utils.query.statistic.DatasetInfo;

/**
 * 获取TBox中对象的虚拟文档信息
 * @author chengwenyao
 *
 */
public class TBox {
	// 原始的链接数据集合，包含ABOX和TBOX
	DatasetInfo datasetInfo;
	
	private TBox(){
		
	}
	
	public TBox(DatasetInfo datasetInfo){
		this.datasetInfo = datasetInfo;
	}
	
	
}
