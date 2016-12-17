package com.cse.utils.dbpedia.backdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tartarus.snowball.ext.EnglishStemmer;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class Nlp {
	/**
	 * 按照大写字母进行分词
	 * @param a
	 * @return
	 */
	public static List<String> separateWord(String a) {
		List<String> list = new ArrayList<String>();
		List<Integer> indexs = new ArrayList<Integer>(); //记录大写字母出现的位置
	    String word=null;
		for (int i = 0; i < a.length(); i++) {
			char c = a.charAt(i);
			byte b = (byte) c;
			if (b < 97) {
				indexs.add(i);
				if (indexs.size() == 1) {
					if (i == 0) {
						continue;
					}
					word=a.substring(0, i).toLowerCase();
					list.add(word);
				} else {
					word=a.substring(indexs.get(indexs.size() - 2), i).toLowerCase();
					list.add(word);
				}

			}
		}
		word=a.substring(indexs.get(indexs.size() - 1)).toLowerCase();
		list.add(word);
		return list;
	}
    
	/**
	 * 判断字符串里是否有大写字母
	 * @param s
	 * @return
	 */
	public static boolean containUpcase(String s){
		boolean b=false;
		if(s.length()>1){
			s=s.substring(1,s.length());
		    if(s.contains("A") || s.contains("B") || s.contains("C") || s.contains("D")
				||s.contains("E") || s.contains("F") || s.contains("G") || s.contains("H")
				||s.contains("I") || s.contains("J") || s.contains("K") || s.contains("L")
				||s.contains("M") || s.contains("N") || s.contains("O") || s.contains("P")
				||s.contains("Q") || s.contains("R") || s.contains("S") || s.contains("T")
				||s.contains("U") || s.contains("V") || s.contains("W") || s.contains("X")
				||s.contains("Y") || s.contains("Z") )
			b=true;
		}
		return b;
	}
	
	/**
	 * 清楚字符串中的字符
	 * @param s
	 * @return
	 */
	public static String cleanSymbol(String s){
		String word=null;
		for(int i=0;i<s.length();i++){
			char c = s.charAt(i);
			byte b = (byte) c;
			if((b>=32 && b<=64) || (b>=91 && b<=95) || (b>=123 && b<=126))
				continue;
			else{
				if(word==null)
					word="";
				word+=c;
			}
		}
		return word;
	}
	
	/**
	 * 根据符号分词
	 * @param s
	 * @return
	 */
	public static List<String>  separateWordBySymbol(String s){
		List<String> list = new ArrayList<String>();
		List<Integer> indexs = new ArrayList<Integer>(); //记录字符出现的位置
	    String word=null;
	 
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			byte b = (byte) c;
			if((b>=33 && b<=64) || (b>=91 && b<=96) || (b>=123 && b<=126)){
				indexs.add(i);
			}
		}
		int size=indexs.size();
		for(int i=0;i<size;i++){
			if(i==0){
				if(indexs.get(i)!=0){
				  word=s.substring(0,indexs.get(i));
				  word=cleanSymbol(word);
				  if(word != null)
					  list.add(word);
				}
				else
					continue;
			}
			else if(i!=(size-1) && ( (indexs.get(i+1)-indexs.get(i)) != 1) ){
				word=s.substring(indexs.get(i)+1,indexs.get(i+1));
				word=cleanSymbol(word);
				 if(word != null)
					 
					 list.add(word);
			}
			else if(i == (size-1)){
				word=s.substring(indexs.get(size-1)+1);
				if(word != null){
					word=cleanSymbol(word);
				    if(word != null)
					  list.add(word);
				}
			}
		}
		return list;
	}
	
	
	/**
	 * 得到localname
	 * @param uri
	 * @return
	 */
	public static String getLocalName(String uri){
		String word="localname";
		for(int i=uri.length()-1;i>=0;i--){
			if(uri.charAt(i)=='#' || uri.charAt(i)=='/'){
				word=uri.substring(i);
				if(word!=null){
					word=cleanSymbol(word);
				   if(word==null){
					  continue;
				  }
				  else
					return word;
			     }
				else
					continue;
			}
		}
		return word;
	}
	
	
	/**
	 * 得到localname、path
	 * @param uri
	 * @return
	 */
	public static String[] getLocalNamePath(String uri){
		String[] word={"",""};
		String w1=null;
		
		int n=uri.length();
		int m=0;
		for(int i=uri.length()-1;i>=0;i--){
			if(uri.charAt(i)=='#' || uri.charAt(i)=='/' ){
				w1=uri.substring(i,n);
				if(w1!=null && !w1.equals("")){
				   w1=cleanSymbol(w1);
				   if(w1==null || w1.equals("")){
					  continue;
				  }
				  else{
					  word[m]=w1;
					  n=i;
					  m++;
				  }
					
			     }
				else
					continue;
				if(m>=2)
					break;
			}
		}
		return word;
	}
	
	/**
	 * localname、path插入文档
	 * @param sub
	 * @param wordmap
	 */
	public static void insetMap(String[] sub , Map<String,Double> wordmap){
		for(int i=0; i<sub.length ; i++){
			if((sub[i] != null) && !sub[i].equals("")){
				separate(sub[i],wordmap);
			}
		}
	}
	
	/**
	 * 分词
	 * @param s
	 * @param wordmap
	 */
	public static void separate(String s,Map<String,Double> wordmap){
		
		String words[]=s.split(" +");
		 for(int i=0;i<words.length;i++){ 
		    	String word=Nlp.cleanSymbol(words[i]);
		    	if((word != null) && !word.equals("")){
		    	if(Nlp.containUpcase(word)){
		    		List<String> wordList=Nlp.separateWord(word);
		    		for(String wo : wordList){
		    			if((wo != null) && !wo.equals("")){
		    				if(!wordmap.containsKey(wo)){
		    					
		    					    wordmap.put(wo, 1.0);
		    				}
		    				else{
		    					double n=wordmap.get(wo);
		    					wordmap.put(wo, n++);
		    				}
		    			}	
		    			
		    		}
		    	}
		    	else{
		    		word=word.toLowerCase();
		    		if((word != null)&&!word.equals("")){
	    					if(!wordmap.containsKey(word)){
	    					   wordmap.put(word, 1.0);
	    				    }
	    				    else{
	    				    	double n=wordmap.get(word);
	    					     wordmap.put(word, n++);
	    				   }
	    				}	
	    			
		    	}
		    	}
		 }
	}
	
	
	/**
	 * 自然语言处理过程，包括分词、去停用词、取词根
	 * @param s
	 * @param wordmap
	 * @param stopwords
	 */
	public static void nlp(String s, Map<String, Double> wordmap, List<String> stopwords ){
		String words[]=s.split(" +");
		for(int i=0;i<words.length;i++){ 
	    	String word=Nlp.cleanSymbol(words[i]);
	    	if((word != null) && !word.equals("")){
	    	if(Nlp.containUpcase(word)){
	    		List<String> wordList=Nlp.separateWord(word);
	    		for(String wo : wordList){
	    			if(stopwords.contains(wo))
	    				continue;
	    			else{
	    				String w=Nlp.stemTerm(wo);
	    				if((w != null) && !w.equals("")){
	    				if(!wordmap.containsKey(w)){
	    					
	    					    wordmap.put(w, 1.0);
	    				}
	    				else{
	    					double n=wordmap.get(w);
	    					wordmap.put(w, n++);
	    				}
	    				}	
	    			}
	    		}
	    	}
	    	else{
	    		word=word.toLowerCase();
	    		
	    		if(stopwords.contains(word))
    				continue;
    			else{
    				String w=Nlp.stemTerm(word);
    				
    				if((w != null)&&!w.equals("")){
    					if(!wordmap.containsKey(w)){
    					   wordmap.put(w, 1.0);
    				    }
    				    else{
    				    	double n=wordmap.get(w);
    					     wordmap.put(w, n++);
    				   }
    				}	
    			}
	    	}
	    	
	    }
		}
	}
	
	
	
	/**
	 * localname、path插入虚拟文档
	 * @param sub
	 * @param wordmap
	 */
	public static void insetVdoc(String[] sub , Map<String,Double> wordmap,List<String> stopwords){
		for(int i=0; i<sub.length ; i++){
			if((sub[i] != null) && !sub[i].equals("")){
				nlp(sub[i],wordmap,stopwords);
			}
		}
	}
	
	/**
	 * 语言识别，识别属于哪种语言
	 * @param text
	 * @return
	 */
	public  static String languageDetection(String text) {
		try
        {
//            DetectorFactory.loadProfile("F:\\eclipse_workplace\\language-detection\\profiles");
			DetectorFactory.loadProfile("profiles");
			
        } catch (LangDetectException e)
        {
            e.printStackTrace();
        }
        
        Detector detect;
        try
        {
            detect = DetectorFactory.create();
            detect.append(text);
            return detect.detect();
        } catch (LangDetectException e)
        {
            e.printStackTrace();
        }
        
		return "hhjjjkb";
	}
	
	
	public static Detector getDetector() throws LangDetectException{
	
      DetectorFactory.loadProfile("profiles");
	  Detector detect=null;
      detect = DetectorFactory.create();
           
      return detect;
	}
	/**
	 * 取词根
	 * @param args
	 */
	public static String stemTerm (String term) {
		EnglishStemmer english = new EnglishStemmer();
	
		 english.setCurrent(term);
		 english.stem();
		
		return english.getCurrent();
	}
	
    /**
     * 停用词表
     * @param args
     * @throws IOException 
     */
	public static List<String> stopWordTable(String filename) throws IOException{
		FileReader fileread=new FileReader(filename);
		BufferedReader reader=new BufferedReader(fileread);
		List<String> wordTable=new ArrayList<String>();
		String word=null;
		while((word=reader.readLine()) != null){
			word=word.trim();     //忽略前导空白和尾部空白，中间的并没有滤掉
			wordTable.add(word);
		}
		reader.close();
		return wordTable;
	}
	
	public static void main(String[] args) throws IOException, LangDetectException{
//		 Detector detect=getDetector();
//		String word="μία περίοδος για μία αθλητική ομάδα@el";
//		detect.append(word);
//		String s=detect.detect();
//		System.out.println(s);
//		detect.append("中华人民共和国上发布速度发货速度跟");
		
//		DetectorFactory.loadProfile("profiles");
//		Detector detect=null;
//		String[] words={"μία περίοδος για μία αθλητική ομάδα@el","中华人民共和国上发布速度发货速度跟","R$^wsgdussidhahdajk中国"};
//		for(int i=0;i<words.length;i++){
//			  
//		      detect = DetectorFactory.create();
//		      detect.append(words[i]);
//		      System.out.println(detect.detect());
//		}
//		List<String> list=stopWordTable("D:\\stopword.txt");
//		for(String s : list)
//			System.out.println(s);
//		if(list.contains("a"))
//			System.out.println("++++++");
//		separateWord("AbscEfsFF");
//		System.out.println(stemTerm(""));
//		String s="sd/dfdg/df";
//		System.out.println(s.substring(s.lastIndexOf("/")+1));
//		String ss="http://dg345j/fhdgh/wr#sdf";
//		System.out.println(getLocalNamePath(ss)[0]);
//		System.out.println(getLocalNamePath(ss)[1]);
//		List<String> list=separateWordBySymbol(ss);
//		for(String a : list)
//			System.out.println(a);
		String str="aaaa  df gh      fhgj";
		String words[]=str.split(" +");
		for(int i=0;i<words.length;i++){
			System.out.println(words[i]);
		}
	}
}
