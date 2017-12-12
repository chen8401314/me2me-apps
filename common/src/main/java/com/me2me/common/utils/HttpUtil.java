package com.me2me.common.utils;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: 马秀成
 * Date: 2017/3/9
 */
@Slf4j
public class HttpUtil {
	
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();

    public static String get(String httpUrl) {
        HttpGet httpGet = new HttpGet(httpUrl);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            httpClient = HttpClients.createDefault();
            httpGet.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpGet);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            log.info("get responseContent message:"+responseContent);
        } catch (Exception e) {
        	log.error("get error!", e);
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
        return responseContent;
    }

    public static String post(String httpUrl, String postParams){
        HttpPost httppost = new HttpPost(httpUrl);
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        StringEntity reqEntity = null;
        String responseContent = null;
        try {
        	httpclient = HttpClients.createDefault();
        	reqEntity = new StringEntity(postParams, "UTF-8");
            httppost.setEntity(reqEntity);
            httppost.setHeader("Content-Type", "application/json;charset=UTF-8");
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            log.info("post responseContent message:"+responseContent);
        } catch (Exception e) {
        	log.error("post error!", e);
        } finally {
        	if(null != response){
        		try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(null != httpclient){
        		try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	} 
        } 
        return responseContent;
    }
    
    public static void main(String[] args){
    	HttpPost httppost = new HttpPost("http://test2.me-to-me.com/api/live/shareImgInfo");
    	CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String responseContent = null;
        try {
        	httpclient = HttpClients.createDefault();
        	List<BasicNameValuePair> rp = new ArrayList<BasicNameValuePair>();
        	rp.add(new BasicNameValuePair("is_skip", "ok"));
        	rp.add(new BasicNameValuePair("uid", "446"));
        	rp.add(new BasicNameValuePair("token", "fd4c3445358c4b44866fd045d3bde95d"));
        	rp.add(new BasicNameValuePair("topicId", "6992"));
        	rp.add(new BasicNameValuePair("fid", "256910"));
        	rp.add(new BasicNameValuePair("targetUid", "446"));
    		HttpEntity reqEntity= new UrlEncodedFormEntity(rp, "utf-8");
        	
            httppost.setEntity(reqEntity);
            response = httpclient.execute(httppost);
            HttpEntity respEntity = response.getEntity();
            responseContent = EntityUtils.toString(respEntity, "UTF-8");
            System.out.println("post responseContent message:"+responseContent);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if(null != response){
        		try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(null != httpclient){
        		try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	} 
        } 
    }
}
