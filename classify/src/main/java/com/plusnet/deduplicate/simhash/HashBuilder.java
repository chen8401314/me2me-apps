package com.plusnet.deduplicate.simhash;

import java.io.File;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;
import org.apdplat.word.lucene.ChineseWordAnalyzer;
import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.plusnet.autoclassfiy.idf.IDFKeywordService;
import com.plusnet.autoclassfiy.idf.IDFKeywordServiceImpl;
import com.plusnet.autoclassfiy.idf.TFIDFKeyword;
/**
 * 准确度99%。 分两步，第一步，生成simHash; 第二步检查重复。
 * @author jiwei.zhang
 * @date 2016年5月20日
 */

public class HashBuilder {
	private ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES,new LinkedBlockingQueue<Runnable>());
	private static final String INDEX_PATH = "D:/index";
	private static final IDFKeywordService TF_IDF=new IDFKeywordServiceImpl();;
	private static final int PAGES=Integer.MAX_VALUE;
	private MongoClient mongoClient;
	private DBCollection collection;
	private IndexSearcher searcher;
	
	
	public HashBuilder() {
			try {
				mongoClient=new MongoClient("192.168.89.103", 27017);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			DB db = mongoClient.getDB("crawlerd");
			collection = db.getCollection("crawlerData");
	}
	
	public static void main(String[] args) {
		try {
			HashBuilder hb = new HashBuilder();
			//hb.buildIndex();
			
			//hb.deDuplicate();
			
			/*// 判断文本是否重复
			String content= StringUtils.toString(HashBuilder.class.getResourceAsStream("simple.txt"),"utf-8");
			content= Jsoup.parse(content).text();
			boolean rst = hb.isDuplicateContent(content);
			System.out.println(rst);*/
			
			
			
			boolean rst =hb.isDuplicateContent(hb.getDBContent("2145138"));
			System.out.println("在数据中存在："+rst);
			
			
		/*	
		    String id="2149442-2856466";
			String[] ids = id.split("-");
		 	String s1=new SimHash(hb.getKeywords(hb.getDBContent(ids[0]))).getStrSimHash();
			String s2=new SimHash(hb.getKeywords(hb.getDBContent(ids[1]))).getStrSimHash();
			int distance = SimHash.getHammingDistance(s1, s2);
			System.out.println(s1);
			System.out.println(s2);
			System.out.println("a-b hamming:"+distance);
			*/
		} catch (Exception e) {
			e.printStackTrace();  
		}
	}
	public void initSearcher(){
		try{
			File file = new File(INDEX_PATH);
			Directory dir = FSDirectory.open(file);
	        IndexReader reader = DirectoryReader.open(dir);
	        searcher = new IndexSearcher(reader);
	        Document doc =reader.document(1);
	        System.out.println(String.format("IndexTest---------id:%s,title:%s,simHash:%s,p1:%s,p2:%s,p3:%s,p4:%s"
	        		, doc.get("id")
	        		, doc.get("title")
	        		, doc.get("simHash")
	        		, doc.get("part1")
	        		, doc.get("part2")
	        		, doc.get("part3")
	        		, doc.get("part4")
	        		
	        		));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 判断文本在数据库中是否重复
	 * @param content
	 * @throws Exception
	 */
	public boolean isDuplicateContent(String content) throws Exception {
		initSearcher();
		boolean ret = false;
		
		long begin= System.currentTimeMillis();
		List<TFIDFKeyword> keyList =TF_IDF.getTFIDFKeywordByDoc(content, 100, true,new HashMap<>());
		SimHash hash = new SimHash(keyList);
		int[] hashPart =hash.toFourPart();
		NumericRangeQuery<Integer> query1 = NumericRangeQuery.newIntRange("part1", hashPart[0], hashPart[0], true, true);
		NumericRangeQuery<Integer> query2 = NumericRangeQuery.newIntRange("part2", hashPart[1], hashPart[1], true, true);
		NumericRangeQuery<Integer> query3 = NumericRangeQuery.newIntRange("part3", hashPart[2], hashPart[2], true, true);
		NumericRangeQuery<Integer> query4 = NumericRangeQuery.newIntRange("part4", hashPart[3], hashPart[3], true, true);

        BooleanQuery bq =new BooleanQuery();
        bq.add(query4,Occur.MUST);
//        bq.add(query2,Occur.SHOULD);
//        bq.add(query3,Occur.SHOULD);
//        bq.add(query4,Occur.SHOULD);
       
        TopDocs topDocs = searcher.search(bq,10);
         System.out.println("命中总数：" + topDocs.totalHits);
        ScoreDoc[] hits = topDocs.scoreDocs;
        // 应该与topDocs.totalHits相同
        for (ScoreDoc scoreDoc : hits) {
            Document document = searcher.doc(scoreDoc.doc);
            String docId = document.get("id");
            String keywords=document.get("keywords");
            long docHash = document.getField("simHash").numericValue().longValue();
            int distance = SimHash.getHammingDistance(hash.getSimHashValue(), docHash);
            if(distance<=1){
            	// 计算相似度：
            	int same=0;
            	for(TFIDFKeyword key:keyList){
            		if(keywords.contains(key.getName())){
            			same++;
            		}
            	}
            	float score = (float)same/(float)keyList.size();
            	if(score>0.8){
            		String msg = String.format("find same: id:%s, distance:%d,title:%s",docId,distance,document.get("title"));
		        	System.out.println(msg);
		        	ret=true;
            	}
	        }
        }
        long timeUsed = System.currentTimeMillis()-begin;
        System.out.println("used time(ms) :"+timeUsed);
        return ret;
	}
	/**
	 * 取id对象的内容。
	 * @param id
	 * @return
	 */
	private String getDBContent(String id){
		DBObject obj =  collection.findOne(new BasicDBObject("_id",Long.parseLong(id)));
		String title= (String) obj.get("title");
		Map rst = (Map) obj.get("rst");
		String content = (String) rst.get("content");
		content = Jsoup.parse(content).text();
		return title+" "+content;
	}

	/**
	 * 去重
	 * @throws Exception 
	 */
	public  void deDuplicate() throws Exception{
		pool = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES,new LinkedBlockingQueue<Runnable>());
		File file = new File(INDEX_PATH);
		Directory dir = FSDirectory.open(file);
        IndexReader reader = DirectoryReader.open(dir);
        final IndexSearcher is = new IndexSearcher(reader);

		
		
		final int batchLen = 100;
		long allRecords = collection.count();
		long allPages = allRecords / batchLen + 1;
		allPages=Math.min(PAGES, allPages);
		final StringBuffer result = new StringBuffer();
		final Vector<String> dpList = new Vector<>();
		for (int i = 0; i < allPages; i++) {
			final int curPos = i*batchLen;
			System.out.println("load data set:" + curPos);
			pool.execute(new Runnable() {
				@Override
				public void run() {
					DBCursor cursor = collection.find().skip(curPos).limit(batchLen);

					while (cursor.hasNext()) {
						try {
							
							DBObject obj = cursor.next();
							String id = obj.get("_id").toString();
							if(dpList.contains(id)){
								continue;		// 重复的数据不再对比 。
							}
							String url = obj.get("url").toString();
							Map rst = (Map) obj.get("rst");
							String content = (String) rst.get("content");
							String title=(String)rst.get("title");
							content =  title+" "+Jsoup.parse(content).text();
							List<TFIDFKeyword> keyList =TF_IDF.getTFIDFKeywordByDoc(content, 100, true,new HashMap<>());
							SimHash hash = new SimHash(keyList);
							int[] hashPart =hash.toFourPart();
							
							NumericRangeQuery<Integer> query1 = NumericRangeQuery.newIntRange("part1", hashPart[0], hashPart[0], true, true);
							NumericRangeQuery<Integer> query2 = NumericRangeQuery.newIntRange("part2", hashPart[1], hashPart[1], true, true);
							NumericRangeQuery<Integer> query3 = NumericRangeQuery.newIntRange("part3", hashPart[2], hashPart[2], true, true);
							NumericRangeQuery<Integer> query4 = NumericRangeQuery.newIntRange("part4", hashPart[3], hashPart[3], true, true);
							BooleanQuery bq =new BooleanQuery();
					        bq.add(query1,Occur.SHOULD);
					        bq.add(query2,Occur.SHOULD);
					        bq.add(query3,Occur.SHOULD);
					        bq.add(query4,Occur.SHOULD);
					       
					        TopDocs topDocs = is.search(bq,10);
					        // System.out.println("命中总数：" + topDocs.totalHits);
					        ScoreDoc[] hits = topDocs.scoreDocs;
					        // 应该与topDocs.totalHits相同
					        for (ScoreDoc scoreDoc : hits) {
					            Document document = is.doc(scoreDoc.doc);
					            String docId = document.get("id");
					            if(docId.equals(id)){
					            	continue; // 跳过与本身对比
					            }
					            String keywords=document.get("keywords");
					            long docHash = document.getField("simHash").numericValue().longValue();
					            int distance = SimHash.getHammingDistance(hash.getSimHashValue(), docHash);
					            if(distance<=1){
					            	// 计算相似度：
					            	int same=0;
					            	for(TFIDFKeyword key:keyList){
					            		if(keywords.contains(key.getName())){
					            			same++;
					            		}
					            	}
					            	float score = (float)same/(float)keyList.size();
					            	if(score>0.8){
					            		String msg = String.format("find same: %s-%s, distance:%d,title1:%s,title2:%s",id,docId,distance,title,document.get("title"));
							        	//System.out.println(msg);
							        	result.append(msg+"\n");
							        	dpList.add(docId);		// 重复的数据不再拿出来对比。
					            	}
						        }
					        }
						     
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		pool.shutdown();
		while (!pool.isTerminated()) {
			Thread.sleep(2000);
			System.out.println("finished:"+(int) ((float) pool.getCompletedTaskCount() / (float) allPages * 100) + "%");
		}
		FileWriter fos = new FileWriter(new File("D:/duplicate.txt"));
		fos.write(result.toString());
		fos.flush();
		fos.close();
	}
	/**
	 * 建立索引
	 * @throws Exception
	 */
	public  void buildIndex() throws Exception {
				
		File file = new File(INDEX_PATH);
		FSDirectory fsd = FSDirectory.open(file);
		Analyzer az = new ChineseWordAnalyzer();
		IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST,az);
		final IndexWriter writer = new IndexWriter(fsd, conf);
		
		final int batchLen = 100;
		long allRecords = collection.count();
		long allPages = allRecords / batchLen + 1;
		allPages=Math.min(PAGES, allPages);
		for (int i = 0; i < allPages; i++) {
			final int curPos = i*batchLen;
			System.out.println("load data set:" + curPos);
			pool.execute(new Runnable() {
				@Override
				public void run() {
					DBCursor cursor = collection.find().skip(curPos).limit(batchLen);

					while (cursor.hasNext()) {
						try {
							DBObject obj = cursor.next();
							String id = obj.get("_id").toString();
							String url = obj.get("url").toString();
							Map rst = (Map) obj.get("rst");
							String content = (String) rst.get("content");
							String title=(String) rst.get("title");
							content =  title+" "+Jsoup.parse(content).text();
							List<TFIDFKeyword> keyList = TF_IDF.getTFIDFKeywordByDoc(content, 100, true,new HashMap<>());
							SimHash simHash=new SimHash(keyList);
							int[] hashPart = simHash.toFourPart();
							

							Document doc = new Document();

							doc.add(new StringField("id", id, Store.YES));
							doc.add(new StringField("url", url, Store.YES));
							doc.add(new StringField("title", title, Store.YES));
							doc.add(new IntField("part1", hashPart[0], Store.YES));
							doc.add(new IntField("part2", hashPart[1], Store.YES));
							doc.add(new IntField("part3", hashPart[2], Store.YES));
							doc.add(new IntField("part4", hashPart[3], Store.YES));
							doc.add(new LongField("simHash", simHash.getSimHashValue(), Store.YES));
							doc.add(new StringField("keywords",keyList.toString(),Store.YES));
							writer.addDocument(doc);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		pool.shutdown();
		while (!pool.isTerminated()) {
			Thread.sleep(2000);
			System.out.println("finished:"+(int) ((float) pool.getCompletedTaskCount() / (float) allPages * 100) + "%");
		}
		
		writer.close();

	}
}
