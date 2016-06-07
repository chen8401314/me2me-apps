package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.LikeDto;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/7
 * Time :13:35
 */
public class LikeAdapter {

    @Setter
    private Likes target;

    public LikeAdapter(Likes likes) {
        this.target = likes;
    }

    public Response execute(LikeDto likeDto){
        return target.likes(likeDto);
    }
}
