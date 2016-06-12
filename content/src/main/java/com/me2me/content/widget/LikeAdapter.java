package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.LikeDto;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:35
 */
@Component
public class LikeAdapter {

    public Response execute(LikeDto likeDto){
        return LikesFactory.getInstance(likeDto.getType()).likes(likeDto);
    }

}
