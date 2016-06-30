package com.me2me.article.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/21.
 */
public class Main {


    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("http://music.163.com/song?id=287817").maxBodySize(1024*1024*10).timeout(100000).get();
        String value = document.select("div[class='g-mn4']").html();
        System.out.println(value);
    }
}
