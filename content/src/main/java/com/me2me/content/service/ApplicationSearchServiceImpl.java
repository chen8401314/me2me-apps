package com.me2me.content.service;

import com.plusnet.forecast.domain.GPS;
import com.plusnet.search.content.ContentRequest;
import com.plusnet.search.content.ContentResponse;
import com.plusnet.search.content.RecommendRequest;
import com.plusnet.search.content.RecommendResponse;
import com.plusnet.search.content.api.ContentQueryService;
import com.plusnet.search.content.api.ContentRecommendService;
import com.plusnet.search.content.domain.ContentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class ApplicationSearchServiceImpl implements ApplicationSearchService {


    @Autowired
    private ContentRecommendService contentRecommendService;

    public RecommendResponse recommend(RecommendRequest recommendRequest) {
        System.out.println(contentRecommendService);
        return contentRecommendService.recommend(recommendRequest);
    }


    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/content-context.xml");
        ContentRecommendService searchService = ctx.getBean(ContentRecommendService.class);
        System.out.println(ctx.getBean(ContentRecommendService.class));
        RecommendRequest recommendRequest = new RecommendRequest();
        recommendRequest.setUserId("100310");
        int count = 0;
        while(true) {
            if(count>100){
                break;
            }
            RecommendResponse recommendResponse = searchService.recommend(recommendRequest);

            List<ContentTO> s = recommendResponse.getContents();
            for (ContentTO t : s) {
                System.out.println(t.getTitle());
            }
            System.out.println("#########################################");
            System.out.println("#########################################");
            System.out.println("#########################################");
            System.out.println("#########################################");

            System.out.println("count : " + count);
            count ++;
        }
    }

}
