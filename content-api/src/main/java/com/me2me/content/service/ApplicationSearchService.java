package com.me2me.content.service;
import com.plusnet.search.content.RecommendRequest;
import com.plusnet.search.content.RecommendResponse;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
public interface ApplicationSearchService {

    RecommendResponse recommend(RecommendRequest recommendRequest);

}
