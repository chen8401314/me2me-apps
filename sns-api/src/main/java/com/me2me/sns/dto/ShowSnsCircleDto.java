package com.me2me.sns.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :16:50
 */
@Data
public class ShowSnsCircleDto implements BaseEntity{

    private int members;

    private int coreCircleMembers;

    private int inCircleMembers;

    private int outCircleMembers;

    private List<SnsCircleElement> circleElements = Lists.newArrayList();

    public SnsCircleElement createElement(){
        return  new SnsCircleElement();
    }

    public static class SnsCircleElement extends SnsCircleDto implements BaseEntity{


    }
}
