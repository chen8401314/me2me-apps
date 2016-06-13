package com.me2me.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/12.
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Value("#{app.logRoot}")
    private String logRoot;

    @Override
    public void set(String key, String value) {
        log.info("set members ... ");
    }
}
