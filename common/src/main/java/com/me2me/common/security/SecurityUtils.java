package com.me2me.common.security;

import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.Random;

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
            int a = random.nextInt(10);
            sb.append(a);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String value = getMask();
        System.out.println(value);
        int len = "69f95dccbb4bdd6370f3a426ecea3979".length();
        System.out.println(len);

        String s = SecurityUtils.md5("123456","820610");
        System.out.println(s);
    }
}
