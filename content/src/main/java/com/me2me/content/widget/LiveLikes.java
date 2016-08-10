package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.LikeDto;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:41
 */
@Component
public class LiveLikes extends AbstractLikes implements Likes {

    @Override
    public Response likes(LikeDto likeDto) {
        //创建直播点赞，弹幕

        return super.likes(likeDto);
    }
}
