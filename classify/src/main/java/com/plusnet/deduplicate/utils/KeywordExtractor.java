package com.plusnet.deduplicate.utils;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
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
public class KeywordExtractor {
	private MongoClient mongoClient;
	private DBCollection collection;
	private File outputDir;
	private ExecutorService pool= Executors.newFixedThreadPool(4);
	private GroupCounter gc= new GroupCounter();
	
	public  KeywordExtractor(){
		try {
			mongoClient = new MongoClient("192.168.89.103", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB db = mongoClient.getDB("crawlerd");
		collection = db.getCollection("crawlerData");
	}
	public void extractDataByChannelId(int channelId){
		Iterator<DBObject> docs =collection.find(new BasicDBObject("channelId", channelId)).sort(new BasicDBObject("updateTime",-1)).limit(10000).iterator();
		
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
					List<Word> words = WordSegmenter.seg(txt);
					for(Word term:words){
						gc.countUp(term.getText());
					}
				}
			});
		}
	}
	
	public static void main(String[] args) {
		KeywordExtractor ke = new KeywordExtractor();
		int[] channels = new int[]{178,
				181,
				471,
				4988,
				5344,
				5356,
				5360,
				5377,
				5867,
				5871,
				5920};
		//ke.extractDataByChannelId(channels[0]);
		for(int channel:channels){
			ke.extractDataByChannelId(channel);
		}
		try {
			ke.pool.shutdown();
			ke.pool.awaitTermination(999, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ke.gc.getSortedKeyCounts());
	}
}
