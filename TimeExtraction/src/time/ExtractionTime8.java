package time;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.util.CoreMap;
import util.ReadFilePath;
import util.SUTimeTool2;

/**
 * 在ExtractionTime7的基础上加了个循环要求输入文件的功能
 * @author Lynn
 *
 */
public class ExtractionTime8 {
	 public static void main(String[] args) throws IOException{
		 
		 long t1 = System.currentTimeMillis();
		  System.out.println("Input the directory path:\n");
		  Scanner scanner= new Scanner(System.in);
		  String dirPath = scanner.nextLine();
		  ArrayList<String> pathList = ReadFilePath.readDir(dirPath);
		  Iterator<String> iterator = pathList.iterator();
		  scanner.close();
		  while(iterator.hasNext()){
		  String filePath = iterator.next();
		  String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.indexOf("."));
		  
		  FileWriter fileWriter = new FileWriter(new File("C:\\Users\\Lynn\\Desktop\\Academic\\LinkedDataProject\\markedFile\\fileCompare\\" + fileName + ".txt"));
		  BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		  //创建对象调用函数
		  new ExtractionTime8().timeExtraction(filePath, fileName, bufferedWriter);
		  long t2 = System.currentTimeMillis();
		  System.out.println( fileName + "   Total process time is:  " + (t2 - t1)/1000.0 + "seconds");
		  bufferedWriter.close();
		  }
	  }
	  /**
	   *  
	   * @param filePath
	   * @param dstPath
	   * @param bufferedWriter
	   * @throws IOException
	   */
	  public void timeExtraction(String filePath, String dstPath, BufferedWriter bufferedWriter) throws IOException{
		 //initialize the annotationPipeline
		  AnnotationPipeline pipeline = SUTimeTool2.PipeInit();
		
		  if(ReadFilePath.filePathValidation(filePath)){
			 Model model = ModelFactory.createDefaultModel();
			 InputStream inputStream = FileManager.get().open(filePath);
			 if(inputStream != null){
				 model.read(inputStream, null);
				 StmtIterator iterator = model.listStatements();
				 int lineNum = 1;
				 while(iterator.hasNext()){
					 Statement statement = iterator.next();
					 Resource resource = statement.getSubject();
					 bufferedWriter.write(lineNum++ + "  " +resource.toString() + "    ");
					 Property property = statement.getPredicate();
				     bufferedWriter.write(property.getLocalName() + "    ");
					 RDFNode object = statement.getObject();
					
					 if(object instanceof Resource){
						bufferedWriter.write(object.toString());
						bufferedWriter.newLine();
					 }else{ 
						    List<CoreMap> list = SUTimeTool2.SUTimeJudgeFunc(pipeline, object.toString());
						 if(list.isEmpty()){
							 bufferedWriter.write(object.toString());
							 bufferedWriter.newLine();
						 }else{
							 writeTimeObjectToFile(object, list, bufferedWriter);
						 }
					     
					 }
					 
				 }
				 System.out.println("Statement Number: " + lineNum);
			 }
	       }//C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\conferences\dc-2010-complete.rdf
		 }
	  /**
	   * 
	   * @param object
	   * @param list
	   * @param buff
	   * @throws IOException
	   */
	  public static void writeTimeObjectToFile(RDFNode object, List<CoreMap> list, BufferedWriter buff) throws IOException{
		  buff.write(object.toString() + "    ");
		//  System.out.println("Object:" + object.toString() + "\n");
		  int objectLength = object.toString().length();
		  int timeInfoSize = 0;
		  for(CoreMap cm: list){
			//  List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
			//timeInfoSize += tokens.size();
			  timeInfoSize += cm.toString().length();
			  buff.write("<br>" + cm.toString() + "</br>    ");
		  }
		
		  double percentage = timeInfoSize/(double)objectLength * 100;
		  buff.write(String.valueOf(percentage + "%"));
		  buff.newLine(); 
	  }
}
