package test;

import java.util.Scanner;

import arq.version;

public class huaWei1 {
   public static void main(String[] args){
	/*   Scanner sc = new Scanner(System.in);
	 //  while (sc.hasNext()){
		   String s = sc.next();
		   s = s.toLowerCase();
		   System.out.println(s);
		   String reg = "[^a-z]";
		   s = s.replaceAll(reg, "");
		   System.out.println(s);
	  // }
*/	   String string = "Hellothis ...is a tesT";
	   toLowArray(string);
	   //sc.close();  
   }
   
   public static void toLowArray(String a){
	   char[] arry = a.toCharArray();
	   int length = a.length();
	   String newStr = "";
	   for(int i = 0; i < length; i++){
		if('A' <= arry[i] && arry[i] <= 'Z'){
			int gap = 'A' - 'a';
			arry[i] = (char) (arry[i] - gap) ;
		}if('a' <= arry[i] && arry[i] <= 'z'){
			
		}else{
			arry[i] = ' ';
		}
		if(arry[i] != ' '){
			newStr += String.valueOf(arry[i]);
		}
		
	   }
	  
	 /*  String newStr = new String(arry);
	   newStr.replaceAll(String.valueOf(' '), "");
	   System.out.println(arry);*/
	   System.out.println(newStr);
   }
   
   
}
