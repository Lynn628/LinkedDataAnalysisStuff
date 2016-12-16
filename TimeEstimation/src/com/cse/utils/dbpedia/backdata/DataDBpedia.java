package com.cse.utils.dbpedia.backdata;

import java.util.Set;

import com.cse.utils.dbpedia.bean.LiteraMessage;
import com.cse.utils.query.statistic.DatasetInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import virtuoso.jena.driver.VirtDataset;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.cse.utils.query.statistic.bean.Triple;
import com.cse.utils.query.statistic.bean.TripleString;
import com.cse.utils.query.virtuoso.SparqlQuery;
import com.cse.utils.query.virtuoso.bean.DoubleColumn;
import com.cse.utils.query.virtuoso.bean.SingleColumn;
import com.cse.utils.readparam.ReadConfParam;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

public class DataDBpedia {
	private static DatasetInfo datasetInfo;
	
	//构造函数
	public DataDBpedia(String url,String graphName,String username,String password){
		this.datasetInfo=new DatasetInfo(url,graphName, username, password);
	} 
	
	public DatasetInfo getDatasetInfo(){
		return datasetInfo;
	}
	
	public static List<TripleString> getIndividualRelation() {
		/*
		 * 得到实例之间的关系
		 */
		List<TripleString> result = new ArrayList<TripleString>();
		DatasetInfo datasetInfo = new DatasetInfo(
				"jdbc:virtuoso://223.3.69.83:1111", "http://dbpedia.org",
				"dba", "dba");
		Set<String> classes = datasetInfo.getAllClassWithSparql();
		for (String c : classes) {
			if (!c.equals("http://www.wikidata.org/entity/Q1914636")
					&& !c.equals("http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#Situation")
					&& !c.equals("http://www.wikidata.org/entity/Q194189")
					&& !c.equals("http://www.ontologydesignpatterns.org/ont/d0.owl#Activity")
					&& !c.equals("http://www.w3.org/2002/07/owl#Thing")) {
				Set<String> instenceOfClass = datasetInfo
						.getAllInstancesByClassWithSparql(c);
				for (String instence : instenceOfClass) {
					List<TripleString> instenceofTriples = datasetInfo
							.getConnectedRelationWithSparql(instence);
					result.addAll(instenceofTriples);
				}
			}
		}
		return result;
	}
	

	
    public static Set<String> getAllProperty(){
    	Set<String> propertySet=new HashSet<String>();
    	List<String> propertyClass=new ArrayList<String>();
    	propertyClass.add("http://www.w3.org/2002/07/owl#ObjectProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#DatatypeProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#AnnotationProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#topObjectProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#bottomDataProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#TransitiveProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#SymmetricProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#AsymmetricProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#FunctionalProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#InverseFunctionalProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#ReflexiveProperty");
    	propertyClass.add("http://www.w3.org/2002/07/owl#IrreflexiveProperty");
    	propertyClass.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
    	for(String pc : propertyClass){
    		Set<String> instenceOfClass = datasetInfo
					.getAllInstancesByClassWithSparql(pc);
    		for(String ioc : instenceOfClass)
    			propertySet.add(ioc);
    	}
    	return propertySet;
    }
    
    
    /**
	 * 得到实例之间的关系
	 */
    public static List<TripleString> getRelation(Set<String> classes,Set<String> propertySet,Judge judge){
    	List<TripleString> relationTriple = new LinkedList<TripleString>();
    	String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		TripleString t=null;
		int sum=0;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			RDFNode p = result.get("p");
			RDFNode o= result.get("o");
			subject=s.toString();
			property=p.toString();
			object=o.toString();
			
			if(!classes.contains(subject) && !propertySet.contains(subject)
					&& !judge.judgePropery(property)){        //过滤掉类声明的三元组、属性声明三元组
				
				if(!property.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
					&& o instanceof Resource){   //对象关系三元组
					sum++;
					System.out.println(sum);
					t=new TripleString(subject,property,object);
					relationTriple.add(t);
				}
			}
		}
		return relationTriple;
    }
    
    
    /**
     * 得到实例的文本
     * @param classes
     * @param propertySet
     * @param judge
     * @return
     */
    public static Map<String,LiteraMessage> getLitera(Set<String> classes,Set<String> propertySet,Judge judge){
    	Map<String,LiteraMessage>  litera=new HashMap<String,LiteraMessage>();
    	String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		String pro=null;
		LiteraMessage l=null;
		Map<String,List<String>> commentproperty=null;
		List<String> comment=null;
		Map<String,List<String>> dataproperty=null;
		List<String> data=null;
		int sum=0;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			RDFNode p = result.get("p");
			RDFNode o= result.get("o");
			subject=s.toString();
			property=p.toString();
			object=o.toString();
			if(!classes.contains(subject) && !propertySet.contains(subject)
					&& !judge.judgePropery(property)){
				if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
						|| !(o instanceof Resource)){
					sum++;
					System.out.println(sum);
					if (property.contains("#")) {
						pro = property.substring(property.lastIndexOf("#") + 1);
					} else {
						pro = property.substring(property.lastIndexOf("/") + 1);
					}
					if(!litera.containsKey(subject)){
						l=new LiteraMessage();
						commentproperty=new HashMap<String,List<String>>();
						dataproperty=new HashMap<String,List<String>>();
						
						l.setIndividual(subject);
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							l.setType(object);
						}
						else if(pro.equals("comment")
								|| pro.equals("label")
								|| pro.equals("name")){
							if(!commentproperty.containsKey(property)){
								comment=new ArrayList<String>();
								comment.add(object);
								commentproperty.put(property,comment);
							}
							else {
								comment=commentproperty.get(property);
								comment.add(object);
							}
							
						}
						else{
							if(!dataproperty.containsKey(property)){
								data=new ArrayList<String>();
								data.add(object);
								dataproperty.put(property,data);
							}
							else {
								data=dataproperty.get(property);
								data.add(object);
							}
						}
						l.setCommentproperty(commentproperty);
						l.setDataproperty(dataproperty);
						litera.put(subject, l);
					}
					else if(litera.containsKey(subject)){
						System.out.println(object);
						l=litera.get(subject);
					
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							l.setType(object);
						}
						else if(pro.equals("comment")
								|| pro.equals("label")
								|| pro.equals("name")){
							commentproperty=l.getCommentproperty();
							if(!commentproperty.containsKey(property)){
								comment=new ArrayList<String>();
								comment.add(object);
								commentproperty.put(property,comment);
							}
							else {
								comment=commentproperty.get(property);
								comment.add(object);
							}
						}
						else{
							dataproperty=l.getDataproperty();
							if(!dataproperty.containsKey(property)){
								data=new ArrayList<String>();
								data.add(object);
								dataproperty.put(property,data);
							}
							else {
								data=dataproperty.get(property);
								data.add(object);
								System.out.println(object);
							}
						}
					}
				
				}
			
			}
		}
		return litera;
    }
    
    
    /**
     * 一次遍历，得到实例间的关系、实例的文本
     */
	public static void getAll(){
		List<TripleString> relationTriple = new ArrayList<TripleString>(); //实例之间的关系
		Map<String,LiteraMessage>  litera=new HashMap<String,LiteraMessage>();
		Judge judge=new Judge();
		Set<String> classes = datasetInfo.getAllClassWithSparql(); //得到所有类名
		Set<String> propertySet=getAllProperty();                //所有属性
		String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		String pro=null;
		TripleString t=null;
		LiteraMessage l=null;
		Map<String,List<String>> commentproperty=null;
		List<String> comment=null;
		Map<String,List<String>> dataproperty=null;
		List<String> data=null;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			RDFNode p = result.get("p");
			RDFNode o= result.get("o");
			subject=s.toString();
			property=p.toString();
			object=o.toString();
			if(!classes.contains(subject) && !propertySet.contains(property)
					&& !judge.judgePropery(property)){        //过滤掉类声明的三元组、属性声明三元组
				
				if(!property.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
					&& o instanceof Resource){               //对象关系三元组
					t=new TripleString(subject,property,object);
					relationTriple.add(t);
				}
				else {
					if (property.contains("#")) {
						pro = property.substring(property.lastIndexOf("#") + 1);
					} else {
						pro = property.substring(property.lastIndexOf("/") + 1);
					}
					if(!litera.containsKey(subject)){
						l=new LiteraMessage();
						commentproperty=new HashMap<String,List<String>>();
						dataproperty=new HashMap<String,List<String>>();
						
						l.setIndividual(subject);
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							l.setType(object);
						}
						else if(pro.equals("comment")
								|| pro.equals("label")
								|| pro.equals("name")){
							if(!commentproperty.containsKey(property)){
								comment=new ArrayList<String>();
								comment.add(object);
								commentproperty.put(property,comment);
							}
							else {
								comment=commentproperty.get(property);
								comment.add(object);
							}
							
						}
						else{
							if(!dataproperty.containsKey(property)){
								data=new ArrayList<String>();
								data.add(object);
								dataproperty.put(property,data);
							}
							else {
								data=commentproperty.get(property);
								data.add(object);
							}
						}
						l.setCommentproperty(commentproperty);
						l.setDataproperty(dataproperty);
						litera.put(subject, l);
					}
					else if(litera.containsKey(subject)){
						l=litera.get(subject);
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							l.setType(object);
						}
						else if(pro.equals("comment")
								|| pro.equals("label")
								|| pro.equals("name")){
							commentproperty=l.getCommentproperty();
							if(!commentproperty.containsKey(property)){
								comment=new ArrayList<String>();
								comment.add(object);
								commentproperty.put(property,comment);
							}
							else {
								comment=commentproperty.get(property);
								comment.add(object);
							}
						}
						else{
							dataproperty=l.getDataproperty();
							if(!dataproperty.containsKey(property)){
								data=new ArrayList<String>();
								data.add(object);
								dataproperty.put(property,data);
							}
							else {
								data=commentproperty.get(property);
								data.add(object);
							}
						}
					}
					
					
				}
			}
		}
	}
	
	
	/**
	 * 得到sameAs实例
	 * @param sameList
	 */
	public static Map<String,Set<String>> sameIndividual(){
		/**
		 * 在遍历Map过程中,不能用map.put(key,newVal),map.remove(key)来修改和删除元素， 会引发 并发修改异常,可以通过迭代器的remove()： 
	     * 从迭代器指向的 collection 中移除当前迭代元素 
	     * 来达到删除访问中的元素的目的。
		 */
		String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		Map<String,Set<String>> sameIndividual=new LinkedHashMap<String,Set<String>>(); //遍历一遍之后得到的相同的实例
		Set<String> s=null;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			subject=result.get("s").toString();
			property=result.get("p").toString();
			object=result.get("o").toString();
			if(property.equals("http://www.w3.org/2002/07/owl#sameAs")){
				if(!sameIndividual.containsKey(subject)){
					s=new HashSet<String>();
					s.add(object);
					sameIndividual.put(subject, s);
				}
				else{
					s=sameIndividual.get(subject);
					s.add(object);
				}
				System.out.println("+++");
			}
		}

		String individual=null;
		Set<String> value=null;
		String flag=null;
		Iterator<Map.Entry<String, Set<String>>> it = sameIndividual.entrySet().iterator(); 
		while(it.hasNext()){
			individual=it.next().getKey();
			value=it.next().getValue();
			Iterator<String> iterator=value.iterator();
			while(iterator.hasNext()){
				flag=iterator.next();
				if(sameIndividual.get(flag) != null){
					value.addAll(sameIndividual.get(flag));
					sameIndividual.remove(flag);
				}
			}
		}
		return sameIndividual;
	}
	
    /**
     * 两个实例之间有几条边
     * @param relationTriple
     * @return
     */
    public static Map<String,Map<String,Integer>> getNumberRelation(List<TripleString> relationTriple){
    	Map<String,Map<String,Integer>> relation=new HashMap<String,Map<String,Integer>>();
    	Map<String,Integer> relationNumber=null;
    	Iterator <TripleString> it = relationTriple.iterator(); 
    	TripleString t=null;
    	String subject=null;
    	String object=null;
    	while(it.hasNext()){
    		t=it.next();
    		subject=t.getSubject();
    		object=t.getObject();
    		if(!relation.containsKey(subject)){
    			relationNumber=new HashMap<String,Integer>();
    			relationNumber.put(object, 1);
    			relation.put(subject, relationNumber);
    		}
    		else{
    			relationNumber=relation.get(subject);
    			if(!relationNumber.containsKey(object)){
    				relationNumber.put(object, 1);
    			}
    			else {
    				relationNumber.put(object, relationNumber.get(object)+1);
    			}
    		}
    		it.remove();
    	}
    	return relation;
    }
    
    
	public static void test() throws UTFDataFormatException {
		String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		int index=0;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			
			
			RDFNode o= result.get("o");
		
			
			index++;
		}
	    System.out.println("总数：  "+index);
	}
	
	/**
	 * 得到所有AnnotionProperty属性
	 * @return
	 */
	public static Set<String> getAnnotionProperty(){
		String c="http://www.w3.org/2002/07/owl#AnnotationProperty";
		Set<String> annotionproperty = datasetInfo
				.getAllInstancesByClassWithSparql(c);
		return annotionproperty;
	}
	
	public static Map<String,LiteraMessage> getVdoc(Set<String> classes,Set<String> propertySet,Judge judge,Set<String> annotationproperty){
		Map<String,LiteraMessage>  litera=new HashMap<String,LiteraMessage>();
		
    	String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		String pro=null;
		String type=null;
		LiteraMessage l=null;
		Map<String,List<String>> commentproperty=null;
		List<String> comment=null;
		int sum=0;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			RDFNode p = result.get("p");
			RDFNode o= result.get("o");
			subject=s.toString();
			property=p.toString();
			object=o.toString();
			if(!classes.contains(subject) && !propertySet.contains(subject)
					&& !judge.judgePropery(property)){
				if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
						|| !(o instanceof Resource)){
				
					if (property.contains("#")) {
						pro = property.substring(property.lastIndexOf("#") + 1);
					} else {
						pro = property.substring(property.lastIndexOf("/") + 1);
					}
					if(!litera.containsKey(subject)){
						l=new LiteraMessage();
						commentproperty=new HashMap<String,List<String>>();
						
						l.setIndividual(subject);
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							if (object.contains("#")) {
								type = object.substring(object.lastIndexOf("#") + 1);
							} else {
								type = object.substring(object.lastIndexOf("/") + 1);
							}
							l.setType(type);
						}
						else if(pro.equals("comment")
								|| pro.equals("label")
								|| pro.equals("name") || annotationproperty.contains(property)){
							System.out.println(object);sum++;
							if(!commentproperty.containsKey(property)){
								comment=new ArrayList<String>();
								comment.add(object);
								commentproperty.put(property,comment);
							}
							else {
								comment=commentproperty.get(property);
								comment.add(object);
							}
							
						}
						
						l.setCommentproperty(commentproperty);
						litera.put(subject, l);
					}
					else if(litera.containsKey(subject)){
						
						l=litera.get(subject);
					
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							if (object.contains("#")) {
								type = object.substring(object.lastIndexOf("#") + 1);
							} else {
								type = object.substring(object.lastIndexOf("/") + 1);
							}
							l.setType(type);
						}
						else if(pro.equals("comment")
								|| pro.equals("label")
								|| pro.equals("name") || annotationproperty.contains(property)){
							System.out.println(object);sum++;
							commentproperty=l.getCommentproperty();
							if(!commentproperty.containsKey(property)){
								comment=new ArrayList<String>();
								comment.add(object);
								commentproperty.put(property,comment);
							}
							else {
								comment=commentproperty.get(property);
								comment.add(object);
							}
						}
						
					}
				
				}
			
			}
		}
		System.out.println(sum);
		return litera;
	}
	
	
	
	
	public static void getVDoc(Set<String> classes,Set<String> propertySet,Judge judge,Set<String> annotationproperty) throws IOException{
	    List<String> stopwords=Nlp.stopWordTable("D:\\stopword.txt");
		Map<String,Map<String,Integer>> vdoc=new HashMap<String,Map<String,Integer>>();
		Map<String,Integer> wordmap=null;
    	String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		String sub=null;
		String pro=null;
		String type=null;
		int sum=0;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			RDFNode p = result.get("p");
			RDFNode o= result.get("o");
			subject=s.toString();
			property=p.toString();
			object=o.toString();
			if(!classes.contains(subject) && !propertySet.contains(subject)
					&& !judge.judgePropery(property)){
				if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
						|| !(o instanceof Resource)){
				
					if (property.contains("#")) {
						pro = property.substring(property.lastIndexOf("#") + 1);
					} else {
						pro = property.substring(property.lastIndexOf("/") + 1);
					}
					if(!vdoc.containsKey(subject)){
						
						wordmap=new HashMap<String, Integer>();
						
						if (subject.contains("#")) {
							sub = subject.substring(subject.lastIndexOf("#") + 1);
						} else {
							sub = subject.substring(subject.lastIndexOf("/") + 1);
						}
						wordmap.put(sub, 1);
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							if (object.contains("#")) {
								type = object.substring(object.lastIndexOf("#") + 1);
							} else {
								type = object.substring(object.lastIndexOf("/") + 1);
							}
							wordmap.put(type, 1);
						}
						else if(pro.equals("comment")
								|| pro.equals("label")
								 || annotationproperty.contains(property)){
							if(Nlp.languageDetection(object).equals("en")){
								System.out.println(object);sum++;
							    String[] words=object.split(" ");
							    for(int i=0;i<words.length;i++){ 
							    	String word=Nlp.cleanSymbol(words[i]);
							    	if(Nlp.containUpcase(word)){
							    		List<String> wordList=Nlp.separateWord(word);
							    		for(String wo : wordList){
							    			if(stopwords.contains(wo))
							    				continue;
							    			else{
							    				String w=Nlp.stemTerm(wo);
							    				if(!wordmap.containsKey(w)){
							    					wordmap.put(w, 1);
							    				}
							    				else{
							    					int n=wordmap.get(w);
							    					wordmap.put(w, n++);
							    				}
							    					
							    			}
							    		}
							    	}
							    	else{
							    		if(stopwords.contains(word))
						    				continue;
						    			else{
						    				String w=Nlp.stemTerm(word);
						    				if(!wordmap.containsKey(w)){
						    					wordmap.put(w, 1);
						    				}
						    				else{
						    					int n=wordmap.get(w);
						    					wordmap.put(w, n++);
						    				}
						    					
						    			}
							    	}
							    	
							    }
							}
							
							
						}
						
						
						vdoc.put(subject, wordmap);
					}
					else if(vdoc.containsKey(subject)){
						
						wordmap=vdoc.get(subject);
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							if (object.contains("#")) {
								type = object.substring(object.lastIndexOf("#") + 1);
							} else {
								type = object.substring(object.lastIndexOf("/") + 1);
							}
							wordmap.put(type, 1);
						}
						else if(pro.equals("comment")
								|| pro.equals("label") || pro.equals("name")
								 || annotationproperty.contains(property)){
							if(Nlp.languageDetection(object).equals("en")){
								System.out.println(object);sum++;
							    String[] words=object.split(" ");
							    for(int i=0;i<words.length;i++){ 
							    	String word=Nlp.cleanSymbol(words[i]);
							    	if(Nlp.containUpcase(word)){
							    		List<String> wordList=Nlp.separateWord(word);
							    		for(String wo : wordList){
							    			if(stopwords.contains(wo))
							    				continue;
							    			else{
							    				String w=Nlp.stemTerm(wo);
							    				if(!wordmap.containsKey(w)){
							    					wordmap.put(w, 1);
							    				}
							    				else{
							    					int n=wordmap.get(w);
							    					wordmap.put(w, n++);
							    				}
							    					
							    			}
							    		}
							    	}
							    	else{
							    		if(stopwords.contains(word))
						    				continue;
						    			else{
						    				String w=Nlp.stemTerm(word);
						    				if(!wordmap.containsKey(w)){
						    					wordmap.put(w, 1);
						    				}
						    				else{
						    					int n=wordmap.get(w);
						    					wordmap.put(w, n++);
						    				}
						    					
						    			}
							    	}
							    	
							    }
							}
							
							
						}
					}
				}
			}
		}
		
	}

	public static void main(String[] args) throws UTFDataFormatException {
	DataDBpedia dbpedia=new DataDBpedia("jdbc:virtuoso://223.3.69.83:1111/charset=UTF-8/GBK/UTF-16/log_enable=2", "http://jamendo.org",
			"dba", "dba");
		Set<String> classes=dbpedia.getDatasetInfo().getAllClassWithSparql();
		Set<String> propertySet=dbpedia.getAllProperty();
		Judge judge=new Judge();
		Map<String, Set<String>> same=dbpedia.sameIndividual();
		System.out.println(classes.size());
		System.out.println(propertySet.size());
		System.out.println(same.size());
	//	List<TripleString> relationTriple=dbpedia.getRelation(classes,propertySet,judge);
    //  Map<String,Map<String,Integer>> map=dbpedia.getNumberRelation(relationTriple);
//		System.out.println(map.size());
//		for(String c : classes)
//			System.out.println(c);
//		System.out.println(classes.size());
//		System.out.println(propertySet.size());
//	    Set<String> annotationproperty=dbpedia.getAnnotionProperty();
//	    dbpedia.getVdoc(classes, propertySet, judge, annotationproperty);
//	    for(String p : annotionproperty)
//			System.out.println(p);
//	    System.out.println(annotionproperty.size());
	}
}
