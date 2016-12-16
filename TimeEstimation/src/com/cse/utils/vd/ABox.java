package com.cse.utils.vd;

import com.cse.utils.query.statistic.DatasetInfo;

/**
 * 获取ABox中对象的虚拟文档信息
 * @author chengwenyao
 *
 */
public class ABox {
	// 原始的链接数据集合，包含ABOX和TBOX
	DatasetInfo datasetInfo;
	
	private ABox(){
		
	}
	
	public ABox(DatasetInfo datasetInfo){
		this.datasetInfo = datasetInfo;
	}
	
	
}
