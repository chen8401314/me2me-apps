package com.me2me.im.dto;

import com.me2me.common.web.BaseEntity;
import com.me2me.im.model.GroupMember;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Data
public class ShowGroupMemberDto implements BaseEntity {

    private List<GroupMember> result = new ArrayList<GroupMember>();



}
