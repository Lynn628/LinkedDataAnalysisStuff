package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class testHashMap {
	  public static void main(String[] args) {
	        List<String> list = new ArrayList<String>();
	        list.add("string1");
	        list.add("string1");
	        list.add("string1");
	        list.add("string1");
	        list.add("string3");
	        list.add("string2");
	        list.add("string2");
	        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	        for (String string : list) {
	            if (hashMap.get(string) != null) {
	                Integer value = hashMap.get(string);
	                hashMap.put(string, value+1);
	                System.out.println("the element:"+string+" is repeat");
	            } else {
	                hashMap.put(string, 1);
	            }
	           
	        }
	        System.out.println(hashMap.size());
	  }
}
