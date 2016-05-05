package com.me2me.common.security;

import com.google.common.base.Charsets;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public class SecurityUtils {

    private static Random random = new Random();

    /**
     * MD5 加盐加密
     * @param password
     * @param salt
     * @return
     */
    public static String md5(String password,String salt){
        return new Md5Hash(password,salt).toHex();
    }

    /**
     * 6位安全码
     * @return
     */
    public static String getMask(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<6;i++){
            int temp = random.nextInt(10);
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 生成用户token工具类
     * @return
     */
    public static String getToken(){
        return UUID.randomUUID().toString().replace("-","");
    }




    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    // 计算并获取CheckSum
    public static String getCheckSum(String appId,String appSecret, String nonce, String curTime) {
        return encode("sha1", appId + appSecret + curTime + nonce);
    }

    // 计算并获取md5值
    public static String getMD5(String requestBody) {
        return encode("md5", requestBody);
    }

    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest
                    = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes(Charsets.UTF_8.name()));
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        String value = SecurityUtils.getMD5(System.currentTimeMillis()+"");
        System.out.println(value);
    }

}
