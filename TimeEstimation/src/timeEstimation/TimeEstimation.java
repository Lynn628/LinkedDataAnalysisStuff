package timeEstimation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class TimeEstimation {
	public static void main(String[] args){
		 DataProcess.ProcessAndStoreNumberedData();
		String filePathInput = "";
				    //提交文件路径
				    do {
				      System.out.println("Please enter the file dir:");
				      /*filePathInput = Console.readLine()*/
				      Scanner sc = new Scanner(System.in);
				      filePathInput = sc.nextLine();
				    } while (!filePathValidation(filePathInput));
				    //C:\Users\Lynn\Desktop\Academic\GraphX\DataSets\yagoFacts\test1.tsv
				   String filePath = filePathInput.replace("\\", "/");

				    String operationChoosed = "";
				    //写成可变的集合Set，后期可以添加功能模块
				   List operationList =  new ArrayList();
				   
				    do {
				      //选择要进行的操作
				      System.out.println("Please the operation number:" + "\n" +
				        "1.PageRank; 2.HITS; 3.Closeness; 4.EigenvectorCentrality");
				      Scanner sc = new Scanner(System.in);
				      operationChoosed = sc.nextLine();
				      if (!operationList.contains(operationChoosed)) {
				       System.out.println("No such operation");
				      }

				    } while (!operationList.contains(operationChoosed));
				    //计算文件的大小，并给出，对于较大文件的计算可能花费时间较长，因而可以考虑多线程
				 //   val file = Source.fromFile(filePath, "utf-8");
				    File file = readFileByLines(filePath);
				    Long fileSize = file.length();
				   showFileSize(fileSize);
				  
				    //对给定的文件大小和操作给出预估时间
				   String timePredication = operationTimeEstimation(fileSize, operationChoosed);
				   System.out.println("Time supposed to cost is" + timePredication);
				    //提供了合理的文件路径和操作选项，进行操作
				    //operationOnDataSet(filePath, operationChoosed);
				  }
       

				  //判断文件是否存在
				  public static Boolean filePathValidation(String filePath) {
				    File file = new File(filePath);
				    if (!file.exists()) {
				      System.out.println("Wrong path or no such file exists.");
				    }
				    return file.exists();
				  }
				  
				  //读取文件
				   /**
				     * 以行为单位读取文件，常用于读面向行的格式化文件
				     */
				    public static File readFileByLines(String fileName) {
				        File file = new File(fileName);
				        BufferedReader reader = null;
				        try {
				            System.out.println("以行为单位读取文件内容，一次读一整行：");
				            reader = new BufferedReader(new FileReader(file));
				            String tempString = null;
				            int line = 1;
				            // 一次读入一行，直到读入null为文件结束
				            while ((tempString = reader.readLine()) != null) {
				                // 显示行号
				                System.out.println("line " + line + ": " + tempString);
				                line++;
				            }
				            reader.close();
				        } catch (IOException e) {
				            e.printStackTrace();
				        } finally {
				            if (reader != null) {
				                try {
				                    reader.close();
				                } catch (IOException e1) {
				                }
				            }
				        }
				        return file;
				    }
				    
				  //给出文件大小
				  public static void showFileSize(Long fileSize) {
				    DecimalFormat df = new DecimalFormat("#.00");
				    String fileSizeString = " ";
				    if (fileSize < 1024) {
				      fileSizeString = df.format(fileSize) + "B";
				    } else if (fileSize < 1048576) {
				      fileSizeString = df.format(fileSize / 1024) + "K";
				    } else if (fileSize < 1073741824) {
				      fileSizeString = df.format(fileSize / 1048576.0) + "M";
				    } else {
				      fileSizeString = df.format(fileSize / 1073741824.0) + "G";
				    }
				   System.out.println("File size: " + fileSizeString );
				  }

				  public static String operationTimeEstimation(Long fileSize, String operation){
				    return "Ha Ha";
				  }
			  }
