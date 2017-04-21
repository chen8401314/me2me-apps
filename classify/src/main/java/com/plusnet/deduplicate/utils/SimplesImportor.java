package com.plusnet.deduplicate.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * 从mongodb中提取关键词
 * @author zhangjiwei
 *
 */
public class SimplesImportor {
	private static MongoClient mongoClient;
	private static DBCollection collection;
	private static ExecutorService pool= Executors.newFixedThreadPool(4);
	
	private static DB mlDb;
	private static DBCollection mlCollection;
	public  static void initDB(){
		try {
			mongoClient = new MongoClient("192.168.89.103", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB db = mongoClient.getDB("crawlerd");
		collection = db.getCollection("crawlerData");
		mlDb = mongoClient.getDB("ml_db");
		mlCollection= mlDb.getCollection("ml_simples");
	}
	public static void extractDataByChannelId(final int classifyId,final String classifyName,int channelId){
		Iterator<DBObject> docs =collection.find(new BasicDBObject("channelId", channelId))
				.sort(new BasicDBObject("updateTime",-1))
				.limit(2000).iterator();
		
		while(docs.hasNext()){
			final DBObject doc =docs.next();
			pool.execute(new Runnable() {
				@Override
				public void run() {
					String title = (String)((Map)doc.get("rst")).get("title");
					String content = (String)((Map)doc.get("rst")).get("content");
					String txt = Jsoup.parse(content).text();
					if(!"".equals(txt)){
						System.out.println(String.format("parse content:%s",title));
					}else{
						return;
					}
					
					
					BasicDBObject obj = new BasicDBObject();
					obj.put("_id", doc.get("_id"));
					obj.put("classifyId", classifyId);
					obj.put("classifyName", classifyName);
					obj.put("content", txt);
					mlCollection.insert(obj);
					/*
					List<Term> words =HanLP.segment(txt);
					for(Term term:words){
						if (term.nature.startsWith("w") || term.nature.equals(Nature.nx) || term.nature.startsWith("v")
								|| term.nature.startsWith("p")) {
							continue;
						}
					}*/
					
				}
			});
		}
	}
	
	public static void main(String[] args) {
		initDB();
		try {
			InputStream in = SimplesImportor.class.getResourceAsStream("/conf/分类对应的频道.csv");
			 CSVParser parser =CSVFormat.EXCEL.parse(new InputStreamReader(in,"GBK"));
			 Iterator<CSVRecord> it = parser.iterator();
			 while(it.hasNext()){
				 CSVRecord record =it.next(); 
				 System.out.println(record.toString());
				 int classsifyId = Integer.parseInt(record.get(0));
				 String classsifyName = record.get(1);
				 String channels = record.get(2);
				 if(!StringUtils.isEmpty(channels)){
					 String[] channelArr= channels.split(",");
					 for(int i=0;i<5 && i<channelArr.length;i++){
						 String channel=channelArr[i];
						extractDataByChannelId(classsifyId,classsifyName,Integer.parseInt(channel));
					}
				 }
			 }
			 in.close();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		try {
			pool.shutdown();
			pool.awaitTermination(999, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//System.out.println(gc.getSortedKeyCounts());
	}
}
