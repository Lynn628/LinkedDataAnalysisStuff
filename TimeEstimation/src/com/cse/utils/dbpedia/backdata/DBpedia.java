package com.cse.utils.dbpedia.backdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.cse.utils.query.statistic.DatasetInfo;
import com.cse.utils.query.statistic.bean.TripleString;
import com.cse.utils.readparam.ReadConfParam;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class DBpedia {
	private static DatasetInfo datasetInfo;	
	
	public DBpedia(String url,String graphName,String username,String password){
		this.datasetInfo=new DatasetInfo(url,graphName, username, password);
	} 
	
	public DatasetInfo getDatasetInfo(){
		return datasetInfo;
	}
	/**
	 * 得到所有属性
	 * @return
	 */
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
	 * 得到所有AnnotionProperty属性
	 * @return
	 */
	public static Set<String> getAnnotionProperty(){
		String c="http://www.w3.org/2002/07/owl#AnnotationProperty";
		Set<String> annotionproperty = datasetInfo
				.getAllInstancesByClassWithSparql(c);
		return annotionproperty;
	}
	

	/**
	 * 得到VDoc
	 * @param classes
	 * @param propertySet
	 * @param judge
	 * @param annotationproperty
	 * @throws IOException
	 * @throws LangDetectException 
	 */
	public static Map<String,Map<String,Double>> getVDoc(Set<String> classes,Set<String> propertySet,Judge judge,Set<String> annotationproperty) throws IOException, LangDetectException{
		DetectorFactory.loadProfile("profiles");
		Detector detect=null;
		List<String> stopwords=Nlp.stopWordTable("D:\\stopword.txt");
		Map<String,Map<String,Double>> vdoc=new HashMap<String,Map<String,Double>>();
		Map<String,Double> wordmap=null;
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
		String language="";
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
						
						wordmap=new HashMap<String, Double>();
						sub=Nlp.getLocalName(subject);
						if((sub != null) && !sub.equals(""))
							wordmap.put(sub, 1.0);
						if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
							if (object.contains("#")) {
								type = object.substring(object.lastIndexOf("#") + 1);
							} else {
								type = object.substring(object.lastIndexOf("/") + 1);
							}
							if((type!=null) && !type.equals(""))
								wordmap.put(type, 1.0);
						}
						else if(pro.equals("comment")
								|| pro.equals("label") || pro.equals("name")
								 || annotationproperty.contains(property)){
							try{
							detect = DetectorFactory.create();
							detect.append(object);
							language=detect.detect();
							}catch (LangDetectException e)
					        {
					            
					        }
							if(language.equals("en")){
								object=object.substring(0,object.length()-3);
							    String[] words=object.split(" +");
							    for(int i=0;i<words.length;i++){ 
							    	String word=Nlp.cleanSymbol(words[i]);
							    	if((word != null) && !word.equals("")){
							    	if(Nlp.containUpcase(word)){
							    		List<String> wordList=Nlp.separateWord(word);
							    		for(String wo : wordList){
							    			if(stopwords.contains(wo))
							    				continue;
							    			else{
							    				String w=Nlp.stemTerm(wo);
							    				if(!wordmap.containsKey(w)){
							    					if((w != null) && !w.equals(""))
							    					   wordmap.put(w, 1.0);
							    			
							    				}
							    				else{
							    					double n=wordmap.get(w);
							    					wordmap.put(w, n++);
							    				}
							    					
							    			}
							    		}
							    	}
							    	else{
							    		word=word.toLowerCase();
							    		if(stopwords.contains(word))
						    				continue;
						    			else{
						    				String w=Nlp.stemTerm(word);
						    				if(!wordmap.containsKey(w)){
						    					if((w != null) && !w.equals(""))
							    					   wordmap.put(w, 1.0);
						    				}
						    				else{
						    					double n=wordmap.get(w);
						    					wordmap.put(w, n++);
						    				}
						    					
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
							if((type != null) && !type.equals(""))
							   wordmap.put(type, 1.0);
						}
						else if(pro.equals("comment")
								|| pro.equals("label") || pro.equals("name")
								 || annotationproperty.contains(property)){
							try{
								detect = DetectorFactory.create();
								detect.append(object);
								language=detect.detect();
								}catch (LangDetectException e)
						        {
						            
						        }
							if(language.equals("en")){
								object=object.substring(0,object.length()-3);
								System.out.println(object);sum++;
							    String[] words=object.split(" +");
							    for(int i=0;i<words.length;i++){ 
							    	String word=Nlp.cleanSymbol(words[i]);
							    	if((word != null) && !word.equals("")){
							    	if(Nlp.containUpcase(word)){
							    		List<String> wordList=Nlp.separateWord(word);
							    		for(String wo : wordList){
							    			if(stopwords.contains(wo))
							    				continue;
							    			else{
							    				String w=Nlp.stemTerm(wo);
							    				if(!wordmap.containsKey(w)){
							    					if((w != null) && !w.equals(""))
							    					    wordmap.put(w, 1.0);
							    				}
							    				else{
							    					double n=wordmap.get(w);
							    					wordmap.put(w, n++);
							    				}
							    					
							    			}
							    		}
							    	}
							    	else{
							    		word=word.toLowerCase();
							    		if(stopwords.contains(word))
						    				continue;
						    			else{
						    				String w=Nlp.stemTerm(word);
						    				if(!wordmap.containsKey(w)){
						    					if((w != null)&&!w.equals(""))
						    					   wordmap.put(w, 1.0);
						    				}
						    				else{
						    					double n=wordmap.get(w);
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
	   return vdoc;	
	}
	
	
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
    
	
	
	public static Map<String,Map<String,Double>> getNumberRelation(List<TripleString> relationTriple){
    	Map<String,Map<String,Double>> relation=new HashMap<String,Map<String,Double>>();
    	Map<String,Double> relationNumber=null;
    	Iterator <TripleString> it = relationTriple.iterator(); 
    	TripleString t=null;
    	String subject=null;
    	String object=null;
    	while(it.hasNext()){
    		t=it.next();
    		subject=t.getSubject();
    		object=t.getObject();
    		if(!relation.containsKey(subject)){
    			relationNumber=new HashMap<String,Double>();
    			relationNumber.put(object, 1.0);
    			relation.put(subject, relationNumber);
    		}
    		else{
    			relationNumber=relation.get(subject);
    			if(!relationNumber.containsKey(object)){
    				relationNumber.put(object, 1.0);
    			}
    			else {
    				relationNumber.put(object, relationNumber.get(object)+1);
    			}
    		}
    		it.remove();
    	}
    	return relation;
    }
	
	/**
	 * 查找空白节点
	 * @return
	 */
	public static int emptyNode(){
		String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String object=null;
		int sum=0;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			RDFNode o= result.get("o");
			subject=s.toString();
			object=o.toString();
			if(subject.contains("_:") || object.contains("_:"))
				sum++;
		}
		return sum;
	}
	/**
	 * get edge Map
	 * @return
	 */
	public static Map<String, Map<String, Double>> getEdgeInfo() {
		DBpedia dbpedia=new DBpedia("jdbc:virtuoso://223.3.69.83:1111/charset=UTF-8/GBK/UTF-16/log_enable=2", "http://jamendo.org",
				"dba", "dba");
		Set<String> classes=dbpedia.getDatasetInfo().getAllClassWithSparql();
		Set<String> propertySet=dbpedia.getAllProperty();
		Set<String> annotationproperty=dbpedia.getAnnotionProperty();
		Judge judge=new Judge();
		Map<String, Map<String, Double>> edgeInfo = dbpedia.getNumberRelation(dbpedia.getRelation(classes, propertySet, judge));
		return edgeInfo;
	}


	/**
	 * get vertex Map
	 * @return
	 * @throws IOException
	 * @throws LangDetectException
	 */
	public static Map<String, Map<String, Double>> getVertexInfo() throws IOException, LangDetectException {
		DBpedia dbpedia=new DBpedia("jdbc:virtuoso://223.3.69.83:1111/charset=UTF-8/GBK/UTF-16/log_enable=2", "http://jamendo.org",
				"dba", "dba");
		Set<String> classes=dbpedia.getDatasetInfo().getAllClassWithSparql();
		Set<String> propertySet=dbpedia.getAllProperty();
		Set<String> annotationproperty=dbpedia.getAnnotionProperty();
		Judge judge=new Judge();
		Map<String, Map<String, Double>> vertexInfo = dbpedia.getVDoc(classes,propertySet,judge,annotationproperty);
		return vertexInfo;
	}


	public static void main(String[] args) throws IOException, LangDetectException{
		DBpedia dbpedia=new DBpedia("jdbc:virtuoso://223.3.69.83:1111/charset=UTF-8/GBK/UTF-16/log_enable=2", "http://jamendo.org",
				"dba", "dba");
			Set<String> classes=dbpedia.getDatasetInfo().getAllClassWithSparql();
			Set<String> propertySet=dbpedia.getAllProperty();
			Set<String> annotationproperty=dbpedia.getAnnotionProperty();
			Judge judge=new Judge();
			Map<String,Map<String,Double>> map=dbpedia.getVDoc(classes,propertySet,judge,annotationproperty);
			Map<String,Double> m=null;
			int sum=0;
			int sums=0;
			for(Entry<String, Map<String, Double>> entry : map.entrySet()){
				String s=entry.getKey();
				if(s==null || s.equals(""))
					sum++;
				m=entry.getValue();
				for(Entry<String,Double> e : m.entrySet()){
					String ss=e.getKey();
					if(ss==null || ss.equals(""))
						sums++;
				}
			}
			System.out.println(sum);
			System.out.println(sums);
	}
}
