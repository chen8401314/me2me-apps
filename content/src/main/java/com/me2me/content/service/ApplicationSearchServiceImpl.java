package com.me2me.content.service;

import com.plusnet.search.content.ContentRequest;
import com.plusnet.search.content.ContentResponse;
import com.plusnet.search.content.api.ContentQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class ApplicationSearchServiceImpl implements ApplicationSearchService {


    @Autowired
    private ContentQueryService contentQueryService;

    public ContentResponse search(ContentRequest contentRequest) {
        return contentQueryService.query(contentRequest);
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/content-context.xml");
        ApplicationSearchService searchService = ctx.getBean(ApplicationSearchService.class);
        ContentRequest contentRequest = new ContentRequest();
        contentRequest.setScene("蹲坑");

        ContentResponse contentResponse = searchService.search(contentRequest);
        List contents = contentResponse.getContents();
        System.out.println(contents);
    }

}
