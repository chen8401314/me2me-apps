package com.me2me.article.service;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/21.
 */
public class OschinaBlogPageProcesser implements PageProcessor {
    private Site site = Site.me().setDomain("news.china.com");
//    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        List<String> links = page.getHtml().links().regex("http://news\\.china\\.com/domestic/\\d+/\\d+/\\d+.html").all();
        System.out.println(links.size());
        page.addTargetRequests(links);
        String title = page.getHtml().xpath("//div[@id='chan_newsBlk']/h1/text()").toString();
        System.out.println(title);

//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name")==null){
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='chan_newsBlk']/h1/text()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new OschinaBlogPageProcesser()).addUrl("http://news.china.com/zh_cn/domestic/index.html").thread(5).run();
    }
}
