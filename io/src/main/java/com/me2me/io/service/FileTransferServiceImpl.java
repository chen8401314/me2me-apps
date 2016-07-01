package com.me2me.io.service;

import com.me2me.common.web.BaseEntity;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.io.dto.QiniuAccessTokenDto;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class FileTransferServiceImpl implements FileTransferService{

    private static final String ACCESS_KEY ="1XwLbO6Bmfeqyj7goM1ewoDAFHKiQOI8HvkvkDV0";

    private static final String SECRET_KEY ="9fmLV9tnplKRITWQV7QOQYANArqCNELd_SXtjwh9";

    private static final String BUCKET = "ifeeling";

    private static int DEFAULT_TIME_OUT = 60000000;


    /**
     * 文件上传
     * @param multipartFile
     */
    public void upload(MultipartFile multipartFile) {

    }

    @Override
    public Response getQiniuAccessToken() {
        Auth auth = Auth.create(ACCESS_KEY,SECRET_KEY);
        String token = auth.uploadToken(BUCKET);
        QiniuAccessTokenDto qiniuAccessTokenDto = new QiniuAccessTokenDto();
        qiniuAccessTokenDto.setToken(token);
        qiniuAccessTokenDto.setExpireTime(60*1000*10);
        return Response.success(ResponseStatus.GET_QINIU_TOKEN_SUCCESS.status,ResponseStatus.GET_QINIU_TOKEN_SUCCESS.message,qiniuAccessTokenDto);
    }

    public void upload(byte[] data, String key){
        //上传到七牛后保存的文件名
        Auth auth = Auth.create(ACCESS_KEY,SECRET_KEY);
        String token = auth.uploadToken(BUCKET);
        UploadManager up = new UploadManager();
        try {
            up.put(data,key,token);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    public QiniuFile download(String domain,String key) throws IOException {
        String resourceUrl = domain +"/" + key;
        Connection.Response response = Jsoup.connect(resourceUrl).timeout(DEFAULT_TIME_OUT).ignoreContentType(true).execute();
        String fileName = resourceUrl.substring(resourceUrl.lastIndexOf("/"));
        QiniuFile qiniuFile = new QiniuFile();
        qiniuFile.setFileName(fileName);
        qiniuFile.setStreams(response.bodyAsBytes());
        return qiniuFile;

    }

    @Data
    public class QiniuFile implements BaseEntity{

        private String fileName;

        private byte[] streams;
    }

}
