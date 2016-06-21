package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.LikeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/21
 * Time :16:43
 */
@Component
@Slf4j
public class ActivityLikes extends AbstractLikes implements Likes{

    @Override
    public Response likes(LikeDto likeDto) {
        return super.likes(likeDto);
    }
}
