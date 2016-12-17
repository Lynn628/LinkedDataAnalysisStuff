import java.util.List;

import com.cse.utils.query.statistic.DatasetInfo;
import com.cse.utils.query.statistic.bean.TripleString;


public class TimeEstimation {
	public static void main(String[] args){
		DatasetInfo datasetInfo = new DatasetInfo(
				"jdbc:virtuoso://223.3.69.83:1111", "http://dbpedia2015.org",
				"dba", "dba");

		/* for (String s : datasetInfo
					 .getAllInstancesByClassWithSparql("http://dbpedia.org/ontology/Film"))
					 {
					 System.out.println(s);
					 }*/
		List<TripleString> allTrilplets = datasetInfo.getAllTripleWithSparql();
		for(TripleString triple: allTrilplets){
			System.out.println(triple.toString());
		}
	}
}
