package com.me2me.monitor.dto;

import com.me2me.common.web.BaseEntity;
import com.me2me.monitor.model.AccessTrack;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/13.
 */
public class AccessTrackDto implements BaseEntity {

    public AccessTrack getAccessTrack(){
        AccessTrack accessTrack = new AccessTrack();
        accessTrack.setCreateTime(new Date());
        return accessTrack;
    }


    public enum MonitorAction{
        BOOT(0,"用户启动"),
        LOGIN(1,"用户登录"),
        REGISTER(2,"用户注册"),
        CONTENT_VIEW(3,"用户浏览"),
        CONTENT_PUBLISH(4,"发布内容"),
        LIVE_PUBLISH(5,"发布直播"),
        LIKE(6,"用户点赞"),
        UN_LIKE(7,"用取消点赞"),
        REVIEW(8,"用户评论"),
        FEELING_TAG(9,"添加感受标签"),
        FOLLOW(10,"关注"),
        UN_FOLLOW(11,"取消关注"),
        FORWARD(12,"转发内容");

        public int index;

        public String name;

        MonitorAction(int index,String name){
            this.index = index;
            this.name = name;
        }

    };

}
