package com.plusnet.autoclassfiy.idf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.plusnet.autoclassfiy.Constant;

public class IDFKeywordServiceImpl implements IDFKeywordService{

	private KeyGenenerator keyGen;
	private Map<String,TFIDFKeyword> keyNameMap;
	private Map<Integer,TFIDFKeyword> keyIndexMap;
	private Integer allDocs=0;
	private Logger log = LoggerFactory.getLogger(IDFKeywordServiceImpl.class);
	
	public IDFKeywordServiceImpl(){
		try {
			InputStream fin = IDFKeywordServiceImpl.class.getResourceAsStream( Constant.KEYWORD_FILE);
			reloadModel(fin);
			fin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 指定模型。
	 * @param modelFile 模型文件，json格式。
	 */
	public IDFKeywordServiceImpl(InputStream modelFile){
		try {
			reloadModel(modelFile);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				modelFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void saveTrainResult(File outFile) {		
		
		System.out.println(keyNameMap.size()+" pos:"+keyGen.getCurKey());
		
		// 修改变更过的关键字
		List<TFIDFKeyword> argList = new ArrayList<>();
		// 插入当前文档号。
		TFIDFKeyword countKeyword = createDocCountKeyword();
		TFIDFKeyword posKeyword = createCurKeyPosKeyword();
		argList.add(countKeyword);
		argList.add(posKeyword);
		
		
		for(TFIDFKeyword keyword:keyNameMap.values()){
			// 计算IDF
			double keycount=(double)keyword.getAppearCount();
			double allDocs =(double)this.allDocs;
			
			double idf =Math.log(allDocs/keycount);
			keyword.setIdf(idf);
			argList.add(keyword);
		}
		String json = JSON.toJSONString(argList,true);
		try {
			FileUtils.write(outFile, json,"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reloadModel(List<TFIDFKeyword> keyList){
		this.keyNameMap=new ConcurrentHashMap<>();
		this.keyIndexMap= new ConcurrentHashMap<>();
		
		for(TFIDFKeyword keyword:keyList){
			keyNameMap.put(keyword.getName(), keyword);
			keyIndexMap.put(keyword.getIndex(), keyword);
		}
		
		// 插入当前文档号。
		TFIDFKeyword countKeyword = keyNameMap.get(Constant.ALL_DOCS_KEY);
		TFIDFKeyword posKeyword = keyNameMap.get(Constant.CUR_POS_KEY);
		if(countKeyword!=null && posKeyword!=null){
			allDocs=countKeyword.getAppearCount();
			keyGen= new KeyGenenerator(posKeyword.getAppearCount());
		}
	}
	/**
	 * 从文件加载idf模型
	 * @param modelFile
	 * @throws IOException 
	 */
	public  void reloadModel(InputStream modelFile) throws IOException {
			log.info("loading idf keyword model.");
			String txt = IOUtils.toString(modelFile, "utf-8");
			List<TFIDFKeyword> keyList = JSON.parseArray(txt, TFIDFKeyword.class);
			reloadModel(keyList);
	}

	/**
	 * @param key
	 * @return
	 */
	 private TFIDFKeyword createDocCountKeyword(){
		TFIDFKeyword keyword= new TFIDFKeyword();
		keyword.setName(Constant.ALL_DOCS_KEY);
		keyword.setAppearCount(allDocs);
		keyword.setIndex(Constant.ALL_DOCS_KEY_ID);  
		return keyword;
	}
		/**
		 * @param key
		 * @return
		 */
	 private TFIDFKeyword createCurKeyPosKeyword(){
		TFIDFKeyword keyword= new TFIDFKeyword();
		keyword.setName(Constant.CUR_POS_KEY);
		keyword.setAppearCount(keyGen.getCurKey());
		keyword.setIndex(Constant.CUR_KEY_POS_ID);  
		return keyword;
	}
	
	/**
	 * 新建一个词,词的初始出现次数为0
	 * @param key
	 * @return
	 */
	 synchronized public TFIDFKeyword createKeyword(String key){
		TFIDFKeyword keyword=null;
		keyword = this.keyNameMap.get(key);
		if(keyword==null){
			keyword= new TFIDFKeyword();
			keyword.setName(key);
			keyword.setAppearCount(0);
			int keyIndexPos= keyGen.getNextKey();
			keyword.setIndex(keyIndexPos);
			keyword.setTf(0);
			keyword.setIdf(0);
			keyword.setTfidf(0);
			keyNameMap.put(key, keyword);
			keyIndexMap.put(keyword.getIndex(), keyword);
		}
		return keyword;
	}
	
	/**
	 * 词语的出现次数+1
	 * @param keyword
	 */
	synchronized public void incKeyAppearCount(TFIDFKeyword keyword){
		keyword.setAppearCount(keyword.getAppearCount()+1);
	}

	@Override
	synchronized public TFIDFKeyword getKeyword(String key) {
		TFIDFKeyword keyword = keyNameMap.get(key);
		if(keyword==null){
			keyword=createKeyword(key);
		}
		return keyword;
	}
	


	@Override
	public  List<TFIDFKeyword> cutWord(String content) {
		List<Word> termList = WordSegmenter.seg(content);
		List<TFIDFKeyword> result=new ArrayList<>();
		for (Word term : termList) {
			TFIDFKeyword keyword =createKeyword(term.getText());
			result.add(keyword);
		}
		return result;
	}
	@Override
	synchronized public void addTrainDoc(String doc) {
		List<TFIDFKeyword> keywordList = this.cutWord(doc);
		for(TFIDFKeyword key:keywordList){
			incKeyAppearCount(key);
		}
		synchronized (this) {
			allDocs++;
		}
	}

	@Override
	public List<TFIDFKeyword> getTFIDFKeywordByDoc(String doc) {
		return getTFIDFKeywordByDoc(doc,Integer.MAX_VALUE,true);
	}

	@Override
	public List<TFIDFKeyword> getTFIDFKeywordByDoc(String doc, int maxKeywordNum, boolean sort) {
		List<TFIDFKeyword> termList = this.cutWord(doc);
		Map<TFIDFKeyword, Integer> groupMap = new HashMap<>();
		int validDocKeyCount = 0;
		
		//按单词统计词频。
		for (TFIDFKeyword term : termList) {		
			if(term.getAppearCount()==0){
				term.setAppearCount(1);
			}
			Integer termCount = groupMap.get(term);
			if (termCount == null) {
				termCount = 0;
			}
			termCount = termCount + 1;
			groupMap.put(term, termCount);
			validDocKeyCount++;
		}
		
		// 计算 TF,IDF
		List<TFIDFKeyword> keywordList = new ArrayList<>();
		for(Map.Entry<TFIDFKeyword,Integer> entry:groupMap.entrySet()){
			
			TFIDFKeyword keyword = entry.getKey();
			if (keyword.getName().equals("运输车")) {
				//System.out.println(keyword);
			}
			keyword.setIdf(getIDFByKey(keyword));
			keyword.setTf(getTF(entry.getValue(), validDocKeyCount));
			keyword.setTfidf(keyword.getTf()*keyword.getIdf());
			
			keywordList.add(keyword);
		}
		if(sort){
			Collections.sort(keywordList,new Comparator<TFIDFKeyword>() {
				@Override
				public int compare(TFIDFKeyword o1, TFIDFKeyword o2) {
					int ret =(int) (o1.getTfidf()-o2.getTfidf());
					return ret;
				}
			});
		}
		if(keywordList.size()>0){
			if(maxKeywordNum < keywordList.size()){
				return keywordList.subList(0, maxKeywordNum);
			}
		}
		return keywordList;
	}
	/**
	 * 计算本词的TF
	 * @param key
	 * @param curKeyCount
	 * @param allKeyCount
	 * @return
	 */
	private double getTF(int curKeyCount,int allKeyCount){
		
		return (double)curKeyCount/(double)allKeyCount;
	}
	/**
	 * 取一个关键字的IDF值。
	 * @param key 关键字
	 * @return
	 */
	private double getIDFByKey(TFIDFKeyword keyword){
		double keycount = (double)keyword.getAppearCount();
		double docCount = (double)allDocs;
		double val =Math.log(docCount/(keycount));
		return val;
	}


}
