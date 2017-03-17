package time;

import java.io.File;
/**
 * 主要内容，读取RDF文件，导入到Model中，用Model输出
 */
import java.io.FileNotFoundException;
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

public class ExtractionTime {
   
   //public static String inputFileName = "C:/Users/Lynn/Desktop/Academic/LinkedDataProject/DataSet/SWCC/conferences/dc-2010-complete.rdf";
   //C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\workshops\om-2011-complete.rdf
   //C:\Users\Lynn\Desktop\Academic\LinkedDataProject\DataSet\SWCC\workshops\sdow-2008-complete.rdf
   //C:\Users\Lynn\Desktop\jamendo-rdf\mbz_jamendo.rdf
	public static void main(String[] args) throws FileNotFoundException{
	   
	   String filePathInput = "";
	   //从控制台输入路径中读取文件，可以改成读取某dir下的所有文件
				    do {
				      System.out.println("Please enter the file path:");
				      /*filePathInput = Console.readLine()*/
				      Scanner sc = new Scanner(System.in);
				      filePathInput = sc.nextLine();
				    } while (!filePathValidation(filePathInput));
	    
		ArrayList<String> pathList = ReadFilePath.readDir(filePathInput);
		Iterator<String> iter = pathList.iterator();
	  
	   //使用Jena的FileManager查找文件，可以用来从文件系统中加载RDF文件，导入至已存在的Model或者创建新的Model
		while(iter.hasNext()){
	    Model model = ModelFactory.createDefaultModel();
		String inputFileName = iter.next();	
		System.out.println("**********************fileName" + inputFileName + "\n\n");
		InputStream in = FileManager.get().open(inputFileName);
	    if(in == null){
		   throw new IllegalArgumentException("File" + inputFileName + "not found");
	      }
	    model.read(in, null);
	  //将RDF文件读入至Model中
	  //第一种标准输出，第二种输出到文件中
	   StmtIterator iterator = model.listStatements();
	   while(iterator.hasNext()){
		   org.apache.jena.rdf.model.Statement statement = iterator.nextStatement();
		   //Resource subject = statement.getSubject();
		   Property predicate = statement.getPredicate();
		   System.out.print("*******Predicate " + predicate.toString() + "\n");
		   SUTimeTool.SUTimeFunc(predicate.toString());
		   //RDFNode是包含Resource和literal的接口，可以用来判断一个object是resource、literal或者是blank node
		   RDFNode object = statement.getObject();
		   if(object instanceof Resource){
			   System.out.print("********Obj-Rse " + object.toString()+ "\n");	   
			   SUTimeTool.SUTimeFunc(object.toString());  
		   }else{
			   System.out.print("*******Obj-Liter \"" + object.toString() + "\"\n");
			   SUTimeTool.SUTimeFunc(object.toString());
		   }
		  // System.out.println(" .");
	     }
		}
	
		//System.out.println("\n\nthe size of file is" + pathList.size());
   }
 
   //判断文件是否存在
	  public static Boolean filePathValidation(String filePath) {
	    File file = new File(filePath);
	    if (!file.exists()) {
	      System.out.println("Wrong path or no such file.");
	    }
	    return file.exists();
	  }
}
