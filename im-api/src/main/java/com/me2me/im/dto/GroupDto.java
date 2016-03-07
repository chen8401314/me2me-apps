package com.me2me.im.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Data
public class GroupDto implements BaseEntity {

    private String groupName;

    private long uid;

    private String groupMember;

}
