package timeEstimation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.cse.utils.query.statistic.DatasetInfo;
import com.cse.utils.query.statistic.bean.TripleString;

public class DataProcess {
	
	public static void ProcessAndStoreNumberedData(){
	DatasetInfo datasetInfo = new DatasetInfo(
			"jdbc:virtuoso://223.3.69.83:1111", "http://dbpedia2015.org",
			"dba", "dba");
	
	 //获取所有实例
	Set<String> classes = datasetInfo.getAllClassWithSparql(); // 数据库中共有多少类
	 //实例的集合,用set来存储则没有重复的instance
	Set<String> allInstances = null;
	List<TripleString> allTriples = null; 
	for (String c : classes) {
		Set<String> instenceOfClass = datasetInfo
				.getAllInstancesByClassWithSparql(c);
		List<TripleString> tripleOfClass = datasetInfo.getConnectedRelationWithSparql(c); 
		 allInstances.addAll(instenceOfClass);	
		 allTriples.addAll(tripleOfClass);
	}
	
	 //给所有的实例加上序号，用HashMap来存储键值对,利用instance来查找其对应的编号
	HashMap numeredInstance = new HashMap<String, Long>();
	int i = 0;
	for(String instance : allInstances){
		numeredInstance.put(instance, i++);
	}
	//创建只有顶点序号的三元组list
    List<NumberedTriple> numberedTriplesList = null;
	for(TripleString triple : allTriples){
		Long subjectNo = (Long)numeredInstance.get(triple.getSubject());
		Long objectNo = (Long)numeredInstance.get(triple.getObject());
		numberedTriplesList.add(new NumberedTriple(subjectNo, objectNo));
	}
	//将结果写入到文件中
    try {           
        FileWriter writer1 = new FileWriter("D:/numberedTriple.txt",true);
        FileWriter writer2 = new FileWriter("D:/numberedInstance.txt",true);
        for(int k = 0; k < numberedTriplesList.size(); k++) {
            writer1.write(numberedTriplesList.get(i).toString());
        }         
        
       //遍历numberedInstance,将实例和编号写入文件中
        Iterator iter = numeredInstance.entrySet().iterator();
        while(iter.hasNext()){
        	Entry entry = (Entry) iter.next();
        	String key = (String)entry.getKey();
        	Long value = (Long)entry.getValue();
        	writer2.write(value + "  " + key);
        }
        writer1.close();
        writer2.close();
    } catch (IOException e1) {
        e1.printStackTrace();
    }
}
}
