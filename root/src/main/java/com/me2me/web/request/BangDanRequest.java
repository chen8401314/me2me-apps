package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Data;

/**
 * Created by pc329 on 2017/3/17.
 */
@Data
public class BangDanRequest extends Request {

    private long sinceId;

    // 0 找组织，1找谁
    private int listType;

    // 榜单ID
    private int listId;

}
