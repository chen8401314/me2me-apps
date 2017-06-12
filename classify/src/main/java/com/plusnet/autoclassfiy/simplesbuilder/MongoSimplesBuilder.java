package com.plusnet.autoclassfiy.simplesbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
public class MongoSimplesBuilder extends AbsSVMSimplesBuilder {
	private MongoClient mongoClient;
	private DBCollection collection;
	private File targetSimpleFile = new File("D:/opt/svm/svm-simples-all");
	public MongoSimplesBuilder(){
		try {
			mongoClient = new MongoClient("192.168.89.103", 27017);
			DB db = mongoClient.getDB("ml_db");
			collection = db.getCollection("ml_simples");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将指定分类的文件拷贝到目标目录下。
	 * @param targetDir
	 * @param value
	 * @throws IOException 
	 */
	public void buildSimples() throws IOException {
		final FileWriter fw = new FileWriter(targetSimpleFile);
		Iterator<DBObject> docs =collection.find().iterator();
		long all = collection.count();
		int i=0;
		while(docs.hasNext()){
			i++;
			DBObject doc =docs.next();
			int id = (int) doc.get("_id");
			String content = (String)doc.get("content");
			Integer classifyId = (Integer) doc.get("classifyId");
			String txt = buildLine(content, classifyId);
			if(!StringUtils.isEmpty(txt)){
				fw.write(txt+"\n");
			}
			if(i%1000==0){
				System.out.println(String.format("%d of %d",i,all));
			}
		}
		fw.flush();
		fw.close();
		System.out.println("finished.");
	}
	public static void main(String[] args) {
		MongoSimplesBuilder msb = new MongoSimplesBuilder();
		try {
			msb.buildSimples();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
