package com.me2me.user.dto;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/6/15 0015.
 */
@Data
public class PermissionDescriptionDto implements BaseEntity {

    private  int level;

    private List<PermissionNodeDto> nodes = Lists.newArrayList();




    @Data
    public static class PermissionNodeDto implements BaseEntity,Comparable<PermissionNodeDto>{

        private  Integer code ;

        private String name;

        private Integer seconds;

        private Integer num;

        private int status;

        private  Integer isShow;

        private Integer openLevel;

        private  Integer sort;

        private  Integer minOpenLevel;


        @Override
        public int compareTo(PermissionNodeDto o) {

            if(minOpenLevel!=null && o.getOpenLevel()!=null && minOpenLevel>o.getOpenLevel()){
                return 1;
            }else if(minOpenLevel==o.getMinOpenLevel()){
                return 0;
            }else{
                return -1;
            }
        }
    }

    public static PermissionNodeDto create(PermissionDescriptionDto root,String name,Integer num,Integer seconds,int status , Integer isShow ,Integer code ){
        PermissionNodeDto node = new PermissionNodeDto();
        node.setName(name);
        node.setCode(code);
        node.setNum(num);
        node.setSeconds(seconds);
        node.setStatus(status);
        node.setIsShow(isShow);
        root.getNodes().add(node);
        return node;
    }

    public static void main(String[] args) {
        PermissionDescriptionDto permissionDescriptionDto1 = new PermissionDescriptionDto();
        permissionDescriptionDto1.setLevel(1);
        create(permissionDescriptionDto1,"米汤专属表情包",null,null,0 ,1,1);
        create(permissionDescriptionDto1,"王国专属文字排版",null,null,0,1,2);
        create(permissionDescriptionDto1,"超清晰原图展示",null,null,0,1,3);
        create(permissionDescriptionDto1,"5分钟超长语音",null,5,0,1,4);
        create(permissionDescriptionDto1,"发送视频",null,null,0,1,5);
        create(permissionDescriptionDto1,"私下单独聊聊",null,null,0,1,6);
        create(permissionDescriptionDto1,"10个聚合王国",null,null,0,1,7);
        create(permissionDescriptionDto1,"可偷王国数目",4,null,1,0,8);
        create(permissionDescriptionDto1,"单次偷取上限",1,null,1,0,9);
        create(permissionDescriptionDto1,"个人每日获取金币上限",30,null,1,0,10);

        PermissionDescriptionDto permissionDescriptionDto2 = new PermissionDescriptionDto();
        permissionDescriptionDto2.setLevel(2);
        create(permissionDescriptionDto2,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto2,"王国专属文字排版",null,null,0,1,2);
        create(permissionDescriptionDto2,"超清晰原图展示",null,null,0,1,3);
        create(permissionDescriptionDto2,"5分钟超长语音",null,5,0,1,4);
        create(permissionDescriptionDto2,"发送视频",null,null,0,1,5);
        create(permissionDescriptionDto2,"私下单独聊聊",null,null,0,1,6);
        create(permissionDescriptionDto2,"10个聚合王国",null,null,0,1,7);
        create(permissionDescriptionDto2,"可偷王国数目",5,null,1,0,8);
        create(permissionDescriptionDto2,"单次偷取上限",2,null,1,0,9);
        create(permissionDescriptionDto2,"个人每日获取金币上限",40,null,1,0,10);

        PermissionDescriptionDto permissionDescriptionDto3 = new PermissionDescriptionDto();
        permissionDescriptionDto3.setLevel(3);
        create(permissionDescriptionDto3,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto3,"王国专属文字排版",null,null,1,1,2);
        create(permissionDescriptionDto3,"超清晰原图展示",null,null,0,1,3);
        create(permissionDescriptionDto3,"5分钟超长语音",null,5,0,1,4);
        create(permissionDescriptionDto3,"发送视频",null,null,0,1,5);
        create(permissionDescriptionDto3,"私下单独聊聊",null,null,0,1,6);
        create(permissionDescriptionDto3,"10个聚合王国",null,null,0,1,7);
        create(permissionDescriptionDto3,"可偷王国数目",6,null,1,0,8);
        create(permissionDescriptionDto3,"单次偷取上限",3,null,1,0,9);
        create(permissionDescriptionDto3,"个人每日获取金币上限",50,null,1,0,10);

        PermissionDescriptionDto permissionDescriptionDto4 = new PermissionDescriptionDto();
        permissionDescriptionDto4.setLevel(4);
        create(permissionDescriptionDto4,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto4,"王国专属文字排版",null,null,1,1,2);
        create(permissionDescriptionDto4,"超清晰原图展示",null,null,0,1,3);
        create(permissionDescriptionDto4,"5分钟超长语音",null,5,0,1,4);
        create(permissionDescriptionDto4,"发送视频",null,null,1,1,5);
        create(permissionDescriptionDto4,"私下单独聊聊",null,null,1,1,6);
        create(permissionDescriptionDto4,"10个聚合王国",null,null,0,1,7);
        create(permissionDescriptionDto4,"可偷王国数目",7,null,1,0,8);
        create(permissionDescriptionDto4,"单次偷取上限",5,null,1,0,9);
        create(permissionDescriptionDto4,"个人每日获取金币上限",80,null,1,0,10);


        PermissionDescriptionDto permissionDescriptionDto5 = new PermissionDescriptionDto();
        permissionDescriptionDto5.setLevel(5);
        create(permissionDescriptionDto5,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto5,"王国专属文字排版",null,null,1,1,2);
        create(permissionDescriptionDto5,"超清晰原图展示",null,null,0,1,3);
        create(permissionDescriptionDto5,"5分钟超长语音",null,120,0,1,4);
        create(permissionDescriptionDto5,"发送视频",null,null,1,1,5);
        create(permissionDescriptionDto5,"私下单独聊聊",null,null,1,1,6);
        create(permissionDescriptionDto5,"10个聚合王国",3,null,0,1,7);
        create(permissionDescriptionDto5,"可偷王国数目",8,null,1,0,8);
        create(permissionDescriptionDto5,"单次偷取上限",5,null,1,0,9);
        create(permissionDescriptionDto5,"个人每日获取金币上限",110,null,1,0,10);

        PermissionDescriptionDto permissionDescriptionDto6 = new PermissionDescriptionDto();
        permissionDescriptionDto6.setLevel(6);
        create(permissionDescriptionDto6,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto6,"王国专属文字排版",null,null,1,1,2);
        create(permissionDescriptionDto6,"超清晰原图展示",null,null,1,1,3);
        create(permissionDescriptionDto6,"5分钟超长语音",null,120,0,1,4);
        create(permissionDescriptionDto6,"发送视频",null,null,1,1,5);
        create(permissionDescriptionDto6,"私下单独聊聊",null,null,1,1,6);
        create(permissionDescriptionDto6,"10个聚合王国",5,null,0,1,7);
        create(permissionDescriptionDto6,"可偷王国数目",9,null,1,0,8);
        create(permissionDescriptionDto6,"单次偷取上限",5,null,1,0,9);
        create(permissionDescriptionDto6,"个人每日获取金币上限",140,null,1,0,10);

        PermissionDescriptionDto permissionDescriptionDto7 = new PermissionDescriptionDto();
        permissionDescriptionDto7.setLevel(7);
        create(permissionDescriptionDto7,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto7,"王国专属文字排版",null,null,1,1,2);
        create(permissionDescriptionDto7,"超清晰原图展示",null,null,1,1,3);
        create(permissionDescriptionDto7,"5分钟超长语音",null,120,0,1,4);
        create(permissionDescriptionDto7,"发送视频",null,null,1,1,5);
        create(permissionDescriptionDto7,"私下单独聊聊",null,null,1,1,6);
        create(permissionDescriptionDto7,"10个聚合王国",10,null,1,1,7);
        create(permissionDescriptionDto7,"可偷王国数目",10,null,1,0,8);
        create(permissionDescriptionDto7,"单次偷取上限",8,null,1,0,9);
        create(permissionDescriptionDto7,"个人每日获取金币上限",200,null,1,0,10);

        PermissionDescriptionDto permissionDescriptionDto8 = new PermissionDescriptionDto();
        permissionDescriptionDto8.setLevel(8);
        create(permissionDescriptionDto8,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto8,"王国专属文字排版",null,null,1,1,2);
        create(permissionDescriptionDto8,"超清晰原图展示",null,null,1,1,3);
        create(permissionDescriptionDto8,"5分钟超长语音",null,300,1,1,4);
        create(permissionDescriptionDto8,"发送视频",null,null,1,1,5);
        create(permissionDescriptionDto8,"私下单独聊聊",null,null,1,1,6);
        create(permissionDescriptionDto8,"10个聚合王国",10,null,1,1,7);
        create(permissionDescriptionDto8,"可偷王国数目",11,null,1,0,8);
        create(permissionDescriptionDto8,"单次偷取上限",8,null,1,0,9);
        create(permissionDescriptionDto8,"个人每日获取金币上限",250,null,1,0,10);

        PermissionDescriptionDto permissionDescriptionDto9 = new PermissionDescriptionDto();
        permissionDescriptionDto9.setLevel(9);
        create(permissionDescriptionDto9,"米汤专属表情包",null,null,1 ,1,1);
        create(permissionDescriptionDto9,"王国专属文字排版",null,null,1,1,2);
        create(permissionDescriptionDto9,"超清晰原图展示",null,null,2,1,3);
        create(permissionDescriptionDto9,"五分钟超长语音",null,300,1,1,4);
        create(permissionDescriptionDto9,"发送视频",null,null,1,1,5);
        create(permissionDescriptionDto9,"私下单独聊聊",null,null,1,1,6);
        create(permissionDescriptionDto9,"10个聚合王国",999999999,null,1,1,7);
        create(permissionDescriptionDto9,"可偷王国数目",12,null,1,0,8);
        create(permissionDescriptionDto9,"单次偷取上限",8,null,1,0,9);
        create(permissionDescriptionDto9,"个人每日获取金币上限",300,null,1,0,10);

        List<PermissionDescriptionDto> list = Lists.newArrayList();
        list.add(permissionDescriptionDto9);
      /*  for(PermissionDescriptionDto xx : list){
            String ret = JSON.toJSONString(xx);
            System.out.println(ret);
            System.out.println("=====================================================");
        }*/
        String ret = JSON.toJSONString(list);
        System.out.println(ret);
    }

}
