package com.me2me.common.utils;

import java.util.Random;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/28.
 */
public class CommonUtils {

    private static Random r = new Random();

    public static String getRandom(String prefix,int len){
        StringBuilder sb = new StringBuilder(prefix);
        for(int i = 0;i<len;i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }
}
