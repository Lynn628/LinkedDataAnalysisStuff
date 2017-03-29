package time;

import java.io.File;
/**
 * 主要内容，读取RDF文件，导入到Model中，用Model输出
 */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import util.ReadFilePath;
import util.SUTimeTool;	
/**
 * 
 * @author Lynn
 * @Date 2017/3/2
 * //public static String inputFileName = "C:/Users/Lynn/Desktop/Academic/LinkedDataProject/DataSet/SWCC/conferences/dc-2010-complete.rdf";
   //C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\workshops\om-2011-complete.rdf
   //C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\workshops\sdow-2008-complete.rdf
   //C:\Users\Lynn\Desktop\jamendo-rdf\mbz_jamendo.rdf
	//E:\DataSet\DBLP2\testSet\dblp-publications-1944.rdf
	  C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC
 */
public class ExtractionTime2 {
   
	public static void main(String[] args) throws IOException{
	    long timeBegin =  System.currentTimeMillis();
	    String filePathInput = "";
	    String fileName = "";
	    File markedFile = new File("E:\\DataSet\\DBLP2\\markedFile.txt");
	    FileWriter  outStream= new FileWriter(markedFile); 
	   //从控制台输入路径中读取文件，可以改成读取某dir下的所有文件
	    do {
	      System.out.println("Please enter the file path:");
	      /*filePathInput = Console.readLine()*/
	      Scanner sc = new Scanner(System.in);
	      filePathInput = sc.nextLine();
	    } while (!ReadFilePath.filePathValidation(filePathInput));
	    
		ArrayList<String> pathList = ReadFilePath.readDir(filePathInput);
		Iterator<String> iter = pathList.iterator();
	  
	  //使用Jena的FileManager查找文件，可以用来从文件系统中加载RDF文件，导入至已存在的Model或者创建新的Model
		Model model = ModelFactory.createDefaultModel();
		Model newModel = ModelFactory.createDefaultModel();
	    //i- indicate the num of file has been read; j - indicate the column of statement 
		int i = 0;
	    int columnNum =0;
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
		   columnNum++;
		   org.apache.jena.rdf.model.Statement statement = iterator.nextStatement();
		   Resource subject = statement.getSubject();
		   Property predicate = statement.getPredicate();
		   RDFNode object = statement.getObject(); 
		   outStream.write(columnNum +"  " + subject.toString() + "     ");
		   //写一个函数要分别判断是否是有含有时间信息的概念层的URI
		   if(SUTimeTool.SUTimeJudgeFunc(predicate.toString())){
			//如果predicate包含时间信息，则将predicate做上标记,输入到文件中
			   //输出时尝试使用Jena的local path，减少输出对象的长度。
			   outStream.write(predicate.toString() + "#*********#     "); 
		   }else{
			   outStream.write(predicate.toString() + "     "); 
		   }
		   //RDFNode是包含Resource和literal的接口，可以用来判断一个object是resource、literal或者是blank node
		   if(object instanceof Resource){
			   if(SUTimeTool.SUTimeJudgeFunc(object.toString())){
			       //object带有时间信息则做上标记，输出
				   outStream.write(object.toString() + "#*********#\r\n\n");
			   }else{
				   outStream.write(object.toString() + "\r\n\n");
			   }
		    }else{
			   if(SUTimeTool.SUTimeJudgeFunc(object.toString())){
			       //object带有时间信息则做上标记，输出
				   outStream.write(object.toString() + "#*********#\r\n\n");
			   }else{
				   outStream.write(object.toString() + "\r\n\n");
		          }
	        }
	    }
	  }
	     System.out.println("\n\n#############################File" + i + "#############################\n\n");
		 long timeEnd =  System.currentTimeMillis();
		 System.out.println("Num of file has been read " + i + "\n");
		 System.out.println("time total cost" + (timeEnd - timeBegin)/1000/60 + "min\n"); 
     
		outStream.close();
	}
   
}

