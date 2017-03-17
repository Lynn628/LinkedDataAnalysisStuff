package time;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class manipulateExcel {
   public static void wirteToExcel() throws IOException, RowsExceededException, WriteException{
	   //创建工作簿（writableworkbook）对象，打开Excel文件，文件若不存在，则创建文件
	   WritableWorkbook writeBook = Workbook.createWorkbook(new File("C://Users//Lynn//Desktop//Academic//LinkedDataProject//markedFile//fileCompare"));
      
	   //新建工作表（sheet）对象，并声明其属于第几页
	   WritableSheet firstSheet = writeBook.createSheet("Sheet 1", 1);
	   
	   //创建单元格（label）对象
	   Label label1 = new Label(1, 2, "Orign file name");//第一个参数指定单元格列数，第二个参数指定单元格行数，第三个指定写的字符串内容
	   firstSheet.addCell(label1);
	   
	   //打开流，开始写文件
	   writeBook.write();
	   
	   //关闭流
	   writeBook.close();
   }
}
