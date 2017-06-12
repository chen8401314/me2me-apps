package com.me2me.common.sms;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * 网易云信短信服务
 * Created by pc62 on 2016/2/23.
 */
public class YunXinSms {

    public static final Logger logger = LoggerFactory.getLogger(YunXinSms.class);

    private static final String VERIFYCODE_URL = "https://api.netease.im/sms/verifycode.action";
    private static final String SENDCODE_URL="https://api.netease.im/sms/sendcode.action";
    private static final String SENDTEMPLATE_URL="https://api.netease.im/sms/sendtemplate.action";
    private static final String SENDTEMPLATE_CODE="3032367";

    /**
     * 设置请求头
     * @return 请求头列表
     */
    private static List<Header> setUpHeader(){
        //开发者平台分配的appkey
        String appKey = "c377d4ae5654a97f0407dc027861ba8b";

        String appSecret = "b78d3334512b";
        String nonce = "1234567890";
        String curTime = String.valueOf(System.currentTimeMillis()/1000);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret,nonce,curTime);
        List<Header> headerList =new ArrayList<Header>();
        headerList.add(new BasicHeader("AppKey", appKey));
        headerList.add(new BasicHeader("Nonce",nonce));
        headerList.add(new BasicHeader("CurTime",curTime));
        headerList.add(new BasicHeader("CheckSum",checkSum));
        headerList.add(new BasicHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8"));
        return headerList;
    }

    /**
     * 发送短信验证码
     * @param mobileNo 接收方手机号
     * @return 返回执行结果，true表示成功，false表示失败
     * @throws UnsupportedEncodingException
     */
    public static Boolean sendSms(String mobileNo)  {
        logger.info("{} send verify code",mobileNo);
        Map<String,String> param = new HashMap<String, String>();
        param.put("mobile",mobileNo);
        String resultCode = executeURLRequest(SENDCODE_URL,param);
        return processResultCode(resultCode);
    }

    public static Boolean sendSms2(String nickName ,String awardName ,String mobile ,String OperateMobile)  {
        Map<String,String> param = new HashMap<String, String>();
        param.put("templateid",SENDTEMPLATE_CODE);
        List list = Lists.newArrayList();
        list.add(nickName);
        list.add(mobile);
        list.add(awardName);
        param.put("params", JSONArray.toJSONString(list));
        param.put("mobiles","["+mobile+","+OperateMobile+"]");
        String resultCode = executeURLRequest(SENDTEMPLATE_URL,param);
        return processResultCode(resultCode);
    }

    //七天活动报名成功通知短信
    public static Boolean sendSms3(String mobile)  {
        Map<String,String> param = new HashMap<String, String>();
        param.put("templateid",SENDTEMPLATE_CODE);
        List list = Lists.newArrayList();
        //报名成功通知内容 存入list里
//        list.add(mobile);
//        param.put("params", JSONArray.toJSONString(list));
        param.put("mobiles","["+mobile+"]");
        String resultCode = executeURLRequest(SENDTEMPLATE_URL,param);
        return processResultCode(resultCode);
    }

    //七天活动审核通过通知短信
    public static Boolean sendSms4(List mobileList)  {
        Map<String,String> param = new HashMap<String, String>();
        param.put("templateid",SENDTEMPLATE_CODE);
        List list = Lists.newArrayList();
        //报名成功通知内容 存入list里
//        list.add(mobile);
//        param.put("params", JSONArray.toJSONString(list));
        list.add("测试了");
        list.add(13132132);
        list.add("活动审核通过");
        param.put("params", JSONArray.toJSONString(list));
        param.put("mobiles",mobileList.toString());
        String resultCode = executeURLRequest(SENDTEMPLATE_URL,param);
        return processResultCode(resultCode);
    }

    /**
     * 校验验证码
     * @param mobileNo 手机号
     * @param code 要校验的验证码
     * @return 返回执行结果，true表示成功，false表示失败
     * @throws UnsupportedEncodingException
     */
    public static Boolean verify(String mobileNo,String code){
        logger.info("execute verify method");
        Map<String,String> param = new HashMap<String, String>();
        param.put("mobile",mobileNo);
        param.put("code",code);
        String resultCode = executeURLRequest(VERIFYCODE_URL, param);
        return processResultCode(resultCode);

    }

    /**
     * 执行URL请求
     * @param url URL地址
     * @param param 请求参数
     * @return 请求的状态码
     * @throws UnsupportedEncodingException
     */
    private static String executeURLRequest(String url, Map<String,String> param)  {
        logger.info("execute yunxin url {}",url);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultHeaders(setUpHeader())
                .build();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }


        String resultCode = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try{
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
                resultCode = jsonObject.getString("code");
                logger.info("yunxin sms resultCode is {}",resultCode);
            }finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultCode;
        }
    }


    private static Boolean processResultCode(String resultCode){
        if("200".equals(resultCode)){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    public static void main(String[] args)  {
        String[] mobileList = new String[]{"18616336525","15618552389","18221006454"};
//"18621848116","15821376667","18629601696","13818695402","13601932249","18616336525"};
//        for (String s : mobileList) {
//            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//            System.out.println(sendSms(s));
//            Thread.sleep(2000L);
//        }
        sendSms("17721431009");
    }
}
