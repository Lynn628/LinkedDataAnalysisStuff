package com.cse.utils.dbpedia.backdata;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.cybozu.labs.langdetect.LangDetectException;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

public class Mysql {
	  public static  String url ;
	    public static  final String name = "com.mysql.jdbc.Driver";
	    public static final String user = "root";
//	    public static final String user = "linerjing";
	    public static final String password = "123456";

	    public Connection conn = null;
	    //public PreparedStatement pst = null;

	    /**
	     * �������ݿ�
	     * @param
	     */
	    public Mysql(String url) {
	        try {
	            Class.forName(name);//ָ����������
	            conn = DriverManager.getConnection(url, user, password);//��ȡ����
	            //pst = conn.prepareStatement(sql);//׼��ִ�����

	            if (conn != null){
	                System.out.println("MySql connection is successful");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * �ر����ݿ�
	     */
	    public void close() {
	        try {
	            this.conn.close();
	            //this.pst.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    /**
	     * 插入关系表
	     * @param relation
	     * @param url
	     */
       public static void insertLink(Map<Integer,Map<Integer,Double>> relation, Mysql mysql) {
    	   try {
			Statement statement=(Statement) mysql.conn.createStatement();
			String start="INSERT INTO link (srcId,destId,linkCount) VALUES ";
			
			int sub=0;
			int obj=0;
			Map<Integer,Double> map=null;
			StringBuffer buffer=new StringBuffer("");
			buffer.append(start);
			int sum=0;
			for(Entry<Integer, Map<Integer, Double>> entry : relation.entrySet()){
				sub=entry.getKey();
				map=entry.getValue();
				for(Entry<Integer,Double> e : map.entrySet()){
					obj=e.getKey();
					
					buffer.append("(");buffer.append(sub);buffer.append(",");buffer.append(obj);
					buffer.append(",");buffer.append(e.getValue());buffer.append("),");
					sum++;
					if(sum==200000){
						
						statement.executeUpdate(buffer.substring(0,buffer.length()-1));
						sum=0;
						buffer=new StringBuffer("");
						buffer.append(start);
					}
					
				
				}
				 
			}
			String s=buffer.substring(0,buffer.length()-1);
			if(s != null && !s.equals("") && !s.equals(start.substring(0,start.length()-1))){
				statement.executeUpdate(s);
			}
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }
       
       /**
        * 拼接字符串
        * @param document
        * @return
        */
       public static String getString(Map<String,Double> document){
           StringBuilder builder = new StringBuilder("");
           for (Map.Entry<String,Double> entry : document.entrySet()){
               builder.append(entry.getKey());builder.append(",");builder.append(entry.getValue());
               builder.append(";");
           }
           builder.substring(0,builder.length()-1);
           return  builder.toString();
       }
       
       
       /**
        * 插入vdoc表
        * @param stepvdoc
        * @param url
        */
       public static void insertVertex(Map<Integer,Map<String,Double>> stepvdoc,Mysql mysql){
    	 
    	   try {
			Statement statement=(Statement) mysql.conn.createStatement();
			Map<String,Double> map=null;
			int sub=0;
			String s=null;
			int sum=0;
			int insertCount = 0;
			String start="INSERT INTO vertex (vertexId,vDoc) VALUES ";
			StringBuffer buffer=new StringBuffer("");
			buffer.append(start);
			for(Entry<Integer,Map<String,Double>> entry : stepvdoc.entrySet()){
				sub=entry.getKey();
				map=entry.getValue();
				s=getString(map);
				
				buffer.append("(");buffer.append(sub);buffer.append(", ");buffer.append("'");buffer.append(s);
				buffer.append("' ),");
				sum++;
				if(sum==50000){
					insertCount = statement.executeUpdate(buffer.substring(0,buffer.length()-1));
					if(insertCount >0){
						System.out.println(insertCount+"  records are inserted successfully");
					}
					sum=0;
					buffer=new StringBuffer("");
					buffer.append(start);
				}
				
			}
			String sql=buffer.substring(0,buffer.length()-1);
			if(sql != null && !sql.equals("") && !sql.equals(start.substring(0,start.length()-1))){
				insertCount = statement.executeUpdate(sql);
				if(insertCount >0){
					System.out.println(insertCount+"  records are inserted successfully");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }

       
       public static void insertCoding(Map<String, Integer> coding,Mysql mysql){
    	   
    	   try {
			Statement statement=(Statement) mysql.conn.createStatement();
			String start="INSERT INTO uritoid (vertexId,uri) VALUES ";
			int sum=0;
			StringBuffer buffer=new StringBuffer("");
			buffer.append(start);
			for(Entry<String,Integer> entry : coding.entrySet()){
				buffer.append("(");buffer.append(entry.getValue());buffer.append(", ");buffer.append("'");buffer.append(entry.getKey());
				buffer.append("' ),");
				System.out.println(entry.getKey());
				sum++;
				if(sum==1000000){
					statement.executeUpdate(buffer.substring(0,buffer.length()-1));
					sum=0;
					buffer=new StringBuffer("");
					buffer.append(start);
				}
			}
			String sql=buffer.substring(0,buffer.length()-1);
			if(sql != null && !sql.equals("") && !sql.equals(start.substring(0,start.length()-1))){
				statement.executeUpdate(sql);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }
       
       
       public static Map<String, Integer> getCoding(Mysql mysql){
    	   Map<String, Integer> coding=new HashMap<String,Integer>();
    	   try {
    		     
			Statement statement=(Statement) mysql.conn.createStatement();
			String sql="SELECT * from uritoid";
			ResultSet rs=(ResultSet) statement.executeQuery(sql);
			while(rs.next()){
				coding.put(rs.getString("uri"),rs.getInt("vertexId"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	   return coding;
       }
       
       
       public static Map<Integer,Set<Integer>> getRelation(Mysql mysql){
    	   Map<Integer,Set<Integer>> relation=new HashMap<Integer,Set<Integer>>();
    	   Set<Integer> subset=null;
    	   Set<Integer> objset=null;
    	   Statement statement;
    	   int sub=0;
    	   int obj=0;
		try {
			statement = (Statement) mysql.conn.createStatement();
			String sql="SELECT srcId,destId from link";
			ResultSet rs=(ResultSet) statement.executeQuery(sql);
			while(rs.next()){
				sub=rs.getInt("srcId");
				obj=rs.getInt("destId");
				if(!relation.containsKey(sub)){
					subset=new HashSet<Integer>();
					subset.add(obj);
					relation.put(sub,subset);
				}
				else{
					subset=relation.get(sub);
					subset.add(obj);
				}
				if(!relation.containsKey(obj)){
					objset=new HashSet<Integer>();
					objset.add(sub);
					relation.put(obj,objset);
				}
				else{
					objset=relation.get(obj);
					objset.add(sub);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return relation;
       }
       
       
       
       
       public static void main(String[] args) throws LangDetectException, IOException{
    	   
    	   NewDBpedia dbpedia=new NewDBpedia("jdbc:virtuoso://223.3.69.83:1111/charset=UTF-8/GBK/UTF-16/log_enable=2", "http://dbpedia2015.org",
					"dba", "dba");
//    	   Set<String> classes=dbpedia.getDatasetInfo().getAllClassWithSparql();
//			Set<String> propertySet=dbpedia.getAllProperty();
//			Judge judge=new Judge();
//			
//			Map<String,Integer> codingUri=dbpedia.coding(classes, propertySet, judge);
//    		String url="jdbc:mysql://223.3.69.83:3306/dbpedia";
//	        Mysql mysql=new Mysql(url);
//	        insertCoding(codingUri,mysql);
    	   Map<Integer,Map<Integer,Double>> relation=new HashMap<Integer,Map<Integer,Double>>();
    	   Map<Integer,Map<String,Double>>  vdoc=new HashMap<Integer,Map<String,Double>>();
    	  
    	   dbpedia.getRelationVDoc(relation, vdoc, dbpedia);
			Map<Integer,Map<String,Double>> stepvdoc=dbpedia.stepVdoc(relation,vdoc);
			
			System.out.println("vDoc size is :-----"+stepvdoc.size());
			
    		String url="jdbc:mysql://223.3.69.83:3306/dbpedia";
	        Mysql mysql=new Mysql(url);
//   	   insertLink(relation,mysql);
//	        Map<Integer,Map<String,Double>> stepvdoc=NewDBpedia.getVDoc(dbpedia, mysql);
    	   insertVertex(stepvdoc,mysql);
    	   
       }
}
