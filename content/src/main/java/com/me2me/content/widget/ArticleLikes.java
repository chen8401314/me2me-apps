package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.LikeDto;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:04
 */
@Component
public class ArticleLikes extends AbstractLikes implements Likes {


    public Response likes(LikeDto likeDto) {
        contentService.createArticleLike(likeDto);
        return null;
    }
}
