package com.me2me.io.service;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseWapx;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface FileTransferService {

    /**
     * 文件上传接口
     * @param multipartFile
     */
    void upload(MultipartFile multipartFile);

    /**
     * 获取七牛访问token
     * @return
     */
    Response getQiniuAccessToken();


    String upload(byte[] data, String key);


    byte[] download(String domain,String key) throws IOException;

    String getUserInfo(String code) throws Exception;

    boolean IosWapxActivate(String callbackUrl);

}
