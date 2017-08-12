package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
public class PublishContentRequest extends Request {

    @Getter
    @Setter
    private String feeling;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private String imageUrls;
    
    @Getter
    @Setter
    private String imageWidths;

    @Getter
    @Setter
    private String imageHeights;
    
    @Getter
    @Setter
    private int contentType;

    @Getter
    @Setter
    private long forwardCid;

    @Getter
    @Setter
    private int type;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private int rights;

    @Getter
    @Setter
    private String forwardUrl;

    @Getter
    @Setter
    private String forwardTitle;

    @Getter
    @Setter
    private String coverImage;

    @Getter
    @Setter
    private long targetTopicId;

}
