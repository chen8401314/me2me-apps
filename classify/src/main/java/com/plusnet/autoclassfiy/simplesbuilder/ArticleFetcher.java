package com.plusnet.autoclassfiy.simplesbuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.plusnet.autoclassfiy.idf.IDFKeywordService;
/**
 * 文章获取器。
 * @author zhangjiwei
 * @date 2016年9月8日
 *
 */
public class ArticleFetcher {
	private MongoClient mongoClient;
	private DBCollection collection;
	private File outputDir;
	private ExecutorService pool= Executors.newFixedThreadPool(4);
	
	private IDFKeywordService keywordService;
	
	public ArticleFetcher(){

		try {
			mongoClient = new MongoClient("192.168.89.103", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB db = mongoClient.getDB("crawlerd");
		collection = db.getCollection("crawlerData");
	}
	
	/**
	 * 将指定分类的文件拷贝到目标目录下。
	 * @param targetDir
	 * @param value
	 */
	private  void fetchDocsIntoDir(String typeName, String channelIds) {
		File targetDir = new File(outputDir,typeName);
		if(!targetDir.exists()){
			targetDir.mkdirs();
		}
		
		String[] channels= channelIds.split(",");
		for(String channelId:channels){
			Iterator<DBObject> docs =collection.find(new BasicDBObject("channelId", Integer.parseInt(channelId))).sort(new BasicDBObject("updateTime",-1)).limit(10000).iterator();
			System.out.println("load data set of code:"+typeName+"-"+channelId);
			while(docs.hasNext()){
				DBObject doc =docs.next();
				int id = (int) doc.get("_id");
				String content = (String)((Map)doc.get("rst")).get("content");
				String txt = Jsoup.parse(content).text();
				keywordService.addTrainDoc(txt);
				File idFile = new File(targetDir,id+"");
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(idFile);
					IOUtils.write(txt,out,"utf-8");
					out.flush();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						if(out!=null)	out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
	/**
	 * 启动抓取数据
	 * @param targetDir 样本保存目录
	 */
	public void start(File outputDir){
		this.outputDir=outputDir;
		if(!this.outputDir.exists()){
			this.outputDir.mkdirs();
		}
		
		
		Map<String,String> typeDic = new HashMap<>();
		typeDic.put("美食1", "178,5867");
		typeDic.put("居家2", "182,5853");
		typeDic.put("健康养生3", "186,476,5373");
		typeDic.put("时尚4", "5936,5933");
		typeDic.put("科技5", "179,5921,5951");
		typeDic.put("汽车6", "5970,5923,5922,5886");
		typeDic.put("政治军事7", "5857");
		typeDic.put("旅行8", "17417,17416,41,39");
		typeDic.put("娱乐9", "5021,467,243,240");
		typeDic.put("文化10", "12786,5350,523");
		int types = typeDic.size();
//		final CountDownLatch cd = new CountDownLatch(types);
		
		for(final Map.Entry<String, String> entry:typeDic.entrySet()){
			fetchDocsIntoDir(entry.getKey(),entry.getValue());
//			pool.execute(new Runnable() {
//				public void run() {
//					fetchDocsIntoDir(entry.getKey(),entry.getValue());
//					cd.countDown();
//				}
//			});
		}
		
//		while(cd.getCount()>0){
//			int precent =(int)((float)(types-cd.getCount())/(float)types*100);
//			System.out.println("finished:"+precent);
//			
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		System.out.println("save");
		System.out.println("finished");
		pool.shutdown();
	}

}
