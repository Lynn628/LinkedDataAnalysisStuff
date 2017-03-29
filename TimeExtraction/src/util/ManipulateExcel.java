package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.rdf.model.Property;

import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ManipulateExcel {
   //public static WritableWorkbook createExcel( ) throws IOException, RowsExceededException, WriteException{
	   //创建工作簿（WritableWorkbook）对象，打开Excel文件，文件若不存在，则创建文件
	  /* WritableWorkbook writeBook = Workbook.createWorkbook(new File("C://Users//Lynn//Desktop//Academic//LinkedDataProject//markedFile//fileCompare//A.xlsx"));
	   //设置工作表字体
	   WritableFont wf = new WritableFont(WritableFont.TIMES,18,WritableFont.BOLD,true);
       WritableCellFormat wcf = new WritableCellFormat(wf);
	   //新建工作表（sheet）对象，并声明其属于第几页
	   WritableSheet firstSheet = writeBook.createSheet("Sheet 1", 1);
	   ArrayList<Label> columnList = new ArrayList<>();
	   columnList.add(new Label(0, 0, "Orign file path", wcf));
	   columnList.add(new Label(1, 0, "Dst file path", wcf));
	   columnList.add( new Label(2, 0, "Triple amount", wcf));
	   columnList.add(new Label(3, 0, "Time cost", wcf));
	   int size = columnList.size();
	   for(int i = 0; i < size; i++){
		 firstSheet.addCell(columnList.get(i));
	   }*/
	   
	 /*  //创建单元格（label）对象
	   Label label1 = new Label(0, 0, "Orign file path");//第一个参数指定单元格列数，第二个参数指定单元格行数，第三个指定写的字符串内容
	   firstSheet.addCell(label1);*/
	   //打开流，开始写文件
/*	   writeBook.write();
	   //关闭流
	   //writeBook.close();
	   return writeBook;
   }*/
   
   public static void writeToExcel(WritableWorkbook writeBook, String srcFileName, String dstFileName, 
		   int tripleAmount, double timeCost, int colNum) throws IOException, RowsExceededException, WriteException{
	   
	   WritableSheet firstSheet = writeBook.getSheet("Sheet 1");
	   //System.out.println("************" + firstSheet.getCell(0, 0).getContents());
	   //System.out.println("Whether sheet is null"+ firstSheet == null);
	   WritableFont wf = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD,true);
       WritableCellFormat wcf = new WritableCellFormat(wf);
	   ArrayList<Label> columnList = new ArrayList<>();
	   columnList.add(new Label(0, colNum, srcFileName, wcf));
	   columnList.add(new Label(1, colNum, dstFileName, wcf));
	   columnList.add(new Label(2, colNum, String.valueOf(tripleAmount), wcf));
	   columnList.add(new Label(3, colNum, String.valueOf(timeCost), wcf));
	   int size = columnList.size();
	   for(int i = 0; i < size; i++){
		 Label content = columnList.get(i);
		 firstSheet.addCell(content);
    }
	   
	   //writeBook.close();
	   System.out.println("In the method of write to excel" + srcFileName + " " + dstFileName +
			                                            " " + tripleAmount + " " + timeCost + 
			                                            " " + colNum);
   }
   
   
   public static void writeToExcel(WritableWorkbook writeBook, String srcFileName, String dstFileName, 
		   int tripleAmount, HashSet<String> timeProperty, double timeCost, int colNum) throws IOException, RowsExceededException, WriteException{
	   
	   WritableSheet firstSheet = writeBook.getSheet("Sheet 1");
	   //System.out.println("************" + firstSheet.getCell(0, 0).getContents());
	   //System.out.println("Whether sheet is null"+ firstSheet == null);
	   Iterator<String> iterator = timeProperty.iterator();
	   String timePropertyStr = "";
	   while(iterator.hasNext()){
		   timePropertyStr += iterator.next().toString() + ";";
	   }
	   WritableFont wf = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD,true);
       WritableCellFormat wcf = new WritableCellFormat(wf);
	   ArrayList<Label> columnList = new ArrayList<>();
	   columnList.add(new Label(0, colNum, srcFileName, wcf));
	   columnList.add(new Label(1, colNum, dstFileName, wcf));
	   columnList.add(new Label(2, colNum, String.valueOf(tripleAmount), wcf));
	   columnList.add(new Label(3, colNum, timePropertyStr, wcf));
	   columnList.add(new Label(4, colNum, String.valueOf(timeCost), wcf));
	   int size = columnList.size();
	   for(int i = 0; i < size; i++){
		 Label content = columnList.get(i);
		 firstSheet.addCell(content);
    }

   }
   /*public static void main(String[] args) throws RowsExceededException, WriteException, IOException{
	   WritableWorkbook writebook = createExcel();
	   String srcPath = "AAAAAAAAAAAAA";
	   String dStPath = "BBBBBBBBBBBBBBBBBB";
	   writeToExcel(writebook, srcPath, dStPath, 3330, 20.0, 2);
   }*/
   
}
