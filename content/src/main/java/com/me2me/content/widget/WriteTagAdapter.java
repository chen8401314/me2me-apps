package com.me2me.content.widget;

import com.me2me.common.web.Response;
import com.me2me.content.dto.WriteTagDto;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/15
 * Time :16:51
 */
@Component
public class WriteTagAdapter {
    public Response execute(WriteTagDto writeTagDto){
        return WriteTagFactory.getInstance(writeTagDto.getType()).writeTag(writeTagDto);
    }
}
