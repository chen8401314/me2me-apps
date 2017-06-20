package com.me2me.user.dto;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/6/12 0012.
 */
@Data
public class UserPermissionDto implements BaseEntity {

    private List<UserLevelDto> levels = Lists.newArrayList();
    //等级定义
    private String levelDefinition;
    @Data
    public static class UserLevelDto implements BaseEntity{

        private int level;

        private String icon;

        private String name;

        private String lvName;

        private int needCoins;

        private int[] permissions;

    }


    public static void main(String[] args) {

        UserPermissionDto userPermissionDto = new UserPermissionDto();

        for(int i = 1;i<=9;i++){
            UserLevelDto userLevelDto = new UserLevelDto();
            userLevelDto.setPermissions(new int[]{5});
            userLevelDto.setLevel(i);
            userLevelDto.setIcon(i+".jpg");
            userLevelDto.setNeedCoins(1000*i);
            userLevelDto.setName("name:"+i);
            userPermissionDto.getLevels().add(userLevelDto);
        }


        String value = JSON.toJSONString(userPermissionDto);
        System.out.println(value);

        UserPermissionDto dto = JSON.parseObject(value,UserPermissionDto.class);
        System.out.println(dto.getLevels());


    }


}
