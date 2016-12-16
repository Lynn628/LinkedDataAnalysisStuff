package com.cse.utils.dbpedia.bean;

public class LinkMessage {
	private String subindividual;
	private String property;
	private String objindividual;
    public LinkMessage(){
    	
    }
    public LinkMessage(String subindividual,String property,String objindividual){
    	this.subindividual=subindividual;
    	this.property=property;
    	this.objindividual=objindividual;
    }
	public String getSubindividual() {
		return subindividual;
	}
	public void setSubindividual(String subindividual) {
		this.subindividual = subindividual;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getObjindividual() {
		return objindividual;
	}
	public void setObjindividual(String objindividual) {
		this.objindividual = objindividual;
	}
    
}
