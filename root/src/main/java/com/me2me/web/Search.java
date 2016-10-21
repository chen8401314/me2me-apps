package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.kafka.model.ClientLog;
import com.me2me.kafka.service.KafkaService;
import com.me2me.search.service.SearchService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/11
 * Time :18:09
 */
@Controller
@RequestMapping(value = "/api/search")
public class Search extends BaseController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private KafkaService kafkaService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response search(SearchRequest searchRequest, HttpServletRequest req){
        //埋点
        kafkaService.saveClientLog(searchRequest,req.getHeader("User-Agent"),Specification.ClientLogAction.HOME_SEARCH);

        return searchService.search(searchRequest.getKeyword(),searchRequest.getPage(),searchRequest.getPageSize(),searchRequest.getUid(),searchRequest.getIsSearchFans());
    }
    @ResponseBody
    @RequestMapping(value = "/assistant",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response assistant(SearchRequest searchRequest){
        return searchService.assistant(searchRequest.getKeyword());
    }

}
