package com.me2me.sms.channel;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/8/2.
 */
@Component
public class MessageClient {

    @Value("#{app.ACCOUNT_SID}")
    private String accountId;
    @Value("#{app.AUTH_TOKEN}")
    private String authToken;
    @Value("#{app.APP_ID}")
    private String appId;
    @Value("#{app.REST_URL}")
    private String restUrl;

    @Getter
    private CCPRestSmsSDK ccpRestSmsSDK;

    @Getter
    private CCPRestSDK ccpRestSDK;

    @PostConstruct
    public void init(){
        String restUrl = this.restUrl;
        String serverUrl = restUrl.split(":")[0];
        String port = restUrl.split(":")[1];
        ccpRestSDK = new CCPRestSDK();
        ccpRestSDK.init(serverUrl,port);
        ccpRestSDK.setAccount(accountId,authToken);
        ccpRestSDK.setAppId(appId);

        ccpRestSmsSDK = new CCPRestSmsSDK();
        ccpRestSmsSDK.init(serverUrl,port);
        ccpRestSmsSDK.setAccount(accountId,authToken);
        ccpRestSmsSDK.setAppId(appId);
    }

}
