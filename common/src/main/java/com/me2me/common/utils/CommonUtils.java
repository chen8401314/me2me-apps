package com.me2me.common.utils;

import java.util.Random;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/28.
 */
public class CommonUtils {

    private static Random r = new Random();

    public static String getRandom(int len){
        StringBuilder sb = new StringBuilder("8");
        for(int i = 0;i<len;i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        for(int i = 0;i<100;i++) {
            String value = getRandom(10);
            System.out.println(value);
        }
    }
}
