package com.me2me.content.service;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
public interface ContentService {

    Response square(int sinceId);

    Response publish(ContentDto contentDto);

}
