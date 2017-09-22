package com.me2me.io.service;

import com.alibaba.fastjson.JSON;
import com.me2me.common.Constant;
import com.me2me.common.utils.HttpUtil;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.io.dto.QiniuAccessTokenDto;
import com.me2me.io.dto.ShowRecContentDTO;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.annotation.PostConstruct;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
@Slf4j
public class FileTransferServiceImpl implements FileTransferService{

    private static final String ACCESS_KEY ="1XwLbO6Bmfeqyj7goM1ewoDAFHKiQOI8HvkvkDV0";

    private static final String SECRET_KEY ="9fmLV9tnplKRITWQV7QOQYANArqCNELd_SXtjwh9";

    private static final String BUCKET = "ifeeling";

    private static int DEFAULT_TIME_OUT = 60000000;

    public static final String APPID = "wx06b8675378eb1a62";

    public static final String SECRET = "59114162f6c8f043cb3e9f204a78bede";

    @Value("#{app.meappRecUrl}")
    private String meappRecUrl;
    
    private Auth auth;
    private UploadManager uploadManager;
    private BucketManager bucketManager;
    
    
    @PostConstruct
    public void init(){
    	auth = Auth.create(ACCESS_KEY,SECRET_KEY);
    	uploadManager= new UploadManager();
    	bucketManager = new BucketManager(auth);
    }

    /**
     * 文件上传
     * @param multipartFile
     */
    public void upload(MultipartFile multipartFile) {
    	 
    }

    @Override
    public Response getQiniuAccessToken() {
        String token = auth.uploadToken(BUCKET);
        QiniuAccessTokenDto qiniuAccessTokenDto = new QiniuAccessTokenDto();
        qiniuAccessTokenDto.setToken(token);
        qiniuAccessTokenDto.setExpireTime(60*1000*10);
        return Response.success(ResponseStatus.GET_QINIU_TOKEN_SUCCESS.status,ResponseStatus.GET_QINIU_TOKEN_SUCCESS.message,qiniuAccessTokenDto);
    }

    public String upload(byte[] data, String key){
        //上传到七牛后保存的文件名
        String token = auth.uploadToken(BUCKET);
        try {
        	uploadManager.put(data,key,token);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public byte[] download(String domain,String key) throws IOException {
        String resourceUrl = domain + "/" + key;
        Connection.Response response = Jsoup.connect(resourceUrl).timeout(DEFAULT_TIME_OUT).ignoreContentType(true).execute();
        return response.bodyAsBytes();
    }

    @Override
    public String getUserInfo(String code) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid="+APPID+"&secret="+SECRET+"&" +
                "code="+code+"&grant_type=authorization_code"
        );
        HttpResponse res = null;
        try {
            res = httpclient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = res.getEntity();

        JSONObject jsonObject = getJSONObject(entity);
        if(!jsonObject.isNull("errcode")) {
            System.out.println(jsonObject.get("errcode"));
            return jsonObject.toString();
        }
        System.out.println(jsonObject.toString());
        System.out.println("openid:"+(String) jsonObject.get("openid"));
        log.info("get user openid and token"+jsonObject.toString());
        //取得返回的openid和token去请求用户信息
        String openId = (String) jsonObject.get("openid");
        String access_token = (String) jsonObject.get("access_token");
        HttpGet httpget1 = new HttpGet( "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token="+access_token+"&" +
                "openid="+openId);
        HttpResponse res1 = httpclient.execute(httpget1);
        HttpEntity entity1 = res1.getEntity();
        JSONObject userProfile = getJSONObject(entity1);
        userProfile.put("access_token",access_token);

        String headimgurl = (String) userProfile.get("headimgurl");
        if(!StringUtils.isEmpty(headimgurl)) {
            //获取到七牛key返回给前台
            String QnKey = getQNImageKey(headimgurl);
            userProfile.put("headimgurl",QnKey);
        }else{
            userProfile.put("headimgurl",Constant.DEFAULT_AVATAR);
        }
        log.info("get user profile"+userProfile.toString());
        return userProfile.toString();
    }

    @Override
    public boolean IosWapxActivate(String callbackUrl) {
        String json = HttpUtil.get(callbackUrl);
        JSONObject jsonObject = null;
        Boolean b = null;
        try {
            jsonObject = new JSONObject(json);
            b = (Boolean) jsonObject.get("success");
        } catch (JSONException e) {
            log.error(e.getMessage());
        }
        return b;
    }

    @Override
    public int DaodaoActivate(String callbackUrl) {
        String json = HttpUtil.get(callbackUrl);
        JSONObject jsonObject = null;
        //默认激活失败-1
        int code = -1;
        try {
            jsonObject = new JSONObject(json);
            code = jsonObject.getInt("code");
        } catch (JSONException e) {
            log.error(e.getMessage());
        }
        return code;
    }


    //获取上传七牛后的图片key
    private String getQNImageKey(String headimgurl) throws Exception {
        URL url = new URL(headimgurl);
        //http://cdn.me-to-me.com/key七牛地址
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        // 输入流
        con.setRequestMethod("GET");
        con.setConnectTimeout(5 * 1000);
        InputStream is = con.getInputStream();
        byte[] btImg = readInputStream(is);//得到图片的二进制数据
        String key = UUID.randomUUID().toString();
        upload(btImg,key);
        return key;
    }

    public static JSONObject getJSONObject(HttpEntity entity) throws UnsupportedEncodingException, IllegalStateException, IOException, JSONException {
        StringBuilder entityStringBuilder=new StringBuilder();
        BufferedReader reader=new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
        String line=null;
        while ((line=reader.readLine()) != null) {
            entityStringBuilder.append(line);
        }
        JSONObject json = new JSONObject(entityStringBuilder.toString());
        return json;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
    
    @Override
    public ShowRecContentDTO getRecContents(String uid, String token, String version, String emotion){
    	com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
    	obj.put("uid", uid);
    	obj.put("token", token);
    	obj.put("version", version);
    	if(null != emotion){
    		obj.put("emotion", emotion);
    	}
    	
    	ShowRecContentDTO dto = null;
    	try{
    		String reqJson = obj.toJSONString();
        	String respJson = HttpUtil.post(meappRecUrl, reqJson);
        	dto = com.alibaba.fastjson.JSON.parseObject(respJson, ShowRecContentDTO.class);
    	}catch(Exception e){
    		log.error("get rec article error", e);
    	}
    	return dto;
    }

	@Override
	public void deleteQiniuResource(String key) {
		try {
			bucketManager.delete(BUCKET, key);
		} catch (QiniuException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getWxJsApiTicket(String appId,String appSecret){
		try{
			// 拿token
			String api = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId
					+ "&secret=" + appSecret;
			String ret = Request.Get(api).execute().returnContent().asString();
			log.info("wx token api:{}", ret);
			String ACCESS_TOKEN = JSON.parseObject(ret).getString("access_token");
			// 拿ticket
			String api2 = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + ACCESS_TOKEN
					+ "&type=jsapi";
			String ret2 = Request.Get(api2).execute().returnContent().asString();
			log.info("wx ticket api:{}", ret2);
			String ticket = JSON.parseObject(ret2).getString("ticket");
			return ticket;
		}catch(Exception e){
			log.error("获取H5 jsapi_ticket失败:",e);
			return "";
		}
	}
}
