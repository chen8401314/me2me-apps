//package com.me2me.article.service;
//
//import com.google.common.collect.Sets;
//import us.codecraft.webmagic.Page;
//import us.codecraft.webmagic.Site;
//import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.pipeline.ConsolePipeline;
//import us.codecraft.webmagic.processor.PageProcessor;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * 上海拙心网络科技有限公司出品
// * Author: 赵朋扬
// * Date: 2016/6/21.
// */
//public class OschinaBlogPageProcesser implements PageProcessor {
//    private Site site = Site.me().setDomain("music.163.com");
//    @Override
//    public void process(Page page) {
//
//        String links = page.getHtml().links().get();
//
//        page.addTargetRequests(links);
//        String lyric = page.getHtml().getDocument().getElementById("lyric-content").text();
//        System.out.println(lyric);
//        // page.putField("content", page.getHtml().$("#text110").toString());
//        //page.putField("tags",page.getHtml().xpath("//div[@class='BlogTags']/a/text()").all());
//    }
//
//    @Override
//    public Site getSite() {
//        return site;
//    }
//
//    public static void main(String[] args) {
//        Spider.create(new OschinaBlogPageProcesser()).addUrl("http://music.163.com/#/song?id=287817")
//                .addPipeline(new ConsolePipeline()).run();
//    }
//}
