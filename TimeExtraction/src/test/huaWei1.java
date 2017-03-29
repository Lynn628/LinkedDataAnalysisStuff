package test;

import java.util.Scanner;

import javax.xml.validation.SchemaFactoryConfigurationError;

import arq.version;
import java.math.BigInteger;
public class huaWei1 {
	        public static void main(String args[]){ 
	          //  Scanner scanner = new Scanner(System.in);
	         //   Integer big = Integer.parseInt(scanner.nextLine());
	        	//String str = scanner.nextLine();
	            if(args.length >= 1){ 
	                        Integer base = new Integer(args[0]); 
	                        BigInteger result = new BigInteger("1"); 
	                        for(int i = 1; i <= base; i++){ 
	                                String temp1 = Integer.toString(i); 
	                                BigInteger temp2 = new  BigInteger(temp1); 
	                                result = result.multiply(temp2); 
	                        } 
	                        System.out.println("" + base + "! = " + result); 
	                } 
	                else{ 
	                        System.out.println("Format Error"); 
	                } 
	        } 
	} 

