package com.cse.utils.dbpedia.backdata;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.cse.utils.query.statistic.DatasetInfo;
import com.cse.utils.readparam.ReadConfParam;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class WordDBpedia {
private static DatasetInfo datasetInfo;	
	
	public WordDBpedia(String url,String graphName,String username,String password){
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
		 * 给节点编码
		 * @param classes
		 * @param propertySet
		 * @param judge
		 * @param same
		 * @return
		 */
		public static Map<String, Integer> coding(Set<String> classes,Set<String> propertySet,Judge judge){
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
						&& !judge.judgePropery(property)){
					if(!codingUri.containsKey(subject)){
						codingUri.put(subject, codingNumber);
						codingNumber++;
					}
					if(!property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
							&&	o instanceof Resource){
						if(!codingUri.containsKey(object)){
							codingUri.put(object, codingNumber);
							codingNumber++;
						}
					}
				}
			}
			return codingUri;
		}
		
		/**
		 * 一次遍历得到节点间的关系以及得到节点的虚拟文档
		 * @throws LangDetectException 
		 * @throws IOException 
		 */
		public static void getRelationVDoc(Map<Integer,Map<Integer,Double>> relation,Map<Integer,Map<String,Double>> vdoc, WordDBpedia dataset) throws LangDetectException, IOException{
			
			DetectorFactory.loadProfile("profiles");
			Detector detect=null;
			String language=null;
		
			//自然语言处理
			
			Set<String> classes=dataset.getDatasetInfo().getAllClassWithSparql();
			Set<String> propertySet=dataset.getAllProperty();
			Judge judge=new Judge();
			
			Map<String,Integer> codingUri=dataset.coding(classes, propertySet, judge);
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
						if(!vdoc.containsKey(subNumber)){
							
							wordmap=new HashMap<String, Double>();
							sub=Nlp.getLocalNamePath(subject);
							Nlp.insetMap(sub, wordmap);;
							vdoc.put(subNumber, wordmap);
						}
	                    if(!vdoc.containsKey(objNumber)){
							
							wordmap=new HashMap<String, Double>();
							sub=Nlp.getLocalNamePath(subject);
							Nlp.insetMap(sub, wordmap);;
							vdoc.put(objNumber, wordmap);
						}
					}
				
				
				//VDoc
				else if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
						|| !(o instanceof Resource)){

					if (property.contains("#")) {
						pro = property.substring(property.lastIndexOf("#") + 1);
					} else {
						pro = property.substring(property.lastIndexOf("/") + 1);
					}
					subNumber=codingUri.get(subject);
					if(!vdoc.containsKey(subNumber)){
						
						wordmap=new HashMap<String, Double>();
						sub=Nlp.getLocalNamePath(subject);
						Nlp.insetMap(sub, wordmap);;
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
								 || annotationproperty.contains(property)
								 || property.equals("http://www.w3.org/2000/01/rdf-schema#comment")
								 || property.equals("http://www.w3.org/2000/01/rdf-schema#label")){
							
							try{
							detect = DetectorFactory.create();
							detect.append(object);
							language=detect.detect();
							}catch (LangDetectException e)
					        {
					            
					        }
							if(language != null && language.equals("en")){
								object=object.substring(0,object.length()-3);
							    String[] words=object.split(" +");
							    for(int i=0;i<words.length;i++){ 
							    	String word=Nlp.cleanSymbol(words[i]);
							    	if((word != null) && !word.equals("")){
							    	if(Nlp.containUpcase(word)){
							    		List<String> wordList=Nlp.separateWord(word);
							    		for(String wo : wordList){
							    			
							    			
							    				
							    				if((wo != null) && !wo.equals("")){
							    				if(!wordmap.containsKey(wo)){
							    					
							    					   wordmap.put(wo, 1.0);
							    			
							    				}
							    				else{
							    					double n=wordmap.get(wo);
							    					wordmap.put(wo, n++);
							    				}
							    				}	
							    			
							    		}
							    	}
							    	else{
							    		word=word.toLowerCase();
							    		
						    			
						    				
						    				if((word != null) && !word.equals("")){
						    				if(!wordmap.containsKey(word)){
						    					
							    					   wordmap.put(word, 1.0);
						    				}
						    				else{
						    					double n=wordmap.get(word);
						    					wordmap.put(word, n++);
						    				}
						    			    }	
						    			}
							    	
							    	
							    	
							    }
							}   //for
							}   //language
							
						}   //comment
						
						
						vdoc.put(subNumber, wordmap);
					}   //vdoc
					else if(vdoc.containsKey(subNumber)){
						
						wordmap=vdoc.get(subNumber);
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
								 || annotationproperty.contains(property)
								 || property.equals("http://www.w3.org/2000/01/rdf-schema#comment")
								 || property.equals("http://www.w3.org/2000/01/rdf-schema#label")){
							try{
								detect = DetectorFactory.create();
								detect.append(object);
								language=detect.detect();
								}catch (LangDetectException e)
						        {
						            
						        }
							if(language != null && language.equals("en")){
								object=object.substring(0,object.length()-3);
								
							    String[] words=object.split(" +");
							    for(int i=0;i<words.length;i++){ 
							    	String word=Nlp.cleanSymbol(words[i]);
							    	if((word != null) && !word.equals("")){
							    	if(Nlp.containUpcase(word)){
							    		List<String> wordList=Nlp.separateWord(word);
							    		for(String wo : wordList){
							    			
							    			
							    				
							    				if((wo != null) && !wo.equals("")){
							    				if(!wordmap.containsKey(wo)){
							    					
							    					    wordmap.put(wo, 1.0);
							    				}
							    				else{
							    					double n=wordmap.get(wo);
							    					wordmap.put(wo, n++);
							    				}
							    				}	
							    			
							    		}
							    	}
							    	else{
							    		word=word.toLowerCase();
							    		
							    		
						    			
						    				
						    				
						    				if((word != null)&&!word.equals("")){
						    					if(!wordmap.containsKey(word)){
						    					   wordmap.put(word, 1.0);
						    				    }
						    				    else{
						    				    	double n=wordmap.get(word);
						    					     wordmap.put(word, n++);
						    				   }
						    				}	
						    			
							    	}
							    	
							    }
							}  //for
							
							}  //language
						}  //comment
					}    //vdoc
					
					
					
					
				} //else if(property.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
			}  //if(!classes.contain)
			}  //while
			//stepVdoc(relation,vdoc);
		}
		
		
		public static Map<Integer,Map<String,Double>> stepVdoc(Map<Integer,Map<Integer,Double>> relation,Map<Integer,Map<String,Double>> vdoc){
			int subNumber=0;
			
			Map<Integer,Double> objRelation=null;
			
			Map<Integer,Set<Integer>> relations=new HashMap<Integer,Set<Integer>>();
			Set<Integer> subset=null;
			Set<Integer> objset=null;
			for(Entry<Integer, Map<Integer, Double>> r : relation.entrySet()){
				subNumber=r.getKey();
				if(!relations.containsKey(subNumber)){
					subset=new HashSet<Integer>();
					relations.put(subNumber, subset);
				}
				else
					subset=relations.get(subNumber);
				objRelation=r.getValue();
				
				for(Integer k : objRelation.keySet()){
					subset.add(k);
					if(relations.containsKey(k)){
						objset=relations.get(k);
						objset.add(subNumber);
					}
					else{
						objset=new HashSet<Integer>();
						objset.add(subNumber);
						relations.put(k, objset);
					}
				}
				relations.put(subNumber, subset);
			}
			Map<Integer,Map<String,Double>> vDoc=new HashMap<Integer,Map<String,Double>>();
			
			int subnumber=0;
			Set<Integer> objnumber=null;
			Map<String,Double> subVdoc=null;
			Map<String,Double> objVdoc=null;
			String word=null;
			double d=0.0;
			Map<String,Double> wordMap=null;
			for(Entry<Integer,Map<String,Double>> e : vdoc.entrySet()){
				if(!vDoc.containsKey(e.getKey())){
					wordMap=new HashMap<String,Double>();
					for(Entry<String,Double> entry : e.getValue().entrySet())
						wordMap.put(entry.getKey(), entry.getValue());
					vDoc.put(e.getKey(), wordMap);
				}
			}
			//Map<String,Double> map=null;
			for(Entry<Integer,Set<Integer>> rs: relations.entrySet()){
				subnumber=rs.getKey();
				if(!vDoc.containsKey(subnumber)){
					wordMap=new HashMap<String,Double>();
					subVdoc=vdoc.get(subnumber);
					wordMap.putAll(subVdoc);
					vDoc.put(subnumber, wordMap);
				}
				else
					wordMap=vDoc.get(subnumber);
				objnumber=rs.getValue();
				for(Integer o : objnumber){
					objVdoc=vdoc.get(o);
					for(Entry<String,Double> entry : objVdoc.entrySet()){
						word=entry.getKey();
						if(wordMap.containsKey(word)){
							d=wordMap.get(word);
							wordMap.put(word, d+entry.getValue());
						}
						else{
							wordMap.put(word, entry.getValue());
						}
					}
					
			   }
				
			}
			
			return vDoc;
		}
		
		
		
		/**
		 * 统计虚拟文档含词量数级
		 * @param args
		 * @throws LangDetectException
		 * @throws IOException
		 */
		public static void countWord(Map<Integer,Map<String,Double>> stepVdoc,String fileWord,String fileWords) throws IOException{
			Map<Integer,Integer> word=new LinkedHashMap<Integer,Integer>();
			Map<Integer,Integer> words=new LinkedHashMap<Integer,Integer>();
			for(int i=0; i<=700; i++){
				word.put(i, 0);
				words.put(i, 0);
			}
			Map<String,Double> wordMap=null;
			
			for(Entry<Integer,Map<String,Double>> entry : stepVdoc.entrySet()){
				wordMap=entry.getValue();
				double wordsNumber=0.0;
				for(double d : wordMap.values()){
					wordsNumber=wordsNumber+d;
				}
				for(int i=0;i<=700;i++){
					if(wordMap.size()>=i)
						word.put(i, word.get(i)+1);
					if(wordsNumber>=i)
						words.put(i, words.get(i)+1);
				}
			}
			FileWriter wordfile=new FileWriter(fileWord,true);
			BufferedWriter wordWriter=new BufferedWriter(wordfile);
			for(Entry<Integer,Integer> e : word.entrySet()){
				wordWriter.write(e.getKey()+" : "+e.getValue());
				wordWriter.newLine();
			}
			wordWriter.close();
			FileWriter wordsfile=new FileWriter(fileWords,true);
			BufferedWriter wordsWriter=new BufferedWriter(wordsfile);
			for(Entry<Integer,Integer> e : words.entrySet()){
				wordsWriter.write(e.getKey()+" : "+e.getValue());
				wordsWriter.newLine();
			}
			wordsWriter.close();
		}
		
		public static void main(String[] args) throws LangDetectException, IOException{
			WordDBpedia dbpedia=new WordDBpedia("jdbc:virtuoso://223.3.69.83:1111/charset=UTF-8/GBK/UTF-16/log_enable=2", "http://dbpedia2015.org",
					"dba", "dba");
			Map<Integer,Map<Integer,Double>> relation=new HashMap<Integer,Map<Integer,Double>>();
			Map<Integer,Map<String,Double>>  vdoc=new HashMap<Integer,Map<String,Double>>();
			
			dbpedia.getRelationVDoc(relation, vdoc, dbpedia);
			Map<Integer,Map<String,Double>> stepvdoc=stepVdoc(relation,vdoc);
			String fileWord="D:\\newword\\dbpedia.txt";
	    	String fileWords="D:\\newwords\\dbpedias.txt";
			countWord(stepvdoc,fileWord,fileWords);
		}

}
