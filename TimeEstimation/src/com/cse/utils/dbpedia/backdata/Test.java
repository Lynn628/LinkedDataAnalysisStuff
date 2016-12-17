package com.cse.utils.dbpedia.backdata;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

import com.cse.utils.query.statistic.DatasetInfo;
import com.cse.utils.readparam.ReadConfParam;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

public class Test{ 

private static DatasetInfo datasetInfo;	
	
	public Test(String url,String graphName,String username,String password){
		this.datasetInfo=new DatasetInfo(url,graphName, username, password);
	} 
	
	public DatasetInfo getDatasetInfo(){
		return datasetInfo;
	}
	/**
	 * 得到所有的属性
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
	  * 得到评价属性
	  * @return
	  */
	 public static Set<String> getAnnotionProperty(){
			String c="http://www.w3.org/2002/07/owl#AnnotationProperty";
			Set<String> annotionproperty = datasetInfo
					.getAllInstancesByClassWithSparql(c);
			return annotionproperty;
		}
	
	/**
	 * 得到sameAs实例
	 * @param sameList
	 */
	public static Map<String,String> sameIndividual(){
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
//				System.out.println("+++");
			}
		}

		String individual=null;
		Set<String> value=null;
		String flag=null;
		Iterator<Map.Entry<String, Set<String>>> it = sameIndividual.entrySet().iterator(); 
		while(it.hasNext()){
			Entry<String,Set<String>> e=it.next();
			individual=e.getKey();
			value=e.getValue();
			Iterator<String> iterator=value.iterator();
			while(iterator.hasNext()){
				flag=iterator.next();
				if(sameIndividual.get(flag) != null){
					value.addAll(sameIndividual.get(flag));
					sameIndividual.remove(flag);
				}
			}
		}
		Map<String , String> same=new HashMap<String,String>();
		Set<String> set=null;
		String st=null;
		for(Map.Entry<String, Set<String>> m : sameIndividual.entrySet()){
			st=m.getKey();
			set=m.getValue();
			for(String str: set){
				same.put(str,st);
			}
		}
		return same;
	}
	
	
	/**
	 * 给节点编码
	 * @param classes
	 * @param propertySet
	 * @param judge
	 * @param same
	 * @return
	 */
	public static Map<String, Integer> coding(Set<String> classes,Set<String> propertySet,Judge judge, Map<String,String> same){
		Map<String, Integer> codingUri=new HashMap<String, Integer>();
		String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		String sameValue=null;
		int codingNumber=0;
		while(results.hasNext()){
			QuerySolution result = results.nextSolution();
			RDFNode s = result.get("s");
			RDFNode p = result.get("p");
			RDFNode o= result.get("o");
			subject=s.toString();
			property=p.toString();
			object=o.toString();
			if(!classes.contains(subject) && !propertySet.contains(subject)
					&& !judge.judgeCountPropery(property)){
				if(!codingUri.containsKey(subject)){
					sameValue=same.get(subject);
					if(sameValue != null){
						if(!codingUri.containsKey(sameValue)){
							codingUri.put(sameValue, codingNumber);
							codingUri.put(subject, codingNumber);
							codingNumber++;
						}
						else{
							codingUri.put(subject, codingUri.get(sameValue));
						}
					}
					
					else{
						codingUri.put(subject, codingNumber);
						codingNumber++;
					}
				}
				if(!property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
					&&	o instanceof Resource){
					if(!codingUri.containsKey(object)){
						sameValue=same.get(object);
						if(sameValue != null){
							if(!codingUri.containsKey(sameValue)){
								codingUri.put(sameValue, codingNumber);
								codingUri.put(object, codingNumber);
								codingNumber++;
							}
							else{
								codingUri.put(object, codingUri.get(sameValue));
							}
						}
						
						else{
							codingUri.put(object, codingNumber);
							codingNumber++;
						}
						
					}
					
				}
				
			}
		}
		return codingUri;
	}
	
public static void getRelationVDoc(Map<Integer,Map<Integer,Double>> relation,Map<Integer,Map<String,Double>> vdoc, Test dataset) throws LangDetectException, IOException{
		
		DetectorFactory.loadProfile("profiles");
		Detector detect=null;
		String language=null;
		List<String> stopwords=Nlp.stopWordTable("D:\\stopword.txt");
		//自然语言处理
		
		Set<String> classes=dataset.getDatasetInfo().getAllClassWithSparql();
		Set<String> propertySet=dataset.getAllProperty();
		Judge judge=new Judge();
		Map<String,String> same=dataset.sameIndividual();
		Map<String,Integer> codingUri=dataset.coding(classes, propertySet, judge, same);
		Set<String> annotationproperty=dataset.getAnnotionProperty();
		String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");
		Query sparql = QueryFactory.create(queryString);
        
		QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql,
				datasetInfo.getSparqlQuery().getVirtGraph());
		ResultSet results = vqe.execSelect();
		String subject=null;
		String property=null;
		String object=null;
		
		int subNumber=0;
		int objNumber=0;
		Map<Integer,Double> relationObject=null;
		String pro=null;
		String[] sub=null;
		String type=null;
	
		Map<String,Double> wordmap=null;
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
				if(!property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
						&& o instanceof Resource){ 
					subNumber=codingUri.get(subject);
					objNumber=codingUri.get(object);
					if(subNumber==objNumber)
						System.out.println("自连边");
					if(!relation.containsKey(subNumber)){
						relationObject=new HashMap<Integer,Double>();
						relationObject.put(objNumber, 1.0);
						relation.put(subNumber, relationObject);
					}
					else{
						relationObject=relation.get(subNumber);
						if(!relationObject.containsKey(objNumber)){
							relationObject.put(objNumber, 1.0);
						}
						else{
						
							double number=relationObject.get(objNumber);
							relationObject.put(objNumber, number++);
						}
					}
				}
			}
		}
   }
   public static void main(String[] args) throws LangDetectException, IOException{
	   Test dataset=new Test("jdbc:virtuoso://223.3.69.83:1111/charset=UTF-8/GBK/UTF-16/log_enable=2", "http://linkedmdb.org",
				"dba", "dba");
	   Map<Integer,Map<Integer,Double>> relation=new HashMap<Integer,Map<Integer,Double>>();
		Map<Integer,Map<String,Double>>  vdoc=new HashMap<Integer,Map<String,Double>>();
		
		dataset.getRelationVDoc(relation, vdoc, dataset);
   }

}