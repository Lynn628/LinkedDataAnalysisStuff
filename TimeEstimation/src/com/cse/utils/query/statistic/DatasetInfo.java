package com.cse.utils.query.statistic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import com.cse.utils.query.statistic.bean.Triple;
import com.cse.utils.query.statistic.bean.TripleString;
import com.cse.utils.query.virtuoso.SparqlQuery;
import com.cse.utils.query.virtuoso.bean.DoubleColumn;
import com.cse.utils.query.virtuoso.bean.SingleColumn;
import com.cse.utils.query.virtuoso.bean.TripleColumn;
import com.cse.utils.readparam.ReadConfParam;

import virtuoso.jena.driver.VirtDataset;

/**
 * 获取数据集中的一些基本信息，比如class、instance等
 * 
 * @author chengwenyao
 * 
 */
public class DatasetInfo {
	private VirtDataset virtDataset;

	private OntModel model;

	// 图数据库地址
	private String url;
	// 图数据库中graph的名字
	private String graphName;
	// 用户名
	private String username;
	// 密码
	private String password;
	//查询图数据类
	private static SparqlQuery sparqlQuery;

	// 保存所有以字符串形式表示的class
	private Set<String> allClassesString = new HashSet<String>();

	private DatasetInfo() {
	}

	/**
	 * 构造函数的参数
	 * 
	 * @param url
	 *            链接数据图的url（形如"jdbc:virtuoso://localhost:1111"）
	 * @param graphName
	 *            图名
	 * @param username
	 *            virtuoso用户名（默认dba）
	 * @param password
	 *            virtuoso密码（默认dba）
	 */
	public DatasetInfo(String url, String graphName, String username,
			String password) {
		this.virtDataset = new VirtDataset(url, username, password);
		this.url = url;
		this.graphName = graphName;
		this.username = username;
		this.password = password;
		this.sparqlQuery=new SparqlQuery(url,graphName, username, password);

		Model baseModel = virtDataset.getNamedModel(graphName);

		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
				baseModel); // jean中model
	}

	public void init() {
		// 保存所有以字符串形式表示的class
		getAllClassesString();
	}
  
	public SparqlQuery getSparqlQuery(){
		return sparqlQuery;
	}
	/**
	 * 获取model中包含的所有classes
	 * 
	 * @return
	 */
	public List<OntClass> getAllClasses() {
		List<OntClass> result = new ArrayList<OntClass>();

		ExtendedIterator classes = model.listClasses();

		while (classes.hasNext()) {
			OntClass thisClass = (OntClass) classes.next();
			System.out.println("Found class: " + thisClass.toString());
			result.add(thisClass);
		}

		return result;
	}

	/**
	 * 获取model中包含的所有classes（转化为String）格式
	 * 
	 * @return
	 */
	public Set<String> getAllClassesString() {
		Set<String> result = new HashSet<String>();

		List<OntClass> list = getAllClasses();
		for (OntClass c : list) {
			result.add(c.toString());
		}

		allClassesString.addAll(result);
		return result;
	}

	/**
	 * 利用sparql语句获取数据中的所有class
	 * 
	 * @return
	 */
	public Set<String> getAllClassWithSparql() {
		String queryString = ReadConfParam.getMessage("get.allclass.sparql");

		List<SingleColumn> allClass = sparqlQuery.querySingleColumn(
				queryString, "class");

		Set<String> result = new HashSet<String>();

		for (SingleColumn c : allClass) {
			result.add(c.getFirstValue());
		}

		return result;
	}

	/**
	 * 获取指定Class下的所有实例
	 * 
	 * @param classUri
	 * @return
	 */
	public List<Individual> getAllInstancesByClass(OntClass classUri) {
		List<Individual> result = new ArrayList<Individual>();

		ExtendedIterator instances = classUri.listInstances();
		while (instances.hasNext()) {
			Individual thisInstance = (Individual) instances.next();
			System.out.println("  Found instance: " + thisInstance.toString());

			result.add(thisInstance);
		}

		return result;
	}

	/**
	 * 使用Sparql获取一个指定class下的所有property
	 * 
	 * @param classUri
	 * @return
	 */
	public Set<String> getAllInstancesByClassWithSparql(String classUri) {
		// 获取某个class下的所有instance的Sparql语句
		String queryString = ReadConfParam
				.getMessage("get.instancesByClass.sparql");
		// 用用户传入的参数替换掉sparql语句中的参数
		queryString = queryString.replaceAll("userParam", "<" + classUri + ">");

		List<SingleColumn> instances = sparqlQuery.querySingleColumn(
				queryString, "instance");

		Set<String> result = new HashSet<String>();

		for (SingleColumn c : instances) {
			result.add(c.getFirstValue());
		}

		return result;
	}

	/**
	 * 获取一个节点（主语）相关联的边及宾语（非Literal）
	 * 
	 * @param classUri
	 * @return
	 */
	public List<Triple> getConnectedRelationNotLiteral(OntClass classUri) {
		Resource resouce = model.getResource(classUri.toString());
		StmtIterator stmts = model
				.listStatements(resouce, null, (RDFNode) null);

		List<Triple> result = new ArrayList<Triple>();

		// 主语
		RDFNode subject = classUri;

		while (stmts.hasNext()) {
			Statement st = stmts.next();
			// 如果Object不是Literal，则判断为两个对象之间的链接关系
			if (!st.getObject().isLiteral()
					&& allClassesString.contains(st.getObject().toString())) {
				// 谓语
				RDFNode predicate = st.getPredicate();
				// 宾语
				RDFNode object = st.getObject();

				Triple triple = new Triple(subject, predicate, object);

				result.add(triple);
			}
		}
		return result;
	}

	/**
	 * 获取一个节点（主语）相关联的边及宾语（非Literal）
	 * 
	 * @param classUri
	 * @return
	 */
	public List<TripleString> getConnectedRelationNotLiteralWithSparql(
			String classUri) {
		String queryString = ReadConfParam
				.getMessage("get.subjectLinkage.notliteral.sparql");
		// 用用户传入的参数替换掉sparql语句中的参数
		queryString = queryString.replaceAll("userParam", "<" + classUri + ">");

		List<DoubleColumn> linkage = sparqlQuery.queryDoubleColumn(queryString,
				"p", "o");

		List<TripleString> result = new ArrayList<TripleString>();

		for (DoubleColumn d : linkage) {
			TripleString triple = new TripleString(classUri, d.getFirstValue(),
					d.getSecondValue());

			result.add(triple);
		}

		return result;
	}

	/**
	 * 获取一个节点（主语）相关联的边及宾语（Literal）
	 * 
	 * @param classUri
	 * @return
	 */
	public List<Triple> getConnectedRelationLiteral(OntClass classUri) {
		Resource resouce = model.getResource(classUri.toString());
		StmtIterator stmts = model
				.listStatements(resouce, null, (RDFNode) null);

		List<Triple> result = new ArrayList<Triple>();

		// 主语
		RDFNode subject = classUri;

		while (stmts.hasNext()) {
			Statement st = stmts.next();
			// 如果Object不是Literal，则判断为两个对象之间的链接关系
			if (st.getObject().isLiteral()) {
				// 谓语
				RDFNode predicate = st.getPredicate();
				// 宾语
				RDFNode object = st.getObject();

				Triple triple = new Triple(subject, predicate, object);

				result.add(triple);
			}
		}
		return result;
	}

	/**
	 * 获取一个节点（主语）相关联的边及宾语（Literal）
	 * 
	 * @param classUri
	 * @return
	 */
	public List<TripleString> getConnectedRelationLiteralWithSparql(
			String classUri) {
		String queryString = ReadConfParam
				.getMessage("get.subjectLinkage.literal.sparql");
		// 用用户传入的参数替换掉sparql语句中的参数
		queryString = queryString.replaceAll("userParam", "<" + classUri + ">");

		List<DoubleColumn> linkage = sparqlQuery.queryDoubleColumn(queryString,
				"p", "o");

		List<TripleString> result = new ArrayList<TripleString>();

		for (DoubleColumn d : linkage) {
			TripleString triple = new TripleString(classUri, d.getFirstValue(),
					d.getSecondValue());

			result.add(triple);
		}

		return result;
	}

	/**
	 * 获取连个都是实例的三元组，即实例之间关系的三元组
	 * 
	 * @param args
	 * @throws IOException
	 */
	public List<TripleString> getConnectedRelationWithSparql(String classUri) {
		List<TripleString> result = new ArrayList<TripleString>();
		if (!classUri.equals("http://dbpedia.org/ontology/")) {

			String queryString = ReadConfParam
					.getMessage("get.subjectLinkage.notliteral.sparql");
			// 用用户传入的参数替换掉sparql语句中的参数
			queryString = queryString.replaceAll("userParam", "<" + classUri
					+ ">");

			List<DoubleColumn> linkage = sparqlQuery.queryRelation(queryString,
					"p", "o");

			for (DoubleColumn d : linkage) {
				TripleString triple = new TripleString(classUri,
						d.getFirstValue(), d.getSecondValue());

				result.add(triple);
			}
		}
		return result;
	}
	
	
	/**
	 * 获取所有三元组
	 * 
	 * @param args
	 * @throws IOException
	 */
	public List<TripleString> getAllTripleWithSparql(){
		List<TripleString> result = new ArrayList<TripleString>();
		String queryString = ReadConfParam
				.getMessage("get.allTriple.sparql");

		List<TripleColumn> triples= sparqlQuery.queryTripleColumn(queryString,
				"s","p", "o");
		for(TripleColumn t : triples){
			TripleString triple = new TripleString(t.getFirstValue(),
					 t.getSecondValue(),t.getThirdValue());
			result.add(triple);
		}
		return result;
	}

	
	
	
	public static void main(String[] args) throws IOException {
		DatasetInfo datasetInfo = new DatasetInfo(
				"jdbc:virtuoso://223.3.69.83:1111", "http://dbpedia2015.org",
				"dba", "dba");
		System.out.println("------------");
		Set<String> classes = datasetInfo.getAllClassWithSparql(); // 数据库中共有多少类
		// for(String s : classes)
		// System.out.println(s);
		// System.out.println(classes.size()); //数据库中共有多少类
		double sum = 0; // 实例的个数
//		String filename = "E:\\dbpediasum\\instence.txt";
		//String typename = "E:\\dbpediasum\\dbpediaclass.txt";
//		FileWriter file = new FileWriter(filename, true);
//		BufferedWriter instencewriter = new BufferedWriter(file);
		//FileWriter type = new FileWriter(typename, true);
		//BufferedWriter typewriter = new BufferedWriter(type);
		for (String c : classes) {
			//typewriter.write(c);
			//typewriter.newLine();
			
			Set<String> instenceOfClass = datasetInfo
					.getAllInstancesByClassWithSparql(c);
			
			sum = sum + instenceOfClass.size();
		}
		//typewriter.close();
//		instencewriter.close();
		System.out.println(sum);
		// for (String s : datasetInfo
		// .getAllInstancesByClassWithSparql("http://dbpedia.org/ontology/Film"))
		// {
		// System.out.println(s);
		// }

		// List<OntClass> list = datasetInfo.getAllClasses();
		// System.out.println(list.size());
		// datasetInfo.init();
		// for (OntClass i : list) {
		// // System.out.println(i);
		// List<Triple> c = datasetInfo.getConnectedRelationNotLiteral(i);
		// for (Triple t : c) {
		// System.out.println(t);
		// }
		// }

	}

}
