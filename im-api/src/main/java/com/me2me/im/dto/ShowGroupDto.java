package com.me2me.im.dto;

import com.me2me.common.web.BaseEntity;
import com.me2me.im.model.Group;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Data
public class ShowGroupDto implements BaseEntity {

    private List<Group> result = new ArrayList<Group>();
}
