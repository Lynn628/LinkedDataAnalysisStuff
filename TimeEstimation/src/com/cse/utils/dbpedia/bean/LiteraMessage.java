package com.cse.utils.dbpedia.bean;

import java.util.List;
import java.util.Map;

public class LiteraMessage {
	private String individual;
	private String type;
	private Map<String,List<String>> commentproperty;
    private Map<String,List<String>> dataproperty;	
    public LiteraMessage(){
    	
    }
    public LiteraMessage(String individual,String type,Map<String,List<String>> commentproperty,Map<String,List<String>> dataproperty){
    	this.individual=individual;
    	this.type=type;
    	this.commentproperty=commentproperty;
    	this.dataproperty=dataproperty;
    }
	public String getIndividual() {
		return individual;
	}
	public void setIndividual(String individual) {
		this.individual = individual;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String,List<String>> getCommentproperty() {
		return commentproperty;
	}
	public void setCommentproperty(Map<String,List<String>> commentproperty) {
		this.commentproperty = commentproperty;
	}
	public Map<String,List<String>> getDataproperty() {
		return dataproperty;
	}
	public void setDataproperty(Map<String,List<String>> dataproperty) {
		this.dataproperty = dataproperty;
	}
    
}
