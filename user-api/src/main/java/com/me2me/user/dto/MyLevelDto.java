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

    private List<node> levels = Lists.newArrayList();
    //头像
    private  String avatar ;
    //当前米汤币
    private  int availabeCoin ;
    //下一等级所需米汤币
    private  int nextLevelCoin ;

    private  InnerLevel preLevel;

    private  InnerLevel currentLevel ;

    private  InnerLevel nextLevel;


    public InnerLevel createInnerLevel(){
        return new InnerLevel();
    }

    @Data
    public  static class  InnerLevel implements  BaseEntity{

        private  int level ;

        private  String  name ;
    }


    @Data
    public static  class  node {

        private  long  id ;
        //权限名称
        private  String name ;
        //是否拥有该权限 1 表示 拥有  0 表示 未拥有
        private  int owner ;

    }




}
