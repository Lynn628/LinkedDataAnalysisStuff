package test;

import java.util.Scanner;

public class subStrTest {
  public static void main(String[] args){
	  Scanner in = new Scanner(System.in);
	  
	  String filePath = in.nextLine();
	  in.close();
	  String fileName = filePath.substring(filePath.lastIndexOf("\\")+1, filePath.indexOf("."));
	  System.out.println("File name: " + fileName);
  }
}
