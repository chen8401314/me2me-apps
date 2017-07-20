package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/6/13 0013.
 */
@Data
public class MyLevelDto implements BaseEntity {

    private PermissionDescriptionDto permissions;
    //头像
    private  String avatar ;
    //当前米汤币
    private  int availableCoin ;
    //下一等级所需米汤币
    private  int nextLevelCoin ;
    //人民币
    private double priceRMB;

    private  InnerLevel preLevel;

    private  InnerLevel currentLevel ;

    private  InnerLevel nextLevel;

    private  long stealTopicId;

    private  long randomTopicId;


    public InnerLevel createInnerLevel(){
        return new InnerLevel();
    }

    @Data
    public  static class  InnerLevel implements  BaseEntity{

        private  int level ;

        private  String  name ;
    }
}
