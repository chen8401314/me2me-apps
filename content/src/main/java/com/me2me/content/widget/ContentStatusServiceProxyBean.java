package com.me2me.content.widget;

import com.plusnet.search.content.api.ContentStatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/15.
 */
@Component
public class ContentStatusServiceProxyBean extends HttpInvokerProxyFactoryBean {

    @Value("#{app.http_content_status_invoker}")
    private String serverUrl;

    @PostConstruct
    void init() {
        this.setServiceUrl(serverUrl);
        this.setServiceInterface(ContentStatService.class);
    }

    public ContentStatService getTarget(){
        return (ContentStatService) this.getObject();
    }

}
