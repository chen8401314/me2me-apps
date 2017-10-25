package com.me2me.mgmt.task.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.me2me.mgmt.dao.LocalJdbcDao;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

@Component
public class TransferTask {
	
	private static final Logger logger = LoggerFactory.getLogger(TransferTask.class);
	
	@Autowired
	private LocalJdbcDao localJdbcDao;

	private static final String ACCESS_KEY ="1XwLbO6Bmfeqyj7goM1ewoDAFHKiQOI8HvkvkDV0";
    private static final String SECRET_KEY ="9fmLV9tnplKRITWQV7QOQYANArqCNELd_SXtjwh9";
    private static final String BUCKET_VIDEO = "m2m-video";
    
    private Auth auth;
    private UploadManager uploadManager;
    
    private void init(){
    	auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    	uploadManager = new UploadManager();
    }
    
//    @Scheduled(cron="0 6 18 * * ?")
    public void doTask(){
    	logger.info("任务开始");
    	long s = System.currentTimeMillis();
    	try{
    		this.init();//初始化七牛相关配置
    		Map<String, String> allMap = this.getALL();
    		int total = allMap.size();
    		logger.info("本次需转换的有{}个视频", total);
    		int n = 0;
    		for(Map.Entry<String, String> entry : allMap.entrySet()){
    			n++;
    			this.exec(Long.valueOf(entry.getKey()), entry.getValue());
    			if(n%100 == 0){
    				logger.info("转换了[{}]，还剩[{}]", n, total-n);
    			}
    		}
    		logger.info("转换了[{}]，还剩[{}]", n, total-n);
    	}catch(Exception ex){
    		logger.error("任务失败", ex);
    	}
    	long e = System.currentTimeMillis();
    	logger.info("任务结束，共耗时[{}]秒", (e-s)/1000);
    }
    
    private void exec(long id, String vedioPath){
    	//先下载到本地
    	String fileName = vedioPath.substring(vedioPath.lastIndexOf("/")+1);
    	String localFilePath = "D://test/vedio/"+fileName;
    	if(!this.download(vedioPath, localFilePath)){//下载失败
    		logger.info("[{}]下载失败", vedioPath);
    		return;
    	}
    	
    	//上传七牛
    	if(!this.upload7Niu(localFilePath, fileName)){
    		logger.info("[{}]上传失败", localFilePath);
    		return;
    	}
    	
    	//更改状态
    	this.updateStatus(id);
    }
    
    private void updateStatus(long id){
    	String sql = "update zcl_transfer set status=1 where id="+id;
    	localJdbcDao.executeSql(sql);
    }
    
    private boolean upload7Niu(String localPath, String fileName){
    	File file = new File(localPath);
    	if(!file.exists()){
    		logger.info("本地文件[{}]不存在", localPath);
    		return false;
    	}
    	
        try {
        	String token = auth.uploadToken(BUCKET_VIDEO);
        	uploadManager.put(file,"v/"+fileName,token);
        	return true;
        } catch (QiniuException e) {
        	logger.error("上传七牛失败", e);
        	return false;
        }
    }
    
    private boolean download(String vedioPath, String localPath){
    	CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            // 执行请求
            HttpGet httpGet = new HttpGet(vedioPath);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            File file = new File(localPath);
            if(file.exists()){
            	file.delete();
            }
            FileOutputStream fileout = new FileOutputStream(file);
            byte[] buffer=new byte[1024];  
            int ch = 0;  
            while ((ch = is.read(buffer)) != -1) {  
                fileout.write(buffer,0,ch);  
            } 
            is.close();  
            fileout.flush();  
            fileout.close();
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private Map<String, String> getALL(){
    	String sql = "select id,fragment from zcl_transfer where status=0 order by id";
    	List<Map<String, Object>> list = localJdbcDao.queryEvery(sql);
    	Map<String, String> result = new HashMap<String, String>();
    	if(null != list && list.size() > 0){
    		for(Map<String, Object> m : list){
    			result.put(String.valueOf(m.get("id")), (String)m.get("fragment"));
    		}
    	}
    	return result;
    }
}
