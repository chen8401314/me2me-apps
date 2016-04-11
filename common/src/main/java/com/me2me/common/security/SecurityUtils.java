package com.me2me.common.security;

import org.apache.shiro.crypto.hash.Md5Hash;
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

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String value = getMask();
        // System.out.println(value);
        int len = "69f95dccbb4bdd6370f3a426ecea3979".length();
        // System.out.println(len);
        String s = SecurityUtils.md5("123456","");
        // 9db06bcff9248837f86d1a6bcf41c9e7
        // System.out.println(s);
        // System.out.println(getToken());
        String x = new Md5Hash("111111").toString();
        System.out.println(new Md5Hash(x).toString());
    }
}
