package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import time.ReadFilePath;
import time.SUTimeTool;
//C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\workshops\admire-2012-complete.rdf
//C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\workshops\sdow-2008-complete.rdf    28kb
/**
 * 
 * @author Lynn
 * @description  比较输出subject 的localname和URI；predicate的localname 和URI，判断object的类型；
 *当object为resource判断其是否有时间信息，进行2重判断：第一重，判断URI是否有时间信息，
 *第二重，判断localname是否具有时间信息，获取localname是要判断localname是否为空，否则会导致空指针异常
 *当object为literal时再进行判断
 *
 */
public class localNameTest {
	public static void main(String[] args) throws IOException{
	    long timeBegin =  System.currentTimeMillis();
	    String filePathInput = "";
	    String fileName = "";
	    Scanner sc = new Scanner(System.in);
	    //从控制台输入路径中读取该路径下所有文件，给存放标注好三元组的文件命名
	    do {
	      System.out.println("Please enter the file path:");
	      
	      filePathInput = sc.nextLine();
	      System.out.println("Please enter the marked file name:");
	      fileName= sc.nextLine();
	     } while (!ReadFilePath.filePathValidation(filePathInput));
	    sc.close();
	    
	    File markedFile = new File("D:\\markedFile\\" + fileName + ".txt");
	    FileWriter  outStream= new FileWriter(markedFile); 
	    
		ArrayList<String> pathList = ReadFilePath.readDir(filePathInput);
		Iterator<String> iter = pathList.iterator();
	   //使用Jena的FileManager查找文件，可以用来从文件系统中加载RDF文件，导入至已存在的Model或者创建新的Model
		Model model = ModelFactory.createDefaultModel();
	    //i- indicates the num of file has been read; j - indicates the column of statement 
		int i = 0;
		int j = 0;
		while(iter.hasNext()){
			i++;
		String inputFileName = iter.next();	
		System.out.println("**********************fileName********\n" + inputFileName + "\n\n");
		InputStream in = FileManager.get().open(inputFileName);
	    if(in == null){
		   throw new IllegalArgumentException("File" + inputFileName + "not found");
	      }
	    //将RDF文件读入至Model中
	    model.read(in, null);
	    //第一种标准输出，第二种输出到文件中
	   StmtIterator iterator = model.listStatements();
	   while(iterator.hasNext()){
		   j++;
		   org.apache.jena.rdf.model.Statement statement = iterator.nextStatement();
		   Resource subject = statement.getSubject();
		   Property predicate = statement.getPredicate();
		   RDFNode object = statement.getObject(); 
		 /*  System.out.println("subject string: " + subject.toString() +"\n");
		   System.out.println(" subject local name: " + subject.getLocalName() +"\n");*/
		   outStream.write("\n" + j + "subject string: " + subject.toString() +"\n");
		   outStream.write("  subject string: " + subject.getLocalName() +"\n");
		   //写一个函数要分别判断是否是有含有时间信息的概念层的URI
		   if(SUTimeTool.SUTimeJudgeFunc(predicate.toString())){
			   outStream.write("  Judge str - predicate string: " + "<br>" + predicate.toString() +"<br/>\n");
			   outStream.write("  Judge str - predicate local name: " + "<br>" + predicate.getLocalName() +  "<br/>\n");
		   }
		   else if(SUTimeTool.SUTimeJudgeFunc(predicate.getLocalName())){
			//如果predicate包含时间信息，则将predicate做上标记,输入到文件中
			   //输出时尝试使用Jena的local path，减少输出对象的长度。
			  // outStream.write("<br>" + predicate.getLocalName() + "<br/>     ");
			/*   System.out.println("predicate string: " + "<br>" + predicate.toString() +"<br/>\n");
			   System.out.println("predicate local name: " + "<br>" + predicate.getLocalName() +  "<br/>\n");*/
			   outStream.write("  predicate string: " + "<br>" + predicate.toString() +"<br/>\n");
			   outStream.write("  predicate local name: " + "<br>" + predicate.getLocalName() +  "<br/>\n");
		   }else{
			  // outStream.write(predicate.getLocalName() + "     "); 
			  /* System.out.println("predicate string: " + predicate.toString() +"\n");
			   System.out.println("predicate local name: " + predicate.getLocalName() + "\n");*/
			   outStream.write("  predicate string: " + predicate.toString() +"\n");
			   outStream.write("  predicate local name: " + predicate.getLocalName() + "\n");
		   }
		   //RDFNode是包含Resource和literal的接口，可以用来判断一个object是resource、literal或者是blank node
		   if(object instanceof Resource){
			   if(SUTimeTool.SUTimeJudgeFunc(object.toString())){
				   outStream.write("  Judge str -  Object is resource & string: " + "<br>" + object.toString()+  "<br/>\n");
				   outStream.write("  Judge str - Object is rsource & Local name: "+"<br>"  + ((Resource) object).getLocalName()+"<br/>\n");
			   }
			   else if(((Resource) object).getLocalName() != null && SUTimeTool.SUTimeJudgeFunc(((Resource) object).getLocalName())){
				/*   System.out.println("Object is resource & string: " + "<br>" + object.toString()+  "<br/>\n");
				   System.out.println("Object is rsource & Local name: "+"<br>"  + ((Resource) object).getLocalName()+"<br/>\n");*/
				   outStream.write("  Object is resource & string: " + "<br>" + object.toString()+  "<br/>\n");
				   outStream.write("  Object is rsource & Local name: "+"<br>"  + ((Resource) object).getLocalName()+"<br/>\n");
			       //object带有时间信息则做上标记，输出
				   //outStream.write("<br>" + ((Resource) object).getLocalName() + "<br/>\r\n\n");
			   }else{
				  /* System.out.println("Object is resource & string: " + object.toString()+  "\n");
				   System.out.println("Object is resource & Local name: "  + ((Resource) object).getLocalName()+"\n");*/
				   outStream.write("  Object is resource & string: " + object.toString()+  "\n");
				   outStream.write("  Object is resource & Local name: "  + ((Resource) object).getLocalName()+"\n");
				   //  outStream.write(((Resource) object).getLocalName() + "\r\n\n");
			   }
		    }else{//object是literal
			   if(SUTimeTool.SUTimeJudgeFunc(object.toString())){
			       //object带有时间信息则做上标记，输出
				 //  System.out.println("Object is liter & string: " + "<br>" + object.toString()+  "<br/>\n");
				   outStream.write("  Object is liter & string: " + "<br>" + object.toString()+  "<br/>\n");
				  // outStream.write("<br>" + object.toString() + "<br/>\r\n\n");
			   }else{
				  // outStream.write(object.toString() + "\r\n\n");
				  // System.out.println("Object is liter & string: " + object.toString()+  "\n");
				   outStream.write("  Object is liter & string: " + object.toString()+  "\n");
		          }
	        }
	    }
	  }
	     System.out.println("\n\n#############################File" + i + "#############################\n\n");
		 long timeEnd =  System.currentTimeMillis();
		 System.out.println("Num of file has been read " + i + "\n");
		 System.out.println("time total cost: " + (timeEnd - timeBegin)/1000.0/60.0 + "min\n"); 
		 outStream.close();
	}
}
