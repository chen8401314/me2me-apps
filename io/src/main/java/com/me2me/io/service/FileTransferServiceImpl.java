package com.me2me.io.service;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.io.dto.QiniuAccessTokenDto;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class FileTransferServiceImpl implements FileTransferService{

    private static final String ACCESS_KEY ="1XwLbO6Bmfeqyj7goM1ewoDAFHKiQOI8HvkvkDV0";

    private static final String SECRET_KEY ="9fmLV9tnplKRITWQV7QOQYANArqCNELd_SXtjwh9";

    private static final String BUCKET = "http://7xqn7o.com2.z0.glb.qiniucdn.com/";


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
}
