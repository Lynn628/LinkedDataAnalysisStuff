package timeEstimation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.cse.utils.query.statistic.DatasetInfo;
import com.cse.utils.query.statistic.bean.TripleString;
/**
 * 
 * @author Lynn
 * 提取出的triple只是一个类下面有连接关系的instance之间的，所以数目较少
 * 
 */
public class DataProcess {
	
	public static void main(String[] args) throws IOException{
	DatasetInfo datasetInfo = new DatasetInfo(
			"jdbc:virtuoso://223.3.69.83:1111", "http://dbpedia2015.org",
			"dba", "dba");
	
	 //获取所有实例
	 //Set<String> classes = datasetInfo.getAllClassWithSparql(); // 数据库中共有多少类
	 //实例的集合,用set来存储则没有重复的instance
	HashSet<String> allInstances = new HashSet<String>();
	LinkedList<TripleString> allTriples = new LinkedList<TripleString>(); 
	
	//读取DBpedia所有的class
	HashSet<String> allClasses = new HashSet<String>();
	BufferedReader bf= new BufferedReader(new FileReader("D:/classFile.txt"));
	while((bf.readLine()) != null){
		System.out.println(bf.readLine());
		allClasses.add(bf.readLine());
	};
	bf.close();
	/*将读取出的class文件写入classFile.txt中
	  FileWriter writer3 = new FileWriter("D:/classFile.txt",false);
	  writer3.write(c+"\r\n");
	  writer3.flush();
	  writer3.close();*/
	Iterator<String> iter1 = allClasses.iterator();
	while(iter1.hasNext()){
		//获得当前class
		String c = (String)iter1.next();
		System.out.println("Class is "+ c +"\n");
		//获取某一class的所有instance
		Set<String> instenceOfClass = datasetInfo.getAllInstancesByClassWithSparql(c);

		//获取某一class的所有triple
		List<TripleString> tripleOfClass = datasetInfo.getConnectedRelationWithSparql(c); 
		//构建所有instance和triple的序列集合
		if(instenceOfClass != null ){
		allInstances.addAll(instenceOfClass);	
		}
		if( tripleOfClass != null){
		//	System.out.println("~~~~~~~~Get all the triple~~~~~~~~\n" + tripleOfClass.toString());
		System.out.println("the size of a triple of a class" + tripleOfClass.size()+"\n");
		allTriples.addAll(tripleOfClass);
		}
	 }
	//输出triple的总体数目
	System.out.println("print the number of all triple" + allTriples.size());
	/*for (String c : allClasses) {
		System.out.println("Class is "+ c +"\n");
		 writer3.write(c+"\r\n");
		  writer3.flush();
		Set<String> instenceOfClass = datasetInfo
			 	.getAllInstancesByClassWithSparql(c);
		//将所有的类存储在List当中
		allClasses.add(c);
		List<TripleString> tripleOfClass = datasetInfo.getConnectedRelationWithSparql(c); 
		
		if(instenceOfClass != null ){
		allInstances.addAll(instenceOfClass);	
		}
		if( tripleOfClass != null){
		allTriples.addAll(tripleOfClass);
		}
	}*/
	
	//给所有的实例加上序号，用HashMap来存储键值对,利用instance来查找其对应的编号
	HashMap<String, Long> numeredInstance = new HashMap<String, Long>();
	Long i = 0L;
	for(String instance : allInstances){
		numeredInstance.put(instance, i++);
	}
	//创建只有顶点序号的三元组list,一边创建一边写入文件当中
   //LinkedList<NumberedTriple> numberedTriplesList = new LinkedList<>();
	//FileWriter设置为true使得新读的数据追加到文件末尾
 try{
	System.out.println("Prepare to build and numbered triple into file");
	FileWriter writer1 = new FileWriter("D:/numberedTriple.txt",true);
    for(TripleString triple : allTriples){
    	//System.out.println("the subject is" + allInstances.contains(triple.getSubject()) + " the object is" + allInstances.contains(triple.getObject()));
    	System.out.println("whether the instanceSet contains this subject" + allInstances.contains(triple.getSubject()) +
    			   "whether the instanceSet contains this object" + allInstances.contains(triple.getObject()));
    	Long subjectNo = (Long)numeredInstance.get(triple.getSubject());
		Long objectNo = (Long)numeredInstance.get(triple.getObject());
		System.out.println("subjectNo is"+ subjectNo + " " + "objectNo is"+ objectNo);
		  }
		//生成triple的编号对
	    //NumberedTriple numberedTriple = new NumberedTriple(subjectNo, objectNo);
	   // System.out.println("New born numbered triple"+ numberedTriple.getSubjectNo().toString() + " " + nu);
	   // String inputStr = subjectNo.toString() + " " + objectNo.toString();
	   // writer1.write(inputStr + "\r\n");
       // writer1.flush();
	   // numberedTriplesList.add(new NumberedTriple(subjectNo, objectNo));
    FileWriter writer2 = new FileWriter("D:/numberedInstance.txt",false);
    //遍历numberedInstance,将实例和编号写入文件中
    Iterator iter = numeredInstance.entrySet().iterator();
    while(iter.hasNext()){
    	Entry entry = (Entry) iter.next();
    	String key = (String)entry.getKey();
    	Long value = (Long)entry.getValue();
    	writer2.write(value + "  " + key + "\r\n");
    	writer2.flush();
    }
	   writer1.close();
	   writer2.close();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	
   /* try {           
    	System.out.println("Prepare to write into file");
        FileWriter writer1 = new FileWriter("D:/numberedTriple.txt",false);
        FileWriter writer2 = new FileWriter("D:/numberedInstance.txt",false);
        //遍历numberedInstance,将实例和编号写入文件中
        Iterator iter = numeredInstance.entrySet().iterator();
        while(iter.hasNext()){
        	Entry entry = (Entry) iter.next();
        	String key = (String)entry.getKey();
        	Long value = (Long)entry.getValue();
        	writer2.write(value + "  " + key + "\r\n");
        	writer2.flush();
        }
        System.out.println("Print the numberedTripleSize" + numberedTriplesList.size());
        for(int k = 0; k < numberedTriplesList.size(); k++) {
        	NumberedTriple numberedTriple = numberedTriplesList.get(k);
        	if(numberedTriple != null){
        	String numberedTripleStr = numberedTriple.toString();
        	System.out.println("The numbered triple " + numberedTripleStr);
            writer1.write(numberedTripleStr + "\r\n");
            writer1.flush();
         }    
       }
        writer1.close();
       // writer2.close();
    } catch (IOException e1) {
        e1.printStackTrace();
    }*/
 }
}
