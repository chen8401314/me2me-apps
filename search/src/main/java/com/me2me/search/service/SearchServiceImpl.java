package com.me2me.search.service;

import com.me2me.common.web.Response;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private UserService userService;

    @Override
    public Response search(String keyword,int page,int pageSize,long uid) {
        return userService.search(keyword,page,pageSize,uid);
    }

    @Override
    public Response assistant(String keyword) {
        return userService.assistant(keyword);
    }


}
