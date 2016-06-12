package com.me2me.content.widget;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.LikeDto;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:35
 */
@Component
public class LikeAdapter implements InitializingBean {

    private Likes target;

    @Setter
    private int type;

    public Response execute(LikeDto likeDto){
        try {
            afterPropertiesSet();
            return target.likes(likeDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status,ResponseStatus.ILLEGAL_REQUEST.message);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.target = LikesFactory.getInstance(type);
    }
}
