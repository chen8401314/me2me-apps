package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc62 on 2016/3/25.
 */
@Data
public class PasteTagDto implements BaseEntity {

    private String tag; //标签内容

    private Long uid;  //被贴标签的用户Id

    private Long fuid;  //贴标签的用户Id

}
