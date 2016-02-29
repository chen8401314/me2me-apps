package com.me2me.io.service;

import org.springframework.web.multipart.MultipartFile;

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

}
