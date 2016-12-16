package com.cse.utils.vd.bean;

import org.apache.jena.rdf.model.RDFNode;

/**
 * 用来表示与指定节点关联节点之间的关系，包括连接边的类型以及是作为出还是入
 * 比如：当前节点为A，有一个关系为A->B,则当前表示A和B之间有某种关系，且这种
 * 关系是从A出去的
 * @author chengwenyao
 *
 */
public class RelationAndObject {
	// 表示关联的边
	private RDFNode relation;
	// 表示关联的节点
	private RDFNode node;
	// 表示关系是输入关系还是输出关系
	private int inout;
	public RDFNode getRelation() {
		return relation;
	}
	public void setRelation(RDFNode relation) {
		this.relation = relation;
	}
	public RDFNode getNode() {
		return node;
	}
	public void setNode(RDFNode node) {
		this.node = node;
	}
	public int getInout() {
		return inout;
	}
	public void setInout(int inout) {
		this.inout = inout;
	}
}
