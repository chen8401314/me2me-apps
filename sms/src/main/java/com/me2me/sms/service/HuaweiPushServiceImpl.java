package com.me2me.sms.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.me2me.cache.CacheConstant;
import com.me2me.cache.service.CacheService;

/**
 * 华为Push
 * @author pc340
 *
 */
@Slf4j
@Component
public class HuaweiPushServiceImpl implements SpecialPushService {

	private static final String appSecret = "555agl00lr0nilmaf4kv7095l03e80m5";
	private static final String appId = "10434619";
	private static final String tokenUrl = "https://login.vmall.com/oauth2/token";
	private static final String apiUrl = "https://api.push.hicloud.com/pushsend.do";
	
	private static final String TITLE = "米汤";
	
	@Autowired
	private CacheService cacheService;
	
	@Override
	public void payloadByIdExtra(String uid, String message, Map<String, String> extraMaps) {
		//获取accessToken
		String accessToken = this.getAccessToken();
		
		// 目标设备Token
		JSONArray deviceTokens = new JSONArray();
		deviceTokens.add(uid);
		
		JSONObject hps = new JSONObject();// 华为PUSH消息总结构体
		if(null != extraMaps && extraMaps.size() > 0){
			//通知栏消息
			JSONObject body = new JSONObject();
			body.put("title", TITLE);
			body.put("content", message);
			
			JSONObject param = new JSONObject();
			param.put("appPkgName", "com.mao.zx.metome");
			
			JSONObject action = new JSONObject();
			action.put("type", 3);// 类型3为打开APP
			action.put("param", param);
			
			JSONObject msg = new JSONObject();
			msg.put("type", 3);// 3: 通知栏消息
			msg.put("action", action);// 消息点击动作
			msg.put("body", body);// 通知栏消息body内容
			
			JSONArray customize = new JSONArray();
			for(Map.Entry<String, String> entry : extraMaps.entrySet()){
				JSONObject ex = new JSONObject();
				ex.put(entry.getKey(), entry.getValue());
				customize.add(ex);
			}
			
			JSONObject ext = new JSONObject();
			ext.put("biTag", "M2M");
			ext.put("customize", customize);
			
			hps.put("msg", msg);
			hps.put("ext", ext);
		}else{
			//透传消息
			JSONObject msg = new JSONObject();
			msg.put("type", 1);// 1: 透传异步消息
			msg.put("body", message);
			
			hps.put("msg", msg);
		}
		
		JSONObject payload = new JSONObject();
		payload.put("hps", hps);
		
		try {
			String postBody = MessageFormat.format("access_token={0}&nsp_svc={1}&nsp_ts={2}&device_token_list={3}&payload={4}",
					URLEncoder.encode(accessToken, "UTF-8"), 
					URLEncoder.encode("openpush.message.api.send", "UTF-8"),
					URLEncoder.encode(String.valueOf(System.currentTimeMillis() / 1000), "UTF-8"),
					URLEncoder.encode(deviceTokens.toString(), "UTF-8"),
					URLEncoder.encode(payload.toString(), "UTF-8"));

			String postUrl = apiUrl + "?nsp_ctx=" + URLEncoder.encode("{\"ver\":\"1\", \"appId\":\"" + appId + "\"}", "UTF-8");
			this.httpPost(postUrl, postBody, 5000, 5000);
		} catch (Exception e) {
			log.error("发送华为推送消息失败", e);
		}
	}

	/**
	 * 获取accessToken
	 * 防止混乱，加把锁(也许有隐患)
	 * @return
	 */
	private synchronized String getAccessToken(){
		try{
			String accessToken = cacheService.get(CacheConstant.HUAWEI_ACCESSTOKEN_KEY);
			if(StringUtils.isBlank(accessToken)){
				log.info("缓存中accessToken已失效，从华为服务端再次获取");
				String msgBody = MessageFormat.format("grant_type=client_credentials&client_secret={0}&client_id={1}", URLEncoder.encode(appSecret, "UTF-8"), appId);
				String response = this.httpPost(tokenUrl, msgBody, 5000, 5000);
				if(StringUtils.isNotBlank(response)){
					JSONObject obj = JSONObject.parseObject(response);
					accessToken = obj.getString("access_token");
					if(StringUtils.isNotBlank(accessToken)){
						int expires = obj.getIntValue("expires_in") - 5 * 60;//提前个5分钟
						cacheService.setex(CacheConstant.HUAWEI_ACCESSTOKEN_KEY, accessToken, expires);
					}
				}
			}
			return accessToken;
		}catch(Exception e){
			log.error("获取华为推送AccessToken失败", e);
		}
		return null;
	}
	
	private String httpPost(String httpUrl, String data, int connectTimeout, int readTimeout) throws IOException {
		OutputStream outPut = null;
		HttpURLConnection urlConnection = null;
		InputStream in = null;

		try {
			URL url = new URL(httpUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			urlConnection.setConnectTimeout(connectTimeout);
			urlConnection.setReadTimeout(readTimeout);
			urlConnection.connect();

			// POST data
			outPut = urlConnection.getOutputStream();
			outPut.write(data.getBytes("UTF-8"));
			outPut.flush();

			// read response
			if (urlConnection.getResponseCode() < 400) {
				in = urlConnection.getInputStream();
			} else {
				in = urlConnection.getErrorStream();
			}

			List<String> lines = IOUtils.readLines(in, urlConnection.getContentEncoding());
			
			StringBuilder strBuf = new StringBuilder();
			for (String line : lines) {
				strBuf.append(line);
			}
			return strBuf.toString();
		} finally {
			IOUtils.closeQuietly(outPut);
			IOUtils.closeQuietly(in);
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}
}
