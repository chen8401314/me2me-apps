package com.me2me.core.security;

import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/5.
 */
public class ApplicationSecurity {

    static String publicKey;
    static String privateKey;

    static {
        try {
            Map<String, Object> keyMap = RSAUtils.genKeyPair();
            publicKey = RSAUtils.getPublicKey(keyMap);
            privateKey = RSAUtils.getPrivateKey(keyMap);
//            System.err.println("公钥: \n\r" + publicKey);
//            System.err.println("私钥： \n\r" + privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception {
        // test();
        testSign();
    }
    static void test() throws Exception {
        System.err.println("公钥加密——私钥解密");
        String source = "Hello";
        System.out.println("\r加密前文字：\r\n" + source);
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
        System.out.println("加密后文字：\r\n" + new String(encodedData));
        byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);
        String target = new String(decodedData);
        System.out.println("解密后文字: \r\n" + target);
    }

    static void testSign() throws Exception {
        String source = "Hello";
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        // System.out.println("加密后：\r\n" + new String(encodedData));
        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
        String target = new String(decodedData);
        // System.out.println("解密后: \r\n" + target);
        // System.err.println("私钥签名——公钥验证签名");
        String sign = RSAUtils.sign(encodedData, privateKey);
        System.err.println("签名:\r" + sign);
        boolean status = RSAUtils.verify(encodedData, publicKey, sign);
        // System.err.println("验证结果:\r" + status);
    }

}

