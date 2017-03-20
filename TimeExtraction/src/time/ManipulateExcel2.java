package time;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ManipulateExcel2 {
	/**
	 * 
	 * @param writeBook
	 * @param srcFileName
	 * @param dstFileName
	 * @param tripleAmount
	 * @param timeCost
	 * @param colNum
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * 对Excel的写操作，不包含统计数据集中的property信息
	 */
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
	   /**
	    * 
	    * @param writeBook
	    * @param srcFileName
	    * @param dstFileName
	    * @param tripleAmount
	    * @param timeProperty，数据集中携带时间信息的property的List（property有重复）
	    * @param timeCost
	    * @param colNum
	    * @throws IOException
	    * @throws RowsExceededException
	    * @throws WriteException
	    */
	   
	   public static void writeToExcel(WritableWorkbook writeBook, String srcFileName, String dstFileName, 
			   int tripleAmount, ArrayList<String> timeProperty, double timeCost, int colNum) throws IOException, RowsExceededException, WriteException{
		   
		   WritableSheet firstSheet = writeBook.getSheet("Sheet 1");
		   WritableFont wf = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD,true);
	       WritableCellFormat wcf = new WritableCellFormat(wf);
	      //调用处理propertylist的函数，返回property处理信息字符串
	       String timePropertyStr = timePropertyProcess(timeProperty);
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
	   /**
	    * 对携带时间Property的数据进行统计处理
	    * @param timeProperty
	    * @return
	    */
	   public static String timePropertyProcess(ArrayList<String> timeProperty){
           String timePropertyStr = "";
		   
		   HashMap<String, Integer> propertyGlobalFrequency = new HashMap<String, Integer>();
		   int totalPropertyNum = timeProperty.size();
		   for(String property : timeProperty){
			   if(propertyGlobalFrequency.get(property) != null){
				   int frequency = propertyGlobalFrequency.get(property);
				   propertyGlobalFrequency.put(property,frequency +1);
			   }else{
				   propertyGlobalFrequency.put(property, 1);
			   }
		   }
		   //循环输出hashMap的值
		   Iterator iterator = propertyGlobalFrequency.entrySet().iterator();
		   while(iterator.hasNext()){
			   Map.Entry entry = (Map.Entry) iterator.next();
			   String property = (String)entry.getKey();
			   Double proptionNum = ((Integer)entry.getValue())/(double)totalPropertyNum;
			   DecimalFormat df = new DecimalFormat("0.00");
			   String proptionStr = df.format(proptionNum);
			   timePropertyStr += property + ": " + proptionStr + "; ";
			   
		   }
		   return timePropertyStr;
	   }
}
