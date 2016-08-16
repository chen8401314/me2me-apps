package com.me2me.im;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/8/16.
 */
public class Main2 {

    public static void main(String[] args) {
        String a = "aaabaaa";
        char lastChar = a.charAt(a.length()-1);
//        a = a.substring(0,a.length()-1);
//        a = a.substring(0,a.length()-1);
//        a = a.substring(0,a.length()-1);
        // 获取最后一个字符
//
        while(true){
            a = a.substring(0,a.length()-1);
            char temp = a.charAt(a.length()-1);
            if(temp!=lastChar){
                break;
            }
        }
        System.out.println(a);
    }
}
